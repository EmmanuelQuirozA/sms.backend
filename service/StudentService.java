package com.monarchsolutions.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.monarchsolutions.sms.dto.student.CreateStudentRequest;
import com.monarchsolutions.sms.dto.student.StudentListResponse;
import com.monarchsolutions.sms.dto.student.UpdateStudentRequest;
import com.monarchsolutions.sms.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<StudentListResponse> getStudentsList(Long tokenSchoolId, Long student_id, Long group_id, String search_criteria, String lang, int status_filter){
        // If tokenSchoolId is not null, the SP will filter students by school.
        return studentRepository.getStudentsList(tokenSchoolId, student_id, group_id, search_criteria, lang, status_filter);
    }

    public String createStudent(Long userSchoolId, String lang, Long responsible_user_id, CreateStudentRequest request) throws Exception {
        // Hash the password before storing it
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashedPassword);
        
        // Call the repository method that converts the request to JSON and executes the stored procedure
        return studentRepository.createStudent(request, userSchoolId, lang, responsible_user_id);
    }

    public String updateStudent(Long userSchoolId, Long user_id, String lang, Long responsible_user_id, UpdateStudentRequest request) throws Exception {
        // Call the repository method that converts the request to JSON and executes the stored procedure
        return studentRepository.updateStudent(userSchoolId, user_id, lang, responsible_user_id, request);
    }
}
