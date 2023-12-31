package com.nexcode.examsystem.mapper.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nexcode.examsystem.mapper.CourseMapper;
import com.nexcode.examsystem.mapper.ExamMapper;
import com.nexcode.examsystem.mapper.LevelMapper;
import com.nexcode.examsystem.mapper.QuestionMapper;
import com.nexcode.examsystem.model.dtos.ExamDto;
import com.nexcode.examsystem.model.dtos.QuestionDto;
import com.nexcode.examsystem.model.entities.Exam;
import com.nexcode.examsystem.model.responses.ExamResponse;
import com.nexcode.examsystem.model.responses.QuestionResponse;
import com.nexcode.examsystem.model.responses.TakeExamResponse;

@Component
public class ExamMapperImpl implements ExamMapper {

	private final LevelMapper levelMapper;
	private final CourseMapper courseMapper;
	private final QuestionMapper questionMapper;

	
	public ExamMapperImpl(LevelMapper levelMapper, CourseMapper courseMapper, QuestionMapper questionMapper) {
		this.levelMapper = levelMapper;
		this.courseMapper = courseMapper;
		this.questionMapper = questionMapper;
	}

	@Override
	public ExamDto toDto(Exam exam) {
		if (exam == null) {
			return null;
		}
		ExamDto dto = new ExamDto();
		dto.setId(exam.getId());
		dto.setName(exam.getName());
		dto.setDescription(exam.getDescription());
		dto.setExamDurationMinute(exam.getExamdurationMinutes());
		dto.setPublished(exam.isPublished());
		dto.setExamTotalMark(exam.getExamTotalMark());
		dto.setNumberOfQuestionsToGenerate(exam.getNumberOfQuestionsToGenerate());
		Date date = exam.getExamPublishDate();
		if (date == null) {
			dto.setPublishedDate(null);
			;
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String formattedDate = dateFormat.format(date);
			dto.setPublishedDate(formattedDate);
		}
		dto.setLevel(levelMapper.toDto(exam.getLevel()));
		dto.setCourse(courseMapper.toDto(exam.getCourse()));
		dto.setQuestions(questionMapper.toDtoList(exam.getQuestions()));
		return dto;
	}

	@Override
	public List<ExamDto> toDtoList(List<Exam> exams) {
		if (exams == null) {
			return null;
		}
		return exams.stream().map(e -> toDto(e)).collect(Collectors.toList());
	}

	@Override
	public ExamResponse toResponse(ExamDto dto) {
		ExamResponse response = new ExamResponse();
		response.setId(dto.getId());
		response.setName(dto.getName());
		response.setDescription(dto.getDescription());
		response.setPublishedDate(dto.getPublishedDate());
		response.setPublished(dto.isPublished());
		response.setExamTotalMark(dto.getExamTotalMark());
		response.setExamDurationMinute(dto.getExamDurationMinute());
		response.setNoOfQuestion(dto.getNumberOfQuestionsToGenerate());
		response.setCategory(courseMapper.toResponse(dto.getCourse()));
		response.setLevel(levelMapper.toResponse(dto.getLevel()));
		return response;
	}

	@Override
	public List<ExamResponse> toResponseList(List<ExamDto> dtos) {
		return dtos.stream().map(e -> toResponse(e)).collect(Collectors.toList());
	}

	@Override
	public TakeExamResponse toTakeExamResponse(ExamDto dto, List<QuestionDto> questions) {
		TakeExamResponse response=new TakeExamResponse();
		ExamResponse examResponse=toResponse(dto);
		List<QuestionResponse> questionResponses=questionMapper.toResponseList(questions);
		response.setExam(examResponse);
		response.setQuestions(questionResponses);
		return response;
	}

}
