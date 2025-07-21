package ru.practicum.ewm.mainservice.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.mainservice.advice.constant.ValidationConst;
import ru.practicum.ewm.mainservice.advice.validation.NullOrNotBlank;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCompilationRequest {
    @NullOrNotBlank
    @Size(min = ValidationConst.COMP_TITLE_MIN,
            max = ValidationConst.COMP_TITLE_MAX,
            message = "Длина title должна быть от {min} до {max} символов")
    private String title;
    private Boolean pinned;
    private Set<Long> events;
}
