package br.com.locadora.adicional;

/**
 * Componente do padrao Decorator: tudo que pode ter seu valor total calculado.
 * Implementado por {@link br.com.locadora.modelo.Aluguel} (componente concreto)
 * e pelos decoradores de servicos adicionais.
 */
public interface ComponenteAluguel {
    double calcularTotal();

    String getDescricao();

    int getQuantidadeDias();
}
