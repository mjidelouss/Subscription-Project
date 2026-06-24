package com.insurance.subscription_manager.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.insurance.subscription_manager.dto.ClientRequestDTO;
import com.insurance.subscription_manager.dto.ClientResponseDTO;
import com.insurance.subscription_manager.entity.Client;
import com.insurance.subscription_manager.exception.DuplicateResourceException;
import com.insurance.subscription_manager.mapper.ClientMapper;
import com.insurance.subscription_manager.repository.ClientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional
    public ClientResponseDTO create(ClientRequestDTO request) {
        // Business rule: impossible to create a client with an already-used email.
        if (clientRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Un client existe deja avec l'email " + request.email());
        }
        Client saved = clientRepository.save(clientMapper.toEntity(request));
        return clientMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ClientResponseDTO> findAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .toList();
    }
}
