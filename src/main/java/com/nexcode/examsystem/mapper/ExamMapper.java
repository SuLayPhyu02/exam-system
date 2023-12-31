package com.nexcode.examsystem.mapper;

import java.util.List;

import com.nexcode.examsystem.model.dtos.ExamDto;
import com.nexcode.examsystem.model.dtos.QuestionDto;
import com.nexcode.examsystem.model.entities.Exam;
import com.nexcode.examsystem.model.responses.ExamResponse;
import com.nexcode.examsystem.model.responses.TakeExamResponse;

public interface ExamMapper {

	public ExamDto toDto(Exam exam);
	public List<ExamDto>toDtoList(List<Exam>exams);
	public ExamResponse toResponse(ExamDto dto);
	public List<ExamResponse>toResponseList(List<ExamDto>dtos);
	public TakeExamResponse toTakeExamResponse(ExamDto dto,List<QuestionDto>response);
}
