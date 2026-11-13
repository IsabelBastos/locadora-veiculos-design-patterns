package br.com.locadora;

import br.com.locadora.modelo.Aluguel;
import br.com.locadora.modelo.Cliente;
import br.com.locadora.modelo.Veiculo;
import br.com.locadora.tarifa.TarifaLongaDuracao;
import br.com.locadora.tarifa.TarifaPadrao;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        Locale portuguesBrasil = Locale.forLanguageTag("pt-BR");
        Cliente cliente = new Cliente(1, "Ana");
        Veiculo veiculo = new Veiculo(1, "Fiat Argo", 100.0, true);
        Aluguel aluguel = new Aluguel(cliente, veiculo, 7, new TarifaPadrao());

        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("Veiculo: " + veiculo.getModelo());
        System.out.println("Dias: " + aluguel.getQuantidadeDias());
        System.out.printf(portuguesBrasil, "Tarifa padrao: R$ %.2f%n", aluguel.calcularValorBase());
        aluguel.setTarifaStrategy(new TarifaLongaDuracao());
        System.out.printf(portuguesBrasil, "Tarifa longa duracao: R$ %.2f%n", aluguel.calcularValorBase());
    }
}
