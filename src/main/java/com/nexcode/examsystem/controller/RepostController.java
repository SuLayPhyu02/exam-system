package com.nexcode.examsystem.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexcode.examsystem.model.responses.OverAllPieResponse;
import com.nexcode.examsystem.model.responses.OverAllReportResponse;
import com.nexcode.examsystem.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class RepostController {
	
	
	private final ReportService reportService;
	
	@GetMapping
	public List<OverAllPieResponse>getTotalStudentByCourse()
	{
		return reportService.getTotalStudentByCourse();
	}
	@GetMapping
	public List<OverAllReportResponse>getOverAllReport()
	{
		return reportService.getOverAllReports();
	}

}
