package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.response.CoachCoacheeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomCoacheeRepository {

    Page<CoachCoacheeResponse> findCoacheesForCoach(
            Long coachId,
            Pageable pageable,
            String name
    );
}
