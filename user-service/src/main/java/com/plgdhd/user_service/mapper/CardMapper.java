package com.plgdhd.user_service.mapper;

import com.plgdhd.user_service.dto.CardInfoDTO;
import com.plgdhd.user_service.model.CardInfo;
import org.mapstruct.Mapper;

import javax.smartcardio.Card;

@Mapper(componentModel = "spring")
public interface CardMapper {

    CardInfoDTO toDTO(Card card);
    CardInfo toEntity(CardInfoDTO cardDTO);

//    List<CardDTO> toDTOList(List<Card> cards);
}
