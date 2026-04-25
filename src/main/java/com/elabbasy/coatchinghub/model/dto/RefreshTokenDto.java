package com.elabbasy.coatchinghub.model.dto;

import com.elabbasy.coatchinghub.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDto {

    private Long id;
    private String token;
    private User user;
    private Instant expiryDate;
}
