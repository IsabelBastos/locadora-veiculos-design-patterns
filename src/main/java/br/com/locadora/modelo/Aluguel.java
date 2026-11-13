package br.com.locadora.modelo;

import br.com.locadora.adicional.ComponenteAluguel;
import br.com.locadora.tarifa.TarifaStrategy;
import java.util.Objects;

public class Aluguel implements ComponenteAluguel {
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

    /** Componente concreto do Decorator: sem adicionais, o total e o valor base. */
    @Override
    public double calcularTotal() {
        return calcularValorBase();
    }

    @Override
    public String getDescricao() {
        return "Aluguel do " + veiculo.getModelo();
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

    @Override
    public int getQuantidadeDias() {
        return quantidadeDias;
    }

    public TarifaStrategy getTarifaStrategy() {
        return tarifaStrategy;
    }
}
