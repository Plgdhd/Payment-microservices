package com.plgdhd.userservice.service;

import com.plgdhd.userservice.dto.UserRequestDTO;
import com.plgdhd.userservice.dto.UserResponseDTO;
import com.plgdhd.userservice.exception.UserException;
import com.plgdhd.userservice.mapper.UserMapper;
import com.plgdhd.userservice.model.User;
import com.plgdhd.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @InjectMocks private UserService userService;

    private User user;
    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    private final long TEST_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(TEST_USER_ID);
        user.setName("Pavel");
        user.setSurname("Beloded");
        user.setEmail("oleg@mail.com");
        user.setBirthDate(LocalDate.of(2006, 1, 1));

        requestDTO = new UserRequestDTO();
        requestDTO.setName("Pavel");
        requestDTO.setSurname("Beloded");
        requestDTO.setEmail("oleg@mail.com");
        requestDTO.setBirthDate(LocalDate.of(2006, 1, 1));

        responseDTO = new UserResponseDTO();
        responseDTO.setId(TEST_USER_ID);
        responseDTO.setName("Pavel");
        responseDTO.setSurname("Beloded");
        responseDTO.setEmail("oleg@mail.com");
        responseDTO.setBirthDate(LocalDate.of(2006, 1, 1));
    }

    @Test
    void createUser_success() {
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.createUser(requestDTO);

        assertThat(result.getId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getEmail()).isEqualTo(requestDTO.getEmail());
        assertThat(result.getName()).isEqualTo(requestDTO.getName());
        assertThat(result.getSurname()).isEqualTo(requestDTO.getSurname());
        verify(userRepository).save(user);
    }

    @Test
    void createUser_fail() {
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(requestDTO))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    void getById_success() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.getById(TEST_USER_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getEmail()).isEqualTo("oleg@mail.com");
        assertThat(result.getName()).isEqualTo("Pavel");
        assertThat(result.getSurname()).isEqualTo("Beloded");
    }

    @Test
    void getById_fail() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(TEST_USER_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found!");
    }

    @Test
    void updateUser_success() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.updateUser(TEST_USER_ID, requestDTO);

        assertThat(result.getId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getEmail()).isEqualTo("oleg@mail.com");
        assertThat(result.getName()).isEqualTo("Pavel");
        assertThat(result.getSurname()).isEqualTo("Beloded");
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_success() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));

        userService.deleteUser(TEST_USER_ID);

        verify(userRepository).delete(user);
    }

    @Test
    void getAll_success() {
        Page<User> usersPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(PageRequest.of(0, 5))).thenReturn(usersPage);
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        Page<UserResponseDTO> result = userService.getAll(0, 5);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("oleg@mail.com");
        assertThat(result.getContent().get(0).getName()).isEqualTo("Pavel");
        assertThat(result.getContent().get(0).getSurname()).isEqualTo("Beloded");
    }
}
