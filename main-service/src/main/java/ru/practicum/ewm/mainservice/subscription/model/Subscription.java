package ru.practicum.ewm.mainservice.subscription.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@IdClass(SubscriptionId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {
    @Id
    @Column(name = "subscriber_id")
    private Long subscriberId;

    @Id
    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "subscribed_at", nullable = false)
    @Builder.Default
    private LocalDateTime subscribedAt = LocalDateTime.now();
}
