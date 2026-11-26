package com.sportsmatch.presentation.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminSessionController {

    @GetMapping("/sesion")
    public ResponseEntity<Void> validarSesion() {
        return ResponseEntity.noContent().build();
    }
}
