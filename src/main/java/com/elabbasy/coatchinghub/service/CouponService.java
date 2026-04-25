package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.model.entity.Coach;
import com.elabbasy.coatchinghub.model.entity.Coupon;
import com.elabbasy.coatchinghub.model.request.ApiRequest;
import com.elabbasy.coatchinghub.model.request.PortalCouponRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.PortalCouponResponse;
import com.elabbasy.coatchinghub.repository.BookingRepository;
import com.elabbasy.coatchinghub.repository.CoachRepository;
import com.elabbasy.coatchinghub.repository.CouponRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CoachRepository coachRepository;
    private final BookingRepository bookingRepository;

    public PortalCouponResponse createCoupon(PortalCouponRequest request) {
        validateCouponRequest(request);

        if (couponRepository.existsActiveByCode(request.getCode().trim())) {
            throw new BusinessException(ErrorMessage.INVALID_COUPON_CODE);
        }

        Coupon coupon = new Coupon();
        applyCouponChanges(coupon, request);

        return toResponse(couponRepository.save(coupon));
    }

    public ApiResponse<List<PortalCouponResponse>> findAllForAdmin(String search,
                                                                   Integer pageIndex,
                                                                   Integer pageSize,
                                                                   String sortBy,
                                                                   String sortDir) {
        ApiRequest<Void> apiRequest = ApiRequest.<Void>builder()
                .pageIndex(pageIndex == null ? 0 : pageIndex)
                .pageSize(pageSize == null ? 20 : pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();

        Specification<Coupon> specification = Specification
                .where(notSoftDeleted())
                .and(matchesSearch(search));

        Page<PortalCouponResponse> page = couponRepository.findAll(specification, apiRequest.buildPagination())
                .map(this::toResponse);

        return new ApiResponse<>(page.getContent(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    public PortalCouponResponse updateCoupon(Long couponId, PortalCouponRequest request) {
        validateCouponRequest(request);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.INVALID_COUPON_CODE));

        if (couponRepository.existsActiveByCodeAndIdNot(request.getCode().trim(), couponId)) {
            throw new BusinessException(ErrorMessage.INVALID_COUPON_CODE);
        }

        applyCouponChanges(coupon, request);
        return toResponse(couponRepository.save(coupon));
    }

    public ApiResponse<Void> softDeleteCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.INVALID_COUPON_CODE));

        coupon.setSoftDelete(true);
        couponRepository.save(coupon);

        return new ApiResponse<>((Void) null);
    }

    private void validateCouponRequest(PortalCouponRequest request) {
        if (request.getExpiryDate() != null && request.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorMessage.CODE_IS_EXPIRED);
        }

        if (!Boolean.TRUE.equals(request.getUnlimitedUsage())
                && (request.getTimesOfUse() == null || request.getTimesOfUse() <= 0)) {
            throw new BusinessException(ErrorMessage.MISSING_REQUIRED_ATTRIBUTES);
        }

        if (!Boolean.TRUE.equals(request.getAllCoaches())
                && (request.getCoachIds() == null || request.getCoachIds().isEmpty())) {
            throw new BusinessException(ErrorMessage.COACH_IS_REQUIRED);
        }
    }

    private void applyCouponChanges(Coupon coupon, PortalCouponRequest request) {
        coupon.setTitle(request.getTitle().trim());
        coupon.setUnlimitedUsage(request.getUnlimitedUsage());
        coupon.setTimesOfUse(Boolean.TRUE.equals(request.getUnlimitedUsage()) ? null : request.getTimesOfUse());
        coupon.setAllCoaches(request.getAllCoaches());
        coupon.setCode(request.getCode().trim());
        coupon.setDiscountPercentage(request.getDiscountPercentage());
        coupon.setExpiryDate(request.getExpiryDate());
        coupon.setSoftDelete(false);
        coupon.setCoaches(resolveCoaches(request));
    }

    private List<Coach> resolveCoaches(PortalCouponRequest request) {
        if (Boolean.TRUE.equals(request.getAllCoaches())) {
            return Collections.emptyList();
        }

        List<Coach> coaches = coachRepository.findAllById(request.getCoachIds());
        if (coaches.size() != request.getCoachIds().size()) {
            throw new BusinessException(ErrorMessage.COACH_NOT_FOUND);
        }
        return coaches;
    }

    private PortalCouponResponse toResponse(Coupon coupon) {
        Integer usageCount = bookingRepository.countByCouponId(coupon.getId());

        return PortalCouponResponse.builder()
                .id(coupon.getId())
                .createdDate(coupon.getCreatedDate())
                .updatedDate(coupon.getUpdatedDate())
                .title(coupon.getTitle())
                .timesOfUse(coupon.getTimesOfUse())
                .unlimitedUsage(coupon.getUnlimitedUsage())
                .allCoaches(coupon.getAllCoaches())
                .code(coupon.getCode())
                .discountPercentage(coupon.getDiscountPercentage())
                .expiryDate(coupon.getExpiryDate())
                .usageCount(usageCount == null ? 0L : usageCount.longValue())
                .softDelete(coupon.getSoftDelete())
                .coaches(Objects.isNull(coupon.getCoaches())
                        ? Collections.emptyList()
                        : coupon.getCoaches().stream()
                        .map(coach -> PortalCouponResponse.CoachSummary.builder()
                                .id(coach.getId())
                                .fullNameEn(coach.getFullNameEn())
                                .fullNameAr(coach.getFullNameAr())
                                .build())
                        .toList())
                .build();
    }

    private Specification<Coupon> notSoftDeleted() {
        return (root, query, cb) -> cb.or(
                cb.isNull(root.get("softDelete")),
                cb.isFalse(root.get("softDelete"))
        );
    }

    private Specification<Coupon> matchesSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return cb.conjunction();
            }

            String pattern = "%" + search.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("code")), pattern)
            );
        };
    }
}
