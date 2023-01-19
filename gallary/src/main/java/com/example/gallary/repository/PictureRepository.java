package com.example.gallary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.gallary.entity.Pictures;
import com.example.gallary.entity.Users;

public interface PictureRepository extends JpaRepository<Pictures, Integer>{
	
	@Query("SELECT p FROM Pictures p WHERE p.marked = 'accepted'")
	List<Pictures> findAllAcceptedPic();
	
	@Query("update Pictures p set p.marked = ?1 where p.id = ?2")  
	Pictures updatePicture( String marked, Integer id);
	
}
