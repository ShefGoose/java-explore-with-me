package ru.practicum.ewm.mainservice.event.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.mainservice.advice.enums.UpdateRequestStatus;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    @NotEmpty(message = "requestIds не может быть пуст")
    private Set<Long> requestIds;

    @NotNull(message = "status не может быть null")
    private UpdateRequestStatus status;
}
