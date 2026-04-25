package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Coachee;
import com.elabbasy.coatchinghub.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoacheeRepository extends JpaRepository<Coachee, Long>, CustomCoacheeRepository {
}
