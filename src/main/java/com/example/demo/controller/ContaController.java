package com.example.demo.controller;

import com.example.demo.controller.dto.TransferirSaldoDTO;
import com.example.demo.domain.Conta;
import com.example.demo.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conta")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @PostMapping("/salvar")
    private ResponseEntity<Conta> salvar(@RequestBody Conta conta){
        return ResponseEntity.status(201).body(contaService.save(conta));
    }

    @GetMapping("/listarContas")
    public ResponseEntity<List<Conta>> listarContas(){
        return ResponseEntity.ok().body(contaService.toList());
    }

    @GetMapping("/buscarConta/{id}")
    private ResponseEntity<Conta> buscarContaPorId(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(contaService.findById(id));
    }

    @PostMapping("/adicionarSaldoNaConta/{id}")
    private ResponseEntity<Double> adicionarSaldoNaConta(@PathVariable(name = "id") Long id,
            @RequestParam(name = "saldoParaAdicionar") Double saldo){
        return ResponseEntity.ok().body(contaService.adicionarSaldoNaConta(id, saldo));
    }

    @PostMapping("/subtrairSaldoNaConta/{id}")
    private ResponseEntity<Double> subtrairSaldoNaConta(@PathVariable(name = "id") Long id,
        @RequestParam(name = "saldoParaSubtrair") Double saldo){
        return ResponseEntity.ok().body(contaService.subtrairSaldoNaConta(id, saldo));
    }

    @PostMapping("/TransferirSaldoParaAConta/{idContaQueTransfere}/{idContaQueRecebe}")
    private ResponseEntity<TransferirSaldoDTO> transferirSaldoParaAConta(
        @PathVariable(name = "idContaQueTransfere") Long idContaQueTransfere,
        @PathVariable(name = "idContaQueRecebe") Long idContaQueRecebe,
        @RequestParam(name = "saldoParaTransferir") Double saldoParaTransferir){
        return ResponseEntity.ok().body(contaService.transferirSaldo(idContaQueTransfere, idContaQueRecebe, saldoParaTransferir));
    }
}
