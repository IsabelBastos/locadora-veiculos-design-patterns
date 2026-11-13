# Locadora de Veiculos — Parte 1 (implementacao parcial)

Trabalho academico de Padroes de Projeto em Java. Esta entrega corresponde apenas a metade da Parte 1 dedicada as entidades e ao Strategy. Aplicacao de console, sem banco de dados, Spring ou APIs. Decorator e integracao final ficam para a outra integrante; a Parte 2 nao esta implementada.

## Pre-requisitos

- JDK 17, com JAVA_HOME apontando para o JDK e seu diretorio bin no PATH.
- Nao e necessario instalar Maven nem adicionar `mvn` ao PATH: o Maven Wrapper do projeto baixa e utiliza Maven 3.9.9 automaticamente.

No PowerShell, verifique com `java -version` e `.\mvnw.cmd -version`. Ambos devem utilizar Java 17.

## Execucao e testes

No Windows (PowerShell ou Prompt de Comando), na raiz do projeto:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd package
java -jar target/locadora-veiculos-1.0-SNAPSHOT.jar
```

O empacotamento tambem executa os testes. Na primeira execucao, e necessario acesso a internet para baixar o Maven, dependencias e plugins. O Maven fica no cache do usuario (`~/.m2/wrapper/dists`), sem instalacao global.

No Linux/macOS, use `./mvnw clean test` e `./mvnw package`, seguidos do mesmo comando `java -jar`.

Mantenha `mvnw`, `mvnw.cmd` e `.mvn/wrapper/maven-wrapper.properties` no Git. O Wrapper oficial usa o modo `only-script`, que nao precisa de `maven-wrapper.jar`. A pasta `target/` e gerada durante a compilacao e permanece ignorada pelo Git.

Saida da demonstracao provisoria:

```text
Cliente: Ana
Veiculo: Fiat Argo
Dias: 7
Tarifa padrao: R$ 700,00
Tarifa longa duracao: R$ 595,00
```

## Organizacao

- `pom.xml`: Java 17, JUnit 5 e plugins de compilacao, testes e JAR executavel.
- `mvnw`, `mvnw.cmd` e `.mvn/wrapper/`: Maven Wrapper para executar sem Maven instalado.
- `src/main/java/br/com/locadora/modelo/`: Cliente, Veiculo e Aluguel.
- `src/main/java/br/com/locadora/tarifa/`: TarifaStrategy, TarifaPadrao e TarifaLongaDuracao.
- `src/main/java/br/com/locadora/Main.java`: exemplo sem menu definitivo.
- `src/test/java/br/com/locadora/`: testes das tarifas, validacoes e troca de estrategia.
- `.gitignore`: exclui resultados de compilacao e arquivos locais de ferramentas.

## Strategy

`TarifaStrategy` define `calcular(double valorDiaria, int quantidadeDias)`. `TarifaPadrao` multiplica diaria por dias. `TarifaLongaDuracao` aplica 15% de desconto a partir de 7 dias, inclusive; abaixo disso, cobra o valor normal.

`Aluguel` e o contexto: recebe a interface no construtor, delega o calculo a ela e permite mudar a regra usando `setTarifaStrategy`. Nao depende das classes concretas. O Main demonstra a troca no mesmo aluguel.

Com diaria de R$ 100,00:

| Dias | Padrao | Longa duracao |
| --- | --- | --- |
| 1 | R$ 100,00 | R$ 100,00 |
| 6 | R$ 600,00 | R$ 600,00 |
| 7 | R$ 700,00 | R$ 595,00 |
| 8 | R$ 800,00 | R$ 680,00 |

Dias devem ser maiores que zero. A diaria pode ser zero, mas nao negativa, NaN ou infinita. As entidades e as estrategias validam suas entradas; cliente, veiculo e estrategia do aluguel nao podem ser nulos. O calculo usa double conforme a interface solicitada; a exibicao usa duas casas decimais, sem arredondamento intermediario.

## Preparacao para Decorator e pendencias

`Aluguel.calcularValorBase()` e publico e retorna o total da estrategia escolhida, sem adicionais. `getQuantidadeDias()` fornece a duracao para calcular adicionais diarios. Esses metodos ficam disponiveis para a futura composicao. Nenhuma interface ou classe de Decorator foi antecipada.

Responsabilidades da colega nesta Parte 1:

- Implementar Decorator para GPS (R$ 20 por dia) e seguro adicional (R$ 30 por dia).
- Compor os adicionais com o calculo base e testar cada adicional e suas combinacoes.
- Fazer a integracao final e atualizar a demonstracao e a documentacao.

Exemplo esperado para futura integracao: 7 dias com tarifa longa duracao tem base de R$ 595,00; GPS acrescenta R$ 140,00 e seguro R$ 210,00, totalizando R$ 945,00 com ambos. Esse calculo combinado ainda nao esta implementado.

A disponibilidade e um atributo do veiculo; esta demonstracao de tarifas nao implementa fluxo de reserva ou devolucao. Nao ha servicos extras, classes vazias ou outros padroes de projeto implementados.

## Contribuicoes

- Integrante 1 — [preencher nome]: estrutura Maven, entidades, Strategy, Main provisorio, testes e README inicial.
- Integrante 2 — [preencher nome]: Decorator, testes dos adicionais e integracao final (pendentes).

## Uso de inteligencia artificial

Foi utilizado OpenAI Codex como apoio a implementacao do codigo, testes e documentacao desta metade da Parte 1.

- Modelo exato: [preencher].
- Revisao, compreensao e validacao academica pelas integrantes: [preencher apos revisao].
