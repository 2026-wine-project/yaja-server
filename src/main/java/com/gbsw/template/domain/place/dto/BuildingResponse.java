package com.gbsw.template.domain.place.dto;

import com.gbsw.template.domain.place.entity.Building;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BuildingResponse {

    private String id;
    private String name;
    private List<FloorResponse> floors;

    public static BuildingResponse from(Building building) {
        return BuildingResponse.builder()
                .id(String.valueOf(building.getId()))
                .name(building.getName())
                .floors(building.getFloors().stream().map(FloorResponse::from).toList())
                .build();
    }
}
