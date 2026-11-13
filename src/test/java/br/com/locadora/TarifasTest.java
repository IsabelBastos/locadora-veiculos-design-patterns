package br.com.locadora;

import br.com.locadora.modelo.*;
import br.com.locadora.tarifa.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class TarifasTest {
    @ParameterizedTest
    @CsvSource({"1,100", "6,600", "7,700", "8,800"})
    void calculaTarifaPadrao(int dias, double esperado) {
        assertEquals(esperado, new TarifaPadrao().calcular(100, dias), 0.000001);
    }

    @ParameterizedTest
    @CsvSource({"1,100", "6,600", "7,595", "8,680"})
    void aplicaDescontoSomenteAPartirDeSeteDias(int dias, double esperado) {
        assertEquals(esperado, new TarifaLongaDuracao().calcular(100, dias), 0.000001);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 6, 7, 8})
    void aceitaDiariaZero(int dias) {
        assertEquals(0, new TarifaPadrao().calcular(0, dias));
        assertEquals(0, new TarifaLongaDuracao().calcular(0, dias));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void rejeitaDiasInvalidos(int dias) {
        assertThrows(IllegalArgumentException.class, () -> new TarifaPadrao().calcular(100, dias));
        assertThrows(IllegalArgumentException.class, () -> new TarifaLongaDuracao().calcular(100, dias));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, -100, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void rejeitaDiariasInvalidas(double diaria) {
        assertThrows(IllegalArgumentException.class, () -> new TarifaPadrao().calcular(diaria, 7));
        assertThrows(IllegalArgumentException.class, () -> new TarifaLongaDuracao().calcular(diaria, 7));
    }

    @Test
    void calculaComCentavos() {
        assertEquals(139.30, new TarifaPadrao().calcular(19.90, 7), 0.000001);
        assertEquals(118.405, new TarifaLongaDuracao().calcular(19.90, 7), 0.000001);
    }
}
