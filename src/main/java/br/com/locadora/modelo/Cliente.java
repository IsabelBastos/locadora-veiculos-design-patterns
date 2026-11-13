package br.com.locadora.modelo;

public class Cliente {
    private final long id;
    private final String nome;

    public Cliente(long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
