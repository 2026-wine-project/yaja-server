package com.gbsw.template.domain.check.controller;

import com.gbsw.template.domain.check.dto.CheckRecordResponse;
import com.gbsw.template.domain.check.dto.CheckRequest;
import com.gbsw.template.domain.check.service.CheckService;
import com.gbsw.template.global.common.ApiResponse;
import com.gbsw.template.global.security.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Students", description = "학생 출석 API")
@RestController
@RequestMapping("/students/{studentId}")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentCheckController {

    private final CheckService checkService;

    @Operation(summary = "특정 날짜 출석 기록 조회 (없으면 null)")
    @GetMapping("/check")
    public ApiResponse<CheckRecordResponse> getCheck(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        AuthUtil.ensureSelf(studentId);
        return ApiResponse.success(checkService.findByStudentAndDate(studentId, date));
    }

    @Operation(summary = "출석 체크 제출 (Upsert)")
    @PostMapping("/check")
    public ApiResponse<CheckRecordResponse> submitCheck(
            @PathVariable Long studentId,
            @Valid @RequestBody CheckRequest request
    ) {
        AuthUtil.ensureSelf(studentId);
        return ApiResponse.success(checkService.submitCheck(studentId, request));
    }

    @Operation(summary = "본인 출석 히스토리 (date DESC)")
    @GetMapping("/history")
    public ApiResponse<List<CheckRecordResponse>> getHistory(@PathVariable Long studentId) {
        AuthUtil.ensureSelf(studentId);
        return ApiResponse.success(checkService.getHistory(studentId));
    }
}
