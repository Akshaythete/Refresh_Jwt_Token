package com.user.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.Entity.User;

public interface UserRepo extends JpaRepository<User, Integer> {

	Optional<User> findByName(String username);

}
