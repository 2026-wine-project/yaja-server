package com.gbsw.template.domain.user.repository;

import com.gbsw.template.domain.user.entity.Role;
import com.gbsw.template.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    List<UserEntity> findAllByRole(Role role);

    long countByRole(Role role);
}
