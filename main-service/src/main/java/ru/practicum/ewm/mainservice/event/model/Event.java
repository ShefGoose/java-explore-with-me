package ru.practicum.ewm.mainservice.event.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.mainservice.advice.enums.EventState;
import ru.practicum.ewm.mainservice.category.model.Category;
import ru.practicum.ewm.mainservice.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "annotation")
    private String annotation;

    @Column(name = "description")
    private String description;

    @Column(name = "eventdate")
    private LocalDateTime eventDate;

    @Embedded
    private Location location;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "publishedon")
    private LocalDateTime publishedOn;

    @Column(name = "createdon")
    private LocalDateTime createdOn;

    @Column(name = "requestmoderation")
    private Boolean requestModeration;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(name = "confirmedrequests")
    private Long confirmedRequests;

    @Column(name = "participantlimit")
    private Long participantLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;
}
