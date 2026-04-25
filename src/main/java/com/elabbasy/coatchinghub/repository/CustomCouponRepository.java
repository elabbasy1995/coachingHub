package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Coupon;
import com.elabbasy.coatchinghub.model.response.CouponListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CustomCouponRepository {

    Page<CouponListProjection> searchCoupons(Specification<Coupon> specs, Pageable pageable);

}
