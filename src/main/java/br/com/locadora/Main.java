package br.com.locadora;

import br.com.locadora.console.MenuLocadora;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new MenuLocadora(new Scanner(System.in), System.out).executar();
    }
}
