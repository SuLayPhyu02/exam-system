package com.nexcode.examsystem.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nexcode.examsystem.model.dtos.UserExamHistoryDto;
import com.nexcode.examsystem.model.entities.Answer;
import com.nexcode.examsystem.model.entities.Exam;
import com.nexcode.examsystem.model.entities.Question;
import com.nexcode.examsystem.model.entities.User;
import com.nexcode.examsystem.model.entities.UserAnswer;
import com.nexcode.examsystem.model.entities.UserExam;
import com.nexcode.examsystem.model.exception.BadRequestException;
import com.nexcode.examsystem.model.requests.UserAnswerRequest;
import com.nexcode.examsystem.repository.ExamRepository;
import com.nexcode.examsystem.repository.QuestionRepository;
import com.nexcode.examsystem.repository.UserAnswerRepository;
import com.nexcode.examsystem.repository.UserExamRepository;
import com.nexcode.examsystem.repository.UserRepository;
import com.nexcode.examsystem.service.UserExamService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
@Service
@Getter
@Setter
@RequiredArgsConstructor
public class UserExamServiceImpl implements UserExamService{

	private final UserRepository userRepository;
	private final ExamRepository examRepository;
	private final UserExamRepository userExamRepository;
	private final UserAnswerRepository userAnswerRepository;
	private final QuestionRepository questionRepository;
	@Override
	public boolean createUserExam(Long userId, Long examId) {
		User foundedUser=userRepository.findById(userId).orElseThrow(()->new BadRequestException("user not found"));
		Exam foundedExam=examRepository.findById(examId).orElseThrow(()->new BadRequestException("Exam not found"));
		UserExam userExam=new UserExam();
		userExam.setUser(foundedUser);
		userExam.setExam(foundedExam);
		userExam.setObtainedResult(0);
		userExam.setIsActive(true);
		userExamRepository.save(userExam);
		return true;
	}
	@Override
	public Long findUserExamByUserAndExam(Long userId, Long examId,List<UserAnswerRequest>userAnswers) 
	{
		
		User foundedUser=userRepository.findById(userId).orElseThrow(()->new BadRequestException("user not found"));
		Exam foundedExam=examRepository.findById(examId).orElseThrow(()->new BadRequestException("Exam not found"));
		UserExam foundedUserExam=userExamRepository.findByUserExam(userId,examId);
		int markForEachQuestion=foundedExam.getExamTotalMark()/foundedExam.getNoOfQuestion();
		int obtainedMarks=calculateObtainedMarks(markForEachQuestion,userAnswers,foundedUserExam);
		
		foundedUserExam.setObtainedResult(obtainedMarks);
		int passingMark=foundedExam.getExamTotalMark()/2;
		foundedUserExam.setIsPassFail(obtainedMarks>=passingMark);
		try {
			userExamRepository.save(foundedUserExam);
			Long id=foundedUserExam.getId();
			return id;
		}catch(Exception e){
			throw new BadRequestException(e.getMessage());
		}
	}
	public int calculateObtainedMarks(int mark,List<UserAnswerRequest> userAnswers,UserExam userExam) {
	    int obtainedMarks = 0;
	    List<UserAnswer> userAnswerList = new ArrayList<>();
	    for (UserAnswerRequest a : userAnswers) {
	        Long questionId = a.getQuestionId();
	        Question question = questionRepository.findById(questionId).orElseThrow(() -> new BadRequestException("question not found"));
	        Answer correctAnswer = findCorrectAnswer(question);
	        
	        UserAnswer userAnswer = new UserAnswer();
	        userAnswer.setQuestion(question);
	        userAnswer.setSelectedAnswer(a.getSelectedAnswer());

	        userAnswerRepository.save(userAnswer);
	        userAnswerList.add(userAnswer); 
	        if (correctAnswer != null && correctAnswer.getAnswer().equals(a.getSelectedAnswer())) {
	        	userAnswer.setIsSelectedAnswerCorrect(true);          
	        	obtainedMarks+=mark;
	        }else
	        {
	        	userAnswer.setIsSelectedAnswerCorrect(false);
	        }
	        
	    }
	    userExam.setUserAnswers(userAnswerList);
	    return obtainedMarks;
	}
	private Answer findCorrectAnswer(Question question) 
	{
	    for (Answer answer : question.getAnswers()) {
	        if (answer.isCorrectAnswer()) {
	            return answer;
	        }
	    }
	    return null; 
	}
//	@Override
//	public List<UserExamHistoryDto> getExamHistoryByUser(Long userId, Long examId) {
//		
//		List<UserExamHistoryDto> dto= userExamRepository.findUserExamHistory(userId,examId);
//		return dto;
//	}
	@Override
	public UserExamHistoryDto getExamHistoryByUser(Long userId, Long examId) {
		
		UserExamHistoryDto dto= userExamRepository.findUserExamHistory(userId,examId);
		return dto;
	}

	
}
