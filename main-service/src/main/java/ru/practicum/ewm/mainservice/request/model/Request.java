package ru.practicum.ewm.mainservice.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.mainservice.advice.enums.RequestStatus;
import ru.practicum.ewm.mainservice.event.model.Event;
import ru.practicum.ewm.mainservice.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDateTime created;
}
