package com.elabbasy.coatchinghub.model.entity;

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
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coachees")
@SQLRestriction("DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} coachees SET DELETED = true where id = ?")
public class Coachee extends AuditBaseEntity {

    private String fullName;
    private LocalDate birthDate;
    private String profileImageUrl;
    private String phoneNumber;
    @Column(nullable = false)
    private Boolean active = false;

    @JsonIgnoreProperties({"admin", "coach", "coachee"})
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "coachee")
    private List<Booking> bookings;

}
