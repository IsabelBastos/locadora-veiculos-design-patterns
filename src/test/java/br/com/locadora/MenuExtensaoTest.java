package br.com.locadora;

import br.com.locadora.console.MenuLocadora;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MenuExtensaoTest {
    private String executar(String... linhas) {
        String entrada = String.join("\n", linhas) + "\n";
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        new MenuLocadora(new Scanner(entrada), new PrintStream(buffer, true, StandardCharsets.UTF_8)).executar();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void fidelidadeAumentaDescontoACadaAluguelDoCliente() {
        String saida = executar(
                "2", "Ana",
                "3", "1", "1", "3", "3", "n", "n",  
                "3", "1", "2", "3", "3", "n", "n",  
                "3", "1", "3", "2", "3", "n", "n",  
                "0");
        assertTrue(saida.contains("Tarifa: Fidelidade (0 alugueis anteriores, 0% de desconto)"));
        assertTrue(saida.contains("VALOR TOTAL: R$ 300,00"));
        assertTrue(saida.contains("Tarifa: Fidelidade (1 alugueis anteriores, 5% de desconto)"));
        assertTrue(saida.contains("VALOR TOTAL: R$ 342,00"));
        assertTrue(saida.contains("Tarifa: Fidelidade (2 alugueis anteriores, 10% de desconto)"));
        assertTrue(saida.contains("VALOR TOTAL: R$ 360,00"));
    }

    @Test
    void fidelidadeContaHistoricoPorCliente() {
        String saida = executar(
                "2", "Ana", "2", "Bruno",
                "3", "1", "1", "3", "1", "n", "n", 
                "3", "2", "2", "3", "3", "n", "n",   
                "0");
        assertTrue(saida.contains("Tarifa: Fidelidade (0 alugueis anteriores, 0% de desconto)"));
        assertTrue(saida.contains("VALOR TOTAL: R$ 360,00"));
    }

    @Test
    void servicosExtrasDecoramAluguelJaRealizado() {
        String saida = executar("2", "Ana", "3", "1", "1", "7", "2", "s", "s",
                "4", "1", "s", "s", "0");
        assertTrue(saida.contains("Aluguel registrado com codigo 1."));
        assertTrue(saida.contains("VALOR TOTAL: R$ 945,00"));
        assertTrue(saida.contains("Taxa de limpeza (valor unico): R$ 80,00"));
        assertTrue(saida.contains("Retirada no aeroporto (10% do subtotal): R$ 102,50"));
        assertTrue(saida.contains(
                "Descricao: Aluguel do Fiat Argo + GPS + Seguro adicional + Taxa de limpeza + Retirada no aeroporto"));
        assertTrue(saida.contains("VALOR TOTAL: R$ 1127,50"));
    }

    @Test
    void servicoNaoEContratadoDuasVezes() {
        String saida = executar("2", "Ana", "3", "1", "1", "3", "1", "n", "n",
                "4", "1", "s", "n",      
                "4", "1", "s",         
                "4", "1",               
                "0");
        assertTrue(saida.contains("VALOR TOTAL: R$ 380,00"));
        assertTrue(saida.contains("VALOR TOTAL: R$ 418,00"));
        assertTrue(saida.contains("Todos os servicos extras ja foram contratados"));
    }

    @Test
    void servicosExtrasExigemAluguelRealizado() {
        String saida = executar("4", "0");
        assertTrue(saida.contains("Nenhum aluguel realizado ainda"));
    }
}
