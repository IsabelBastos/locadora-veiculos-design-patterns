package br.com.locadora.modelo;

public class Veiculo {
    private final long id;
    private final String modelo;
    private final double valorDiaria;
    private boolean disponibilidade;

    public Veiculo(long id, String modelo, double valorDiaria, boolean disponibilidade) {
        if (!Double.isFinite(valorDiaria) || valorDiaria < 0) {
            throw new IllegalArgumentException("A diaria deve ser finita e nao negativa.");
        }
        this.id = id;
        this.modelo = modelo;
        this.valorDiaria = valorDiaria;
        this.disponibilidade = disponibilidade;
    }

    public long getId() {
        return id;
    }

    public String getModelo() {
        return modelo;
    }

    public double getValorDiaria() {
        return valorDiaria;
    }

    public boolean isDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(boolean disponibilidade) {
        this.disponibilidade = disponibilidade;
    }
}
