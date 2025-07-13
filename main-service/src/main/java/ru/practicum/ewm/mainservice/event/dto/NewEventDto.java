package ru.practicum.ewm.mainservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.mainservice.advice.constant.ValidationConst;
import ru.practicum.ewm.mainservice.advice.validation.EventDateValidation;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {
    @NotBlank
    @Size(min = ValidationConst.EVENT_TITLE_MIN,
            max = ValidationConst.EVENT_TITLE_MAX,
            message = "Длина title должна быть от {min} до {max} символов")
    private String title;

    @NotBlank
    @Size(min = ValidationConst.EVENT_ANN_MIN,
            max = ValidationConst.EVENT_ANN_MAX,
            message = "Длина annotation должна быть от {min} до {max} символов")
    private String annotation;

    @NotBlank
    @Size(min = ValidationConst.EVENT_DESC_MIN,
            max = ValidationConst.EVENT_DESC_MAX,
            message = "Длина description должна быть от {min} до {max} символов")
    private String description;

    @NotNull
    @Future(message = "eventDate должно содержать дату, которая еще не наступила")
    @EventDateValidation
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    @Valid
    private LocationDto location;

    @NotNull
    @Positive
    @JsonProperty("category")
    private Long categoryId;

    private Boolean paid;
    private Boolean requestModeration;

    @PositiveOrZero
    private Long participantLimit;
}
