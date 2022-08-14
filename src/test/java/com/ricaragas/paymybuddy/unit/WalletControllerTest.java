package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.controller.WalletController;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.ricaragas.paymybuddy.controller.WalletController.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WalletController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WalletControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    WalletService walletService;

    @BeforeEach
    public void before_each() {
        var wallet = new Wallet();
            wallet.setConnections(List.of());
            wallet.setSentTransfers(List.of());
        when(walletService.getWalletForAuthenticatedUser()).thenReturn(wallet);
    }

    @Test
    public void when_get_transfer_page_then_success() throws Exception {
        mockMvc.perform(get(URL_TRANSFER))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("connections"))
                .andExpect(model().attributeExists("transfers"));
        verify(walletService).getWalletForAuthenticatedUser();
        verifyNoMoreInteractions(walletService);
    }

    @Test
    public void when_get_new_connection_page_then_success() throws Exception {
        mockMvc.perform(get(URL_NEW_CONNECTION))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("new-connection"));
    }

    @Test
    public void when_post_new_connection_then_redirects() throws Exception {
        mockMvc.perform(post(URL_NEW_CONNECTION)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("email", "an@email.com"))
                .andExpect(status().is3xxRedirection());
        verify(walletService, times(1)).addConnection(anyString());
    }

    @Test
    public void when_get_pay_then_success() throws Exception {
        mockMvc.perform(get(URL_PAY))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("pay"))
                .andExpect(model().attributeExists("connections"));
    }

    @Test
    public void when_post_pay_then_redirects() throws Exception {
        mockMvc.perform(post(URL_PAY)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("to", "1")
                        .param("amount", "10.20")
                        .param("description", "Any reason"))
                .andExpect(status().is3xxRedirection());
        verify(walletService, times(1)).pay(1L, "Any reason", 10.2);
    }

    @Test
    public void when_get_pay_missing_balance_then_button_shown() throws Exception {
        mockMvc.perform(get(URL_PAY)
                        .param("to", "1")
                        .param("amount", "10.20")
                        .param("description", "Any reason")
                        .param("balance", "0.20"))
                .andExpect(status().is2xxSuccessful());
    }

}
