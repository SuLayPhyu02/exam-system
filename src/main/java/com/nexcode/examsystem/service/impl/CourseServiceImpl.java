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
import com.nexcode.examsystem.repository.ExamRepository;
import com.nexcode.examsystem.repository.UserExamRepository;
import com.nexcode.examsystem.repository.UserRepository;
import com.nexcode.examsystem.service.CourseService;
@Service
public class CourseServiceImpl implements CourseService{
	
	private final CourseRepository courseRepository;
	private final UserExamRepository userExamRepository;
	private final ExamRepository examRepository;
	private final UserRepository userRepository;
	
	private final UserMapper userMapper;
	private final CourseMapper courseMapper;
	private final ExamMapper examMapper;

	
	

//	public CourseServiceImpl(CourseRepository courseRepository, UserExamRepository userExamRepository,
//			ExamRepository examRepository, UserMapper userMapper, CourseMapper courseMapper, ExamMapper examMapper) {
//		super();
//		this.courseRepository = courseRepository;
//		this.userExamRepository = userExamRepository;
//		this.examRepository = examRepository;
//		this.userMapper = userMapper;
//		this.courseMapper = courseMapper;
//		this.examMapper = examMapper;
//	}
	public CourseServiceImpl(CourseRepository courseRepository, UserExamRepository userExamRepository,
			ExamRepository examRepository, UserRepository userRepository, UserMapper userMapper, CourseMapper courseMapper,
			ExamMapper examMapper) {
		super();
		this.courseRepository = courseRepository;
		this.userExamRepository = userExamRepository;
		this.examRepository = examRepository;
		this.userRepository = userRepository;
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
	public List<ExamDto> getAllUnTakenExams(String email, Long id)
	{
		User foundedUser=userRepository.findByEmail(email).orElseThrow(()->new NotFoundException("User not Found"));
		List<Exam>exams=courseRepository.getAllUntakenPublishedExams(id,foundedUser.getId());
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
		int examCount =examRepository.countExamsForCourse(id);
		int studentCount=courseRepository.getTotalNoOfStudent(id);
		int expectedExamCount=examCount*studentCount;
		int takenExamCount=userExamRepository.countStudentsForCourse(id);
		if(expectedExamCount==takenExamCount || takenExamCount==0)
		{
			
			foundedCourse.setActive(false);
			for (User user : foundedCourse.getUsers()) {
                user.getCourses().remove(foundedCourse);
                userRepository.save(user);
            }
		}
		else
		{
			throw new BadRequestException("Course cannot be delete because of there is student who taken that course's exam");
		}
		courseRepository.save(foundedCourse);
	}
	@Override
	public CourseDto findCourseById(Long id) {
		Course foundedCourse=courseRepository.findById(id).orElseThrow(()->new NotFoundException("course not found"));
		return courseMapper.toDto(foundedCourse);
	}

	

}
