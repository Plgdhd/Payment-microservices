package com.plgdhd.user_service.service;

import com.plgdhd.user_service.dto.CardInfoRequestDTO;
import com.plgdhd.user_service.dto.CardInfoResponseDTO;
import com.plgdhd.user_service.mapper.CardInfoMapper;
import com.plgdhd.user_service.model.CardInfo;
import com.plgdhd.user_service.repository.CardInfoRepository;
import com.plgdhd.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CardInfoMapper cardInfoMapper;


    @Autowired
    public CardInfoService(CardInfoRepository cardInfoRepository,
                           CardInfoMapper cardInfoMapper,
                           UserRepository userRepository) {

        this.cardInfoRepository = cardInfoRepository;
        this.cardInfoMapper = cardInfoMapper;
        this.userRepository = userRepository;
    }

    public CardInfoResponseDTO createCardInfo(CardInfoRequestDTO cardInfoDTO) {
        CardInfo cardInfo = cardInfoMapper.toEntity(cardInfoDTO);
        cardInfo.setExpirationDate(LocalDate.now().plusYears(5));
        return cardInfoMapper.toResponseDTO(cardInfoRepository.save(cardInfo));
    }

    public Page<CardInfoResponseDTO> findAllCardInfo(int page, int size) {
        return cardInfoRepository.findAll(PageRequest.of(page, size)).map(cardInfoMapper::toResponseDTO);
    }

    public List<CardInfoResponseDTO> findAllUserCards(long userId) {
        return cardInfoRepository.findByUserId(userId)
                .stream()
                .map(cardInfoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public CardInfoResponseDTO updateCardInfo(long id, CardInfoRequestDTO cardInfoDTO) {
        CardInfo cardInfo = cardInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card id " + id + " not found!"));
        cardInfo.setNumber(cardInfoDTO.getNumber());
        cardInfo.setHolder(cardInfoDTO.getHolder());
        cardInfo.setUser(userRepository.findById(cardInfoDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User id " + cardInfoDTO.getUserId() + " not found!")));
        return cardInfoMapper.toResponseDTO(cardInfoRepository.save(cardInfo));
    }

    @Transactional
    public void deleteCardInfoById(long id) {
        CardInfo cardInfo = cardInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card id " + id + " not found!"));
        cardInfoRepository.delete(cardInfo);
    }

}
