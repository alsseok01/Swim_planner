package org.hknu.swim_planer.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hknu.swim_planer.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<MemberResponseDto> login(@RequestBody LoginRequest request) {
        MemberResponseDto response = authService.login(request.getName());
        return ResponseEntity.ok(response);
    }

    @Getter
    public static class LoginRequest {
        private String name;
    }
}