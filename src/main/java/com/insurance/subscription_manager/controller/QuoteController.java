package com.insurance.subscription_manager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.insurance.subscription_manager.dto.QuoteRequestDTO;
import com.insurance.subscription_manager.dto.QuoteResponseDTO;
import com.insurance.subscription_manager.enums.QuoteStatus;
import com.insurance.subscription_manager.service.QuoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<QuoteResponseDTO> create(@Valid @RequestBody QuoteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quoteService.create(request));
    }

    // Optional status filter: GET /api/quotes?statut=PENDING_MANAGER
    @GetMapping
    public ResponseEntity<List<QuoteResponseDTO>> findAll(
            @RequestParam(required = false) QuoteStatus statut) {
        return ResponseEntity.ok(quoteService.findAll(statut));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<QuoteResponseDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(quoteService.approve(id));
    }
}
