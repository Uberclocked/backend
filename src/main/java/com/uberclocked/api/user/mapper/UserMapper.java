package com.uberclocked.api.user.mapper;

import com.uberclocked.api.user.model.dto.UserDataDto;
import com.uberclocked.api.user.model.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "country", source = "country", defaultValue = "")
  @Mapping(target = "cellPhone", source = "cellPhone", defaultValue = "")
  UserDataDto toDto(User entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "auth0Id", ignore = true)
  @Mapping(target = "lastLogin", ignore = true)
  @Mapping(target = "userStatus", ignore = true)
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "userName", ignore = true)
  void update(UserDataDto dto, @MappingTarget User entity);
}
