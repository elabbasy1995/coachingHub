package com.elabbasy.coatchinghub.repository.impl;

import com.elabbasy.coatchinghub.model.entity.Booking;
import com.elabbasy.coatchinghub.model.entity.Coach;
import com.elabbasy.coatchinghub.model.entity.CoachingIndustry;
import com.elabbasy.coatchinghub.model.entity.Language;
import com.elabbasy.coatchinghub.model.entity.User;
import com.elabbasy.coatchinghub.model.enums.CoachStatus;
import com.elabbasy.coatchinghub.model.request.CoachSearchCriteria;
import com.elabbasy.coatchinghub.model.response.CoachSearchDto;
import com.elabbasy.coatchinghub.model.response.PortalCoachListResponse;
import com.elabbasy.coatchinghub.repository.CustomCoachRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CustomCoachRepositoryImpl implements CustomCoachRepository {

    private final EntityManager em;

    @Override
    public Page<CoachSearchDto> search(CoachSearchCriteria criteria, Pageable pageable) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CoachSearchDto> cq = cb.createQuery(CoachSearchDto.class);

        Root<Coach> coach = cq.from(Coach.class);

        Join<Coach, Booking> booking =
                coach.join("bookings", JoinType.LEFT);

        Subquery<String> industriesSubquery = cq.subquery(String.class);
        Root<Coach> subCoach = industriesSubquery.from(Coach.class);
        Join<Coach, CoachingIndustry> subIndustry =
                subCoach.join("coachingIndustries", JoinType.LEFT);

        industriesSubquery.select(
                cb.function(
                        "coalesce",
                        String.class,
                        cb.function(
                                "jsonb_agg",
                                String.class,
                                cb.function(
                                        "jsonb_build_object",
                                        String.class,
                                        cb.literal("id"), subIndustry.get("id"),
                                        cb.literal("nameEn"), subIndustry.get("nameEn"),
                                        cb.literal("nameAr"), subIndustry.get("nameAr")
                                )
                        ),
                        cb.literal("[]")
                )
        );

        industriesSubquery.where(
                cb.and(
                        cb.equal(subCoach.get("id"), coach.get("id")),
                        cb.isNotNull(subIndustry.get("id"))
                )
        );



        // booking count
        Expression<Long> bookingCount =
                cb.countDistinct(booking.get("id"));

        cq.multiselect(
                coach.get("id"),
                coach.get("fullNameEn"),
                coach.get("fullNameAr"),
                coach.get("profileImageUrl"),
                coach.get("yearsOfExperience"),
                coach.get("nationality").get("id"),
                coach.get("nationality").get("nameEn"),
                coach.get("nationality").get("nameAr"),
                industriesSubquery,
                bookingCount
        );

        List<Predicate> predicates =
                buildPredicates(criteria, cb, coach, cq);

        cq.where(predicates.toArray(Predicate[]::new));


        cq.groupBy(
                coach.get("id"),
                coach.get("fullNameEn"),
                coach.get("fullNameAr"),
                coach.get("profileImageUrl"),
                coach.get("yearsOfExperience"),
                coach.get("nationality").get("id"),
                coach.get("nationality").get("nameEn"),
                coach.get("nationality").get("nameAr")
        );

        TypedQuery<CoachSearchDto> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<CoachSearchDto> content = query.getResultList();

        // -------- COUNT QUERY (IMPORTANT) --------
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Coach> countRoot = countQuery.from(Coach.class);

        List<Predicate> countPredicates =
                buildPredicates(criteria, cb, countRoot, countQuery);

        countRoot.join("coachingIndustries", JoinType.LEFT);
        countRoot.join("bookings", JoinType.LEFT);

        countQuery.select(cb.countDistinct(countRoot));
        countQuery.where(countPredicates.toArray(Predicate[]::new));

        Long total = em.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PortalCoachListResponse> searchAdminCoaches(String name, Pageable pageable) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PortalCoachListResponse> cq = cb.createQuery(PortalCoachListResponse.class);

        Root<Coach> coach = cq.from(Coach.class);
        Join<Coach, Booking> booking = coach.join("bookings", JoinType.LEFT);
        Join<Coach, User> user = coach.join("user", JoinType.LEFT);

        Subquery<String> industriesSubquery = buildIndustriesJsonSubquery(cq, cb, coach);
        Subquery<String> industriesSortSubquery = buildIndustriesSortSubquery(cq, cb, coach);
        Expression<Long> bookingCount = cb.countDistinct(booking.get("id"));

        cq.multiselect(
                coach.get("id"),
                coach.get("fullNameAr"),
                coach.get("fullNameEn"),
                coach.get("status"),
                coach.get("halfHourPrice"),
                coach.get("hourlyPrice"),
                coach.get("OneAndHalfHourPrice"),
                coach.get("twoHoursPrice"),
                user.get("enabled"),
                coach.get("nationality").get("id"),
                coach.get("nationality").get("nameEn"),
                coach.get("nationality").get("nameAr"),
                industriesSubquery,
                bookingCount
        );

        List<Predicate> predicates = buildAdminPredicates(name, cb, coach);
        cq.where(predicates.toArray(Predicate[]::new));

        cq.groupBy(
                coach.get("id"),
                coach.get("fullNameAr"),
                coach.get("fullNameEn"),
                coach.get("status"),
                coach.get("halfHourPrice"),
                coach.get("hourlyPrice"),
                coach.get("OneAndHalfHourPrice"),
                coach.get("twoHoursPrice"),
                user.get("enabled"),
                coach.get("nationality").get("id"),
                coach.get("nationality").get("nameEn"),
                coach.get("nationality").get("nameAr"),
                coach.get("createdDate")
        );

        cq.orderBy(buildAdminOrders(pageable, cb, coach, bookingCount, industriesSortSubquery));

        TypedQuery<PortalCoachListResponse> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<PortalCoachListResponse> content = query.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Coach> countRoot = countQuery.from(Coach.class);
        List<Predicate> countPredicates = buildAdminPredicates(name, cb, countRoot);
        countQuery.select(cb.countDistinct(countRoot.get("id")));
        countQuery.where(countPredicates.toArray(Predicate[]::new));

        Long total = em.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    private List<Order> buildAdminOrders(Pageable pageable,
                                         CriteriaBuilder cb,
                                         Root<Coach> coach,
                                         Expression<Long> bookingCount,
                                         Expression<String> industriesSortExpression) {
        List<Order> orders = new ArrayList<>();

        pageable.getSort().forEach(sortOrder -> {
            Expression<?> sortExpression = mapAdminSortExpression(
                    sortOrder.getProperty(),
                    cb,
                    coach,
                    bookingCount,
                    industriesSortExpression
            );
            orders.add(sortOrder.isAscending() ? cb.asc(sortExpression) : cb.desc(sortExpression));
        });

        if (orders.isEmpty()) {
            orders.add(cb.desc(coach.get("createdDate")));
        }

        return orders;
    }

    private Expression<?> mapAdminSortExpression(String sortBy,
                                                 CriteriaBuilder cb,
                                                 Root<Coach> coach,
                                                 Expression<Long> bookingCount,
                                                 Expression<String> industriesSortExpression) {
        return switch (sortBy) {
            case "id" -> coach.get("id");
            case "fullNameAr" -> cb.lower(coach.get("fullNameAr"));
            case "fullNameEn" -> cb.lower(coach.get("fullNameEn"));
            case "status" -> coach.get("status");
            case "halfHourPrice" -> coach.get("halfHourPrice");
            case "hourlyPrice" -> coach.get("hourlyPrice");
            case "OneAndHalfHourPrice", "oneAndHalfHourPrice" -> coach.get("OneAndHalfHourPrice");
            case "twoHoursPrice" -> coach.get("twoHoursPrice");
            case "enabled" -> coach.get("user").get("enabled");
            case "coachingIndustries" -> industriesSortExpression;
            case "bookingCount" -> bookingCount;
            default -> coach.get("createdDate");
        };
    }

    private List<Predicate> buildAdminPredicates(String name,
                                                 CriteriaBuilder cb,
                                                 Root<Coach> coach) {
        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            String like = "%" + name.trim().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(coach.get("fullNameEn")), like),
                    cb.like(cb.lower(coach.get("fullNameAr")), like),
                    cb.like(cb.lower(coach.get("email")), like)
            ));
        }

        return predicates;
    }

    private Subquery<String> buildIndustriesJsonSubquery(CriteriaQuery<?> query,
                                                         CriteriaBuilder cb,
                                                         Root<Coach> coach) {
        Subquery<String> industriesSubquery = query.subquery(String.class);
        Root<Coach> subCoach = industriesSubquery.from(Coach.class);
        Join<Coach, CoachingIndustry> subIndustry = subCoach.join("coachingIndustries", JoinType.LEFT);

        industriesSubquery.select(
                cb.function(
                        "coalesce",
                        String.class,
                        cb.function(
                                "jsonb_agg",
                                String.class,
                                cb.function(
                                        "jsonb_build_object",
                                        String.class,
                                        cb.literal("id"), subIndustry.get("id"),
                                        cb.literal("nameEn"), subIndustry.get("nameEn"),
                                        cb.literal("nameAr"), subIndustry.get("nameAr")
                                )
                        ),
                        cb.literal("[]")
                )
        );

        industriesSubquery.where(
                cb.and(
                        cb.equal(subCoach.get("id"), coach.get("id")),
                        cb.isNotNull(subIndustry.get("id"))
                )
        );

        return industriesSubquery;
    }

    private Subquery<String> buildIndustriesSortSubquery(CriteriaQuery<?> query,
                                                         CriteriaBuilder cb,
                                                         Root<Coach> coach) {
        Subquery<String> industriesSortSubquery = query.subquery(String.class);
        Root<Coach> subCoach = industriesSortSubquery.from(Coach.class);
        Join<Coach, CoachingIndustry> subIndustry = subCoach.join("coachingIndustries", JoinType.LEFT);

        industriesSortSubquery.select(
                cb.function(
                        "coalesce",
                        String.class,
                        cb.function("min", String.class, cb.lower(subIndustry.get("nameEn"))),
                        cb.literal("")
                )
        );

        industriesSortSubquery.where(
                cb.and(
                        cb.equal(subCoach.get("id"), coach.get("id")),
                        cb.isNotNull(subIndustry.get("id"))
                )
        );

        return industriesSortSubquery;
    }
    private List<Predicate> buildPredicates(
            CoachSearchCriteria criteria,
            CriteriaBuilder cb,
            Root<Coach> coach,
            CriteriaQuery<?> query
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getName() != null && !criteria.getName().isBlank()) {
            String like = "%" + criteria.getName().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(coach.get("fullNameEn")), like),
                    cb.like(cb.lower(coach.get("fullNameAr")), like)
            ));
        }

        if (criteria.getGender() != null) {
            predicates.add(cb.equal(coach.get("gender"), criteria.getGender()));
        }

        if (criteria.getMinYearsOfExperience() != null) {
            predicates.add(cb.greaterThanOrEqualTo(
                    coach.get("yearsOfExperience"),
                    criteria.getMinYearsOfExperience()
            ));
        }

        if (criteria.getLanguageIds() != null && !criteria.getLanguageIds().isEmpty()) {

            Subquery<Long> languageExists = query.subquery(Long.class);
            Root<Coach> subCoach = languageExists.from(Coach.class);
            Join<Coach, Language> subLanguage = subCoach.join("languages");

            languageExists.select(cb.literal(1L));
            languageExists.where(
                    cb.and(
                            cb.equal(subCoach.get("id"), coach.get("id")),
                            subLanguage.get("id").in(criteria.getLanguageIds())
                    )
            );

            predicates.add(cb.exists(languageExists));
        }

        if (criteria.getCoachingIndustryIds() != null && !criteria.getCoachingIndustryIds().isEmpty()) {

            Subquery<Long> industryExists = query.subquery(Long.class);
            Root<Coach> subCoach = industryExists.from(Coach.class);
            Join<Coach, CoachingIndustry> subIndustry = subCoach.join("coachingIndustries");

            industryExists.select(cb.literal(1L));
            industryExists.where(
                    cb.and(
                            cb.equal(subCoach.get("id"), coach.get("id")),
                            subIndustry.get("id").in(criteria.getCoachingIndustryIds())
                    )
            );

            predicates.add(cb.exists(industryExists));
        }

        predicates.add(cb.equal(coach.get("status"), CoachStatus.APPROVED));

        return predicates;
    }

}



