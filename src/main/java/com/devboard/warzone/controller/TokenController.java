package com.devboard.warzone.controller;

import com.devboard.warzone.dto.TokenRequest;
import com.devboard.warzone.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Get a JWT token")
public class TokenController {

    private final JwtUtil jwtUtil;

    @PostMapping("/token")
    @Operation(summary = "Get a JWT token with a role")
    public ResponseEntity<?> getToken(@RequestBody TokenRequest request) {

        if (!request.getRole().matches("ADMIN|WRITER|VISITOR")) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Role must be ADMIN, WRITER or VISITOR"));
        }

        String token = jwtUtil.generateToken(
                request.getUsername(),
                request.getRole()
        );

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", request.getUsername(),
                "role", request.getRole()
        ));
    }
}