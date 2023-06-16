package com.biopark.cpa.controllers.auth;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.auth.AuthenticationResponse;
import com.biopark.cpa.form.auth.LoginRequest;
import com.biopark.cpa.services.security.AuthenticationService;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthController {
    
    private final AuthenticationService service;

    @PostMapping("/public/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse authenticationResponse = service.authenticate(request);
        return ResponseEntity.status(authenticationResponse.getStatus()).body(authenticationResponse);
    }

    @PostMapping("/public/email")
    public ResponseEntity<GenericDTO> sendEmail(@RequestBody LoginRequest request) throws IOException, MessagingException{
        GenericDTO response = service.recoverPassword(request.getEmail());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping("/public/resetPassword")
    public ResponseEntity<GenericDTO> resetPassword(@RequestBody LoginRequest request, @RequestParam("token") String token){
        GenericDTO response = service.resetPassword(request.getPassword(), token);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateToken(@RequestHeader("Authorization") String token){
        AuthenticationResponse response = service.authenticate(token);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Boolean> logout(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(service.logout(token));
    }
}
