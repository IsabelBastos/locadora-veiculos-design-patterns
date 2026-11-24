package br.com.locadora.adicional;

/**
 * Retirada no aeroporto: cobra 10% sobre o total do componente que ela envolve.
 * Por ser percentual, e o unico adicional em que a ordem dos decorators altera
 * o total: so incide sobre o que ja estava contratado quando foi adicionada.
 */
public class TaxaAeroportoDecorator extends TaxaDecorator {
    public static final double PERCENTUAL = 0.10;

    public TaxaAeroportoDecorator(ComponenteAluguel componente) {
        super(componente);
    }

    @Override
    public String getNome() {
        return "Retirada no aeroporto";
    }

    @Override
    public String getRegraCobranca() {
        return "10% do subtotal";
    }

    @Override
    public double calcularValorTaxa() {
        return componente.calcularTotal() * PERCENTUAL;
    }
}
