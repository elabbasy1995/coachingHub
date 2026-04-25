package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Coach;
import com.elabbasy.coatchinghub.model.enums.CoachStatus;
import com.elabbasy.coatchinghub.model.response.PortalCoachLookupProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CoachRepository extends JpaRepository<Coach, Long>, CustomCoachRepository {

    List<PortalCoachLookupProjection> findByStatusOrderByFullNameEnAsc(CoachStatus status);
}
