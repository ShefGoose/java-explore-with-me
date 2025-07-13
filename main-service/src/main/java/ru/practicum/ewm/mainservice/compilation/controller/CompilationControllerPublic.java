package ru.practicum.ewm.mainservice.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.compilation.dto.CompilationDto;
import ru.practicum.ewm.mainservice.compilation.service.CompilationService;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/compilations")
public class CompilationControllerPublic {
    private CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto find(@PathVariable Long compId) {
        return compilationService.find(compId);
    }

    @GetMapping
    public Collection<CompilationDto> findAll(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                              @PositiveOrZero @RequestParam(name = "from", required = false,
                                                      defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", required = false,
                                                      defaultValue = "10") Integer size) {
        return compilationService.findAll(pinned, from, size);
    }
}
