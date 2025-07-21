package ru.practicum.ewm.mainservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.mainservice.advice.constant.ValidationConst;
import ru.practicum.ewm.mainservice.advice.enums.StateActionAdmin;
import ru.practicum.ewm.mainservice.advice.validation.EventDateValidation;
import ru.practicum.ewm.mainservice.advice.validation.NullOrNotBlank;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventAdminRequest {
    @Size(min = ValidationConst.EVENT_ANN_MIN,
            max = ValidationConst.EVENT_ANN_MAX,
            message = "Длина annotation должна быть от {min} до {max} символов")
    @NullOrNotBlank
    private String annotation;

    @Size(min = ValidationConst.EVENT_DESC_MIN,
            max = ValidationConst.EVENT_DESC_MAX,
            message = "Длина description должна быть от {min} до {max} символов")
    @NullOrNotBlank
    private String description;

    @Size(min = ValidationConst.EVENT_TITLE_MIN,
            max = ValidationConst.EVENT_TITLE_MAX,
            message = "Длина title должна быть от {min} до {max} символов")
    @NullOrNotBlank
    private String title;

    @Positive
    @JsonProperty("category")
    private Long categoryId;

    @Future(message = "eventDate должно содержать дату, которая еще не наступила")
    @EventDateValidation
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @Valid
    private LocationDto location;

    private Boolean paid;

    @Positive
    private Long participantLimit;

    private Boolean requestModeration;

    private StateActionAdmin stateAction;
}
