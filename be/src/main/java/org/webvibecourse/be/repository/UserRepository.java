package org.webvibecourse.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.webvibecourse.be.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
}
