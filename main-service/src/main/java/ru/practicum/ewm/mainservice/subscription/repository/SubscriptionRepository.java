package ru.practicum.ewm.mainservice.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.mainservice.subscription.model.Subscription;
import ru.practicum.ewm.mainservice.subscription.model.SubscriptionId;

import java.util.Collection;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    @Query("""
            SELECT s.targetId
            FROM Subscription s
            WHERE s.subscriberId = :userId
            """)
    Collection<Long> findAllTargetIds(@Param("userId") Long userId);
}
