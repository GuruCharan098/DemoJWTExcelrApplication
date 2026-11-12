package com.example.jwtApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jwtApp.entity.StudentRecords;

public interface StudentRepository extends JpaRepository <StudentRecords, Long>{
     
	   boolean existsByEmail(String email); 
}
