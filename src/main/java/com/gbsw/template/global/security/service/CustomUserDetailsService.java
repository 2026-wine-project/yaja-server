package com.gbsw.template.global.security.service;

import com.gbsw.template.global.exception.CustomException;
import com.gbsw.template.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // TODO: UserRepository 주입 후 아래 구현체 교체
    // private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: 예시 구현 - UserRepository 도입 후 교체
        // UserEntity user = userRepository.findByUsername(username)
        //         .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // return new CustomUserPrincipal(user.getId(), user.getUsername(), user.getPassword());

        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
}