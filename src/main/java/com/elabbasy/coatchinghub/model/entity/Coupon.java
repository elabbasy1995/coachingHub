package com.elabbasy.coatchinghub.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coupon")
@Where(clause = "DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} coupon SET DELETED = true where id = ?")
public class Coupon extends AuditBaseEntity {

    @Column(name = "title")
    private String title;
    @Column(name = "times_of_use")
    private Integer timesOfUse;
    @Column(name = "unlimited_usage")
    private Boolean unlimitedUsage;
    @Column(name = "all_coaches")
    private Boolean allCoaches;
    @Column(name = "code", nullable = false)
    private String code;
    @Column(name = "discount_percentage", nullable = false)
    private double discountPercentage;
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "soft_delete")
    private Boolean softDelete = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "coupon_coach",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "coach_id")
    )
    private List<Coach> coaches;
}
