package br.com.locadora;

import br.com.locadora.adicional.*;
import br.com.locadora.modelo.*;
import br.com.locadora.tarifa.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class DecoratorTest {
    private static final double DELTA = 0.000001;
    private final Cliente cliente = new Cliente(1, "Ana");
    private final Veiculo veiculo = new Veiculo(1, "Fiat Argo", 100, true);

    private Aluguel aluguel(int dias, TarifaStrategy tarifa) {
        return new Aluguel(cliente, veiculo, dias, tarifa);
    }

    @Test
    void semAdicionaisTotalIgualAoValorBase() {
        Aluguel aluguel = aluguel(7, new TarifaPadrao());
        assertEquals(700, aluguel.calcularTotal(), DELTA);
        assertEquals(aluguel.calcularValorBase(), aluguel.calcularTotal(), DELTA);
    }

    @Test
    void gpsSomaVinteReaisPorDia() {
        ComponenteAluguel comGps = new GpsDecorator(aluguel(3, new TarifaPadrao()));
        assertEquals(300 + 60, comGps.calcularTotal(), DELTA);
    }

    @Test
    void seguroSomaTrintaReaisPorDia() {
        ComponenteAluguel comSeguro = new SeguroAdicionalDecorator(aluguel(3, new TarifaPadrao()));
        assertEquals(300 + 90, comSeguro.calcularTotal(), DELTA);
    }

    @Test
    void gpsESeguroJuntos() {
        ComponenteAluguel ambos = new SeguroAdicionalDecorator(new GpsDecorator(aluguel(3, new TarifaPadrao())));
        assertEquals(300 + 60 + 90, ambos.calcularTotal(), DELTA);
    }

    @Test
    void ordemDosDecoratorsNaoAlteraOTotal() {
        ComponenteAluguel gpsDepois = new GpsDecorator(new SeguroAdicionalDecorator(aluguel(5, new TarifaPadrao())));
        ComponenteAluguel seguroDepois = new SeguroAdicionalDecorator(new GpsDecorator(aluguel(5, new TarifaPadrao())));
        assertEquals(gpsDepois.calcularTotal(), seguroDepois.calcularTotal(), DELTA);
        assertEquals(500 + 100 + 150, gpsDepois.calcularTotal(), DELTA);
    }

    @Test
    void exemploDoReadmeLongaDuracaoComAmbos() {
        ComponenteAluguel ambos = new SeguroAdicionalDecorator(new GpsDecorator(aluguel(7, new TarifaLongaDuracao())));
        assertEquals(945, ambos.calcularTotal(), DELTA); // 595 + 140 + 210
    }

    /** Combinacao Strategy + Decorator: o desconto incide so sobre a diaria do veiculo, nao sobre os adicionais. */
    @ParameterizedTest
    @CsvSource({
            // dias, base padrao, base longa, adicionais (GPS + seguro)
            "1, 100, 100, 50",
            "6, 600, 600, 300",
            "7, 700, 595, 350",
            "8, 800, 680, 400"
    })
    void combinaCadaTarifaComOsDoisAdicionais(int dias, double basePadrao, double baseLonga, double adicionais) {
        ComponenteAluguel padrao = new SeguroAdicionalDecorator(new GpsDecorator(aluguel(dias, new TarifaPadrao())));
        ComponenteAluguel longa = new SeguroAdicionalDecorator(new GpsDecorator(aluguel(dias, new TarifaLongaDuracao())));
        assertEquals(basePadrao + adicionais, padrao.calcularTotal(), DELTA);
        assertEquals(baseLonga + adicionais, longa.calcularTotal(), DELTA);
    }

    @Test
    void trocarEstrategiaRefleteNoTotalDecorado() {
        Aluguel aluguel = aluguel(7, new TarifaPadrao());
        ComponenteAluguel comGps = new GpsDecorator(aluguel);
        assertEquals(700 + 140, comGps.calcularTotal(), DELTA);
        aluguel.setTarifaStrategy(new TarifaLongaDuracao());
        assertEquals(595 + 140, comGps.calcularTotal(), DELTA);
    }

    @Test
    void valorDeCadaAdicionalIsolado() {
        Aluguel aluguel = aluguel(4, new TarifaPadrao());
        GpsDecorator gps = new GpsDecorator(aluguel);
        SeguroAdicionalDecorator seguro = new SeguroAdicionalDecorator(gps);
        assertEquals(80, gps.calcularValorAdicional(), DELTA);
        assertEquals(120, seguro.calcularValorAdicional(), DELTA);
        assertEquals(4, seguro.getQuantidadeDias());
    }

    @Test
    void descricaoAcumulaOsAdicionais() {
        Aluguel aluguel = aluguel(2, new TarifaPadrao());
        assertEquals("Aluguel do Fiat Argo", aluguel.getDescricao());
        ComponenteAluguel ambos = new SeguroAdicionalDecorator(new GpsDecorator(aluguel));
        assertEquals("Aluguel do Fiat Argo + GPS + Seguro adicional", ambos.getDescricao());
    }

    @Test
    void rejeitaComponenteNulo() {
        assertThrows(NullPointerException.class, () -> new GpsDecorator(null));
        assertThrows(NullPointerException.class, () -> new SeguroAdicionalDecorator(null));
    }
}
