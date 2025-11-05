package com.plgdhd.userservice.controller;

import com.plgdhd.userservice.dto.CardInfoResponseDTO;
import com.plgdhd.userservice.dto.UserRequestDTO;
import com.plgdhd.userservice.dto.UserResponseDTO;
import com.plgdhd.userservice.service.CardInfoService;
import com.plgdhd.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CardInfoService cardInfoService;

    @Autowired
    public UserController(UserService userService, CardInfoService cardInfoService) {
        this.userService = userService;
        this.cardInfoService = cardInfoService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDTO));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserResponseDTO> users = userService.getAll(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(users.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable long id) {
        UserResponseDTO user = userService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<List<CardInfoResponseDTO>> getUserCards(@PathVariable long id) {
        List<CardInfoResponseDTO> userCards = cardInfoService.findAllUserCards(id);
        return ResponseEntity.status(HttpStatus.OK).body(userCards);
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@RequestParam String email) {
        UserResponseDTO user = userService.getByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable long id,
            @RequestBody UserRequestDTO userDTO) {

        UserResponseDTO updated = userService.updateUser(id, userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
