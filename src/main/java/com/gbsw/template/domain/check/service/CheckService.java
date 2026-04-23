package com.gbsw.template.domain.check.service;

import com.gbsw.template.domain.check.dto.CheckRecordResponse;
import com.gbsw.template.domain.check.dto.CheckRequest;
import com.gbsw.template.domain.check.entity.CheckRecord;
import com.gbsw.template.domain.check.entity.CheckStatus;
import com.gbsw.template.domain.check.repository.CheckRecordRepository;
import com.gbsw.template.domain.place.entity.Room;
import com.gbsw.template.domain.place.repository.RoomRepository;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final CheckRecordRepository checkRecordRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public CheckRecordResponse findByStudentAndDate(Long studentId, LocalDate date) {
        ensureStudentExists(studentId);
        return checkRecordRepository.findByStudentIdAndDate(studentId, date)
                .map(CheckRecordResponse::from)
                .orElse(null);
    }

    @Transactional
    public CheckRecordResponse submitCheck(Long studentId, CheckRequest request) {
        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));
        Room room = resolveRoom(request.getRoomId());

        LocalTime now = ZonedDateTime.now(KST).toLocalTime().withSecond(0).withNano(0);

        Optional<CheckRecord> existing = checkRecordRepository.findByStudentIdAndDate(studentId, request.getDate());
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

    @Transactional(readOnly = true)
    public List<CheckRecordResponse> getHistory(Long studentId) {
        ensureStudentExists(studentId);
        return checkRecordRepository.findAllByStudentIdOrderByDateDesc(studentId).stream()
                .map(CheckRecordResponse::from)
                .toList();
    }

    private void ensureStudentExists(Long studentId) {
        if (!userRepository.existsById(studentId)) {
            throw new CustomException(ErrorCode.STUDENT_NOT_FOUND);
        }
    }

    private Room resolveRoom(String roomId) {
        long id;
        try {
            id = Long.parseLong(roomId);
        } catch (NumberFormatException e) {
            throw new CustomException(ErrorCode.ROOM_NOT_FOUND);
        }
        return roomRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
    }
}
