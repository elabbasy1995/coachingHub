package com.elabbasy.coatchinghub.controller.mobile;

import com.elabbasy.coatchinghub.constant.Constants;
import com.elabbasy.coatchinghub.model.dto.BookingDto;
import com.elabbasy.coatchinghub.model.request.ApplyCouponRequest;
import com.elabbasy.coatchinghub.model.request.CreateBookingRequest;
import com.elabbasy.coatchinghub.model.request.PayBookingRequest;
import com.elabbasy.coatchinghub.model.request.RescheduleBookingRequest;
import com.elabbasy.coatchinghub.model.response.*;
import com.elabbasy.coatchinghub.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mobile/api/booking")
@RequiredArgsConstructor
public class MobileBookingController {

    private final BookingService bookingService;


    @PreAuthorize("hasRole('COACHEE')")
    @PostMapping("/reserve")
    public ApiResponse<BookingDto> reserve(@RequestBody @Valid CreateBookingRequest createBookingRequest,
                                           @RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId) {
        BookingDto booking = bookingService.createBooking(createBookingRequest, coacheeId);

        return new ApiResponse<>(booking);
    }

    @PreAuthorize("hasRole('COACHEE')")
    @PostMapping("/apply-coupon")
    public ApiResponse<BookingDto> reserve(@RequestBody @Valid ApplyCouponRequest applyCouponRequest,
                                           @RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId) {
        BookingDto booking = bookingService.applyCoupon(applyCouponRequest, coacheeId);

        return new ApiResponse<>(booking);
    }

    @PreAuthorize("hasRole('COACHEE')")
    @PostMapping("/delete-coupon/{id}")
    public ApiResponse<BookingDto> deleteBooking(@PathVariable Long id) {
        BookingDto bookingDto = bookingService.deleteCoupon(id);

        return new ApiResponse<>(bookingDto);
    }

    @PreAuthorize("hasRole('COACHEE')")
    @PostMapping("/pay")
    public ApiResponse<BookingDto> reserve(@RequestBody @Valid PayBookingRequest payBookingRequest,
                                           @RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId) {
        BookingDto booking = bookingService.payBooking(payBookingRequest, coacheeId);

        return new ApiResponse<>(booking);
    }

    @PreAuthorize("hasRole('COACHEE')")
    @PostMapping("/reschedule")
    public ApiResponse<BookingDto> reschedule(@RequestBody @Valid RescheduleBookingRequest request,
                                              @RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId) {
        BookingDto booking = bookingService.rescheduleBooking(request, coacheeId);

        return new ApiResponse<>(booking);
    }

    @PreAuthorize("hasRole('COACH')")
    @Operation(summary = "get all coachee bookings with the coach")
    @GetMapping("/all-coachee-booking-with-coach/{coacheeId}")
    public ApiResponse<List<CoacheeCoachBookingProjection>> getAllCoacheeBooking(@PathVariable Long coacheeId,
                                                                                 @RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId,
                                                                                 @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                                                                 @RequestParam(required = false, defaultValue = Integer.MAX_VALUE+"") Integer pageSize) {

        return bookingService.findAllCoacheeBooking(coachId, coacheeId, pageIndex, pageSize);
    }

    @PreAuthorize("hasRole('COACH')")
    @Operation(summary = "get upcoming coachee bookings with the coach")
    @GetMapping("/upcoming-coachee-booking-with-coach/{coacheeId}")
    public ApiResponse<List<CoacheeCoachBookingProjection>> getUpcomingCoacheeBooking(@PathVariable Long coacheeId,
                                                                                      @RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId,
                                                                                      @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                                                                      @RequestParam(required = false, defaultValue = Integer.MAX_VALUE+"") Integer pageSize) {

        return bookingService.findUpcomingCoacheeBooking(coachId, coacheeId, pageIndex, pageSize);
    }

    @PreAuthorize("hasRole('COACH')")
    @Operation(summary = "get past coachee bookings with the coach")
    @GetMapping("/past-coachee-booking-with-coach/{coacheeId}")
    public ApiResponse<List<CoacheeCoachBookingProjection>> getPastCoacheeBooking(@PathVariable Long coacheeId,
                                                                                  @RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId,
                                                                                  @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                                                                  @RequestParam(required = false, defaultValue = Integer.MAX_VALUE+"") Integer pageSize) {

        return bookingService.findPastCoacheeBooking(coachId, coacheeId, pageIndex, pageSize);
    }

    @PreAuthorize("hasRole('COACH')")
    @Operation(summary = "get upcoming bookings with the coach")
    @GetMapping("/upcoming-booking-with-coach")
    public ApiResponse<List<CoachBookingResponse>> getUpcomingBookingForCoach(@RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId,
                                                                              @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                                                              @RequestParam(required = false, defaultValue = Integer.MAX_VALUE+"") Integer pageSize) {

        return bookingService.findUpcomingBookingForCoach(coachId, pageIndex, pageSize);
    }

    @PreAuthorize("hasRole('COACH')")
    @Operation(summary = "get past bookings with the coach")
    @GetMapping("/past-booking-with-coach")
    public ApiResponse<List<CoachBookingResponse>> getPastBookingForCoach(@RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId,
                                                                                   @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                                                                   @RequestParam(required = false, defaultValue = Integer.MAX_VALUE+"") Integer pageSize) {

        return bookingService.findPastBookingForCoach(coachId, pageIndex, pageSize);
    }

    @PreAuthorize("hasRole('COACHEE')")
    @Operation(summary = "get upcoming bookings with the coachee")
    @GetMapping("/upcoming-booking-with-coachee")
    public ApiResponse<List<CoacheeBookingResponse>> getUpcomingBookingForCoachee(@RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId,
                                                                                @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                                                                @RequestParam(required = false, defaultValue = Integer.MAX_VALUE+"") Integer pageSize) {

        return bookingService.findUpcomingBookingForCoachee(coacheeId, pageIndex, pageSize);
    }

    @PreAuthorize("hasRole('COACHEE')")
    @Operation(summary = "get past bookings with the coachee")
    @GetMapping("/past-booking-with-coachee")
    public ApiResponse<List<CoacheeBookingResponse>> getPastBookingForCoachee(@RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId,
                                                                                @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                                                                @RequestParam(required = false, defaultValue = Integer.MAX_VALUE+"") Integer pageSize) {

        return bookingService.findPastBookingForCoachee(coacheeId, pageIndex, pageSize);
    }

}
