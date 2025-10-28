package com.plgdhd.userservice.integration;

import com.plgdhd.userservice.dto.CardInfoRequestDTO;
import com.plgdhd.userservice.dto.CardInfoResponseDTO;
import com.plgdhd.userservice.dto.UserRequestDTO;
import com.plgdhd.userservice.dto.UserResponseDTO;
import com.plgdhd.userservice.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    private static final String TEST_EMAIL = "oleg+" + System.currentTimeMillis() + "@mail.com";
    private static Long createdUserId;

    @BeforeEach
    void setUp() {
        if (cacheManager.getCache("users") != null) {
            cacheManager.getCache("users").clear();
        }
    }

    @Test
    @Order(1)
    void testCreateUser() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Alexei");
        dto.setSurname("Ivaniuk");
        dto.setEmail(TEST_EMAIL);
        dto.setBirthDate(LocalDate.of(1999, 5, 15));

        UserResponseDTO response = userService.createUser(dto);
        createdUserId = response.getId();

        assertThat(response.getId()).isNotNull();
        assertThat(response.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(response.getName()).isEqualTo("Alexei");
        assertThat(response.getSurname()).isEqualTo("Ivaniuk");
    }

    @Test
    @Order(2)
    void testUpdateUser() {
        UserRequestDTO updated = new UserRequestDTO();
        updated.setName("Alex");
        updated.setSurname("Ivaniuk");
        updated.setEmail(TEST_EMAIL);
        updated.setBirthDate(LocalDate.of(1999, 5, 15));

        UserResponseDTO result = userService.updateUser(createdUserId, updated);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Alex");
        assertThat(result.getSurname()).isEqualTo("Ivaniuk");
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @Order(3)
    void testDeleteUser() {
        userService.deleteUser(createdUserId);

        Assertions.assertThrows(RuntimeException.class, () ->
                userService.getById(createdUserId));
    }
}
