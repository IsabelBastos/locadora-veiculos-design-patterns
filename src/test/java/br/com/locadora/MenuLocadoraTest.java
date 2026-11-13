package br.com.locadora;

import br.com.locadora.console.MenuLocadora;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MenuLocadoraTest {
    private String executar(String... linhas) {
        String entrada = String.join("\n", linhas) + "\n";
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        new MenuLocadora(new Scanner(entrada), new PrintStream(buffer, true, StandardCharsets.UTF_8)).executar();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void fluxoCompletoComTarifaLongaEAmbosAdicionais() {
        String saida = executar("2", "Ana", "3", "1", "1", "7", "2", "s", "s", "0");
        assertTrue(saida.contains("Cliente: Ana"));
        assertTrue(saida.contains("Veiculo: Fiat Argo"));
        assertTrue(saida.contains("Tarifa: Longa duracao"));
        assertTrue(saida.contains("Valor base: R$ 595,00"));
        assertTrue(saida.contains("GPS (7 x R$ 20,00): R$ 140,00"));
        assertTrue(saida.contains("Seguro adicional (7 x R$ 30,00): R$ 210,00"));
        assertTrue(saida.contains("VALOR TOTAL: R$ 945,00"));
    }

    @Test
    void fluxoSemAdicionais() {
        String saida = executar("2", "Bruno", "3", "1", "2", "3", "1", "n", "n", "0");
        assertTrue(saida.contains("Adicionais: nenhum"));
        assertTrue(saida.contains("VALOR TOTAL: R$ 360,00"));
    }

    @Test
    void veiculoAlugadoSaiDaListaDeDisponiveis() {
        String saida = executar("1", "2", "Ana", "3", "1", "1", "2", "1", "n", "n", "1", "0");
        int segundaListagem = saida.lastIndexOf("--- Veiculos disponiveis ---");
        String depois = saida.substring(segundaListagem);
        assertFalse(depois.contains("Fiat Argo"));
        assertTrue(depois.contains("Chevrolet Onix"));
        assertFalse(saida.contains("Toyota Corolla"));
    }

    @Test
    void exigeClienteAntesDoAluguelEReperguntaEntradaInvalida() {
        String saida = executar("3", "abc", "9", "0");
        assertTrue(saida.contains("Cadastre um cliente antes"));
        assertTrue(saida.contains("Valor invalido"));
        assertTrue(saida.contains("Ate logo!"));
    }
}
