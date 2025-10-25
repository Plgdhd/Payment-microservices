package com.plgdhd.user_service.controller;

import com.plgdhd.user_service.dto.CardInfoRequestDTO;
import com.plgdhd.user_service.dto.CardInfoResponseDTO;
import com.plgdhd.user_service.dto.UserResponseDTO;
import com.plgdhd.user_service.service.CardInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/cards")
public class CardInfoController {

    private final CardInfoService cardInfoService;

    @Autowired
    public CardInfoController(CardInfoService cardInfoService) {
        this.cardInfoService = cardInfoService;
    }

    @PostMapping
    public ResponseEntity<CardInfoResponseDTO> createCard(@RequestBody CardInfoRequestDTO cardInfoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardInfoService.createCardInfo(cardInfoDTO));
    }
    //TODO tests creating
    @GetMapping
    public ResponseEntity<List<CardInfoResponseDTO>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CardInfoResponseDTO> cards = cardInfoService.findAllCardInfo(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(cards.getContent());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardInfoResponseDTO> updateCardInfo(
            @PathVariable long id,
            @RequestBody CardInfoRequestDTO cardInfoDTO
    ){
        CardInfoResponseDTO updated = cardInfoService.updateCardInfo(id, cardInfoDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCardInfo(@PathVariable long id) {
        cardInfoService.deleteCardInfoById(id);
        return ResponseEntity.noContent().build();
    }
}
