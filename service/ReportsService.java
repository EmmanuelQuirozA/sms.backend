package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.reports.StudentsBalanceRechargeResponse;
import com.monarchsolutions.sms.dto.reports.PaymentDetailResponse;
import com.monarchsolutions.sms.dto.reports.PaymentRequestListResponse;
import com.monarchsolutions.sms.repository.ReportsRepository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportsService {

    @Autowired
    private ReportsRepository reportsRepository;

    public String getPaymentsPivotReport(Long schoolId, Long student_id, LocalDate start_date, LocalDate end_date) throws Exception {
        return reportsRepository.getPaymentsPivotReport(schoolId, student_id, start_date, end_date);
    }

    public List<PaymentDetailResponse> getPayments(Long tokenSchoolId, Long student_id, Long payment_id, Long payment_request_id, String lang){
        // If tokenSchoolId is not null, the SP will filter students by school.
        return reportsRepository.getPayments(tokenSchoolId, student_id, payment_id, payment_request_id, lang);
    }

    public List<PaymentRequestListResponse> getPaymentRequests(Long tokenSchoolId, Long student_id, Long payment_id, String lang){
        // If tokenSchoolId is not null, the SP will filter students by school.
        return reportsRepository.getPaymentRequests(tokenSchoolId, student_id, payment_id, lang);
    }

    public List<StudentsBalanceRechargeResponse> getStudentsBalanceRecharge(Long tokenSchoolId, Long student_id, String lang){
        // If tokenSchoolId is not null, the SP will filter students by school.
        return reportsRepository.getStudentsBalanceRecharge(tokenSchoolId, student_id, lang);
    }
}