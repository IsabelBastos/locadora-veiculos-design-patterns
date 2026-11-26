package br.com.locadora.console;

import br.com.locadora.adicional.AdicionalDecorator;
import br.com.locadora.adicional.ComponenteAluguel;
import br.com.locadora.adicional.GpsDecorator;
import br.com.locadora.adicional.SeguroAdicionalDecorator;
import br.com.locadora.adicional.TaxaAeroportoDecorator;
import br.com.locadora.adicional.TaxaDecorator;
import br.com.locadora.adicional.TaxaLimpezaDecorator;
import br.com.locadora.modelo.Aluguel;
import br.com.locadora.modelo.Cliente;
import br.com.locadora.modelo.Veiculo;
import br.com.locadora.tarifa.TarifaFidelidade;
import br.com.locadora.tarifa.TarifaLongaDuracao;
import br.com.locadora.tarifa.TarifaPadrao;
import br.com.locadora.tarifa.TarifaStrategy;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Menu de console da locadora. Recebe a entrada e a saida por parametro
 * para que o fluxo completo possa ser testado sem digitar nada.
 */
public class MenuLocadora {
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final Scanner entrada;
    private final PrintStream saida;
    private final List<Veiculo> veiculos = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private long proximoIdCliente = 1;
    private final List<RegistroAluguel> alugueis = new ArrayList<>();
    private final Map<Long, Integer> alugueisPorCliente = new HashMap<>();

    private static final class RegistroAluguel {
        private final Aluguel aluguel;
        private final String nomeTarifa;
        private ComponenteAluguel componente;
        private final List<TaxaDecorator> taxas = new ArrayList<>();

        private RegistroAluguel(Aluguel aluguel, String nomeTarifa, ComponenteAluguel componente) {
            this.aluguel = aluguel;
            this.nomeTarifa = nomeTarifa;
            this.componente = componente;
        }

        private boolean possui(Class<? extends TaxaDecorator> tipo) {
            return taxas.stream().anyMatch(tipo::isInstance);
        }

        private void aplicar(TaxaDecorator taxa) {
            taxas.add(taxa);
            componente = taxa;
        }
    }

    public MenuLocadora(Scanner entrada, PrintStream saida) {
        this.entrada = entrada;
        this.saida = saida;
        veiculos.add(new Veiculo(1, "Fiat Argo", 100.0, true));
        veiculos.add(new Veiculo(2, "Chevrolet Onix", 120.0, true));
        veiculos.add(new Veiculo(3, "Jeep Renegade", 200.0, true));
        veiculos.add(new Veiculo(4, "Toyota Corolla", 250.0, false));
    }

    public void executar() {
        try {
            while (true) {
                saida.println();
                saida.println("===== LOCADORA DE VEICULOS =====");
                saida.println("1 - Listar veiculos disponiveis");
                saida.println("2 - Cadastrar cliente");
                saida.println("3 - Realizar aluguel");
                saida.println("4 - Contratar servicos extras para um aluguel");
                saida.println("0 - Sair");
                int opcao = lerInteiro("Escolha uma opcao: ", 0, 4);
                switch (opcao) {
                    case 1 -> listarVeiculosDisponiveis();
                    case 2 -> cadastrarCliente();
                    case 3 -> realizarAluguel();
                    case 4 -> contratarServicosExtras();
                    default -> {
                        saida.println("Ate logo!");
                        return;
                    }
                }
            }
        } catch (NoSuchElementException fimDaEntrada) {
            saida.println();
            saida.println("Entrada encerrada. Ate logo!");
        }
    }

    private List<Veiculo> veiculosDisponiveis() {
        return veiculos.stream().filter(Veiculo::isDisponibilidade).toList();
    }

    private void listarVeiculosDisponiveis() {
        List<Veiculo> disponiveis = veiculosDisponiveis();
        if (disponiveis.isEmpty()) {
            saida.println("Nenhum veiculo disponivel no momento.");
            return;
        }
        saida.println("--- Veiculos disponiveis ---");
        for (Veiculo v : disponiveis) {
            saida.printf(PT_BR, "%d - %s | diaria: R$ %.2f%n", v.getId(), v.getModelo(), v.getValorDiaria());
        }
    }

    private void cadastrarCliente() {
        String nome;
        do {
            saida.print("Nome do cliente: ");
            nome = entrada.nextLine().trim();
            if (nome.isEmpty()) {
                saida.println("O nome nao pode ficar vazio.");
            }
        } while (nome.isEmpty());
        Cliente cliente = new Cliente(proximoIdCliente++, nome);
        clientes.add(cliente);
        saida.println("Cliente cadastrado com codigo " + cliente.getId() + ".");
    }

