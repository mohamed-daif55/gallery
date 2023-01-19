package com.example.gallary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.gallary.entity.Users;

public interface UserRepository extends JpaRepository<Users, Integer>{

	@Query("Select u from Users u WHERE u.email=:email")
	Users findByEmail(@Param("email") String email);

}
