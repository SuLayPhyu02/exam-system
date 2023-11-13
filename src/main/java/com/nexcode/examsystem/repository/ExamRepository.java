package com.nexcode.examsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nexcode.examsystem.model.entities.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long> {

	@Query("SELECT COUNT(q) FROM Exam e JOIN e.questions q WHERE e.id = :examId")
	public int countQuestionsForExam(@Param("examId") Long examId);

	@Query("SELECT  COUNT(DISTINCT ue.id) * 100.0 / (COUNT(DISTINCT e.id))" + "FROM Exam e " + "JOIN e.course c "
			+ "LEFT JOIN UserExam ue ON e.id = ue.exam.id " + "WHERE c.id = :courseId")
	Long getPercentage(@Param("courseId") Long courseId);
}
