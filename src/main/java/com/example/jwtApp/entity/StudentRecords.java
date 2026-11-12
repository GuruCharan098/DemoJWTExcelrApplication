package com.example.jwtApp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="student_records")
public class StudentRecords {

	   @Id
	   @GeneratedValue(strategy = GenerationType.IDENTITY)
	   private Long id;
	   
	   @NotBlank(message="Name is required")
	   @Size(min=3, max=50, message = "Name must contain 3 to 50 Characters")
	   @Column(nullable=false)
	   private String name; 
	   
	   @NotBlank(message= "Course is required")
	   @Column(nullable= false)
	   private String course; 
	   
	   @NotBlank(message= "Email is required")
	   @Email(message= "Enter a valid email")
	   @Column(nullable= false)
	   private String email; 
	   
	   
	   public StudentRecords() {
		   
	   }
	   
	   public StudentRecords(String name, String course, String email) {
		   this.name = name; 
		   this.course = course; 
		   this.email = email; 
	   }
	   
	   //	Getter Methods: 
	   
       public Long getId() {
    	   return id; 
       }
       
       public String getName() {
    	   return name;
       }
       
       public String getEmail() {
    	   return email;
       }
       
       public String getCourse() {
    	   return course;
       }
       
      // Setter methods: 
       
       public void setName(String name) {
    	   this.name = name;
       }
       
       public void setEmail(String email) {
    	   this.email = email; 
       }
       
       public void setCourse(String course) {
    	   this.course = course; 
       }
	   
}
