package com.example.jwtApp.services;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jwtApp.dtopack.LoginRequest;
import com.example.jwtApp.dtopack.RegisterRequest;
import com.example.jwtApp.entity.AppUser;
import com.example.jwtApp.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passEncode;
    private final AuthenticationManager authManage;
    private final JwtEncoder jwtEncode;

    @Value("${jwt.expiration}")
    private long expiration;

    public AuthService(
            UserRepository userRepo,
            PasswordEncoder passEncode,
            AuthenticationManager authManage,
            JwtEncoder jwtEncode) {

        this.userRepo = userRepo;
        this.passEncode = passEncode;
        this.authManage = authManage;
        this.jwtEncode = jwtEncode;
    }

    // 1. User Registration
    @Transactional
    public void register(RegisterRequest registerReq) {

        String username = registerReq.username().trim();
        String email = registerReq.email().trim()
                .toLowerCase(Locale.ROOT);

        if (userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Username already exists");
        }

        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email already exists");
        }

        AppUser user = new AppUser(
                username,
                email,
                passEncode.encode(registerReq.password()),
                "ROLE_USER"
        );

        userRepo.save(user);
    }

    // 2. User Login and JWT Generation
    public String login(LoginRequest loginReq) {

        Authentication authentication =
                authManage.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginReq.username().trim(),
                                loginReq.password()
                        )
                );

        Instant now = Instant.now();

        // Extract User Authorities
        List<String> authorities =
                authentication.getAuthorities()
                        .stream()
                        .map(authority -> authority.getAuthority())
                        .toList();

        // JWT Claims
        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer("student-jwt-portal")
                        .issuedAt(now)
                        .expiresAt(now.plusSeconds(expiration))
                        .subject(authentication.getName())
                        .claim("authorities", authorities)
                        .build();

        // JWT Header
        JwsHeader header =
                JwsHeader.with(MacAlgorithm.HS256)
                        .build();

        // Generate Signed JWT
        return jwtEncode.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}