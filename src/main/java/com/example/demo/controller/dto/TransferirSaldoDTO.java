package com.example.demo.controller.dto;

import com.example.demo.domain.Conta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TransferirSaldoDTO {
    Conta contaQueTransfere;
    Conta contaQueRecebe;
}
