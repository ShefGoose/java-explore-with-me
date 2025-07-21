package ru.practicum.ewm.mainservice.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.mainservice.event.dto.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompilationDto {
    private Long id;

    private String title;

    private boolean pinned;

    private List<EventShortDto> events;
}
