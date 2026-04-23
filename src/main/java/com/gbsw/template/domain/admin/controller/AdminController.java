package com.gbsw.template.domain.admin.controller;

import com.gbsw.template.domain.admin.dto.DailyStatsResponse;
import com.gbsw.template.domain.admin.dto.ManualCheckRequest;
import com.gbsw.template.domain.admin.dto.RoomStatResponse;
import com.gbsw.template.domain.admin.service.AdminService;
import com.gbsw.template.domain.check.dto.CheckRecordResponse;
import com.gbsw.template.global.common.ApiResponse;
import com.gbsw.template.global.exception.CustomException;
import com.gbsw.template.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Admin", description = "관리자 API")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "일일 통계 (total/checked/unchecked)")
    @GetMapping("/stats")
    public ApiResponse<DailyStatsResponse> getStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ApiResponse.success(adminService.getDailyStats(date));
    }

    @Operation(summary = "장소별 출석 현황 (count DESC)")
    @GetMapping("/room-stats")
    public ApiResponse<List<RoomStatResponse>> getRoomStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ApiResponse.success(adminService.getRoomStats(date));
    }

    @Operation(summary = "미출석 학생 목록")
    @GetMapping("/unchecked")
    public ApiResponse<List<CheckRecordResponse>> getUnchecked(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ApiResponse.success(adminService.getUnchecked(date));
    }

    @Operation(summary = "전체 출석 기록 (date 단일 또는 startDate/endDate 범위)")
    @GetMapping("/records")
    public ApiResponse<List<CheckRecordResponse>> getRecords(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        if (date != null) {
            return ApiResponse.success(adminService.getRecordsOn(date));
        }
        if (startDate != null && endDate != null) {
            return ApiResponse.success(adminService.getRecordsBetween(startDate, endDate));
        }
        throw new CustomException(ErrorCode.MISSING_FIELDS);
    }

    @Operation(summary = "수동 출석 입력 (Upsert)")
    @PostMapping("/check")
    public ApiResponse<CheckRecordResponse> manualCheck(
            @Valid @RequestBody ManualCheckRequest request
    ) {
        return ApiResponse.success(adminService.manualCheck(request));
    }
}
