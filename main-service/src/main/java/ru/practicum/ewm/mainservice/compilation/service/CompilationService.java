package ru.practicum.ewm.mainservice.compilation.service;

import ru.practicum.ewm.mainservice.compilation.dto.CompilationDto;
import ru.practicum.ewm.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.mainservice.compilation.dto.UpdateCompilationRequest;

import java.util.Collection;

public interface CompilationService {
    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto update(Long compId, UpdateCompilationRequest updateComp);

    void delete(Long compId);

    CompilationDto find(Long compId);

    Collection<CompilationDto> findAll(Boolean pinned, Integer from, Integer size);
}
