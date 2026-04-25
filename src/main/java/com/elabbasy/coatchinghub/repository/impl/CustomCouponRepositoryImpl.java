package com.elabbasy.coatchinghub.repository.impl;

import com.elabbasy.coatchinghub.model.entity.Coupon;
import com.elabbasy.coatchinghub.model.response.CouponListProjection;
import com.elabbasy.coatchinghub.repository.CustomCouponRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;

import java.util.List;

public class CustomCouponRepositoryImpl implements CustomCouponRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<CouponListProjection> searchCoupons(Specification<Coupon> specs, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CouponListProjection> cq = cb.createQuery(CouponListProjection.class);

        Root<Coupon> root = cq.from(Coupon.class);

        // Apply specifications (filters)
        Predicate predicate = specs != null ? specs.toPredicate(root, cq, cb) : cb.conjunction();


        cq.select(cb.construct(
                CouponListProjection.class,
                root.get("id"),
                root.get("createdDate"),
                root.get("title"),
                root.get("timesOfUse"),
                root.get("unlimitedUsage"),
                root.get("code"),
                root.get("discountPercentage"),
                root.get("expiryDate"),
                root.get("softDelete")
        ));

        cq.where(predicate);
        cq.groupBy(root.get("id"));

        // Apply sorting
        if (pageable.getSort().isSorted()) {
            cq.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));
        }

        TypedQuery<CouponListProjection> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<CouponListProjection> results = query.getResultList();

        // -----------------------------------------------------
        // COUNT query
        // -----------------------------------------------------
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Coupon> countRoot = countQuery.from(Coupon.class);

        countQuery.select(cb.count(countRoot));
        countQuery.where(specs != null ? specs.toPredicate(countRoot, countQuery, cb) : cb.conjunction());

        Long total = em.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
}