    private void realizarAluguel() {
        if (clientes.isEmpty()) {
            saida.println("Cadastre um cliente antes de realizar um aluguel (opcao 2).");
            return;
        }
        List<Veiculo> disponiveis = veiculosDisponiveis();
        if (disponiveis.isEmpty()) {
            saida.println("Nenhum veiculo disponivel no momento.");
            return;
        }

        Cliente cliente = escolherCliente();
        Veiculo veiculo = escolherVeiculo(disponiveis);
        int dias = lerInteiro("Quantidade de dias: ", 1, 365);

        saida.println("--- Tarifas ---");
        saida.println("1 - Padrao (diaria x dias)");
        saida.println("2 - Longa duracao (15% de desconto a partir de 7 dias)");
        saida.println("3 - Fidelidade (5% de desconto por aluguel anterior do cliente, ate 20%)");
        int opcaoTarifa = lerInteiro("Escolha a tarifa: ", 1, 3);
        TarifaStrategy tarifa;
        String nomeTarifa;
        switch (opcaoTarifa) {
            case 1 -> {
                tarifa = new TarifaPadrao();
                nomeTarifa = "Padrao";
            }
            case 2 -> {
                tarifa = new TarifaLongaDuracao();
                nomeTarifa = "Longa duracao";
            }
            default -> {
                TarifaFidelidade fidelidade = new TarifaFidelidade(alugueisPorCliente.getOrDefault(cliente.getId(), 0));
                tarifa = fidelidade;
                nomeTarifa = String.format(PT_BR, "Fidelidade (%d alugueis anteriores, %.0f%% de desconto)",
                        fidelidade.getAlugueisAnteriores(), fidelidade.getPercentualDesconto() * 100);
            }
        }

        Aluguel aluguel = new Aluguel(cliente, veiculo, dias, tarifa);

        // Decorator: cada adicional escolhido envolve o componente anterior.
        ComponenteAluguel componente = aluguel;
        List<AdicionalDecorator> adicionais = new ArrayList<>();
        if (lerSimNao(String.format(PT_BR, "Adicionar GPS (R$ %.2f por dia)? (s/n): ", GpsDecorator.VALOR_DIARIO))) {
            GpsDecorator gps = new GpsDecorator(componente);
            adicionais.add(gps);
            componente = gps;
        }
        if (lerSimNao(String.format(PT_BR, "Adicionar seguro adicional (R$ %.2f por dia)? (s/n): ",
                SeguroAdicionalDecorator.VALOR_DIARIO))) {
            SeguroAdicionalDecorator seguro = new SeguroAdicionalDecorator(componente);
            adicionais.add(seguro);
            componente = seguro;
        }

        veiculo.setDisponibilidade(false);
        mostrarResumo(aluguel, nomeTarifa, adicionais, componente);

        alugueis.add(new RegistroAluguel(aluguel, nomeTarifa, componente));
        alugueisPorCliente.merge(cliente.getId(), 1, Integer::sum);
        saida.println("Aluguel registrado com codigo " + alugueis.size() + ".");
    }


    private void contratarServicosExtras() {
        if (alugueis.isEmpty()) {
            saida.println("Nenhum aluguel realizado ainda (opcao 3).");
            return;
        }
        saida.println("--- Alugueis realizados ---");
        for (int i = 0; i < alugueis.size(); i++) {
            RegistroAluguel r = alugueis.get(i);
            saida.printf(PT_BR, "%d - %s | %s | %d dias | total: R$ %.2f%n", i + 1, r.aluguel.getCliente().getNome(),
                    r.aluguel.getVeiculo().getModelo(), r.aluguel.getQuantidadeDias(), r.componente.calcularTotal());
        }
        RegistroAluguel registro = alugueis.get(lerInteiro("Codigo do aluguel: ", 1, alugueis.size()) - 1);

        boolean temLimpeza = registro.possui(TaxaLimpezaDecorator.class);
        boolean temAeroporto = registro.possui(TaxaAeroportoDecorator.class);
        if (temLimpeza && temAeroporto) {
            saida.println("Todos os servicos extras ja foram contratados para este aluguel.");
            return;
        }
        if (!temLimpeza && lerSimNao(String.format(PT_BR, "Adicionar taxa de limpeza (R$ %.2f, valor unico)? (s/n): ",
                TaxaLimpezaDecorator.VALOR_FIXO))) {
            registro.aplicar(new TaxaLimpezaDecorator(registro.componente));
        }
        if (!temAeroporto && lerSimNao(String.format(PT_BR, "Retirada no aeroporto (%.0f%% sobre o total)? (s/n): ",
                TaxaAeroportoDecorator.PERCENTUAL * 100))) {
            registro.aplicar(new TaxaAeroportoDecorator(registro.componente));
        }
        mostrarResumoServicos(registro);
    }

