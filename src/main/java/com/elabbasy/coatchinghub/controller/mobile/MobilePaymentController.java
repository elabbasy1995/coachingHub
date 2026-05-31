package com.elabbasy.coatchinghub.controller.mobile;

import com.elabbasy.coatchinghub.constant.Constants;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.StripePaymentIntentResponse;
import com.elabbasy.coatchinghub.service.StripePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mobile/api/payments")
@RequiredArgsConstructor
public class MobilePaymentController {

    private final StripePaymentService stripePaymentService;

    @PreAuthorize("hasRole('COACHEE')")
    @PostMapping("/bookings/{bookingId}/stripe-payment-intent")
    public ApiResponse<StripePaymentIntentResponse> createStripePaymentIntent(
            @PathVariable Long bookingId,
            @RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId) {
        return new ApiResponse<>(stripePaymentService.createPaymentIntent(bookingId, coacheeId));
    }
}
