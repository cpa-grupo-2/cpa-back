package com.biopark.cpa;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.biopark.cpa.entities.user.User;
import com.biopark.cpa.entities.user.enums.Level;
import com.biopark.cpa.entities.user.enums.Role;
import com.biopark.cpa.repository.pessoas.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StartApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${adminAccount}")
    String adminEmail;
    @Value("${adminPassword}")
    String adminPass;
    @Value("${userAccount}")
    String userEmail;
    @Value("${userPassword}")
    String UserPass;

    @Override
    public void run(String... args) throws Exception {
        try{
            User userAdmin = User.builder()
                    .cpf("113.015.639-78")
                    .email(adminEmail)
                    .name("admin")
                    .level(Level.CPA)
                    .telefone("44998139378")
                    .role(Role.EXTERNO)
                    .password(passwordEncoder.encode(adminPass))
                    .build();

            userRepository.save(userAdmin);

            User user = User.builder()
                    .cpf("114.035.789-78")
                    .email(userEmail)
                    .name("user")
                    .level(Level.USER)
                    .telefone("546546546466")
                    .role(Role.EXTERNO)
                    .password(passwordEncoder.encode(UserPass))
                    .build();

            userRepository.save(user);
        }catch(Exception e){

        }
    }
}
