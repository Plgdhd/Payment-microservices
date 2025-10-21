package com.plgdhd.user_service;

import com.plgdhd.user_service.model.User;
import com.plgdhd.user_service.repository.UserRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class service {

    private final UserRepository userRepository;

    @Autowired
    public service(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createById(long id){
         userRepository.deleteById(id);
    }
}
