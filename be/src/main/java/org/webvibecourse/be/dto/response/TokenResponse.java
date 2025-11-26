package org.webvibecourse.be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TokenResponse {
    private String refreshToken;
    private String accessToken;
    private long accessTokenExpiryAt;
    private long refreshTokenExpiryAt;
    private String role;
}
