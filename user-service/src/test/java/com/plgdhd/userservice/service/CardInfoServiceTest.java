package com.plgdhd.userservice.service;

import com.plgdhd.userservice.dto.CardInfoRequestDTO;
import com.plgdhd.userservice.dto.CardInfoResponseDTO;
import com.plgdhd.userservice.mapper.CardInfoMapper;
import com.plgdhd.userservice.model.CardInfo;
import com.plgdhd.userservice.model.User;
import com.plgdhd.userservice.repository.CardInfoRepository;
import com.plgdhd.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardInfoServiceTest {

    @Mock private CardInfoRepository cardInfoRepository;
    @Mock private CardInfoMapper cardInfoMapper;
    @Mock private UserRepository userRepository;
    @InjectMocks private CardInfoService cardInfoService;

    private User user;
    private CardInfo card;
    private CardInfoRequestDTO requestDTO;
    private CardInfoResponseDTO responseDTO;

    private final long TEST_USER_ID = 1L;
    private final long TEST_CARD_ID = 1L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(TEST_USER_ID);

        card = new CardInfo();
        card.setId(TEST_CARD_ID);
        card.setHolder("Pavel");
        card.setNumber("1111-2222-3333-4444");
        card.setUser(user);

        requestDTO = new CardInfoRequestDTO();
        requestDTO.setHolder("Pavel");
        requestDTO.setNumber("1111-2222-3333-4444");
        requestDTO.setUserId(TEST_USER_ID);

        responseDTO = new CardInfoResponseDTO();
        responseDTO.setId(TEST_CARD_ID);
        responseDTO.setHolder("Pavel");
        responseDTO.setNumber("1111-2222-3333-4444");
        responseDTO.setUserId(TEST_USER_ID);
    }

    @Test
    void createCardInfo_success() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(cardInfoMapper.toEntity(requestDTO)).thenReturn(card);
        when(cardInfoRepository.save(card)).thenReturn(card);
        when(cardInfoMapper.toResponseDTO(card)).thenReturn(responseDTO);

        CardInfoResponseDTO result = cardInfoService.createCardInfo(requestDTO);

        assertThat(result.getHolder()).isEqualTo("Pavel");
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        verify(cardInfoRepository).save(card);
    }

    @Test
    void updateCardInfo_success() {
        when(cardInfoRepository.findById(TEST_CARD_ID)).thenReturn(Optional.of(card));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(cardInfoRepository.save(card)).thenReturn(card);
        when(cardInfoMapper.toResponseDTO(card)).thenReturn(responseDTO);

        CardInfoResponseDTO result = cardInfoService.updateCardInfo(TEST_CARD_ID, requestDTO);

        assertThat(result.getHolder()).isEqualTo("Pavel");
        assertThat(result.getNumber()).isEqualTo("1111-2222-3333-4444");
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        verify(cardInfoRepository).save(card);
    }

    @Test
    void deleteCardInfo_success() {
        when(cardInfoRepository.findById(TEST_CARD_ID)).thenReturn(Optional.of(card));

        cardInfoService.deleteCardInfoById(TEST_CARD_ID);

        verify(cardInfoRepository).delete(card);
    }

    @Test
    void findAllCardInfo_success() {
        Page<CardInfo> page = new PageImpl<>(List.of(card));
        when(cardInfoRepository.findAll(PageRequest.of(0, 5))).thenReturn(page);
        when(cardInfoMapper.toResponseDTO(card)).thenReturn(responseDTO);

        Page<CardInfoResponseDTO> result = cardInfoService.findAllCardInfo(0, 5);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getHolder()).isEqualTo("Pavel");
    }

    @Test
    void findAllUserCards_success() {
        when(cardInfoRepository.findByUserId(TEST_USER_ID)).thenReturn(List.of(card));
        when(cardInfoMapper.toResponseDTO(card)).thenReturn(responseDTO);

        List<CardInfoResponseDTO> result = cardInfoService.findAllUserCards(TEST_USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.get(0).getHolder()).isEqualTo("Pavel");
    }
}
