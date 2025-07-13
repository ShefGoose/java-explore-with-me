package ru.practicum.ewm.mainservice.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.mainservice.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsById(@NonNull Long userId);

    Page<User> findAllByIdIn(List<Long> userIds, Pageable pageable);
}
