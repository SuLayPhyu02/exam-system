package com.nexcode.examsystem.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nexcode.examsystem.mapper.CourseMapper;
import com.nexcode.examsystem.mapper.ExamMapper;
import com.nexcode.examsystem.mapper.UserMapper;
import com.nexcode.examsystem.model.dtos.CourseDto;
import com.nexcode.examsystem.model.dtos.ExamDto;
import com.nexcode.examsystem.model.dtos.UserDto;
import com.nexcode.examsystem.model.entities.Course;
import com.nexcode.examsystem.model.entities.Exam;
import com.nexcode.examsystem.model.entities.User;
import com.nexcode.examsystem.model.exception.BadRequestException;
import com.nexcode.examsystem.model.exception.NotFoundException;
import com.nexcode.examsystem.repository.CourseRepository;
import com.nexcode.examsystem.repository.UserExamRepository;
import com.nexcode.examsystem.service.CourseService;
@Service
public class CourseServiceImpl implements CourseService{
	
	private final CourseRepository courseRepository;
	private final UserExamRepository userExamRepository;
	
	private final UserMapper userMapper;
	private final CourseMapper courseMapper;
	private final ExamMapper examMapper;

	
	public CourseServiceImpl(CourseRepository courseRepository, UserExamRepository userExamRepository,
			UserMapper userMapper, CourseMapper courseMapper, ExamMapper examMapper) {
		this.courseRepository = courseRepository;
		this.userExamRepository = userExamRepository;
		this.userMapper = userMapper;
		this.courseMapper = courseMapper;
		this.examMapper = examMapper;
	}

	@Override
	public List<CourseDto> getAllCourses() {
		return courseMapper.toDtoList(courseRepository.findAll());
	}

	@Override
	public CourseDto addCourse(CourseDto dto) {
		Course course=new Course();
		course.setName(dto.getName());
		course.setDescription(dto.getDescription());
		course.setActive(true);
		Course savedCourse=courseRepository.save(course);
		return courseMapper.toDto(savedCourse);
	}
	@Override
	public List<UserDto> getAllUserByCourseId(Long id) {
		List<User>users=courseRepository.getAllUsersByCourseId(id);
		return userMapper.toDtoList(users);
	}
	@Override
	public List<ExamDto>getAllPublishedExams(Long id)
	{
		List<Exam>exams=courseRepository.getAllPublishedExams(id);
		return examMapper.toDtoList(exams);
	}
	@Override
	public CourseDto updateCourse(Long id,CourseDto dto) {
		
		Course foundedCourse=courseRepository.findById(id).orElseThrow(()->new BadRequestException("Course not found."));
		foundedCourse.setName(dto.getName());
		foundedCourse.setDescription(dto.getDescription());
		courseRepository.save(foundedCourse);
		return courseMapper.toDto(foundedCourse);
	}
	@Override
	public CourseDto findByName(String name) {
		return courseMapper.toDto(courseRepository.findByName(name).orElse(null));
	}

	@Override
	public void deleteCourse(Long id) {
		Course foundedCourse=courseRepository.findById(id).orElseThrow(()->new NotFoundException("course not found"));
		List<Exam>exams=userExamRepository.getAllTakenExams();
		for(Exam e :exams)
		{
			if(e.getCourse().getName().equalsIgnoreCase(foundedCourse.getName()))
				throw new BadRequestException("Course cannot be delete because of there is student who taken that course's exam");
		}
		foundedCourse.setActive(false);
		courseRepository.save(foundedCourse);
	}

	

}
