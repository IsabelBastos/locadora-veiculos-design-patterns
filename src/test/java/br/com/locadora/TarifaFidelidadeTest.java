package br.com.locadora;

import br.com.locadora.modelo.Aluguel;
import br.com.locadora.modelo.Cliente;
import br.com.locadora.modelo.Veiculo;
import br.com.locadora.tarifa.TarifaFidelidade;
import br.com.locadora.tarifa.TarifaPadrao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class TarifaFidelidadeTest {
    private static final double DELTA = 0.000001;

    @ParameterizedTest(name = "{0} alugueis anteriores -> R$ {1}")
    @CsvSource({
            "0, 1000.0",
            "1, 950.0",
            "2, 900.0",
            "3, 850.0",
            "4, 800.0",
            "5, 800.0",
            "10, 800.0"
    })
    void descontoCresceCincoPorCentoPorAluguelAteVinte(int anteriores, double esperado) {
        assertEquals(esperado, new TarifaFidelidade(anteriores).calcular(100, 10), DELTA);
    }

    @Test
    void semHistoricoEquivaleATarifaPadrao() {
        assertEquals(new TarifaPadrao().calcular(120, 3), new TarifaFidelidade(0).calcular(120, 3), DELTA);
    }

    @Test
    void informaPercentualDeDesconto() {
        assertEquals(0.10, new TarifaFidelidade(2).getPercentualDesconto(), DELTA);
        assertEquals(0.20, new TarifaFidelidade(7).getPercentualDesconto(), DELTA);
    }

    @Test
    void aluguelPodeTrocarParaFidelidade() {
        Aluguel aluguel = new Aluguel(new Cliente(1, "Ana"), new Veiculo(1, "Fiat Argo", 100, true), 3,
                new TarifaPadrao());
        assertEquals(300, aluguel.calcularValorBase(), DELTA);
        aluguel.setTarifaStrategy(new TarifaFidelidade(2));
        assertEquals(270, aluguel.calcularValorBase(), DELTA);
    }

    @Test
    void validaEntradas() {
        assertThrows(IllegalArgumentException.class, () -> new TarifaFidelidade(-1));
        TarifaFidelidade tarifa = new TarifaFidelidade(1);
        assertThrows(IllegalArgumentException.class, () -> tarifa.calcular(100, 0));
        assertThrows(IllegalArgumentException.class, () -> tarifa.calcular(-1, 3));
        assertThrows(IllegalArgumentException.class, () -> tarifa.calcular(Double.NaN, 3));
    }
}
