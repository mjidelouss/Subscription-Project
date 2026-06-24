package com.insurance.subscription_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.insurance.subscription_manager.dto.ContractCreateDTO;
import com.insurance.subscription_manager.dto.ContractResponseDTO;
import com.insurance.subscription_manager.entity.Client;
import com.insurance.subscription_manager.entity.Contract;
import com.insurance.subscription_manager.entity.Product;
import com.insurance.subscription_manager.entity.Quote;
import com.insurance.subscription_manager.enums.QuoteStatus;
import com.insurance.subscription_manager.exception.BusinessRuleException;
import com.insurance.subscription_manager.exception.DuplicateResourceException;
import com.insurance.subscription_manager.mapper.ContractMapper;
import com.insurance.subscription_manager.repository.ContractRepository;
import com.insurance.subscription_manager.repository.QuoteRepository;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock private ContractRepository contractRepository;
    @Mock private QuoteRepository quoteRepository;
    private final ContractMapper contractMapper = new ContractMapper();

    private ContractService contractService;
    private Client client;
    private Product product;

    @BeforeEach
    void setUp() {
        contractService = new ContractService(contractRepository, quoteRepository, contractMapper);
        client = Client.builder().id(1L).nom("Amine").email("a@b.com").telephone("0600").build();
        product = Product.builder().id(2L).code("AUTO").libelle("Auto").type("VEHICULE").build();
    }

    private Quote quoteWithStatus(QuoteStatus status) {
        return Quote.builder().id(9L).client(client).product(product)
                .montant(new BigDecimal("12500")).statut(status).build();
    }

    @Test
    void create_fromApprovedQuote_succeeds() {
        when(quoteRepository.findById(9L)).thenReturn(Optional.of(quoteWithStatus(QuoteStatus.APPROVED)));
        when(contractRepository.existsByQuoteId(9L)).thenReturn(false);
        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> inv.getArgument(0));

        ContractResponseDTO result = contractService.create(
                new ContractCreateDTO(9L, LocalDate.now()));

        assertThat(result.numeroContrat()).startsWith("CTR-");
        assertThat(result.quoteId()).isEqualTo(9L);
    }

    @Test
    void create_fromPendingQuote_isRejected() {
        when(quoteRepository.findById(9L)).thenReturn(Optional.of(quoteWithStatus(QuoteStatus.PENDING_MANAGER)));

        assertThatThrownBy(() -> contractService.create(new ContractCreateDTO(9L, LocalDate.now())))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void create_whenContractAlreadyExists_isRejected() {
        when(quoteRepository.findById(9L)).thenReturn(Optional.of(quoteWithStatus(QuoteStatus.APPROVED)));
        when(contractRepository.existsByQuoteId(9L)).thenReturn(true);

        assertThatThrownBy(() -> contractService.create(new ContractCreateDTO(9L, LocalDate.now())))
                .isInstanceOf(DuplicateResourceException.class);
    }
}
