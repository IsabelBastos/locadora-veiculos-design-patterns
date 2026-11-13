package br.com.locadora.adicional;

public class SeguroAdicionalDecorator extends AdicionalDecorator {
    public static final double VALOR_DIARIO = 30.0;

    public SeguroAdicionalDecorator(ComponenteAluguel componente) {
        super(componente);
    }

    @Override
    public double getValorDiario() {
        return VALOR_DIARIO;
    }

    @Override
    public String getNome() {
        return "Seguro adicional";
    }
}
