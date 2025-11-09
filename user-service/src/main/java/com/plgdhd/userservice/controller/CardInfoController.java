package com.plgdhd.userservice.controller;

import com.plgdhd.userservice.dto.CardInfoRequestDTO;
import com.plgdhd.userservice.dto.CardInfoResponseDTO;
import com.plgdhd.userservice.service.CardInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
