package com.example.jwtApp.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jwtApp.entity.StudentRecords;
import com.example.jwtApp.repository.StudentRepository;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepo;

    // Constructor Injection
    public StudentService(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }

    // 1. Get All Students
    @Transactional(readOnly = true)
    public List<StudentRecords> findAll() {

        return studentRepo.findAll();
    }

    // 2. Get Student By ID
    @Transactional(readOnly = true)
    public StudentRecords findById(Long id) {

        return studentRepo.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found with ID: " + id
                        )
                );
    }

    // 3. Save Student
    public StudentRecords save(StudentRecords student) {

        return studentRepo.save(student);
    }

    // 4. Delete Student
    public void delete(Long id) {

        if (!studentRepo.existsById(id)) {
            throw new IllegalArgumentException(
                    "Student not found with ID: " + id
            );
        }

        studentRepo.deleteById(id);
    }
}