 package com.monarchsolutions.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monarchsolutions.sms.dto.payments.CreatePaymentRequest;
import com.monarchsolutions.sms.dto.payments.UpdatePaymentDTO;
import com.monarchsolutions.sms.repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository repo;

    public String createPayment(Long tokenUserId, CreatePaymentRequest req, String lang) {
        return repo.createPayment(tokenUserId, req, lang);
    }

    public String updatePayment(Long token_user_id, Long payment_id, UpdatePaymentDTO request, String lang) throws Exception {
        // Call the repository method that converts the request to JSON and executes the stored procedure
        return repo.updatePayment(token_user_id, payment_id, request, lang);
    }
}