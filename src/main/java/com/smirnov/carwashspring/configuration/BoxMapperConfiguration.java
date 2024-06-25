package com.smirnov.carwashspring.configuration;

import com.smirnov.carwashspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BoxMapperConfiguration {

    public final ModelMapper modelMapper;

    private final UserRepository userRepository;

   /* @PostConstruct
    public void configure() {
        modelMapper.typeMap(BoxCreateDTO.class, Box.class)
                .addMappings(mapping -> mapping.using((MappingContext<Integer, User> context) -> {
                    final Integer userId = context.getSource();
                    if (userId != null) {
                        Role role = new Role();
                        role.setRolesUser(RolesUser.OPERATOR);
                        return userRepository.findByIdAndRoleAndDeletedIsFalse(userId, role)
                                .orElseThrow(() -> new UserNotFoundException("Operator not found"));
                    } else {
                        throw new UserNotFoundException("Operator not found");
                    }
                }).map(BoxCreateDTO::userId, Box::setUser));
    }*/
}
