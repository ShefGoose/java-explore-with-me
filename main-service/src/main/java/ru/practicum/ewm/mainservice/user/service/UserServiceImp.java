package ru.practicum.ewm.mainservice.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mainservice.advice.Pagination;
import ru.practicum.ewm.mainservice.advice.exception.DuplicateEmailException;
import ru.practicum.ewm.mainservice.advice.exception.EntityNotFoundException;
import ru.practicum.ewm.mainservice.user.dto.UserDto;
import ru.practicum.ewm.mainservice.user.dto.UserMapper;
import ru.practicum.ewm.mainservice.user.model.User;
import ru.practicum.ewm.mainservice.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEmailException("Email уже используется");
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id=" + userId + " was not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public Collection<UserDto> findAll(List<Long> userIds, Integer from, Integer size) {
        PageRequest pageRequest = Pagination.makePageRequest(from, size);
        Page<User> users;
        Pageable pageable = Objects.requireNonNullElseGet(pageRequest,
                () -> PageRequest.of(0, Integer.MAX_VALUE));

        if (userIds == null || userIds.isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findAllByIdIn(userIds,
                    pageable);
        }

        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }
}