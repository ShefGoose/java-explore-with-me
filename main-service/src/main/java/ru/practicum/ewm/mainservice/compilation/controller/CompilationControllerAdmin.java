package ru.practicum.ewm.mainservice.compilation.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.compilation.dto.CompilationDto;
import ru.practicum.ewm.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.mainservice.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.mainservice.compilation.service.CompilationService;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationControllerAdmin {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return compilationService.create(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable(name = "compId") Long compId,
                                 @RequestBody @Valid UpdateCompilationRequest updateComp) {
        return compilationService.update(compId, updateComp);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(name = "compId") Long compId) {
        compilationService.delete(compId);
    }
}
