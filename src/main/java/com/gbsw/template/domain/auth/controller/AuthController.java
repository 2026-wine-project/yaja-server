package com.gbsw.template.domain.auth.controller;

import com.gbsw.template.domain.auth.dto.LoginRequest;
import com.gbsw.template.domain.auth.dto.LoginResponse;
import com.gbsw.template.domain.auth.dto.RefreshRequest;
import com.gbsw.template.domain.auth.dto.SignUpRequest;
import com.gbsw.template.domain.auth.dto.TokenResponse;
import com.gbsw.template.domain.auth.service.AuthService;
import com.gbsw.template.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원 가입 (개발/시드용)")
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ApiResponse.success();
    }

    @Operation(summary = "로그인", description = "accessToken + user 객체 반환")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.success(authService.refresh(request.getRefreshToken()));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success();
    }
}
