package br.com.locadora.adicional;

/** Taxa de limpeza: valor unico, independente da quantidade de dias. */
public class TaxaLimpezaDecorator extends TaxaDecorator {
    public static final double VALOR_FIXO = 80.0;

    public TaxaLimpezaDecorator(ComponenteAluguel componente) {
        super(componente);
    }

    @Override
    public String getNome() {
        return "Taxa de limpeza";
    }

    @Override
    public String getRegraCobranca() {
        return "valor unico";
    }

    @Override
    public double calcularValorTaxa() {
        return VALOR_FIXO;
    }
}
