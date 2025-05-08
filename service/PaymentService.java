package com.monarchsolutions.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monarchsolutions.sms.dto.payments.CreatePaymentRequest;
import com.monarchsolutions.sms.repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository repo;

    /**
     * Wraps the repository call; any business logic (e.g. validation)
     * would live here.
     */
    public String createPayment(Long tokenUserId,
                                CreatePaymentRequest req,
                                String lang) {
        return repo.createPayment(tokenUserId, req, lang);
    }
}