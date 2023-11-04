package com.nexcode.examsystem.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexcode.examsystem.mapper.CourseMapper;
import com.nexcode.examsystem.mapper.QuestionMapper;
import com.nexcode.examsystem.mapper.UserMapper;
import com.nexcode.examsystem.model.dtos.CourseDto;
import com.nexcode.examsystem.model.dtos.ExamDto;
import com.nexcode.examsystem.model.dtos.QuestionDto;
import com.nexcode.examsystem.model.dtos.UserDto;
import com.nexcode.examsystem.model.exception.AppException;
import com.nexcode.examsystem.model.exception.BadRequestException;
import com.nexcode.examsystem.model.requests.ChangePasswordRequest;
import com.nexcode.examsystem.model.requests.EmailRequest;
import com.nexcode.examsystem.model.requests.NewPasswordRequest;
import com.nexcode.examsystem.model.requests.UserAnswerRequest;
import com.nexcode.examsystem.model.requests.UserRequest;
import com.nexcode.examsystem.model.requests.VerifyOtpRequest;
import com.nexcode.examsystem.model.responses.ApiResponse;
import com.nexcode.examsystem.model.responses.CourseResponse;
import com.nexcode.examsystem.model.responses.QuestionResponse;
import com.nexcode.examsystem.security.CurrentUser;
import com.nexcode.examsystem.security.UserPrincipal;
import com.nexcode.examsystem.service.ExamService;
import com.nexcode.examsystem.service.UserExamSerrvice;
import com.nexcode.examsystem.service.UserService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
@RestController
@RequestMapping("/api/user")
@Getter
@Setter
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	private final ExamService examService;
	private final UserExamSerrvice userExamSerrvice;
	
	private final UserMapper userMapper;
	private final CourseMapper courseMapper;
	private final QuestionMapper questionMapper;
	
	@GetMapping
	public ResponseEntity<?> getAllUsers() {
		List<UserDto>dtos=userService.getAllUser();
	    return new ResponseEntity<>(userMapper.toResponseList(dtos), HttpStatus.OK);
	}
	@GetMapping("/courses")
	public ResponseEntity<?>getCoursesByUser(@CurrentUser UserPrincipal currentUser)
	{
		String email=currentUser.getEmail();
		List<CourseDto>dtos=userService.getAllCategoryByUser(email);
		List<CourseResponse>responses=courseMapper.toResponseList(dtos);
		return new ResponseEntity<>(responses,HttpStatus.OK);
	}
	
	@PostMapping("/student-signup")
	public ResponseEntity<?> createUser(@RequestBody UserRequest request) {
		String email = request.getEmail();
		UserDto existingUser = userService.findByEmailAddress(email);
		if (existingUser != null) {
			return new ResponseEntity<>(new ApiResponse(false, "This email is already in use. Please use another one."), HttpStatus.CONFLICT);
		} 
		else
		{
			try {
				if (userService.signUpUser(request)) 
				{
					return new ResponseEntity<>(new ApiResponse(true, "Signup successful. An email has been sent for verification."), HttpStatus.CREATED);
				}
				else
				{
					return new ResponseEntity<>(new ApiResponse(false, "Signup failed. Please try again later."), HttpStatus.BAD_REQUEST);
				} 
			}catch(AppException e)
			{
				return new ResponseEntity<>(new ApiResponse(false,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); 
			}
		}
	}
	
	@PutMapping("/{id}")
	public ApiResponse updateStudentData(@PathVariable Long id,@RequestBody UserRequest request)
	{
		boolean isUpdated=userService.updateStudent(id, request);
		return new ApiResponse(isUpdated, "Successfully changed user.");
	}
	
	@PostMapping("/forgot-password")
	public ApiResponse processForgetPassword(@RequestBody EmailRequest request) {
		UserDto foundedUser = userService.findByEmailAddress(request.getEmail());
		return new ApiResponse(userService.generateOneTimePassword(foundedUser), "Successfully password reset.");
	}
	@PutMapping("/change-password")
	public ApiResponse processForgetPassword(@CurrentUser UserPrincipal currentUser,
			@RequestBody ChangePasswordRequest request) {
		String requestOldPassword = request.getOldPassword();
		String requestNewPassword = request.getNewPassword();
		String useranme = currentUser.getUsername();
		String email=currentUser.getEmail();
		System.out.println("Username is "+useranme);
		System.out.println("old "+requestOldPassword);
		System.out.println("new "+requestNewPassword);
		return new ApiResponse(userService.changePassword(email, requestOldPassword, requestNewPassword), "Successfully changed password.");

	}
	@PostMapping("/verified-otp")
	public ApiResponse verifyOtp(@RequestBody VerifyOtpRequest request) {
		String email = request.getEmail();
		String storedOtp = request.getOtp();
		return new ApiResponse(userService.validateOtp(email, storedOtp), "validate otp");
	}

	@PostMapping("/set-new-password")
	public ApiResponse setNewForgotPassword(@RequestBody NewPasswordRequest request) {
		String email = request.getEmail();
		String password = request.getNewpassword();
		return new ApiResponse(userService.setNewResetPassword(email, password), "Successfully updated New Password");
	}
	@GetMapping("/exam/{id}/start")
    public List<QuestionResponse> startExam(@CurrentUser UserPrincipal currentUser,@PathVariable Long id) 
    {
		String email=currentUser.getEmail();
		ExamDto foundedExam=examService.findExamById(id);
		if(foundedExam==null)
		{
			throw new BadRequestException("Exam not found");
		}
		List<QuestionDto>dtos=examService.getRandomQuestionsForExam(email,foundedExam);
		boolean createdUserExam=userExamSerrvice.createUserExam(currentUser.getId(),foundedExam.getId());
		System.out.println("this is about to created user exam "+createdUserExam);
		return questionMapper.toResponseList(dtos);
	}
	@PostMapping("/exam/{id}/submit")
	public ResponseEntity<?> submitExam(@CurrentUser UserPrincipal currentUser,@PathVariable Long id,@RequestBody List<UserAnswerRequest>userAnswers)
	{
		Long currentId=currentUser.getId();
		if(userExamSerrvice.findUserExamByUserAndExam(currentId, id,userAnswers)) 
		{
			return new ResponseEntity<>(new ApiResponse(true,"Submitted successfully"),HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse(false,"Something is wrong at service layer"),HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
