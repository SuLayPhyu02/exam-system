package com.nexcode.examsystem.model.requests;

import java.util.List;


public class UserRequest {
	
	private String username;
	private String email;
	private String phone;
	private List<Long>courses;
	
	
	public UserRequest() {
		
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public List<Long> getCourses() {
		return courses;
	}
	public void setCourses(List<Long> courses) {
		this.courses = courses;
	}
	
}
