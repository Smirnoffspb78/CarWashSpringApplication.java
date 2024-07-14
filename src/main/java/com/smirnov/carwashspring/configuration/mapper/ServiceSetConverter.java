package com.smirnov.carwashspring.configuration.mapper;

import com.smirnov.carwashspring.entity.service.CarWashService;
import org.modelmapper.AbstractConverter;

import java.util.Set;
import java.util.stream.Collectors;

public class ServiceSetConverter extends AbstractConverter<Set<CarWashService>, Set<String>> {

    /**
     * Converts {@code source} to an instance of type {@code D}.
     *
     * @param source
     */
    @Override
    protected Set<String> convert(Set<CarWashService> source) {
        if (source == null){
            throw new NullPointerException("source is null");
        }
        return source.stream()
                .map(CarWashService::getName)
                .collect(Collectors.toSet());
    }
}
