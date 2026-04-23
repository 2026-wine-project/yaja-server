package com.gbsw.template.domain.place.controller;

import com.gbsw.template.domain.place.dto.BuildingResponse;
import com.gbsw.template.domain.place.service.BuildingService;
import com.gbsw.template.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Buildings", description = "건물·장소 API")
@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @Operation(summary = "건물·층·장소 전체 목록 조회")
    @GetMapping
    public ApiResponse<List<BuildingResponse>> getAll() {
        return ApiResponse.success(buildingService.getAll());
    }
}
