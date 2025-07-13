package ru.practicum.ewm.mainservice.event.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    private Double lat;

    private Double lon;
}
