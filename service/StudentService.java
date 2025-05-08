package com.monarchsolutions.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.student.CreateStudentRequest;
import com.monarchsolutions.sms.dto.student.UpdateStudentRequest;
import com.monarchsolutions.sms.repository.StudentRepository;

import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResult<Map<String,Object>> getStudentsList(
        Long tokenSchoolId,  
        Long student_id,
        String full_name,
        String payment_reference,
        String generation,
        String grade_group,
        Boolean status_filter,
        String lang,
        int page,
        int size,
        Boolean exportAll,
        String order_by,
        String order_dir
    ) throws Exception {
        // If tokenSchoolId is not null, the SP will filter students by school.
        return studentRepository.getStudentsList(
            tokenSchoolId,  
            student_id,
            full_name,
            payment_reference,
            generation,
            grade_group,
            status_filter,
            lang,
            page,
            size,
            exportAll,
            order_by,
            order_dir
        );
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
