package com.insurance.subscription_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.insurance.subscription_manager.dto.QuoteRequestDTO;
import com.insurance.subscription_manager.dto.QuoteResponseDTO;
import com.insurance.subscription_manager.entity.Client;
import com.insurance.subscription_manager.entity.Product;
import com.insurance.subscription_manager.entity.Quote;
import com.insurance.subscription_manager.enums.QuoteStatus;
import com.insurance.subscription_manager.exception.BusinessRuleException;
import com.insurance.subscription_manager.mapper.QuoteMapper;
import com.insurance.subscription_manager.repository.ClientRepository;
import com.insurance.subscription_manager.repository.ProductRepository;
import com.insurance.subscription_manager.repository.QuoteRepository;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock private QuoteRepository quoteRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private ProductRepository productRepository;
    // Real mapper: no behaviour worth mocking, keeps assertions meaningful.
    private final QuoteMapper quoteMapper = new QuoteMapper();

    private QuoteService quoteService;

    private Client client;
    private Product product;

    void setUp() {
        quoteService = new QuoteService(quoteRepository, clientRepository, productRepository, quoteMapper);
        client = Client.builder().id(1L).nom("Amine").email("a@b.com").telephone("0600").build();
        product = Product.builder().id(2L).code("AUTO").libelle("Auto").type("VEHICULE").build();
    }

    @Test
    void create_amountUnderOrEqualThreshold_isAutoApproved() {
        setUp();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(quoteRepository.save(any(Quote.class))).thenAnswer(inv -> inv.getArgument(0));

        QuoteResponseDTO result = quoteService.create(
                new QuoteRequestDTO(1L, 2L, new BigDecimal("10000")));

        assertThat(result.statut()).isEqualTo(QuoteStatus.APPROVED);
    }

    @Test
    void create_amountAboveThreshold_requiresManagerApproval() {
        setUp();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(quoteRepository.save(any(Quote.class))).thenAnswer(inv -> inv.getArgument(0));

        QuoteResponseDTO result = quoteService.create(
                new QuoteRequestDTO(1L, 2L, new BigDecimal("10000.01")));

        assertThat(result.statut()).isEqualTo(QuoteStatus.PENDING_MANAGER);
    }

    @Test
    void approve_pendingQuote_becomesApproved() {
        setUp();
        Quote pending = Quote.builder().id(5L).client(client).product(product)
                .montant(new BigDecimal("12500")).statut(QuoteStatus.PENDING_MANAGER).build();
        when(quoteRepository.findById(5L)).thenReturn(Optional.of(pending));

        QuoteResponseDTO result = quoteService.approve(5L);

        assertThat(result.statut()).isEqualTo(QuoteStatus.APPROVED);
    }

    @Test
    void approve_alreadyApprovedQuote_isRejected() {
        setUp();
        Quote approved = Quote.builder().id(5L).client(client).product(product)
                .montant(new BigDecimal("12500")).statut(QuoteStatus.APPROVED).build();
        when(quoteRepository.findById(5L)).thenReturn(Optional.of(approved));

        assertThatThrownBy(() -> quoteService.approve(5L))
                .isInstanceOf(BusinessRuleException.class);
    }
}
