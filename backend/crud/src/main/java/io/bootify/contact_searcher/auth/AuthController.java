package io.bootify.contact_searcher.auth;

import io.bootify.contact_searcher.user.User;
import io.bootify.contact_searcher.user.UserDTO;
import io.bootify.contact_searcher.user.UserRepository;
import io.bootify.contact_searcher.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        Optional<User> u1 = userRepository.findByUsername(userDTO.getUsername());
        if (u1.isPresent()) {
            User user = u1.get();
            if (passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getId());
                String refreshToken = jwtUtil.generateRefreshToken(user.getId());
                return ResponseEntity.ok(new JwtResponse(token, refreshToken, user.getUsername(), user.getId()));
            }
        }
        return ResponseEntity.badRequest().body("Invalid username or password");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest refreshRequest) {
        if (jwtUtil.validateToken(refreshRequest.getRefreshToken())) {
            String strUserId = jwtUtil.getUserIdFromToken(refreshRequest.getRefreshToken());
            String newToken = jwtUtil.generateToken(Integer.parseInt(strUserId));
            String newRefreshToken = jwtUtil.generateRefreshToken(Integer.parseInt(strUserId));
            Optional<User> user = userRepository.findById(Integer.parseInt(strUserId));
            if (user.isPresent()) {
                return ResponseEntity.ok(new JwtResponse(newToken, newRefreshToken, user.get().getUsername(), user.get().getId()));
            }
            return ResponseEntity.badRequest().body("User not found");

        }

        return ResponseEntity.badRequest().body("Invalid refresh token");
    }
}
