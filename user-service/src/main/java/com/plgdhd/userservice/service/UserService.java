package com.plgdhd.userservice.service;

import com.plgdhd.userservice.dto.UserRequestDTO;
import com.plgdhd.userservice.dto.UserResponseDTO;
import com.plgdhd.userservice.exception.UserException;
import com.plgdhd.userservice.model.User;
import com.plgdhd.userservice.repository.UserRepository;
import com.plgdhd.userservice.mapper.UserMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO createUser(UserRequestDTO userDTO) {
        if(userRepository.existsByEmail(userDTO.getEmail())){
            throw new UserException("Email already in use");
        };
        User user = userMapper.toEntity(userDTO);
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    public Page<UserResponseDTO> getAll(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size)).map(userMapper::toResponseDTO);
    }

    public UserResponseDTO getById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return userMapper.toResponseDTO(user);
    }

    public UserResponseDTO getByEmail(String email) {
        User user = userRepository.findByEmailNative(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return userMapper.toResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUser(long id, UserRequestDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setBirthDate(userDTO.getBirthDate());
        user.setEmail(userDTO.getEmail());
        return  userMapper.toResponseDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        userRepository.delete(user);
    }

}
