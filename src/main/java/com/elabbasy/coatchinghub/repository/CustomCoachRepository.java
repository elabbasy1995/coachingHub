package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.request.CoachSearchCriteria;
import com.elabbasy.coatchinghub.model.response.CoachSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.elabbasy.coatchinghub.model.response.PortalCoachListResponse;


public interface CustomCoachRepository {

    Page<CoachSearchDto> search(CoachSearchCriteria criteria, Pageable pageable);

    Page<PortalCoachListResponse> searchAdminCoaches(String name, Pageable pageable);
}

