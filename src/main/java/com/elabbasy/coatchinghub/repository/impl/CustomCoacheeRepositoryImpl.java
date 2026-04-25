package com.elabbasy.coatchinghub.repository.impl;

import com.elabbasy.coatchinghub.model.entity.Booking;
import com.elabbasy.coatchinghub.model.entity.Coachee;
import com.elabbasy.coatchinghub.model.response.CoachCoacheeResponse;
import com.elabbasy.coatchinghub.repository.CustomCoacheeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class CustomCoacheeRepositoryImpl implements CustomCoacheeRepository {

    private final EntityManager em;

    @Override
    public Page<CoachCoacheeResponse> findCoacheesForCoach(Long coachId, Pageable pageable, String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CoachCoacheeResponse> cq = cb.createQuery(CoachCoacheeResponse.class);

        Root<Booking> booking = cq.from(Booking.class);
        Join<Booking, Coachee> coachee = booking.join("coachee");


        cq.multiselect(
                coachee.get("id"),
                coachee.get("fullName"),
                coachee.get("birthDate"),
                coachee.get("profileImageUrl"),
                cb.countDistinct(booking.get("id")),
                cb.greatest(booking.<LocalDateTime>get("startTime"))
        );

        List<Predicate> predicates = new ArrayList<>();

        // always filter by coach
        predicates.add(cb.equal(booking.get("coach").get("id"), coachId));

        // optional name filter
        if (name != null && !name.trim().isEmpty()) {
            predicates.add(
                    cb.like(
                            cb.lower(coachee.get("fullName")),
                            "%" + name.toLowerCase() + "%"
                    )
            );
        }

        cq.where(predicates.toArray(new Predicate[0]));

        cq.groupBy(
                coachee.get("id"),
                coachee.get("fullName"),
                coachee.get("birthDate"),
                coachee.get("profileImageUrl")
        );

        cq.orderBy(
                cb.desc(coachee.get("createdDate"))
        );

        TypedQuery<CoachCoacheeResponse> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<CoachCoacheeResponse> content = query.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Booking> countBooking = countQuery.from(Booking.class);
        Join<Booking, Coachee> countCoachee = countBooking.join("coachee");

        countQuery.select(cb.countDistinct(countCoachee.get("id")));
        countQuery.where(
                cb.equal(countBooking.get("coach").get("id"), coachId)
        );

        Long total = em.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(content, pageable, total);

    }
}
