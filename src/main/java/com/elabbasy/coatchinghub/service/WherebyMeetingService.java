package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
public class WherebyMeetingService {

    private final RestClient restClient;
    private final String apiKey;

    public WherebyMeetingService(
            RestClient.Builder restClientBuilder,
            @Value("${app.whereby.base-url:https://api.whereby.dev/v1}") String baseUrl,
            @Value("${app.whereby.api-key:}") String apiKey
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
        this.apiKey = apiKey;
    }

    public WherebyMeetingResult createMeeting(Long bookingId, OffsetDateTime endDate) {
        validateApiKey();

        if (endDate == null) {
            throw new BusinessException(ErrorMessage.WHEREBY_MEETING_OPERATION_FAILED);
        }

        try {
            WherebyMeetingResponse response = restClient.post()
                    .uri("/meetings")
                    .header(HttpHeaders.AUTHORIZATION, buildAuthorizationHeader())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateWherebyMeetingRequest(
                            endDate,
                            buildRoomNamePrefix(bookingId),
                            List.of("hostRoomUrl")
                    ))
                    .retrieve()
                    .body(WherebyMeetingResponse.class);

            if (response == null || !StringUtils.hasText(response.meetingId()) || !StringUtils.hasText(response.roomUrl())) {
                throw new BusinessException(ErrorMessage.WHEREBY_MEETING_OPERATION_FAILED);
            }

            return new WherebyMeetingResult(response.meetingId(), response.roomUrl(), response.hostRoomUrl());
        } catch (BusinessException ex) {
            throw ex;
        } catch (RestClientResponseException ex) {
            log.error("Whereby create meeting failed with status {} and body {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            throw new BusinessException(ErrorMessage.WHEREBY_MEETING_OPERATION_FAILED);
        } catch (RestClientException ex) {
            log.error("Whereby create meeting request failed", ex);
            throw new BusinessException(ErrorMessage.WHEREBY_MEETING_OPERATION_FAILED);
        }
    }

    public void deleteMeeting(String meetingId) {
        validateApiKey();

        if (!StringUtils.hasText(meetingId)) {
            return;
        }

        try {
            restClient.delete()
                    .uri("/meetings/{meetingId}", meetingId)
                    .header(HttpHeaders.AUTHORIZATION, buildAuthorizationHeader())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            log.error("Whereby delete meeting failed with status {} and body {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            throw new BusinessException(ErrorMessage.WHEREBY_MEETING_OPERATION_FAILED);
        } catch (RestClientException ex) {
            log.error("Whereby delete meeting request failed", ex);
            throw new BusinessException(ErrorMessage.WHEREBY_MEETING_OPERATION_FAILED);
        }
    }

    private void validateApiKey() {
        if (!StringUtils.hasText(apiKey)) {
            throw new BusinessException(ErrorMessage.WHEREBY_API_KEY_REQUIRED);
        }
    }

    private String buildAuthorizationHeader() {
        return "Bearer " + apiKey;
    }

    private String buildRoomNamePrefix(Long bookingId) {
        return bookingId == null ? "booking" : "booking-" + bookingId;
    }

    private record CreateWherebyMeetingRequest(
            OffsetDateTime endDate,
            String roomNamePrefix,
            List<String> fields
    ) {
    }

    private record WherebyMeetingResponse(
            String meetingId,
            String roomUrl,
            String hostRoomUrl
    ) {
    }

    public record WherebyMeetingResult(
            String meetingId,
            String roomUrl,
            String hostRoomUrl
    ) {
    }
}
