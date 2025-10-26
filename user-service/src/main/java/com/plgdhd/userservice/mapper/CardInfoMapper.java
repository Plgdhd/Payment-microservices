package com.plgdhd.userservice.mapper;

import com.plgdhd.userservice.dto.CardInfoRequestDTO;
import com.plgdhd.userservice.dto.CardInfoResponseDTO;
import com.plgdhd.userservice.model.CardInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {

    CardInfoRequestDTO toRequestDTO(CardInfo cardInfo);
    CardInfoResponseDTO toResponseDTO(CardInfo cardInfo);
    CardInfo toEntity(CardInfoResponseDTO cardDTO);
    CardInfo toEntity(CardInfoRequestDTO cardDTO);

//    List<CardDTO> toDTOList(List<Card> cards);
}
