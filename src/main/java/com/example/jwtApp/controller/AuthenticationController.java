
package com.example.jwtApp.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.jwtApp.dtopack.LoginRequest;
import com.example.jwtApp.dtopack.RegisterRequest;
import com.example.jwtApp.services.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

    private final AuthService authService;

    @Value("${jwt.expiration:3600}")
    private long expiration;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    // Constructor injection
    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    // =====================================
    // DISPLAY LOGIN PAGE
    // =====================================

    @GetMapping("/login")
    public String loginPage(Model model) {

        model.addAttribute(
                "loginRequest",
                new LoginRequest("", "")
        );

        return "login";
    }

    // =====================================
    // DISPLAY REGISTRATION PAGE
    // =====================================

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute(
                "registerRequest",
                new RegisterRequest("", "", "")
        );

        return "register";
    }

    // =====================================
    // PROCESS REGISTRATION
    // =====================================

    @PostMapping("/register")
    public String register(

            @Valid
            @ModelAttribute("registerRequest")
            RegisterRequest registerRequest,

            BindingResult result,

            Model model) {

        // Validate registration fields
        if (result.hasErrors()) {
            return "register";
        }

        try {

            // Save user through AuthService
            authService.register(registerRequest);

            // Redirect to login after successful registration
            return "redirect:/login?registered";

        } catch (IllegalArgumentException ex) {

            model.addAttribute(
                    "error",
                    ex.getMessage()
            );

            return "register";
        }
    }

    // =====================================
    // PROCESS LOGIN
    // =====================================

    @PostMapping("/login")
    public String login(

            @Valid
            @ModelAttribute("loginRequest")
            LoginRequest loginRequest,

            BindingResult result,

            HttpServletResponse response,

            Model model) {

        // Validate login fields
        if (result.hasErrors()) {

            model.addAttribute(
                    "error",
                    "Please enter valid credentials"
            );

            return "login";
        }

        try {

            // Authenticate user and generate JWT
            String token = authService.login(loginRequest);

            // Create JWT HttpOnly cookie
            ResponseCookie cookie = ResponseCookie
                    .from("JWT_TOKEN", token)
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(Duration.ofSeconds(expiration))
                    .build();

            // Send JWT cookie to browser
            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    cookie.toString()
            );

            // Redirect to dashboard
            return "redirect:/dashboard";

        } catch (Exception ex) {

            model.addAttribute(
                    "error",
                    "Invalid username or password"
            );

            return "login";
        }
    }

    // =====================================
    // LOGOUT
    // =====================================

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {

        // Remove JWT cookie from browser
        ResponseCookie cookie = ResponseCookie
                .from("JWT_TOKEN", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return "redirect:/login?logout";
    }
}
