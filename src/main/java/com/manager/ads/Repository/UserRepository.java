package com.manager.ads.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.manager.ads.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNumber(String number);
    Optional<User> findByEmail(String email);
}

