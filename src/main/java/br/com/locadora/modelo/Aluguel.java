package br.com.locadora.modelo;

import br.com.locadora.tarifa.TarifaStrategy;
import java.util.Objects;

public class Aluguel {
    private final Cliente cliente;
    private final Veiculo veiculo;
    private final int quantidadeDias;
    private TarifaStrategy tarifaStrategy;

    public Aluguel(Cliente cliente, Veiculo veiculo, int quantidadeDias,
                   TarifaStrategy tarifaStrategy) {
        if (quantidadeDias <= 0) {
            throw new IllegalArgumentException("A quantidade de dias deve ser maior que zero.");
        }
        this.cliente = Objects.requireNonNull(cliente, "O cliente e obrigatorio.");
        this.veiculo = Objects.requireNonNull(veiculo, "O veiculo e obrigatorio.");
        this.quantidadeDias = quantidadeDias;
        setTarifaStrategy(tarifaStrategy);
    }

    /** Retorna o valor com a tarifa escolhida, antes de quaisquer adicionais. */
    public double calcularValorBase() {
        return tarifaStrategy.calcular(veiculo.getValorDiaria(), quantidadeDias);
    }

    public void setTarifaStrategy(TarifaStrategy tarifaStrategy) {
        this.tarifaStrategy = Objects.requireNonNull(tarifaStrategy, "A tarifa e obrigatoria.");
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public int getQuantidadeDias() {
        return quantidadeDias;
    }

    public TarifaStrategy getTarifaStrategy() {
        return tarifaStrategy;
    }
}
