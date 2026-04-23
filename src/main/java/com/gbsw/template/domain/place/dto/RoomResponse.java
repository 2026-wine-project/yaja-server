package com.gbsw.template.domain.place.dto;

import com.gbsw.template.domain.place.entity.Room;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomResponse {

    private String id;
    private String name;
    private String floorId;
    private String buildingId;
    private String buildingName;
    private String floorLabel;

    public static RoomResponse from(Room room) {
        return RoomResponse.builder()
                .id(String.valueOf(room.getId()))
                .name(room.getName())
                .floorId(String.valueOf(room.getFloor().getId()))
                .buildingId(String.valueOf(room.getFloor().getBuilding().getId()))
                .buildingName(room.getFloor().getBuilding().getName())
                .floorLabel(room.getFloor().getLabel())
                .build();
    }
}
