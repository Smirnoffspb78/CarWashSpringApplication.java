package com.smirnov.carwashspring.configuration.mapper;

import com.smirnov.carwashspring.dto.response.get.RecordingCompletedDTO;
import com.smirnov.carwashspring.entity.service.CarWashService;
import com.smirnov.carwashspring.entity.service.Recording;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class RecordingMapperConfiguration {

    private final ModelMapper modelMapper;

    @PostConstruct
    private void configure() {
        modelMapper.typeMap(Recording.class, RecordingCompletedDTO.class)
                .setConverter(context -> {
                    Recording r = context.getSource();
                    return RecordingCompletedDTO.builder()
                            .id(r.getId())
                            .cost(r.getCost())
                            .timeComplete(Duration.between(r.getStart(), r.getFinish()).toMinutes())
                            .servicesName(r.getServices().stream()
                                    .map(CarWashService::getName)
                                    .collect(Collectors.toSet()))
                            .build();
                });
    }
}
