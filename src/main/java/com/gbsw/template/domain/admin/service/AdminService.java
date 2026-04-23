package com.gbsw.template.domain.admin.service;

import com.gbsw.template.domain.admin.dto.DailyStatsResponse;
import com.gbsw.template.domain.admin.dto.ManualCheckRequest;
import com.gbsw.template.domain.admin.dto.RoomStatResponse;
import com.gbsw.template.domain.check.dto.CheckRecordResponse;
import com.gbsw.template.domain.check.entity.CheckRecord;
import com.gbsw.template.domain.check.entity.CheckStatus;
import com.gbsw.template.domain.check.repository.CheckRecordRepository;
import com.gbsw.template.domain.place.entity.Room;
import com.gbsw.template.domain.place.repository.RoomRepository;
import com.gbsw.template.domain.user.entity.Role;
import com.gbsw.template.domain.user.entity.UserEntity;
import com.gbsw.template.domain.user.repository.UserRepository;
import com.gbsw.template.global.exception.CustomException;
import com.gbsw.template.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdminService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final CheckRecordRepository checkRecordRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public DailyStatsResponse getDailyStats(LocalDate date) {
        long total = userRepository.countByRole(Role.STUDENT);
        long checked = checkRecordRepository.countByDate(date);
        long unchecked = Math.max(total - checked, 0);
        return DailyStatsResponse.builder()
                .date(date)
                .total(total)
                .checked(checked)
                .unchecked(unchecked)
                .build();
    }

    @Transactional(readOnly = true)
    public List<RoomStatResponse> getRoomStats(LocalDate date) {
        List<CheckRecord> records = checkRecordRepository.findAllByDate(date);

        Map<Room, List<CheckRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(CheckRecord::getRoom));

        return grouped.entrySet().stream()
                .map(entry -> {
                    Room room = entry.getKey();
                    List<String> studentNames = entry.getValue().stream()
                            .map(cr -> cr.getStudent().getName())
                            .toList();
                    return RoomStatResponse.builder()
                            .roomId(String.valueOf(room.getId()))
                            .roomName(room.getName())
                            .buildingName(room.getFloor().getBuilding().getName())
                            .floorLabel(room.getFloor().getLabel())
                            .count(studentNames.size())
                            .students(studentNames)
                            .build();
                })
                .sorted(Comparator.comparingLong(RoomStatResponse::getCount).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CheckRecordResponse> getUnchecked(LocalDate date) {
        Set<Long> checkedIds = checkRecordRepository.findAllByDate(date).stream()
                .map(cr -> cr.getStudent().getId())
                .collect(Collectors.toCollection(HashSet::new));

        return userRepository.findAllByRole(Role.STUDENT).stream()
                .filter(s -> !checkedIds.contains(s.getId()))
                .map(s -> CheckRecordResponse.absent(s, date))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CheckRecordResponse> getRecordsOn(LocalDate date) {
        List<CheckRecord> records = checkRecordRepository.findAllByDate(date);
        Set<Long> checkedIds = records.stream()
                .map(cr -> cr.getStudent().getId())
                .collect(Collectors.toCollection(HashSet::new));

        List<CheckRecordResponse> checkedList = records.stream()
                .map(CheckRecordResponse::from)
                .toList();

        List<CheckRecordResponse> uncheckedList = userRepository.findAllByRole(Role.STUDENT).stream()
                .filter(s -> !checkedIds.contains(s.getId()))
                .map(s -> CheckRecordResponse.absent(s, date))
                .toList();

        return Stream.concat(checkedList.stream(), uncheckedList.stream()).toList();
    }

    @Transactional(readOnly = true)
    public List<CheckRecordResponse> getRecordsBetween(LocalDate startDate, LocalDate endDate) {
        return checkRecordRepository.findAllByDateBetween(startDate, endDate).stream()
                .map(CheckRecordResponse::from)
                .toList();
    }

    @Transactional
    public CheckRecordResponse manualCheck(ManualCheckRequest request) {
        Long studentId = parseLongId(request.getStudentId(), ErrorCode.STUDENT_NOT_FOUND);
        UserEntity student = userRepository.findById(studentId)
                .filter(u -> u.getRole() == Role.STUDENT)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        Long roomId = parseLongId(request.getRoomId(), ErrorCode.ROOM_NOT_FOUND);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        LocalTime now = ZonedDateTime.now(KST).toLocalTime().withSecond(0).withNano(0);

        Optional<CheckRecord> existing =
                checkRecordRepository.findByStudentIdAndDate(student.getId(), request.getDate());

        CheckRecord record = existing.map(r -> {
            r.updateRoomAndCheckedAt(room, now);
            return r;
        }).orElseGet(() -> checkRecordRepository.save(CheckRecord.builder()
                .student(student)
                .room(room)
                .date(request.getDate())
                .checkedAt(now)
                .status(CheckStatus.CHECKED)
                .build()));

        return CheckRecordResponse.from(record);
    }

    private Long parseLongId(String raw, ErrorCode errorCode) {
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            throw new CustomException(errorCode);
        }
    }
}
