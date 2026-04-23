package com.gbsw.template.domain.place.service;

import com.gbsw.template.domain.place.dto.BuildingResponse;
import com.gbsw.template.domain.place.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;

    @Transactional(readOnly = true)
    public List<BuildingResponse> getAll() {
        return buildingRepository.findAllWithFloorsAndRooms().stream()
                .map(BuildingResponse::from)
                .toList();
    }
}
