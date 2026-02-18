package com.plgdhd.userservice.controller;

import com.plgdhd.userservice.dto.UserRequestDTO;
import com.plgdhd.userservice.dto.UserResponseDTO;
import com.plgdhd.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("internal/users")
public class InternalUserController {

    private final UserService userService;

    @Autowired
    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    /*
     is it really need to create second controller only for communicating between services?
     is it a good style?
     */

//    @DeleteMapping("/{userId}")
//    public Mono<ResponseEntity<String>> deleteUserInternal(@PathVariable Long userId) {
//         userService.deleteUser(userId);
//         return Mono.just(ResponseEntity.ok("Deleting success"));
//    }
//
//    @GetMapping("/{userId}")
//    public Mono<ResponseEntity<UserResponseDTO>> getUserInternal(@PathVariable Long userId) {
//          UserResponseDTO user = userService.findById(userId);
//          if(user == null) return Mono.just(ResponseEntity.badRequest().body(new UserResponseDTO()));
//          return Mono.just(ResponseEntity.ok(user));
//    }
}
