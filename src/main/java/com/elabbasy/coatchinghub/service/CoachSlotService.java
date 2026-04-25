package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.mapper.CoachSlotMapper;
import com.elabbasy.coatchinghub.model.dto.CoachSlotDto;
import com.elabbasy.coatchinghub.model.entity.Coach;
import com.elabbasy.coatchinghub.model.entity.CoachSlot;
import com.elabbasy.coatchinghub.model.enums.SlotStatus;
import com.elabbasy.coatchinghub.model.request.CreateCoachSlot;
import com.elabbasy.coatchinghub.model.response.CoachSlotResponse;
import com.elabbasy.coatchinghub.model.response.DaySlotsDto;
import com.elabbasy.coatchinghub.repository.CoachRepository;
import com.elabbasy.coatchinghub.repository.CoachSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CoachSlotService {

    private final CoachSlotMapper coachSlotMapper;
    private final CoachSlotRepository coachSlotRepository;
    private final CoachRepository coachRepository;

    public CoachSlotDto create(CreateCoachSlot createCoachSlot, Long coachId) {

        if (OffsetDateTime.now().isAfter(createCoachSlot.getStartTime())) {
            throw new BusinessException(ErrorMessage.CAN_NOT_RESERVE_SLOT_IN_THE_PAST);
        }

        OffsetDateTime startUtc = createCoachSlot.getStartTime().withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime endUtc = startUtc.plusMinutes(createCoachSlot.getSlotType().getDuration());

        boolean overlap = coachSlotRepository.existsOverlappingSlot(
                coachId, startUtc, endUtc
        );

        if (overlap) {
            throw new BusinessException(ErrorMessage.YOU_ALREADY_HAVE_ANOTHER_SLOT_AT_THE_SAME_TIME);
        }

        Coach coach = coachRepository.findById(coachId).orElseThrow(() -> new BusinessException(ErrorMessage.COACH_NOT_FOUND));
        CoachSlot slot = new CoachSlot();
        slot.setCoach(coach);
        slot.setStartTimeUtc(startUtc);
        slot.setPeriodMinutes(createCoachSlot.getSlotType().getDuration());
        slot.setSlotType(createCoachSlot.getSlotType());
        slot.setEndTimeUtc(endUtc);
        slot.setStatus(SlotStatus.AVAILABLE);

        CoachSlot save = coachSlotRepository.save(slot);
        return coachSlotMapper.toDto(save);
    }

    public void delete(Long id, Long coachId) {
        CoachSlot coachSlot = coachSlotRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorMessage.SLOT_NOT_FOUND));
        if (Objects.isNull(coachSlot.getCoach()) || !coachId.equals(coachSlot.getCoach().getId())) {
            throw new BusinessException(ErrorMessage.UNAUTHORIZE_TO_PERFORM_ACTION);
        }
        if (!SlotStatus.AVAILABLE.equals(coachSlot.getStatus())) {
            throw new BusinessException(ErrorMessage.CANNOT_REMOVE_THIS_SLOT_IT_IS_ALREAY_USED);
        }

        coachSlotRepository.delete(coachSlot);
    }

    public List<DaySlotsDto> getSlotsByDay(Long coachId) {
        List<CoachSlot> slots = coachSlotRepository.findSlotsByCoach(coachId);

        Map<LocalDate, List<CoachSlotResponse>> grouped = slots.stream()
                .map(coachSlotMapper::toCoachSlotResponse)
                .collect(Collectors.groupingBy(
                        dto -> dto.getStartTimeUtc().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Convert map to list of DaySlotsDto
        return grouped.entrySet().stream()
                .map(e -> {
                    DaySlotsDto dayDto = new DaySlotsDto();
                    dayDto.setDate(e.getKey());
                    dayDto.setSlots(e.getValue());
                    return dayDto;
                })
                .collect(Collectors.toList());
    }

    public List<DaySlotsDto> getSlotsByMonthAndYear(Long coachId, Integer month, Integer year) {
        // Month start in UTC
        OffsetDateTime startOfMonth = OffsetDateTime.of(
                year, month, 1,
                0, 0, 0, 0,
                ZoneOffset.UTC
        );

        // First moment of next month (exclusive)
        OffsetDateTime startOfNextMonth = startOfMonth.plusMonths(1);

        List<CoachSlot> slots =
                coachSlotRepository.findSlotsByCoachAndMonth(
                        coachId,
                        startOfMonth,
                        startOfNextMonth
                );

        Map<LocalDate, List<CoachSlotResponse>> grouped = slots.stream()
                .map(coachSlotMapper::toCoachSlotResponse)
                .collect(Collectors.groupingBy(
                        dto -> dto.getStartTimeUtc().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Convert map to list of DaySlotsDto
        return grouped.entrySet().stream()
                .map(e -> {
                    DaySlotsDto dayDto = new DaySlotsDto();
                    dayDto.setDate(e.getKey());
                    dayDto.setSlots(e.getValue());
                    return dayDto;
                })
                .collect(Collectors.toList());
    }

    public List<DaySlotsDto> getAvailableSlotsByMonthAndYear(Long coachId, Integer month, Integer year) {
        // Month start in UTC
        OffsetDateTime startOfMonth = OffsetDateTime.of(
                year, month, 1,
                0, 0, 0, 0,
                ZoneOffset.UTC
        );

        // First moment of next month (exclusive)
        OffsetDateTime startOfNextMonth = startOfMonth.plusMonths(1);

        if (startOfMonth.isBefore(OffsetDateTime.now())) {
            startOfMonth = OffsetDateTime.now();
        }

        List<CoachSlot> slots =
                coachSlotRepository.findAvailableSlotsByCoachAndMonth(
                        coachId,
                        startOfMonth,
                        startOfNextMonth
                );

        Map<LocalDate, List<CoachSlotResponse>> grouped = slots.stream()
                .map(coachSlotMapper::toCoachSlotResponse)
                .collect(Collectors.groupingBy(
                        dto -> dto.getStartTimeUtc().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Convert map to list of DaySlotsDto
        return grouped.entrySet().stream()
                .map(e -> {
                    DaySlotsDto dayDto = new DaySlotsDto();
                    dayDto.setDate(e.getKey());
                    dayDto.setSlots(e.getValue());
                    return dayDto;
                })
                .collect(Collectors.toList());
    }

    public List<DaySlotsDto> getAvailableSlotsByDay(Long coachId) {
        List<CoachSlot> slots = coachSlotRepository.findAvailableSlotsByCoach(coachId);

        Map<LocalDate, List<CoachSlotResponse>> grouped = slots.stream()
                .map(coachSlotMapper::toCoachSlotResponse)
                .collect(Collectors.groupingBy(
                        dto -> dto.getStartTimeUtc().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Convert map to list of DaySlotsDto
        return grouped.entrySet().stream()
                .map(e -> {
                    DaySlotsDto dayDto = new DaySlotsDto();
                    dayDto.setDate(e.getKey());
                    dayDto.setSlots(e.getValue());
                    return dayDto;
                })
                .collect(Collectors.toList());
    }

}
