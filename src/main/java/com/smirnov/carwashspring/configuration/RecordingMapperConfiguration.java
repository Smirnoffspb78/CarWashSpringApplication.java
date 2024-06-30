package com.smirnov.carwashspring.configuration;

import com.smirnov.carwashspring.dto.response.get.RecordingComplitedDTO;
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
        modelMapper.typeMap(Recording.class, RecordingComplitedDTO.class)
                .setConverter(context -> {
                    Recording r = context.getSource();
                    return RecordingComplitedDTO.builder()
                            .id(r.getId())
                            .cost(r.getCost())
                            .timeComplite(Duration.between(r.getStart(), r.getFinish()).toMinutes())
                            .servicesName(r.getServices().stream()
                                    .map(CarWashService::getName)
                                    .collect(Collectors.toSet()))
                            .build();
                });
    }
}
