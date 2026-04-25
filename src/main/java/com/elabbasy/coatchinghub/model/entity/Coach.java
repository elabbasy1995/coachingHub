package com.elabbasy.coatchinghub.model.entity;

import com.elabbasy.coatchinghub.model.enums.CoachStatus;
import com.elabbasy.coatchinghub.model.enums.EnglishLevel;
import com.elabbasy.coatchinghub.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coaches")
@SQLRestriction("DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} coaches SET DELETED = true where id = ?")
public class Coach extends AuditBaseEntity {

    private String fullNameEn;
    private String fullNameAr;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthDate;
    private String email;
    private String whatsAppNumber;

    @ManyToOne
    @JoinColumn(name = "nationality_id")
    private Nationality nationality;

    private Integer yearsOfExperience;
    private Boolean availableEveryWeek;
    private String jobTitle;

    private String username;
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private CoachStatus status;

    private Double halfHourPrice;
    private Double hourlyPrice;
    private Double OneAndHalfHourPrice;
    private Double twoHoursPrice;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "coach_coaching_industries",
            joinColumns = @JoinColumn(name = "coach_id"),
            inverseJoinColumns = @JoinColumn(name = "coaching_industry_id")
    )
    private List<CoachingIndustry> coachingIndustries;

    @JsonIgnoreProperties({"admin", "coach", "coachee"})
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "coach_languages",
            joinColumns = @JoinColumn(name = "coach_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private List<Language> languages = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "coach")
    private List<Booking> bookings;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;


}
