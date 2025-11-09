package com.plgdhd.userservice.integration;

import com.plgdhd.userservice.dto.CardInfoRequestDTO;
import com.plgdhd.userservice.dto.CardInfoResponseDTO;
import com.plgdhd.userservice.dto.UserRequestDTO;
import com.plgdhd.userservice.dto.UserResponseDTO;
import com.plgdhd.userservice.service.CardInfoService;
import com.plgdhd.userservice.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CardInfoServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CardInfoService cardInfoService;

    @Autowired
    private CacheManager cacheManager;

    private Long createdUserId;
    private Long createdCardId;

    private static final String CARD_HOLDER = "Alexei Ivaniuk";
    private static final String CARD_NUMBER_PREFIX = "1234 5678 9012 ";

    @BeforeEach
    void setUp() {
        cacheManager.getCache("users").clear();
        cacheManager.getCache("cards").clear();
        createdUserId = null;
        createdCardId = null;
    }

    @Test
    @Order(1)
    void testCreateUserForCard() {
        UserRequestDTO userDTO = new UserRequestDTO();
        userDTO.setEmail("oleg_" + UUID.randomUUID() + "@mail.com");
        userDTO.setName("Alexei");
        userDTO.setSurname("Ivaniuk");
        userDTO.setBirthDate(LocalDate.of(1999, 5, 15));

        UserResponseDTO user = userService.createUser(userDTO);
        createdUserId = user.getId();

        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo(userDTO.getEmail());
    }

    @Test
    @Order(2)
    void testCreateCardInfo() {
        createUserForTest();

        CardInfoRequestDTO dto = new CardInfoRequestDTO();
        dto.setNumber(generateUniqueCardNumber());
        dto.setHolder(CARD_HOLDER);
        dto.setUserId(createdUserId);

        CardInfoResponseDTO card = cardInfoService.createCardInfo(dto);
        createdCardId = card.getId();

        assertThat(card).isNotNull();
        assertThat(card.getHolder()).isEqualTo(CARD_HOLDER);
    }

    @Test
    @Order(3)
    void testFindAllUserCards() {
        createUserAndCardForTest();

        List<CardInfoResponseDTO> cards = cardInfoService.findAllUserCards(createdUserId);

        assertThat(cards).isNotEmpty();
        assertThat(cards.get(0).getHolder()).isEqualTo(CARD_HOLDER);
    }

    @Test
    @Order(4)
    void testUpdateCardInfo() {
        createUserAndCardForTest();

        CardInfoRequestDTO update = new CardInfoRequestDTO();
        update.setNumber(generateUniqueCardNumber());
        update.setHolder("Alex Ivaniuk");
        update.setUserId(createdUserId);

        CardInfoResponseDTO updated = cardInfoService.updateCardInfo(createdCardId, update);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(createdCardId);
        assertThat(updated.getNumber()).isEqualTo(update.getNumber());
        assertThat(updated.getHolder()).isEqualTo("Alex Ivaniuk");
    }

    @Test
    @Order(5)
    void testDeleteCardInfo() {
        createUserAndCardForTest();

        cardInfoService.deleteCardInfoById(createdCardId);

        Exception exception = Assertions.assertThrows(Exception.class, () ->
                cardInfoService.updateCardInfo(createdCardId, new CardInfoRequestDTO()));

        assertThat(exception.getMessage()).containsIgnoringCase("not found");
    }


    private void createUserForTest() {
        if (createdUserId == null) {
            UserRequestDTO userDTO = new UserRequestDTO();
            userDTO.setEmail("oleg_" + UUID.randomUUID() + "@mail.com");
            userDTO.setName("Alexei");
            userDTO.setSurname("Ivaniuk");
            userDTO.setBirthDate(LocalDate.of(1999, 5, 15));
            UserResponseDTO user = userService.createUser(userDTO);
            createdUserId = user.getId();
        }
    }

    private void createUserAndCardForTest() {
        createUserForTest();
        if (createdCardId == null) {
            CardInfoRequestDTO dto = new CardInfoRequestDTO();
            dto.setNumber(generateUniqueCardNumber());
            dto.setHolder(CARD_HOLDER);
            dto.setUserId(createdUserId);
            CardInfoResponseDTO card = cardInfoService.createCardInfo(dto);
            createdCardId = card.getId();
        }
    }

    private String generateUniqueCardNumber() {
        int suffix = (int) (Math.random() * 9000) + 1000;
        return CARD_NUMBER_PREFIX + suffix;
    }
}
