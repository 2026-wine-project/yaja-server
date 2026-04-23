package com.gbsw.template.domain.check.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gbsw.template.domain.check.entity.CheckRecord;
import com.gbsw.template.domain.check.entity.CheckStatus;
import com.gbsw.template.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class CheckRecordResponse {

    private String id;
    private String studentId;
    private String studentName;
    private Integer grade;

    @JsonProperty("class")
    private Integer classNum;

    private String roomId;
    private String roomName;
    private String buildingName;
    private String floorLabel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime checkedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private CheckStatus status;

    public static CheckRecordResponse from(CheckRecord record) {
        UserEntity student = record.getStudent();
        return CheckRecordResponse.builder()
                .id(String.valueOf(record.getId()))
                .studentId(String.valueOf(student.getId()))
                .studentName(student.getName())
                .grade(student.getGrade())
                .classNum(student.getClassNum())
                .roomId(String.valueOf(record.getRoom().getId()))
                .roomName(record.getRoom().getName())
                .buildingName(record.getRoom().getFloor().getBuilding().getName())
                .floorLabel(record.getRoom().getFloor().getLabel())
                .checkedAt(record.getCheckedAt())
                .date(record.getDate())
                .status(record.getStatus())
                .build();
    }

    public static CheckRecordResponse absent(UserEntity student, LocalDate date) {
        return CheckRecordResponse.builder()
                .id("absent-" + student.getId() + "-" + date)
                .studentId(String.valueOf(student.getId()))
                .studentName(student.getName())
                .grade(student.getGrade())
                .classNum(student.getClassNum())
                .date(date)
                .status(CheckStatus.UNCHECKED)
                .build();
    }
}
