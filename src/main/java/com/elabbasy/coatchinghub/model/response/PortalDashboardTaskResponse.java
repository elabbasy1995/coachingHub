package com.elabbasy.coatchinghub.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PortalDashboardTaskResponse {

    private Long id;
    private String taskTitle;
    private LocalDate dueDate;
    private String status;
    private String coachFullNameEn;
    private String coachFullNameAr;
    private String coacheeFullName;
    private String coacheePhoneNumber;

    public PortalDashboardTaskResponse(Long id,
                                       String taskTitle,
                                       LocalDate dueDate,
                                       Boolean completed,
                                       String coachFullNameEn,
                                       String coachFullNameAr,
                                       String coacheeFullName,
                                       String coacheePhoneNumber) {
        this.id = id;
        this.taskTitle = taskTitle;
        this.dueDate = dueDate;
        this.status = Boolean.TRUE.equals(completed) ? "COMPLETED" : "NOT_COMPLETED";
        this.coachFullNameEn = coachFullNameEn;
        this.coachFullNameAr = coachFullNameAr;
        this.coacheeFullName = coacheeFullName;
        this.coacheePhoneNumber = coacheePhoneNumber;
    }
}
