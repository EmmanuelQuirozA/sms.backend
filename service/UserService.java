package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.user.UserListDTO;
import com.monarchsolutions.sms.dto.user.CreateUserRequest;
import com.monarchsolutions.sms.dto.user.UpdateUserRequest;
import com.monarchsolutions.sms.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<UserListDTO> getUsersList(Long tokenSchoolId, String lang, int status_filter) {
        // If tokenSchoolId is not null, the SP will filter users by school.
        return userRepository.getUsersList(tokenSchoolId, lang, status_filter);
    }

    public String createUser(Long userSchoolId, String lang, Long responsible_user_id, CreateUserRequest request) throws Exception {
        // Hash the password before storing it
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashedPassword);
        String jsonResponse = userRepository.createUser(request, userSchoolId, lang, responsible_user_id);

        // Call the repository method that converts the request to JSON and executes the stored procedure
        return jsonResponse;
    }

    public String updateUser(Long userSchoolId, String lang, Long user_id, Long responsible_user_id, UpdateUserRequest request) throws Exception {
        // Call the repository method that converts the request to JSON and executes the stored procedure
        return userRepository.updateUser(request, userSchoolId, user_id, lang, responsible_user_id);
    }

    public String changeUserStatus(Integer userId, String lang, Long tokenSchoolId, Long responsible_user_id) throws Exception {
        // First, perform the change (this should throw an exception if the action fails)
        String jsonResponse = userRepository.changeUserStatus(userId, lang, tokenSchoolId, responsible_user_id);

        return jsonResponse;
    }
}
