package br.com.locadora.tarifa;

/**
 * Tarifa de fidelidade: 5% de desconto para cada aluguel anterior do cliente,
 * limitado a 20%. Diferente das outras estrategias, esta guarda um estado (o
 * historico do cliente), recebido no construtor, sem precisar alterar a
 * interface {@link TarifaStrategy} nem a classe Aluguel.
 */
public class TarifaFidelidade implements TarifaStrategy {
    public static final double DESCONTO_POR_ALUGUEL = 0.05;
    public static final double DESCONTO_MAXIMO = 0.20;

    private final int alugueisAnteriores;

    public TarifaFidelidade(int alugueisAnteriores) {
        if (alugueisAnteriores < 0) {
            throw new IllegalArgumentException("A quantidade de alugueis anteriores nao pode ser negativa.");
        }
        this.alugueisAnteriores = alugueisAnteriores;
    }

    /** Percentual de desconto aplicado (0.0 a 0.20). */
    public double getPercentualDesconto() {
        return Math.min(alugueisAnteriores * DESCONTO_POR_ALUGUEL, DESCONTO_MAXIMO);
    }

    public int getAlugueisAnteriores() {
        return alugueisAnteriores;
    }

    @Override
    public double calcular(double valorDiaria, int quantidadeDias) {
        if (!Double.isFinite(valorDiaria) || valorDiaria < 0) {
            throw new IllegalArgumentException("A diaria deve ser finita e nao negativa.");
        }
        if (quantidadeDias <= 0) {
            throw new IllegalArgumentException("A quantidade de dias deve ser maior que zero.");
        }
        return valorDiaria * quantidadeDias * (1 - getPercentualDesconto());
    }
}
