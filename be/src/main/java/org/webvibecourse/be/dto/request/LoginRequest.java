package org.webvibecourse.be.dto.request;

import lombok.Data;

/**
 * =====================================================================================
 * LoginRequest
 * -------------------------
 * Request DTO for loginApi
 * Fields
 * ✔ email:email used as username for login
 * ✔ password:password for user login
 * Usage:
 * This object is passed api credentials
 */
@Data
public class LoginRequest {
    private String email;
    private String password;
}
