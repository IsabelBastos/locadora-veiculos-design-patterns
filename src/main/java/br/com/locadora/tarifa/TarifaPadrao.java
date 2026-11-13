package br.com.locadora.tarifa;

public class TarifaPadrao implements TarifaStrategy {
    @Override
    public double calcular(double valorDiaria, int quantidadeDias) {
        if (!Double.isFinite(valorDiaria) || valorDiaria < 0) {
            throw new IllegalArgumentException("A diaria deve ser finita e nao negativa.");
        }
        if (quantidadeDias <= 0) {
            throw new IllegalArgumentException("A quantidade de dias deve ser maior que zero.");
        }
        return valorDiaria * quantidadeDias;
    }
}
