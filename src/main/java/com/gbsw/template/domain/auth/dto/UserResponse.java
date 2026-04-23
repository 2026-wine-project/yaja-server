package com.gbsw.template.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gbsw.template.domain.user.entity.Role;
import com.gbsw.template.domain.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private String id;
    private String name;
    private Role role;
    private Integer grade;
    private Integer classNum;
    private Integer studentNum;
    private Integer admissionYear;

    public static UserResponse from(UserEntity user) {
        UserResponseBuilder builder = UserResponse.builder()
                .id(String.valueOf(user.getId()))
                .name(user.getName())
                .role(user.getRole());

        if (user.getRole() == Role.STUDENT) {
            builder.grade(user.getGrade())
                    .classNum(user.getClassNum())
                    .studentNum(user.getStudentNum())
                    .admissionYear(user.getAdmissionYear());
        }

        return builder.build();
    }
}