    private void mostrarResumoServicos(RegistroAluguel registro) {
        saida.println();
        saida.println("===== SERVICOS EXTRAS =====");
        saida.println("Cliente: " + registro.aluguel.getCliente().getNome());
        saida.println("Veiculo: " + registro.aluguel.getVeiculo().getModelo());
        saida.println("Tarifa: " + registro.nomeTarifa);
        if (registro.taxas.isEmpty()) {
            saida.println("Servicos extras: nenhum");
        } else {
            for (TaxaDecorator t : registro.taxas) {
                saida.printf(PT_BR, "%s (%s): R$ %.2f%n", t.getNome(), t.getRegraCobranca(), t.calcularValorTaxa());
            }
        }
        saida.println("Descricao: " + registro.componente.getDescricao());
        saida.printf(PT_BR, "VALOR TOTAL: R$ %.2f%n", registro.componente.calcularTotal());
    }

    private Cliente escolherCliente() {
        saida.println("--- Clientes ---");
        for (Cliente c : clientes) {
            saida.println(c.getId() + " - " + c.getNome());
        }
        while (true) {
            long id = lerInteiro("Codigo do cliente: ", 1, Integer.MAX_VALUE);
            for (Cliente c : clientes) {
                if (c.getId() == id) {
                    return c;
                }
            }
            saida.println("Cliente nao encontrado.");
        }
    }

    private Veiculo escolherVeiculo(List<Veiculo> disponiveis) {
        listarVeiculosDisponiveis();
        while (true) {
            long id = lerInteiro("Codigo do veiculo: ", 1, Integer.MAX_VALUE);
            for (Veiculo v : disponiveis) {
                if (v.getId() == id) {
                    return v;
                }
            }
            saida.println("Veiculo nao disponivel.");
        }
    }

    private void mostrarResumo(Aluguel aluguel, String nomeTarifa,
                               List<AdicionalDecorator> adicionais, ComponenteAluguel componente) {
        saida.println();
        saida.println("===== RESUMO DO ALUGUEL =====");
        saida.println("Cliente: " + aluguel.getCliente().getNome());
        saida.println("Veiculo: " + aluguel.getVeiculo().getModelo());
        saida.println("Dias: " + aluguel.getQuantidadeDias());
        saida.println("Tarifa: " + nomeTarifa);
        saida.printf(PT_BR, "Valor base: R$ %.2f%n", aluguel.calcularValorBase());
        if (adicionais.isEmpty()) {
            saida.println("Adicionais: nenhum");
        } else {
            for (AdicionalDecorator a : adicionais) {
                saida.printf(PT_BR, "%s (%d x R$ %.2f): R$ %.2f%n", a.getNome(), a.getQuantidadeDias(),
                        a.getValorDiario(), a.calcularValorAdicional());
            }
        }
        saida.println("Descricao: " + componente.getDescricao());
        saida.printf(PT_BR, "VALOR TOTAL: R$ %.2f%n", componente.calcularTotal());
    }

    private int lerInteiro(String mensagem, int minimo, int maximo) {
        while (true) {
            saida.print(mensagem);
            String linha = entrada.nextLine().trim();
            try {
                int valor = Integer.parseInt(linha);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            } catch (NumberFormatException ignorada) {
                // trata abaixo como entrada invalida
            }
            saida.println("Valor invalido. Digite um numero entre " + minimo + " e " + maximo + ".");
        }
    }

    private boolean lerSimNao(String mensagem) {
        while (true) {
            saida.print(mensagem);
            String resposta = entrada.nextLine().trim().toLowerCase(PT_BR);
            if (resposta.equals("s") || resposta.equals("sim")) {
                return true;
            }
            if (resposta.equals("n") || resposta.equals("nao") || resposta.equals("não")) {
                return false;
            }
            saida.println("Responda com s ou n.");
        }
    }
}
