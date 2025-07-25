package ru.practicum.ewm.mainservice.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.mainservice.advice.enums.EventState;
import ru.practicum.ewm.mainservice.event.model.Event;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @EntityGraph(attributePaths = {"category", "initiator"})
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    @EntityGraph(attributePaths = {"category", "initiator"})
    @NonNull
    Optional<Event> findById(@NonNull Long eventId);

    @EntityGraph(attributePaths = {"category", "initiator"})
    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = {"category", "initiator"})
    Collection<Event> findAllByIdIn(Iterable<Long> eventsIds);

    @EntityGraph(attributePaths = {"category", "initiator"})
    Collection<Event> findAllByIdInAndState(Iterable<Long> eventsIds, EventState state);

    @EntityGraph(attributePaths = {"category", "initiator"})
    @Query("""
            SELECT e
            FROM Event e
            WHERE e.initiator.id = :userId
            AND e.state = 'PUBLISHED'
            """)
    Collection<Event> findAllPublishedByInitiatorId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"category", "initiator"})
    @Query("""
            SELECT e
            FROM Event e
            WHERE e.initiator.id IN :initiatorIds
            AND e.state = 'PUBLISHED'
            """)
    Page<Event> findAllPublishedByInitiatorIdIn(@Param("initiatorIds") Collection<Long> initiatorIds, Pageable pageable);

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"category", "initiator"})
    Page<Event> findAll(@Nullable Specification<Event> spec, @NonNull Pageable pageable);
}
