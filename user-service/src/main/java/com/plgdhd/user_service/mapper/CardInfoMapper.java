package com.plgdhd.user_service.mapper;

import com.plgdhd.user_service.dto.CardInfoRequestDTO;
import com.plgdhd.user_service.dto.CardInfoResponseDTO;
import com.plgdhd.user_service.model.CardInfo;
import org.mapstruct.Mapper;

import javax.smartcardio.Card;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {

    CardInfoRequestDTO toRequestDTO(CardInfo cardInfo);
    CardInfoResponseDTO toResponseDTO(CardInfo cardInfo);
    CardInfo toEntity(CardInfoResponseDTO cardDTO);
    CardInfo toEntity(CardInfoRequestDTO cardDTO);

//    List<CardDTO> toDTOList(List<Card> cards);
}
