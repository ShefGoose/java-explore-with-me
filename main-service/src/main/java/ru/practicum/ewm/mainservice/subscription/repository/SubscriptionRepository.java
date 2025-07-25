package ru.practicum.ewm.mainservice.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.mainservice.subscription.model.Subscription;
import ru.practicum.ewm.mainservice.subscription.model.SubscriptionId;

import java.util.Collection;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    Collection<Subscription> findAllBySubscriberId(Long userId);
}
