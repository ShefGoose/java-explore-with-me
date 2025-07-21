package ru.practicum.ewm.mainservice.compilation.dto;

import ru.practicum.ewm.mainservice.compilation.model.Compilation;
import ru.practicum.ewm.mainservice.event.dto.EventShortDto;
import ru.practicum.ewm.mainservice.event.model.Event;

import java.util.*;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventsDto) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                eventsDto
        );
    }

    public static Compilation toCompilation(NewCompilationDto newCompilationDto,
                                            Collection<Event> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .events(new HashSet<>(events))
                .build();
    }
}
