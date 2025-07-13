package ru.practicum.ewm.mainservice.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.mainservice.advice.exception.DuplicateEmailException;
import ru.practicum.ewm.mainservice.user.dto.UserDto;
import ru.practicum.ewm.mainservice.user.model.User;
import ru.practicum.ewm.mainservice.user.repository.UserRepository;
import ru.practicum.ewm.mainservice.user.service.UserServiceImp;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImp userService;

    @Test
    void createDuplicateEmailException() {
        when(userRepository.existsByEmail("wasd@gmail.com")).thenReturn(true);

        UserDto dto = UserDto.builder()
                .name("Name")
                .email("wasd@gmail.com")
                .build();

        assertThrows(DuplicateEmailException.class,
                () -> userService.create(dto));
    }

    @Test
    void findAllWithoutIdsReturnsAllUsers() {

        User u1 = User.builder().id(1L).name("John").email("johng@test.com").build();
        User u2 = User.builder().id(2L).name("Jack").email("jackj@test.com").build();

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(u1, u2)));

        Collection<UserDto> dtos = userService.findAll(null, 0, 10);

        assertEquals(2, dtos.size());
        assertTrue(dtos.stream().anyMatch(d -> d.getName().equals("John")));
        assertTrue(dtos.stream().anyMatch(d -> d.getName().equals("Jack")));
    }
}
