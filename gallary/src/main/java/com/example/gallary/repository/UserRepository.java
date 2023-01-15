package com.example.gallary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gallary.entity.Users;

public interface UserRepository extends JpaRepository<Users, Integer>{

}
