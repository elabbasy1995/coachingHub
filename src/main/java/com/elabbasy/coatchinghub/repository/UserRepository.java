package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "roles.permissions", "admin", "coach", "coachee"})
    Optional<User> findDetailedByEmail(String email);

    boolean existsByEmail(String email);
}
