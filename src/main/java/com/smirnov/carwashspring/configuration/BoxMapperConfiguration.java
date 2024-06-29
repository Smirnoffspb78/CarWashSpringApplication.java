package com.smirnov.carwashspring.configuration;

import com.smirnov.carwashspring.dto.request.create.BoxCreateDTO;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.UserNotFoundException;
import com.smirnov.carwashspring.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BoxMapperConfiguration {

    public final ModelMapper modelMapper;

    private final UserRepository userRepository;

    @PostConstruct
    public void configure() {
        modelMapper.typeMap(BoxCreateDTO.class, Box.class)
                .addMappings(mapping -> mapping.using((MappingContext<Integer, User> context) ->
                        userRepository.findByIdAndRoleAndDeletedIsFalse(context.getSource(), new Role(RolesUser.OPERATOR))
                                .orElseThrow(() -> new UserNotFoundException("Operator not found"))
                ).map(BoxCreateDTO::getOperatorId, Box::setUser));
    }
}
