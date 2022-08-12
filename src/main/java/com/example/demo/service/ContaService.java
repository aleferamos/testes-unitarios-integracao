package com.example.demo.service;

import com.example.demo.controller.dto.TransferirSaldoDTO;
import com.example.demo.domain.Conta;
import com.example.demo.exceptions.ErrorException;
import com.example.demo.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    public Conta save(Conta conta){
        return contaRepository.save(conta);
    }

    public List<Conta> toList(){
        return contaRepository.findAll();
    }

    public Conta findById(Long id){
        return contaRepository.findById(id).orElseThrow(() -> new ErrorException("Conta não existe"));
    }

    public Double adicionarSaldoNaConta(Long idConta, Double saldo) {
        Conta contaParaAdicionarSaldo = findById(idConta);
        contaParaAdicionarSaldo.setSaldo(contaParaAdicionarSaldo.getSaldo() + saldo);
        save(contaParaAdicionarSaldo);
        return contaParaAdicionarSaldo.getSaldo();
    }

    public Double subtrairSaldoNaConta(Long idConta, Double saldo) {
        Conta contaParaAdicionarSaldo = findById(idConta);

        if(contaParaAdicionarSaldo.getSaldo() > saldo){
            contaParaAdicionarSaldo.setSaldo(contaParaAdicionarSaldo.getSaldo() - saldo);
        } else {
            contaParaAdicionarSaldo.setSaldo(0D);
        }

        return contaParaAdicionarSaldo.getSaldo();
    }

    public TransferirSaldoDTO transferirSaldo(Long idContaQueTransfere, Long idContaQueRecebe, Double saldoTransferido){
        Conta contaQueTransfere = findById(idContaQueTransfere);
        Conta contaQueRecebe = findById(idContaQueRecebe);

        if(contaQueTransfere.getSaldo() < saldoTransferido){
            throw new ErrorException("Saldo insulficiente");
        }

        subtrairSaldoNaConta(contaQueTransfere.getId(), saldoTransferido);
        adicionarSaldoNaConta(contaQueRecebe.getId(), saldoTransferido);

        return new TransferirSaldoDTO(contaQueTransfere, contaQueRecebe);
    }


}
