package com.nexcode.examsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nexcode.examsystem.model.dtos.UserExamDto;
import com.nexcode.examsystem.model.entities.UserExam;
import com.nexcode.examsystem.model.projections.UserExamHistoryProjection;

public interface UserExamRepository extends JpaRepository<UserExam, Long> {

	@Query("SELECT ue FROM UserExam ue WHERE ue.user.id = :userId AND ue.exam.id = :examId")
	public UserExam findByUserExam(Long userId, Long examId);

	@Query(value = "SELECT ue.id as id, ue.exam.name as examName, ue.exam.description as examDescription, ue.exam.noOfQuestion as noOfQuestion, ue.exam.examTotalMark as examTotalMark, ue.exam.course.name as courseName, ue.exam.level.name as levelName, ue.obtainedResult as obtainedResult, ue.isPassFail as isPassFail FROM UserExam ue JOIN ue.exam e WHERE ue.user.id = :userId")
	List<UserExamHistoryProjection> findAllExamHistoryByUserId(@Param("userId") Long userId);

}
 