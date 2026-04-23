package com.gbsw.template.domain.place.seed;

import com.gbsw.template.domain.place.entity.Building;
import com.gbsw.template.domain.place.entity.Floor;
import com.gbsw.template.domain.place.entity.Room;
import com.gbsw.template.domain.place.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PlaceSeeder {

    @Bean
    public ApplicationRunner seedBuildings(BuildingRepository buildingRepository) {
        return args -> {
            if (buildingRepository.count() > 0) {
                return;
            }

            Building hoyeon = Building.builder().name("호연관").build();

            Floor floor1 = Floor.builder().building(hoyeon).number(1).label("1F").build();
            floor1.getRooms().add(Room.builder().floor(floor1).name("자습실").build());
            floor1.getRooms().add(Room.builder().floor(floor1).name("도서관").build());

            Floor floor2 = Floor.builder().building(hoyeon).number(2).label("2F").build();
            floor2.getRooms().add(Room.builder().floor(floor2).name("멀티미디어실").build());

            Floor floor3 = Floor.builder().building(hoyeon).number(3).label("3F").build();
            floor3.getRooms().add(Room.builder().floor(floor3).name("세미나실").build());

            hoyeon.getFloors().addAll(List.of(floor1, floor2, floor3));

            buildingRepository.save(hoyeon);
            log.info("Seeded building: 호연관 ({} floors)", hoyeon.getFloors().size());
        };
    }
}
