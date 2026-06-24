package com.insurance.subscription_manager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * End-to-end test of the senior-bonus transition:
 * client -> quote (> 10 000 => PENDING_MANAGER) -> contract refused (422)
 *        -> approve -> contract created (201).
 */
@SpringBootTest
@AutoConfigureMockMvc
class SubscriptionFlowIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void fullFlow_pendingManager_thenApproved_thenContractCreated() throws Exception {
        // 1. Create a client.
        MvcResult clientResult = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nom":"Amine El Idrissi","email":"amine@exemple.com","telephone":"+212600000000"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long clientId = readId(clientResult);

        // 2. Create a quote above 10 000 -> must land in PENDING_MANAGER.
        MvcResult quoteResult = mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientId\":" + clientId + ",\"produitId\":1,\"montant\":12500}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("PENDING_MANAGER"))
                .andReturn();
        long quoteId = readId(quoteResult);

        // 3. Trying to create a contract now is refused with 422.
        mockMvc.perform(post("/api/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quoteId\":" + quoteId + ",\"dateEffet\":\"2030-01-01\"}"))
                .andExpect(status().isUnprocessableEntity());

        // 4. Approve the quote.
        mockMvc.perform(post("/api/quotes/" + quoteId + "/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("APPROVED"));

        // 5. Now the contract can be created.
        mockMvc.perform(post("/api/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quoteId\":" + quoteId + ",\"dateEffet\":\"2030-01-01\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroContrat").exists());
    }

    @Test
    void createClient_withInvalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nom":"Test","email":"not-an-email","telephone":"0600"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listClients_returnsOk() throws Exception {
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk());
    }

    private long readId(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }
}
