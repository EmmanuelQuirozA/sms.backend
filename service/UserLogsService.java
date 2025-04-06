package com.monarchsolutions.sms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monarchsolutions.sms.dto.userLogs.UserLogsListDto;
import com.monarchsolutions.sms.repository.UserLogsRepository;

@Service
public class UserLogsService {

    @Autowired
    private UserLogsRepository UserLogsRepository;
    

    public List<UserLogsListDto> getUsersActivityLog(Long tokenSchoolId, String lang){
        // If tokenSchoolId is not null, the SP will filter students by school.
        return UserLogsRepository.getUsersActivityLog(tokenSchoolId, lang);
    }
}
