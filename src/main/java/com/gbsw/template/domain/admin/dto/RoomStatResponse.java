package com.gbsw.template.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoomStatResponse {

    private String roomId;
    private String roomName;
    private String buildingName;
    private String floorLabel;
    private long count;
    private List<String> students;
}
