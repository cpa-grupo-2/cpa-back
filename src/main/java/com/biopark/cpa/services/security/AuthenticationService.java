package com.biopark.cpa.services.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.auth.AuthenticationResponse;
import com.biopark.cpa.entities.token.BlackListToken;
import com.biopark.cpa.entities.user.User;
import com.biopark.cpa.form.auth.LoginRequest;
import com.biopark.cpa.repository.auth.BlackListTokenRepository;
import com.biopark.cpa.repository.pessoas.UserRepository;
import com.biopark.cpa.services.utils.EmailService;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BlackListTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthenticationResponse authenticate(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));
        } catch (Exception e) {
            return AuthenticationResponse.builder().token(null).status(HttpStatus.FORBIDDEN).level(null).build();
        }

        var user = repository.findByEmail(request.getEmail());

        if (!user.isPresent()) {
            return AuthenticationResponse.builder().token(null).status(HttpStatus.FORBIDDEN).level(null).build();
        }

        var jwtToken = jwtService.generateToken(user.get());
        var level = getLevelAccess(jwtToken);
        return AuthenticationResponse.builder().token(jwtToken).status(HttpStatus.OK).level(level).build();
    }

    public AuthenticationResponse authenticate(String token) {
        token = token.substring(7);
        return AuthenticationResponse.builder().token(token).status(HttpStatus.OK).level(getLevelAccess(token)).build();
    }

    private String getLevelAccess(String token) {
        return jwtService.extractUserLevel(token);
    }

    public Boolean logout(String token) {
        token = token.substring(7);
        var expirationTime = jwtService.extractExpiration(token);
        BlackListToken tokenObj = BlackListToken.builder().token(token).dateExpiration(expirationTime).build();
        tokenRepository.save(tokenObj);
        SecurityContextHolder.clearContext();
        return true;
    }

    public GenericDTO recoverPassword(String email) throws IOException, MessagingException{
        try{
            var userDB = repository.findByEmail(email);
            
            if (!userDB.isPresent()) {
                return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem("Email informado não encontrado").build();
            }

            User user = userDB.get();

            String token = jwtService.generateTokenPassword(user);

            emailService.montaEmail(token, email, user.getName());

            return GenericDTO.builder().status(HttpStatus.OK).mensagem("Em alguns instantes cheque sua caixa de mensagem").build();
        }catch(Exception e){
            return GenericDTO.builder().status(HttpStatus.INTERNAL_SERVER_ERROR).mensagem("Erro: "+e.getMessage()).build();
        }
    }

    public GenericDTO resetPassword(String password, String token) {
        if (!jwtService.isTokenValid(token)) {
            return GenericDTO.builder().status(HttpStatus.FORBIDDEN).mensagem("Token inválido ou expirado").build();
        }

        Long id = jwtService.extractId(token);
        var userDB = repository.findById(id);

        if (!userDB.isPresent()) {
            return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem("Não conseguimos encontrar seu usuario")
                    .build();
        }

        User user = userDB.get();
        user.setPassword(passwordEncoder.encode(password));

        repository.save(user);

        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Nova senha salva com sucesso!").build();
    }
}
