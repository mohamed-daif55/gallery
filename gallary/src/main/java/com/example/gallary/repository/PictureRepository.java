package com.example.gallary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gallary.entity.Pictures;

public interface PictureRepository extends JpaRepository<Pictures, Integer>{

}
