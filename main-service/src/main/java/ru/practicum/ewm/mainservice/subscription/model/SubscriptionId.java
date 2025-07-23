package ru.practicum.ewm.mainservice.subscription.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SubscriptionId implements Serializable {
    private Long subscriberId;
    private Long targetId;
}
