package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.mapper.NotificationMapper;
import com.elabbasy.coatchinghub.model.entity.Coach;
import com.elabbasy.coatchinghub.model.entity.Coachee;
import com.elabbasy.coatchinghub.model.entity.Notification;
import com.elabbasy.coatchinghub.model.enums.NotificationType;
import com.elabbasy.coatchinghub.model.request.NotificationCreateRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.NotificationResponse;
import com.elabbasy.coatchinghub.repository.CoachRepository;
import com.elabbasy.coatchinghub.repository.CoacheeRepository;
import com.elabbasy.coatchinghub.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;
    private final CoachRepository coachRepository;
    private final CoacheeRepository coacheeRepository;

    @Transactional
    public void createNotification(NotificationCreateRequest request) {

        Notification notification = new Notification();

        if (request.getCoachId() != null) {
            Coach coach = coachRepository.findById(request.getCoachId())
                    .orElseThrow(() -> new BusinessException(ErrorMessage.COACH_NOT_FOUND));
            notification.setCoach(coach);
        }

        if (request.getCoacheeId() != null) {
            Coachee coachee = coacheeRepository.findById(request.getCoacheeId())
                    .orElseThrow(() -> new BusinessException(ErrorMessage.COACHEE_NOT_FOUND));
            notification.setCoachee(coachee);
        }

        notification.setTitle(request.getTitle());
        notification.setBody(request.getMessage());
        notification.setNotificationType(request.getNotificationType());
        notification.setReferenceId(request.getReferenceId());

        notification.setRead(false);
        notification.setCreatedDate(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyCoach(Long coachId,
                            String title,
                            String message,
                            NotificationType type,
                            Long referenceId) {

        createNotification(
                NotificationCreateRequest.builder()
                        .coachId(coachId)
                        .title(title)
                        .message(message)
                        .notificationType(type)
                        .referenceId(referenceId)
                        .build()
        );
    }

    @Transactional
    public void notifyCoachee(Long coacheeId,
                              String title,
                              String message,
                              NotificationType type,
                              Long referenceId) {

        createNotification(
                NotificationCreateRequest.builder()
                        .coacheeId(coacheeId)
                        .title(title)
                        .message(message)
                        .notificationType(type)
                        .referenceId(referenceId)
                        .build()
        );
    }

    @Transactional
    public void notifyBoth(Long coachId,
                           Long coacheeId,
                           String title,
                           String message,
                           NotificationType type,
                           Long referenceId) {

        createNotification(
                NotificationCreateRequest.builder()
                        .coachId(coachId)
                        .title(title)
                        .message(message)
                        .notificationType(type)
                        .referenceId(referenceId)
                        .build()
        );
        createNotification(
                NotificationCreateRequest.builder()
                        .coacheeId(coacheeId)
                        .title(title)
                        .message(message)
                        .notificationType(type)
                        .referenceId(referenceId)
                        .build()
        );
    }

    public ApiResponse<List<NotificationResponse>> getCoachNotifications(Long coachId, Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        Page<Notification> page = notificationRepository
                .findByCoachIdOrderByCreatedDateDesc(coachId, pageable);

        if (Objects.nonNull(page) && Objects.nonNull(page.getContent()) && !page.getContent().isEmpty()) {
            return new ApiResponse<>(mapper.toNotificationResponseList(page.getContent()), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
        } else
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());

    }

    public ApiResponse<List<NotificationResponse>> getCoacheeNotifications(Long coacheeId, Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        Page<Notification> page = notificationRepository
                .findByCoacheeIdOrderByCreatedDateDesc(coacheeId, pageable);

        if (Objects.nonNull(page) && Objects.nonNull(page.getContent()) && !page.getContent().isEmpty()) {
            return new ApiResponse<>(mapper.toNotificationResponseList(page.getContent()), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
        } else
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());

    }

    @Transactional
    public void markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.NOTIFICATION_NOT_FOUND));

        if (!notification.getRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        }
    }

    @Transactional
    public void markAllCoachAsRead(Long coachId) {

        List<Notification> notifications =
                notificationRepository.findByCoachIdAndReadFalse(coachId);

        notifications.forEach(n -> {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
        });
    }

    @Transactional
    public void markAllCoacheeAsRead(Long coacheeId) {

        List<Notification> notifications =
                notificationRepository.findByCoacheeIdAndReadFalse(coacheeId);

        notifications.forEach(n -> {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
        });
    }
}