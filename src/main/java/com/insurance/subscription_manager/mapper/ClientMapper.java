package com.insurance.subscription_manager.mapper;

import org.springframework.stereotype.Component;

import com.insurance.subscription_manager.dto.ClientRequestDTO;
import com.insurance.subscription_manager.dto.ClientResponseDTO;
import com.insurance.subscription_manager.entity.Client;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequestDTO dto) {
        return Client.builder()
                .nom(dto.nom())
                .email(dto.email())
                .telephone(dto.telephone())
                .build();
    }

    public ClientResponseDTO toResponse(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getNom(),
                client.getEmail(),
                client.getTelephone()
        );
    }
}
