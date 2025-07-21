package ru.practicum.ewm.mainservice.user.service;

import ru.practicum.ewm.mainservice.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    void delete(Long userId);

    Collection<UserDto> findAll(List<Long> userIds, Integer from, Integer size);
}
