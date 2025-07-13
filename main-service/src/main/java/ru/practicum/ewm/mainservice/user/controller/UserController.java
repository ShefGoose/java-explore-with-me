package ru.practicum.ewm.mainservice.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.user.dto.UserDto;
import ru.practicum.ewm.mainservice.user.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/users")
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    @Validated
    public Collection<UserDto> findAll(@RequestParam(name = "ids", required = false) List<Long> ids,
                                       @PositiveOrZero @RequestParam(name = "from", required = false,
                                               defaultValue = "0") Integer from,
                                       @Positive @RequestParam(name = "size", required = false,
                                               defaultValue = "10") Integer size) {
        return userService.findAll(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        return userService.create(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
