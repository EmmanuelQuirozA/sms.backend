package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.reports.PayDetailResponse;
import com.monarchsolutions.sms.repository.ReportsRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportsService {

    @Autowired
    private ReportsRepository reportsRepository;

    public String getPaymentsPivotReport(Long schoolId) throws Exception {
        return reportsRepository.getPaymentsPivotReport(schoolId);
    }

    public List<PayDetailResponse> getPayDetails(Long tokenSchoolId, Long payment_id ,String lang){
        // If tokenSchoolId is not null, the SP will filter students by school.
        return reportsRepository.getPayDetails(tokenSchoolId, payment_id, lang);
    }
}