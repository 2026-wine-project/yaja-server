package com.gbsw.template.domain.auth.service;

import com.gbsw.template.domain.auth.dto.LoginRequest;
import com.gbsw.template.domain.auth.dto.LoginResponse;
import com.gbsw.template.domain.auth.dto.SignUpRequest;
import com.gbsw.template.domain.auth.dto.TokenResponse;
import com.gbsw.template.domain.auth.dto.UserResponse;
import com.gbsw.template.domain.user.entity.UserEntity;
import com.gbsw.template.domain.user.repository.UserRepository;
import com.gbsw.template.global.exception.CustomException;
import com.gbsw.template.global.exception.ErrorCode;
import com.gbsw.template.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .admissionYear(request.getAdmissionYear())
                .grade(request.getGrade())
                .classNum(request.getClassNum())
                .studentNum(request.getStudentNum())
                .build();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(UserResponse.from(user))
                .build();
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken) || !jwtProvider.isRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtProvider.getUserIdFromToken(refreshToken);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return TokenResponse.builder()
                .accessToken(jwtProvider.generateAccessToken(user.getId(), user.getRole()))
                .refreshToken(jwtProvider.generateRefreshToken(user.getId(), user.getRole()))
                .build();
    }
}
