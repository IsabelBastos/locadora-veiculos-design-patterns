package br.com.locadora.adicional;

import java.util.Objects;

/**
 * Decorator base: envolve outro componente e soma ao total dele o valor diario
 * do adicional multiplicado pela quantidade de dias do aluguel.
 */
public abstract class AdicionalDecorator implements ComponenteAluguel {
    protected final ComponenteAluguel componente;

    protected AdicionalDecorator(ComponenteAluguel componente) {
        this.componente = Objects.requireNonNull(componente, "O componente decorado e obrigatorio.");
    }

    /** Valor cobrado por dia por este adicional. */
    public abstract double getValorDiario();

    /** Nome do adicional exibido na descricao. */
    public abstract String getNome();

    /** Valor que este adicional acrescenta ao aluguel. */
    public double calcularValorAdicional() {
        return getValorDiario() * getQuantidadeDias();
    }

    @Override
    public double calcularTotal() {
        return componente.calcularTotal() + calcularValorAdicional();
    }

    @Override
    public String getDescricao() {
        return componente.getDescricao() + " + " + getNome();
    }

    @Override
    public int getQuantidadeDias() {
        return componente.getQuantidadeDias();
    }
}
