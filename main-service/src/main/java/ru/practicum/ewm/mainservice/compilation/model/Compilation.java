package ru.practicum.ewm.mainservice.compilation.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.mainservice.event.model.Event;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "pinned")
    private Boolean pinned = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"),
            uniqueConstraints = {
                    @UniqueConstraint(
                            columnNames = {"compilation_id", "event_id"})
            }
    )
    private Set<Event> events = new HashSet<>();
}
