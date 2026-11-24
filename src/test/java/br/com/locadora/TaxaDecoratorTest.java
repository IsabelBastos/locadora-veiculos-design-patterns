package br.com.locadora;

import br.com.locadora.adicional.*;
import br.com.locadora.modelo.*;
import br.com.locadora.tarifa.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaxaDecoratorTest {
    private static final double DELTA = 0.000001;
    private final Cliente cliente = new Cliente(1, "Ana");
    private final Veiculo veiculo = new Veiculo(1, "Fiat Argo", 100, true);

    private Aluguel aluguel(int dias, TarifaStrategy tarifa) {
        return new Aluguel(cliente, veiculo, dias, tarifa);
    }

    @Test
    void limpezaCobraValorFixoIndependenteDosDias() {
        assertEquals(100 + 80, new TaxaLimpezaDecorator(aluguel(1, new TarifaPadrao())).calcularTotal(), DELTA);
        assertEquals(1000 + 80, new TaxaLimpezaDecorator(aluguel(10, new TarifaPadrao())).calcularTotal(), DELTA);
    }

    @Test
    void aeroportoCobraDezPorCentoDoSubtotal() {
        TaxaAeroportoDecorator aeroporto = new TaxaAeroportoDecorator(aluguel(3, new TarifaPadrao()));
        assertEquals(30, aeroporto.calcularValorTaxa(), DELTA);
        assertEquals(330, aeroporto.calcularTotal(), DELTA);
    }

    @Test
    void empilhaComOsDecoratorsOriginais() {
        // 595 (longa duracao) + 140 (GPS) + 210 (seguro) + 80 (limpeza) = 1025; + 10% = 1127,50
        ComponenteAluguel componente = aluguel(7, new TarifaLongaDuracao());
        componente = new GpsDecorator(componente);
        componente = new SeguroAdicionalDecorator(componente);
        componente = new TaxaLimpezaDecorator(componente);
        componente = new TaxaAeroportoDecorator(componente);
        assertEquals(1127.5, componente.calcularTotal(), DELTA);
        assertEquals("Aluguel do Fiat Argo + GPS + Seguro adicional + Taxa de limpeza + Retirada no aeroporto",
                componente.getDescricao());
        assertEquals(7, componente.getQuantidadeDias());
    }

    @Test
    void ordemImportaQuandoHaTaxaPercentual() {
        ComponenteAluguel limpezaDepois = new TaxaLimpezaDecorator(new TaxaAeroportoDecorator(aluguel(3, new TarifaPadrao())));
        ComponenteAluguel aeroportoDepois = new TaxaAeroportoDecorator(new TaxaLimpezaDecorator(aluguel(3, new TarifaPadrao())));
        assertEquals(300 + 30 + 80, limpezaDepois.calcularTotal(), DELTA);
        assertEquals((300 + 80) * 1.10, aeroportoDepois.calcularTotal(), DELTA);
    }

    @Test
    void trocaDeEstrategiaRefleteNoPercentual() {
        Aluguel aluguel = aluguel(10, new TarifaPadrao());
        ComponenteAluguel comAeroporto = new TaxaAeroportoDecorator(aluguel);
        assertEquals(1100, comAeroporto.calcularTotal(), DELTA);
        aluguel.setTarifaStrategy(new TarifaFidelidade(4));
        assertEquals(880, comAeroporto.calcularTotal(), DELTA);
    }

    @Test
    void componenteNuloELancado() {
        assertThrows(NullPointerException.class, () -> new TaxaLimpezaDecorator(null));
        assertThrows(NullPointerException.class, () -> new TaxaAeroportoDecorator(null));
    }
}
