package com.janvo.salon.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.janvo.salon.dtos.AdminLoginRequest;
import com.janvo.salon.dtos.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

	static final String ADMIN_SESSION_ATTRIBUTE = "janviAdminAuthenticated";

	@Value("${admin.username:}")
	private String adminUsername;

	@Value("${admin.password:}")
	private String adminPassword;

	@PostMapping("/login")
	public ResponseEntity<?> login(
			@RequestBody AdminLoginRequest request,
			HttpServletRequest servletRequest) {
		if (isBlank(adminUsername) || isBlank(adminPassword)) {
			return error(HttpStatus.SERVICE_UNAVAILABLE, "Admin sign-in is not configured on the server");
		}
		if (!matches(adminUsername, request.username()) || !matches(adminPassword, request.password())) {
			return error(HttpStatus.UNAUTHORIZED, "Username or password is invalid");
		}

		HttpSession currentSession = servletRequest.getSession(false);
		if (currentSession != null) {
			currentSession.invalidate();
		}
		servletRequest.getSession(true).setAttribute(ADMIN_SESSION_ATTRIBUTE, true);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return ResponseEntity.noContent().build();
	}

	private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(ApiErrorResponse.builder()
				.status(status.value())
				.message(message)
				.timestamp(LocalDateTime.now())
				.build());
	}

	private boolean matches(String expected, String provided) {
		return provided != null
				&& MessageDigest.isEqual(
						expected.getBytes(StandardCharsets.UTF_8),
						provided.getBytes(StandardCharsets.UTF_8));
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
