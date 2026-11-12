package com.example.jwtApp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="app_users")
public class AppUser {

	   @Id
	   @GeneratedValue(strategy = GenerationType.IDENTITY)
	   private Long id;
	   
	   @Column(nullable=false, unique=true)
	   private String username;
	   
	   @Column(nullable=false, unique=true)
	   private String email; 
	   
	   @Column(nullable=false)
	   private String password; 
	   
	   @Column(nullable=false)
	   private String role = "ROLE_USER";
	   
	   
	   public AppUser(){
		   
	   }
	   
	   public AppUser(String username, String email, String password, String role) {
		     
		      this.username = username;
		      this.email = email; 
		      this.password = password; 
		      this.role = role;
	   }
	   
	   
       //	Getter Methods: 
	   
	       public Long getId() {
	    	   return id; 
	       }
	       
	       public String getUsername() {
	    	   return username;
	       }
	       
	       public String getEmail() {
	    	   return email;
	       }
	       
	       public String getPassword() {
	    	   return password;
	       }
	       
	       public String getRole() {
	    	   return role;
	       }
	       
	       
	   // Setter methods: 
	       
	       public void setUsername(String username) {
	    	   this.username = username;
	       }
	       
           public void setEmail(String email) {
	    	   this.email = email; 
	       }
           
           public void setPassword(String password) {
	    	   this.password = password; 
	    	   
	       }
 
           public void setRole(String role) {
	    	   this.role = role; 
	       }	   
	   
}
