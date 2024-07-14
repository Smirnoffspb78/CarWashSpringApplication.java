package com.smirnov.carwashspring.configuration.mapper;

import com.smirnov.carwashspring.dto.response.get.RecordingReservedDTO;
import com.smirnov.carwashspring.entity.service.Recording;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RecordingReserveDTOMapper {

    public final ModelMapper modelMapper;

    @PostConstruct
    public void configure() {
        modelMapper.typeMap(Recording.class, RecordingReservedDTO.class)
                .addMappings(mapping -> mapping.using(new ServiceSetConverter())
                        .map(Recording::getServices, RecordingReservedDTO::setServicesName));
        /*modelMapper.typeMap(Recording.class, RecordingReservedDTO.class)
                .addMappings(mapping -> mapping.using((MappingContext<Set<CarWashService>, Set<String>> context) -> {
                            if (context.getSource() == null) {
                                throw new NullPointerException();
                            }
                            return context.getSource().stream()
                                    .map(CarWashService::getName)
                                    .collect(Collectors.toSet());
                        })
                        .map(Recording::getServices, RecordingReservedDTO::setServicesName));*/

    }
}
