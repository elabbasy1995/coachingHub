package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.entity.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse implements Serializable {

    private String accessToken;
    private String refreshToken;

}
