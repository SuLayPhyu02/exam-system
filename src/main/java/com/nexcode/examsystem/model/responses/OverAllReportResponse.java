package com.nexcode.examsystem.model.responses;

public class OverAllReportResponse {
	private Long courseId;
	private String courseName;
	private Integer totalNoOfStudents;
	private Integer inProgressStudents;
	private Integer completeStudents;
	
	public Long getCourseId() {
		return courseId;
	}
	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public Integer getTotalNoOfStudents() {
		return totalNoOfStudents;
	}
	public void setTotalNoOfStudents(Integer totalNoOfStudents) {
		this.totalNoOfStudents = totalNoOfStudents;
	}
	public Integer getInProgressStudents() {
		return inProgressStudents;
	}
	public void setInProgressStudents(Integer inProgressStudents) {
		this.inProgressStudents = inProgressStudents;
	}
	public Integer getCompleteStudents() {
		return completeStudents;
	}
	public void setCompleteStudents(Integer completeStudents) {
		this.completeStudents = completeStudents;
	}
	
	
}
