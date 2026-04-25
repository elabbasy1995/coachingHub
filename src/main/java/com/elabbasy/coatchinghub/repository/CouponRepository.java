package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long>, JpaSpecificationExecutor<Coupon>, CustomCouponRepository {

    Optional<Coupon> findByCode(String code);

    @Query("""
            select coupon
            from Coupon coupon
            where lower(coupon.code) = lower(:code)
              and (coupon.softDelete is null or coupon.softDelete = false)
            """)
    Optional<Coupon> findActiveByCode(@Param("code") String code);

    @Query("""
            select (count(coupon) > 0)
            from Coupon coupon
            where lower(coupon.code) = lower(:code)
              and (coupon.softDelete is null or coupon.softDelete = false)
            """)
    boolean existsActiveByCode(@Param("code") String code);

    @Query("""
            select (count(coupon) > 0)
            from Coupon coupon
            where lower(coupon.code) = lower(:code)
              and coupon.id <> :id
              and (coupon.softDelete is null or coupon.softDelete = false)
            """)
    boolean existsActiveByCodeAndIdNot(@Param("code") String code, @Param("id") Long id);

    @Query("select coupon from Coupon coupon where coupon.id = :id and (coupon.softDelete is null or coupon.softDelete = false)")
    Optional<Coupon> findById(Long id);
}
