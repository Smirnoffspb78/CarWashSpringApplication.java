package com.smirnov.carwashspring.configuration;

import com.smirnov.carwashspring.dto.request.create.BoxCreateDTO;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BoxMapperConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BoxMapperConfiguration.class);
    public final ModelMapper modelMapper;

    private final UserRepository userRepository;

    @PostConstruct
    public void configure() {
        modelMapper.typeMap(BoxCreateDTO.class, Box.class)
                .addMappings(mapping -> mapping.using((MappingContext<Integer, User> context) ->
                        userRepository.findByIdAndRoleAndDeletedIsFalse(context.getSource(), new Role(RolesUser.OPERATOR))
                                .orElseThrow(() -> {
                                    log.error("Оператор с id %d не найден".formatted(context.getSource()));
                                    return new EntityNotFoundException(User.class, context.getSource());})
                ).map(BoxCreateDTO::getOperatorId, Box::setUser));
    }
}
