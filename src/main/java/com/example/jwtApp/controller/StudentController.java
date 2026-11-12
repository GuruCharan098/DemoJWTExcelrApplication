
package com.example.jwtApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.jwtApp.entity.StudentRecords;
import com.example.jwtApp.services.StudentService;

import jakarta.validation.Valid;

@Controller
public class StudentController {

    private final StudentService studentService;

    // Constructor Injection
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Home Page
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute(
            "totalStudents",
            studentService.findAll().size()
        );

        return "dashboard";
    }

    // Display All Students
    @GetMapping("/students")
    public String students(Model model) {

        model.addAttribute(
            "students",
            studentService.findAll()
        );

        return "students";
    }

    // Open New Student Form
    @GetMapping("/students/new")
    public String newStudent(Model model) {

        model.addAttribute(
            "student",
            new StudentRecords()
        );

        return "student-form";
    }

    // Open Edit Student Form
    @GetMapping("/students/edit/{id}")
    public String editStudent(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
            "student",
            studentService.findById(id)
        );

        return "student-form";
    }

    // Save Student (Add + Update)
    @PostMapping("/students/save")
    public String saveStudent(
            @Valid @ModelAttribute("student") StudentRecords student,
            BindingResult result) {

        if (result.hasErrors()) {
            return "student-form";
        }

        studentService.save(student);

        return "redirect:/students";
    }

    // Delete Student
    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {

        studentService.delete(id);

        return "redirect:/students";
    }
}
