package com.example.demo.controller;

import ch.qos.logback.core.net.ObjectWriter;
import com.example.demo.controller.dto.TransferirSaldoDTO;
import com.example.demo.domain.Conta;
import com.example.demo.exceptions.ErrorException;
import com.example.demo.exceptions.ErrorResponse;
import com.example.demo.service.ContaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@WebMvcTest(ContaController.class)
public class ContaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private Gson gson;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ContaService contaService;
    private Conta conta;
    private Conta outraConta;
    private ArrayList<Conta> contasList;

    @BeforeEach
    public void setUp() {
        this.createObjects();
    }

    @Test
    void quandoChamarOEndPointDeListarContasEntaoRetorneUmaListaDeContas() throws Exception {

        when(contaService.toList()).thenReturn(contasList);

        MvcResult result = mvc.perform(get("/conta/listarContas"))
            .andExpect(content().json(objectMapper.writeValueAsString(contasList)))
            .andExpect(status().isOk())
            .andReturn();

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    void quandoChamarOEndPointDeBuscarUmaContaEntaoRetorneUmObjetoConta() throws Exception {

        when(contaService.findById(anyLong())).thenReturn(conta);

        MvcResult result = mvc.perform(get("/conta/buscarConta/1"))
                .andExpect(content().json(objectMapper.writeValueAsString(conta)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    void quandoChamarOEndPointDeBuscarUmaContaEntaoRetorneUmErro() throws Exception {

        when(contaService.findById(2L)).thenThrow(new ErrorException("Conta não existe"));

        mvc.perform(get("/conta/buscarConta/2"))
                .andExpect(status().isBadRequest())
                .andExpect(resultThrow -> assertEquals("Conta não existe", Objects.requireNonNull(resultThrow.getResolvedException()).getMessage()))
                .andReturn();
    }

    @Test
    void quandoChamarOEndPointDeSalvarContaEntaoRetornAContaQueFoiSalva() throws Exception {

        when(contaService.save(any())).thenReturn(conta);

        mvc.perform(post("/conta/salvar")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(conta)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(conta)))
                .andDo(print());
    }

    @Test
    void quandoChamarOEndPointDeAdicionarSaldoNaContaEntaoRetorneIsOk() throws Exception {

        when(contaService.adicionarSaldoNaConta(anyLong(), anyDouble())).thenReturn(2500D);

        mvc.perform(post("/conta/adicionarSaldoNaConta/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("saldoParaAdicionar", "2500"))
                .andExpect(status().isOk())
                .andExpect(content().string("2500.0"))
                .andDo(print());
    }

    @Test
    void quandoChamarOEndPointDeAdicionarSaldoNaContaEntaoRetorneErroDeContaNaoExistente() throws Exception {

        when(contaService.adicionarSaldoNaConta(anyLong(), anyDouble())).thenThrow(new ErrorException("Conta nao existe"));


        mvc.perform(post("/conta/adicionarSaldoNaConta/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("saldoParaAdicionar", "2500"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(new ErrorResponse(400, "Conta nao existe"))))
                .andDo(print());
    }

    @Test
    void quandoChamarOEndPointDeSubtrairSaldoNaContaEntaoRetorneIsOk() throws Exception {

        when(contaService.subtrairSaldoNaConta(anyLong(), anyDouble())).thenReturn(1500D);

        mvc.perform(post("/conta/subtrairSaldoNaConta/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("saldoParaSubtrair", "500"))
                .andExpect(status().isOk())
                .andExpect(content().string("1500.0"))
                .andDo(print());
    }

    @Test
    void quandoChamarOEndPointDeSubtrairSaldoNaContaEntaoRetorneErroDeContaNaoExistente() throws Exception {

        when(contaService.subtrairSaldoNaConta(anyLong(), anyDouble())).thenThrow(new ErrorException("Conta nao existe"));

        mvc.perform(post("/conta/subtrairSaldoNaConta/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("saldoParaSubtrair", "500"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(new ErrorResponse(400, "Conta nao existe"))))
                .andDo(print());
    }

    @Test
    void quandoChamarOEndPointDeSubtrairSaldoNaContaEntaoRetorne0() throws Exception {

        when(contaService.subtrairSaldoNaConta(anyLong(), anyDouble())).thenReturn(0D);

        mvc.perform(post("/conta/subtrairSaldoNaConta/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("saldoParaSubtrair", "500"))
                .andExpect(status().isOk())
                .andExpect(content().string("0.0"))
                .andDo(print());
    }

    @Test
    void quandoChamarOEndPointDeTransferirSaldoParaAContaEntaoRetorneIsOk() throws Exception {

        when(contaService.transferirSaldo(anyLong(), anyLong(), anyDouble())).thenReturn(new TransferirSaldoDTO(conta, outraConta));

        mvc.perform(post("/conta/TransferirSaldoParaAConta/1/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("saldoParaTransferir","2500"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void quandoChamarOEndPointDeTransferirSaldoParaAContaEntaoRetorneErroContaNaoExiste() throws Exception {

        when(contaService.transferirSaldo(anyLong(), anyLong(), anyDouble())).thenThrow(new ErrorException("Conta não existe"));

        mvc.perform(post("/conta/TransferirSaldoParaAConta/1/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("saldoParaTransferir","2500"))
                .andExpect(status().isBadRequest())
                .andExpect(error -> assertEquals("Conta não existe", Objects.requireNonNull(error.getResolvedException()).getMessage()))
                .andDo(print());
    }

    @Test
    void quandoChamarOEndPointDeTransferirSaldoParaAContaEntaoRetorneErroSaldoInsulficiente() throws Exception {

        when(contaService.transferirSaldo(anyLong(), anyLong(), anyDouble())).thenThrow(new ErrorException("Saldo insulficiente"));

        mvc.perform(post("/conta/TransferirSaldoParaAConta/1/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("saldoParaTransferir","2500"))
                .andExpect(status().isBadRequest())
                .andExpect(error -> assertEquals("Saldo insulficiente", Objects.requireNonNull(error.getResolvedException()).getMessage()))
                .andDo(print());
    }

    void createObjects(){
        conta = new Conta(1L, 123, 456, "alefe", 2500D);
        outraConta = new Conta(2L, 456, 789, "lucas", 1500D);
        contasList = new ArrayList<>(List.of(conta, outraConta));
    }


}
