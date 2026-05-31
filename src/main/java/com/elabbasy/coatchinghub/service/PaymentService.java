package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.model.enums.PaymentProvider;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import com.elabbasy.coatchinghub.model.request.ApiRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.PortalPaymentTransactionResponse;
import com.elabbasy.coatchinghub.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public ApiResponse<List<PortalPaymentTransactionResponse>> findTransactionsForPortal(
            String search,
            Long bookingId,
            PaymentProvider provider,
            PaymentStatus status,
            String transaction,
            LocalDate startDate,
            LocalDate endDate,
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

        Pageable pageable = apiRequest.buildPagination();
        LocalDateTime startDateTime = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate == null ? null : endDate.plusDays(1).atStartOfDay();
        String normalizedSearch = search == null || search.trim().isEmpty() ? null : search.trim().toLowerCase();
        String normalizedTransaction = transaction == null || transaction.trim().isEmpty() ? null : transaction.trim();

        Page<PortalPaymentTransactionResponse> page = paymentRepository.findTransactionsForPortal(
                normalizedSearch,
                bookingId,
                provider,
                status,
                normalizedTransaction,
                startDateTime,
                endDateTime,
                pageable
        );

        return new ApiResponse<>(page.getContent(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }
}
