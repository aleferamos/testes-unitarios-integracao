package com.example.demo.service;

import com.example.demo.controller.dto.TransferirSaldoDTO;
import com.example.demo.exceptions.ErrorException;
import com.example.demo.repository.ContaRepository;
import com.example.demo.domain.Conta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@MockitoSettings
public class ContaServiceTest {

    public static final long ID = 1L;
    public static final int AGENCIA = 123;
    public static final int CONTA = 456;
    public static final String PROPRIETARIO = "alefe";
    public static final double SALDO = 2500D;
    @Mock
    private ContaRepository contaRepository;
    @InjectMocks
    private ContaService contaService;
    private Conta conta;
    private Conta outraConta;
    List<Conta> contasList;

    @BeforeEach
    void starts(){
        this.createObjects();
    }

    @Test
    @DisplayName("Quando cadastrar uma conta o objeto conta não deve ser nulo")
    void quandoCadastrarContaDeveRetornarObjetoContaNaoNulo(){

        when(contaRepository.save(any())).thenReturn(conta);

        Conta response = contaService.save(conta);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Quando cadastrar uma conta deve retornar o objeto conta")
    void quandoCadastrarContaDeveRetornarObjetoConta(){

        when(contaRepository.save(any())).thenReturn(conta);

        Conta response = contaService.save(conta);

        assertEquals(ID, response.getId());
        assertEquals(AGENCIA, response.getAgencia());
        assertEquals(CONTA, response.getConta());
        assertEquals(PROPRIETARIO, response.getProprietario());
        assertEquals(SALDO, response.getSaldo());
    }

    @Test
    @DisplayName("Quando listar contas deve retornar uma lista de contas com dois objetos")
    void quandoListarContasDeveRetornarListaDeContasComDoisObjetos(){

        when(contaRepository.findAll()).thenReturn(contasList);

        List<Conta> response = contaService.toList();

        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Quando listar contas deve apresentar os nomes corretos")
    void quandoListarContasDeveApresentarOsNomesCorretos(){

        when(contaRepository.findAll()).thenReturn(contasList);

        List<Conta> response = contaService.toList();

        assertEquals("alefe", response.get(0).getProprietario());
        assertEquals("lucas", response.get(1).getProprietario());
    }

    @Test
    @DisplayName("Quando adicionar saldo de uma conta entao deve retornar o saldo total da conta")
    void quandoAdicionarSaldoDaContaEntaoDeveRetornarOSaldoTotalDaConta(){

        when(contaRepository.findById(anyLong())).thenReturn(Optional.ofNullable(conta));

        Double response = contaService.adicionarSaldoNaConta(anyLong(), 500D);

        assertEquals(3000L, response);
    }

    @Test
    @DisplayName("Quando subtrair saldo de uma conta entao deve retornar o saldo total da conta")
    void quandoSubtrairSaldoDaContaEntaoDeveRetornarOSaldoTotalDaConta(){

        when(contaRepository.findById(anyLong())).thenReturn(Optional.ofNullable(conta));

        Double response = contaService.subtrairSaldoNaConta(anyLong(), 500D);

        assertEquals(2000L, response);
    }

    @Test
    @DisplayName("Quando subtrair saldo maior que o saldo da conta entao deve retornar saldo 0")
    void quandoSubtrairSaldoDaContaEntaoDeveRetornarErroDeSaldoInsulficiente(){

        when(contaRepository.findById(anyLong())).thenReturn(Optional.ofNullable(conta));

        Double response = contaService.subtrairSaldoNaConta(anyLong(), 2600D);

        assertEquals(0D, response);
    }

    @Test
    @DisplayName("Quando transferir saldo de uma conta para outra deve retornar o saldo da conta que transferiu")
    void quandoTransferirSaldoDeUmaContaParaOutraDeveRetornarSaldoDaContaTransferida(){

        when(contaRepository.findById(1L)).thenReturn(Optional.ofNullable(conta));
        when(contaRepository.findById(2L)).thenReturn(Optional.ofNullable(outraConta));

        TransferirSaldoDTO response = contaService.transferirSaldo(1L, 2L, 500D);

        assertEquals(2000D, response.getContaQueTransfere().getSaldo());
    }

    @Test
    @DisplayName("Quando transferir saldo de uma conta para outra deve retornar saldo insulficiente")
    void quandoTransferirSaldoDeUmaContaParaOutraDeveRetornarSaldoInsulficiente(){

        when(contaRepository.findById(1L)).thenReturn(Optional.ofNullable(conta));
        when(contaRepository.findById(2L)).thenReturn(Optional.ofNullable(outraConta));

        Exception exception =  assertThrows(ErrorException.class, () -> contaService.transferirSaldo(1L, 2L, 2600D));

        assertEquals("Saldo insulficiente", exception.getMessage());
    }

    @Test
    @DisplayName("Quando transferir saldo de uma conta para outra deve retornar o saldo da conta que recebeu")
    void quandoTransferirSaldoDeUmaContaParaOutraDeveRetornarSaldoDaContaRecebida(){

        when(contaRepository.findById(1L)).thenReturn(Optional.ofNullable(conta));
        when(contaRepository.findById(2L)).thenReturn(Optional.ofNullable(outraConta));

        TransferirSaldoDTO response = contaService.transferirSaldo(1L, 2L, 500D);

        assertEquals(2000D, response.getContaQueRecebe().getSaldo());
    }

    @Test
    @DisplayName("Quando buscar uma conta por id deve retornar a conta buscada")
    void quandoBuscarUmaContaPorIdEntaoDeveRetornarAConta(){
        when(contaRepository.findById(anyLong())).thenReturn(Optional.ofNullable(conta));

        Conta response = contaService.findById(anyLong());

        assertNotNull(response);
        assertEquals(ID, response.getId());
    }

    @Test
    void quandoCriarObjetoTransferirSaldoDTOEntaoRetorneSuccess(){
        TransferirSaldoDTO transferirSaldoDTO = new TransferirSaldoDTO(conta, outraConta);

        assertEquals(transferirSaldoDTO.getContaQueTransfere().getSaldo(), 2500D);
        assertEquals(transferirSaldoDTO.getContaQueRecebe().getSaldo(), 1500D);
    }
    void createObjects(){
        conta = new Conta(1L, 123, 456, "alefe", 2500D);
        outraConta = new Conta(2L, 456, 789, "lucas", 1500D);
        contasList = new ArrayList<>(List.of(conta, outraConta));
    }
}
