package ru.practicum.ewm.mainservice.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.mainservice.advice.enums.RequestStatus;
import ru.practicum.ewm.mainservice.request.model.Request;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByEventIdAndRequesterId(Long eventId, Long userId);

    Collection<Request> findAllByRequesterId(Long userId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    Collection<Request> findAllByEventId(Long eventId);

    Collection<Request> findAllByIdInAndStatus(Iterable<Long> requestIds, RequestStatus status);

    @Modifying
    @Query("""
            update Request r
            set r.status = 'REJECTED'
            where r.event.id = :eventId
            and r.status = 'PENDING'
            """)
    int rejectAllPendingByEventId(Long eventId);
}
