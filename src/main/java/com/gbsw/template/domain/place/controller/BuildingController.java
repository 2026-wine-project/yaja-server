package com.gbsw.template.domain.place.controller;

import com.gbsw.template.domain.place.dto.BuildingResponse;
import com.gbsw.template.domain.place.service.BuildingService;
import com.gbsw.template.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping
    public ApiResponse<List<BuildingResponse>> getAll() {
        return ApiResponse.success(buildingService.getAll());
    }
}
