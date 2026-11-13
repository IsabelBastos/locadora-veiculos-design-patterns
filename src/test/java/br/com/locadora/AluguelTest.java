package br.com.locadora;

import br.com.locadora.modelo.*;
import br.com.locadora.tarifa.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class AluguelTest {
    private final Cliente cliente = new Cliente(1, "Ana");
    private final Veiculo veiculo = new Veiculo(1, "Fiat Argo", 100, true);

    @Test
    void trocaEReverteEstrategiaNoMesmoAluguel() {
        Aluguel aluguel = new Aluguel(cliente, veiculo, 7, new TarifaPadrao());
        assertEquals(700, aluguel.calcularValorBase(), 0.000001);
        aluguel.setTarifaStrategy(new TarifaLongaDuracao());
        assertEquals(595, aluguel.calcularValorBase(), 0.000001);
        aluguel.setTarifaStrategy(new TarifaPadrao());
        assertEquals(700, aluguel.calcularValorBase(), 0.000001);
    }

    @Test
    void delegaCalculoParaInterface() {
        TarifaStrategy tarifa = (diaria, dias) -> diaria + dias;
        Aluguel aluguel = new Aluguel(cliente, veiculo, 3, tarifa);
        assertEquals(103, aluguel.calcularValorBase(), 0.000001);
        assertSame(cliente, aluguel.getCliente());
        assertSame(veiculo, aluguel.getVeiculo());
        assertEquals(3, aluguel.getQuantidadeDias());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejeitaDiasInvalidos(int dias) {
        assertThrows(IllegalArgumentException.class,
                () -> new Aluguel(cliente, veiculo, dias, new TarifaPadrao()));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void veiculoRejeitaDiariaInvalida(double diaria) {
        assertThrows(IllegalArgumentException.class, () -> new Veiculo(1, "Argo", diaria, true));
    }

    @Test
    void aceitaUmDiaEDiariaZero() {
        Veiculo gratuito = new Veiculo(2, "Argo", 0, true);
        Aluguel aluguel = new Aluguel(cliente, gratuito, 1, new TarifaPadrao());
        assertEquals(0, aluguel.calcularValorBase());
    }

    @Test
    void rejeitaEstrategiaNulaSemPerderAnterior() {
        Aluguel aluguel = new Aluguel(cliente, veiculo, 7, new TarifaPadrao());
        assertThrows(NullPointerException.class, () -> aluguel.setTarifaStrategy(null));
        assertEquals(700, aluguel.calcularValorBase(), 0.000001);
        assertThrows(NullPointerException.class, () -> new Aluguel(cliente, veiculo, 7, null));
    }
}
