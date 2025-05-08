package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.catalogs.PaymentConceptsDto;
import com.monarchsolutions.sms.dto.catalogs.PaymentStatusesDto;
import com.monarchsolutions.sms.dto.catalogs.PaymentThroughDto;
import com.monarchsolutions.sms.repository.catalogs.PaymentConceptsRepository;
import com.monarchsolutions.sms.repository.catalogs.PaymentStatusesRepository;
import com.monarchsolutions.sms.repository.catalogs.PaymentThroughRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogsService {

  @Autowired
  private PaymentConceptsRepository paymentConceptsRepository;
  @Autowired
  private PaymentStatusesRepository paymentStatusesRepository;
  @Autowired
  private PaymentThroughRepository paymentThroughRepository;

  public List<PaymentConceptsDto> getPaymentConcepts(String lang) {
      return paymentConceptsRepository.findAllByLang(lang);
  }

  public List<PaymentStatusesDto> getPaymentStatuses(String lang) {
      return paymentStatusesRepository.findAllByLang(lang);
  }

  public List<PaymentThroughDto> getPaymentThrough(String lang) {
      return paymentThroughRepository.findAllByLang(lang);
  }
  
}
