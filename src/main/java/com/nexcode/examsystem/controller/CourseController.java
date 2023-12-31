package com.nexcode.examsystem.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexcode.examsystem.mapper.CourseMapper;
import com.nexcode.examsystem.mapper.UserMapper;
import com.nexcode.examsystem.model.dtos.CourseDto;
import com.nexcode.examsystem.model.dtos.UserDto;
import com.nexcode.examsystem.model.exception.NotFoundException;
import com.nexcode.examsystem.model.requests.CourseRequest;
import com.nexcode.examsystem.model.responses.ApiResponse;
import com.nexcode.examsystem.model.responses.CourseResponse;
import com.nexcode.examsystem.service.CourseService;

@RestController
@RequestMapping("/api/course")
public class CourseController {
	
	private final CourseService courseService;
	private final CourseMapper courseMapper;
	private final UserMapper userMapper;
	
	public CourseController(CourseService courseService, CourseMapper courseMapper, UserMapper userMapper) {
		this.courseService = courseService;
		this.courseMapper = courseMapper;
		this.userMapper = userMapper;
	}
	@GetMapping
	public List<CourseResponse> getAllCourses()
	{
		List<CourseDto>dtos=courseService.getAllCourses();
		return courseMapper.toResponseList(dtos);
	}
	// filter users by course Id
	@GetMapping("/users/filter")
	public ResponseEntity<?> getUsersByCourseId(@RequestParam("id") Long courseId) {
	    List<UserDto> dtos = courseService.getAllUserByCourseId(courseId);
	    return new ResponseEntity<>(userMapper.toResponseList(dtos), HttpStatus.OK);
	}
	//search by course name
	@GetMapping("/search")
	public ResponseEntity<?> getCourseByName(@RequestParam("courseName") String courseName) {
	    CourseDto foundedCourse = courseService.findByName(courseName);
	    if(foundedCourse==null)
	    {
	    	throw new NotFoundException("Course Not Found");
	    }
	    List<CourseResponse>responses=new ArrayList<>();
	    responses.add(courseMapper.toResponse(foundedCourse));
	    return new ResponseEntity<>(responses, HttpStatus.OK);
	}
	@PostMapping
	public ResponseEntity<?>createNewCourse(@RequestBody CourseRequest request)
	{
		CourseDto existingCourse=courseService.findByName(request.getName());
		if (existingCourse != null) {
			return new ResponseEntity<>("Course already exists", HttpStatus.CONFLICT);
		}
		CourseDto dto = courseMapper.toDto(request);
	    courseService.addCourse(dto);
	    return new ResponseEntity<>(new ApiResponse(true,"Course added Successfully"), HttpStatus.CREATED);
	}
	@GetMapping("/{id}")
	public ResponseEntity<?>getCourseById(@PathVariable Long id)
	{
		CourseDto updatedDto=courseService.findCourseById(id);
		return new ResponseEntity<>(courseMapper.toResponse(updatedDto), HttpStatus.OK);
	}
	@PutMapping("/{id}")
	public ResponseEntity<?>updateCourse(@PathVariable Long id,@RequestBody CourseRequest request)
	{
		CourseDto dto=courseMapper.toDto(request);
		courseService.updateCourse(id, dto);
		
		return new ResponseEntity<>(new ApiResponse(true,"course updated successfully"), HttpStatus.OK);
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<?>deleteCourse(@PathVariable Long id)
	{
		courseService.deleteCourse(id);
		return new ResponseEntity<>(new ApiResponse(true,"Course deleted Successfully"), HttpStatus.OK);

	}
}
