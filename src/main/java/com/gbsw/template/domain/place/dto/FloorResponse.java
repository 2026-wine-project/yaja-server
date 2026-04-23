package com.gbsw.template.domain.place.dto;

import com.gbsw.template.domain.place.entity.Floor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FloorResponse {

    private String id;
    private Integer number;
    private String label;
    private List<RoomResponse> rooms;

    public static FloorResponse from(Floor floor) {
        return FloorResponse.builder()
                .id(String.valueOf(floor.getId()))
                .number(floor.getNumber())
                .label(floor.getLabel())
                .rooms(floor.getRooms().stream().map(RoomResponse::from).toList())
                .build();
    }
}
