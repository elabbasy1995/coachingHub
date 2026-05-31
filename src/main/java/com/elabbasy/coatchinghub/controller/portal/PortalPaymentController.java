package com.elabbasy.coatchinghub.controller.portal;

import com.elabbasy.coatchinghub.constant.PortalPermissionExpressions;
import com.elabbasy.coatchinghub.model.enums.PaymentProvider;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.PortalPaymentTransactionResponse;
import com.elabbasy.coatchinghub.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/portal/api/payments")
@RequiredArgsConstructor
public class PortalPaymentController {

    private final PaymentService paymentService;

    @GetMapping("/transactions")
    @PreAuthorize(PortalPermissionExpressions.BOOKING)
    public ApiResponse<List<PortalPaymentTransactionResponse>> findTransactions(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) PaymentProvider provider,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) String transaction,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir) {
        return paymentService.findTransactionsForPortal(search, bookingId, provider, status, transaction,
                startDate, endDate, pageIndex, pageSize, sortBy, sortDir);
    }
}
