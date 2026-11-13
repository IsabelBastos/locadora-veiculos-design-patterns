package br.com.locadora.adicional;

public class GpsDecorator extends AdicionalDecorator {
    public static final double VALOR_DIARIO = 20.0;

    public GpsDecorator(ComponenteAluguel componente) {
        super(componente);
    }

    @Override
    public double getValorDiario() {
        return VALOR_DIARIO;
    }

    @Override
    public String getNome() {
        return "GPS";
    }
}
