package com.elabbasy.coatchinghub.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_otp")
@Where(clause = "DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} email_otp SET DELETED = true where id = ?")
public class EmailOtp extends BaseEntity {

    @Column(nullable = false)
    private String email;

    @Column(nullable = true)
    private String newEmail;

    @Column(nullable = true)
    private Long userId;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

