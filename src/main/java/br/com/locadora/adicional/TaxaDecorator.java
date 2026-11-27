package br.com.locadora.adicional;

import java.util.Objects;

/**
 * Segundo decorator base da extensao. O {@link AdicionalDecorator} original
 * cobra sempre "valor diario x dias"; este permite outras formas de cobranca
 * (valor fixo, percentual etc.), definidas por cada subclasse em
 * {@link #calcularValorTaxa()}. Como tambem implementa {@link ComponenteAluguel},
 * pode ser empilhado livremente com GPS e seguro.
 */
public abstract class TaxaDecorator implements ComponenteAluguel {
    protected final ComponenteAluguel componente;

    protected TaxaDecorator(ComponenteAluguel componente) {
        this.componente = Objects.requireNonNull(componente, "O componente decorado e obrigatorio.");
    }

    /** Nome da taxa exibido na descricao. */
    public abstract String getNome();

    /** Texto curto que explica como a taxa e cobrada. */
    public abstract String getRegraCobranca();

    /** Valor que esta taxa acrescenta ao aluguel. */
    public abstract double calcularValorTaxa();

    @Override
    public double calcularTotal() {
        return componente.calcularTotal() + calcularValorTaxa();
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
