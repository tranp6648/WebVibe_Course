package org.webvibecourse.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.webvibecourse.be.Enum.Role;
import org.webvibecourse.be.config.JwtService;
import org.webvibecourse.be.dto.request.LoginRequest;
import org.webvibecourse.be.dto.response.ApiResponse;
import org.webvibecourse.be.dto.response.TokenResponse;
import org.webvibecourse.be.entity.User;
import org.webvibecourse.be.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request){
        try {
            // 1. Xác thực email + password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            if (!authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid email or password"));
            }

            // 2. Lấy User từ Authentication
            User account = (User) authentication.getPrincipal();

            // 3. Lấy role dạng String (ví dụ: "STUDENT", "TEACHER", "ADMIN")
            String role = Role.fromCode(account.getRole()).name();
            // nếu bạn chưa có getRoleEnum() thì mình nhắc lại ở dưới

            // 4. Generate access token & refresh token
            String accessToken = jwtService.generateAccessToken(
                    account.getEmail(),
                    account.getId(),
                    role);

            String refreshToken = jwtService.generateRefreshToken(
                    account.getEmail(),
                    account.getId(),
                    role);

            long accessTokenExpiryAt = jwtService.getAccessExpiration();
            long refreshTokenExpiryAt = jwtService.getRefreshExpiration();

            // 5. Build response trả về FE
            TokenResponse tokenResponse = TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .accessTokenExpiryAt(accessTokenExpiryAt)
                    .refreshTokenExpiryAt(refreshTokenExpiryAt)
                    .role(role)
                    .build();

            return ResponseEntity.ok(ApiResponse.success(tokenResponse));

        } catch (Exception e) {
            // Có thể log thêm nếu cần
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid email or password"));
        }

    }
}
