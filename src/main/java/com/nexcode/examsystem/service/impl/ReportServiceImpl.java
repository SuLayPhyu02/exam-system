package com.nexcode.examsystem.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nexcode.examsystem.model.entities.Course;
import com.nexcode.examsystem.model.entities.Exam;
import com.nexcode.examsystem.model.entities.Level;
import com.nexcode.examsystem.model.entities.User;
import com.nexcode.examsystem.model.entities.UserExam;
import com.nexcode.examsystem.model.projections.UserReportProjection;
import com.nexcode.examsystem.model.responses.CourseExamListReportResponse;
import com.nexcode.examsystem.model.responses.CourseExamReportPieResponse;
import com.nexcode.examsystem.model.responses.ExamStudentReportResponse;
import com.nexcode.examsystem.model.responses.OverAllReportResponse;
import com.nexcode.examsystem.model.responses.StudentPassFailCountResponse;
import com.nexcode.examsystem.repository.CourseRepository;
import com.nexcode.examsystem.repository.ExamRepository;
import com.nexcode.examsystem.repository.LevelRepository;
import com.nexcode.examsystem.repository.UserExamRepository;
import com.nexcode.examsystem.repository.UserRepository;
import com.nexcode.examsystem.service.ReportService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{
	
	private final CourseRepository courseRepository;
	private final UserExamRepository userExamRepository;
	private final UserRepository userRepository;
	private final ExamRepository examRepository;
	private final LevelRepository levelRepository;
	
	public List<Course>getAllCourses()
	{
		return courseRepository.findAll();
	}
		@Override
		public List<OverAllReportResponse> getOverAllReports() {
		    List<Course> courses = getAllCourses();
		    List<OverAllReportResponse> list = new ArrayList<>();
		    for (Course course : courses) {
		        Integer totalNoOfStudents = courseRepository.getTotalNoOfStudent(course.getId());
		        Integer totalExamsInCourse = courseRepository.getTotalExamsInCourse(course.getId());
		        
		        int completedStudents = 0;
		        for (User student : course.getUsers()) {
		            Integer distinctTakenExams = userExamRepository.getDistinctTakenExamCount(student.getId(), course.getId());
		            if (distinctTakenExams.equals(totalExamsInCourse)) {
		                completedStudents++;
		            }
		        }

		        int inProgressStudents = totalNoOfStudents - completedStudents;

		        OverAllReportResponse response = new OverAllReportResponse();
		        response.setCourseName(course.getName());
		        response.setTotalNoOfStudents(totalNoOfStudents);
		        response.setCompleteStudents(completedStudents);
		        response.setInProgressStudents(inProgressStudents);
		        list.add(response);
		    }
		    return list;
	}
	@Override
	public List<CourseExamListReportResponse> getAllCourseExamListReport(Long id) {
		List<Exam>examList=courseRepository.getAllPublishedExams(id);
		List<CourseExamListReportResponse>list=new ArrayList<>();
		Integer totalNoOfStudent = courseRepository.getTotalNoOfStudent(id);
		for(Exam e : examList)
		{
			Integer completedStudent=0;
			Integer inProgressStudent=0;
			
			CourseExamListReportResponse response=new CourseExamListReportResponse();
			response.setExamId(e.getId());
			response.setExamName(e.getName());
			response.setExamPublishedDate(e.getExamPublishDate().toInstant().toString());//check
			response.setLevelName(e.getLevel().getName());//
			completedStudent=examRepository.getTotalNoOfStudentsOfEachExam(e.getId());
			System.out.println(completedStudent+"completed student count for exam "+e.getId());// this one is not right 
			response.setCompletedStudents(completedStudent);
			inProgressStudent=totalNoOfStudent-completedStudent;
			response.setInProgressStudents(inProgressStudent);
			list.add(response);
		}
		return list;
	}
	@Override
	public List<CourseExamReportPieResponse> getExamByLevel(Long id) {
		 List<CourseExamReportPieResponse> results = new ArrayList<>();

		    List<Level> levels = levelRepository.findAll();
		    
		    for (Level level : levels) {
		        CourseExamReportPieResponse response = new CourseExamReportPieResponse();
		        response.setLevelName(level.getName());
		        
		        int examCount = examRepository.getCountByLevelAndCourse(level.getId(), id);
		        response.setNoOfExams(examCount);

		        results.add(response);
		    }

		    return results;
	}
	@Override
	public List<ExamStudentReportResponse> getExamStudent(Long examId) {
		List<UserExam>userExams=userExamRepository.findAllUserByExamId(examId);
		List<ExamStudentReportResponse>list=new ArrayList<>();
		for(UserExam ue:userExams)
		{
			ExamStudentReportResponse response=new ExamStudentReportResponse();
			response.setId(ue.getUser().getId());
			response.setRollNo(ue.getUser().getRollNo());
			response.setUserName(ue.getUser().getUsername());
			response.setEmail(ue.getUser().getEmail());
			response.setObtainedMark(ue.getObtainedResult());
			response.setPassFail(ue.getIsPassFail());
			list.add(response);
		}
		return list;
	}
	@Override
	public List<UserReportProjection> getStudentReport(Long id) {
		return userRepository.findUserTakenExams(id);
	}
	@Override
	public StudentPassFailCountResponse getCountPassFail(Long examId) {
		Integer passCount = userExamRepository.getPassCountByExamId(examId);
        Integer failCount = userExamRepository.getFailCountByExamId(examId);
        StudentPassFailCountResponse response=new StudentPassFailCountResponse();
        response.setPassCount(passCount);
        response.setFailCount(failCount);
        return response;
        
	}
	

}
