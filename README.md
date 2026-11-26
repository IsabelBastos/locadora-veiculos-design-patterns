# Locadora de Veiculos — Parte 1

Trabalho academico de Padroes de Projeto em Java. Aplicacao de console, sem banco de dados, Spring ou APIs, que aplica dois padroes de projeto: **Strategy** (regra de calculo da tarifa) e **Decorator** (servicos adicionais do aluguel). A Parte 2 nao esta implementada.

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

## Menu do console

Ao executar o JAR, o menu oferece:

1. Listar veiculos disponiveis.
2. Cadastrar cliente.
3. Realizar aluguel: escolher cliente, veiculo e quantidade de dias, selecionar a tarifa (padrao ou longa duracao), escolher os adicionais (GPS, seguro, os dois ou nenhum) e ver o resumo com o valor total.
0. Sair.

O sistema ja vem com quatro veiculos cadastrados (um deles indisponivel, para demonstrar o filtro). Ao concluir um aluguel, o veiculo escolhido passa a ficar indisponivel. Entradas invalidas (texto, numeros fora da faixa, nome vazio) sao reperguntadas.

Exemplo de resumo (Fiat Argo, 7 dias, tarifa longa duracao, GPS e seguro):

```text
===== RESUMO DO ALUGUEL =====
Cliente: Ana
Veiculo: Fiat Argo
Dias: 7
Tarifa: Longa duracao
Valor base: R$ 595,00
GPS (7 x R$ 20,00): R$ 140,00
Seguro adicional (7 x R$ 30,00): R$ 210,00
Descricao: Aluguel do Fiat Argo + GPS + Seguro adicional
VALOR TOTAL: R$ 945,00
```

## Organizacao

- `pom.xml`: Java 17, JUnit 5 e plugins de compilacao, testes e JAR executavel.
- `mvnw`, `mvnw.cmd` e `.mvn/wrapper/`: Maven Wrapper para executar sem Maven instalado.
- `src/main/java/br/com/locadora/modelo/`: Cliente, Veiculo e Aluguel.
- `src/main/java/br/com/locadora/tarifa/`: TarifaStrategy, TarifaPadrao e TarifaLongaDuracao (Strategy).
- `src/main/java/br/com/locadora/adicional/`: ComponenteAluguel, AdicionalDecorator, GpsDecorator e SeguroAdicionalDecorator (Decorator).
- `src/main/java/br/com/locadora/console/MenuLocadora.java`: menu interativo do console.
- `src/main/java/br/com/locadora/Main.java`: ponto de entrada, apenas inicia o menu.
- `src/test/java/br/com/locadora/`: testes das tarifas, do aluguel, dos adicionais (incluindo a combinacao com o Strategy) e do fluxo do menu.
- `.gitignore`: exclui resultados de compilacao e arquivos locais de ferramentas.

## Strategy

`TarifaStrategy` define `calcular(double valorDiaria, int quantidadeDias)`. `TarifaPadrao` multiplica diaria por dias. `TarifaLongaDuracao` aplica 15% de desconto a partir de 7 dias, inclusive; abaixo disso, cobra o valor normal.

`Aluguel` e o contexto: recebe a interface no construtor, delega o calculo a ela e permite mudar a regra usando `setTarifaStrategy`. Nao depende das classes concretas. O teste `AluguelTest` demonstra a troca no mesmo aluguel.

Com diaria de R$ 100,00:

| Dias | Padrao | Longa duracao |
| --- | --- | --- |
| 1 | R$ 100,00 | R$ 100,00 |
| 6 | R$ 600,00 | R$ 600,00 |
| 7 | R$ 700,00 | R$ 595,00 |
| 8 | R$ 800,00 | R$ 680,00 |

Dias devem ser maiores que zero. A diaria pode ser zero, mas nao negativa, NaN ou infinita. As entidades e as estrategias validam suas entradas; cliente, veiculo e estrategia do aluguel nao podem ser nulos. O calculo usa double conforme a interface solicitada; a exibicao usa duas casas decimais, sem arredondamento intermediario.

## Decorator

O Decorator permite acrescentar servicos extras ao aluguel sem alterar a classe `Aluguel` nem criar uma subclasse para cada combinacao (com GPS, com seguro, com os dois...).

- `ComponenteAluguel` (componente): interface com `calcularTotal()`, `getDescricao()` e `getQuantidadeDias()`.
- `Aluguel` (componente concreto): implementa a interface; sem adicionais, o total e o valor base calculado pelo Strategy.
- `AdicionalDecorator` (decorator abstrato): guarda o componente envolvido e soma ao total dele `valor diario do adicional x dias`. Tambem acrescenta o nome do adicional a descricao.
- `GpsDecorator`: R$ 20,00 por dia.
- `SeguroAdicionalDecorator`: R$ 30,00 por dia.

Como cada decorator tambem e um `ComponenteAluguel`, eles podem ser empilhados livremente:

```java
ComponenteAluguel aluguel = new Aluguel(cliente, veiculo, 7, new TarifaLongaDuracao());
aluguel = new GpsDecorator(aluguel);              // + R$ 140,00
aluguel = new SeguroAdicionalDecorator(aluguel);  // + R$ 210,00
aluguel.calcularTotal();                          // R$ 945,00
```

No menu, cada adicional escolhido envolve o componente anterior; se nenhum for escolhido, o proprio `Aluguel` e usado. A ordem dos decorators nao altera o total.

### Combinacao Strategy + Decorator

O Strategy calcula apenas o valor da diaria do veiculo; os adicionais sao somados depois, por isso o desconto de longa duracao **nao** incide sobre GPS e seguro. Como o decorator chama o componente a cada calculo, trocar a estrategia do `Aluguel` com `setTarifaStrategy` reflete imediatamente no total decorado.

Com diaria de R$ 100,00 e os dois adicionais (R$ 50,00 por dia):

| Dias | Padrao | Longa duracao |
| --- | --- | --- |
| 1 | R$ 150,00 | R$ 150,00 |
| 6 | R$ 900,00 | R$ 900,00 |
| 7 | R$ 1.050,00 | R$ 945,00 |
| 8 | R$ 1.200,00 | R$ 1.080,00 |

## Testes

- `TarifasTest`: calculo das duas tarifas, limite de 7 dias e validacoes.
- `AluguelTest`: troca de estrategia, delegacao e validacoes do aluguel.
- `DecoratorTest`: total sem adicionais, com GPS, com seguro, com os dois, ordem dos decorators, combinacao com cada tarifa, troca de estrategia apos decorar, descricao e componente nulo.
- `MenuLocadoraTest`: simula a digitacao no menu e confere o resumo (com e sem adicionais), a indisponibilidade do veiculo alugado e o tratamento de entradas invalidas.

## Contribuicoes

Responsaveis: Isabel Bastos, Elias Maciel e Sophia dos Santos.

- Isabel Bastos: estrutura Maven, entidades, Strategy, Main provisorio, testes das tarifas e do aluguel e README inicial.
- Elias Maciel: [preencher contribuicao].
- Sophia dos Santos: Decorator (GPS e seguro adicional), menu do console, integracao final, testes do Decorator e do menu e finalizacao do README.

## Uso de inteligencia artificial

- Isabel Bastos: foi utilizado OpenAI Codex como apoio a implementacao do codigo, testes e documentacao das entidades e do Strategy. Modelo exato: [preencher].
- Sophia dos Santos: foi utilizado Claude Code (Anthropic), modelo Claude Opus 5.5, como apoio a implementacao do Decorator, do menu, dos testes correspondentes e desta documentacao.
- Revisao, compreensao e validacao academica pelos responsaveis: [preencher apos revisao].

---

# Parte 2 — Extensao

Extensao desenvolvida a partir de um fork do projeto da equipe dona (registrada acima), na branch `feature/extensao-parte2`. Ela amplia os dois padroes sem alterar a interface `TarifaStrategy`, a classe `Aluguel`, os decorators originais nem os testes existentes; o fluxo original do menu (opcoes 1 a 3) continua respondendo as mesmas perguntas, na mesma ordem.

## Strategy: tarifa de fidelidade

`TarifaFidelidade` (pacote `tarifa`) da 5% de desconto para cada aluguel anterior do cliente, limitado a 20%. Diferente das estrategias originais, ela guarda um estado: o numero de alugueis anteriores e recebido no construtor (`new TarifaFidelidade(2)`), por isso nao foi preciso mudar a assinatura `calcular(valorDiaria, quantidadeDias)`.

Com diaria de R$ 100,00 e 10 dias:

| Alugueis anteriores | Desconto | Valor |
| --- | --- | --- |
| 0 | 0% | R$ 1.000,00 |
| 1 | 5% | R$ 950,00 |
| 2 | 10% | R$ 900,00 |
| 4 ou mais | 20% | R$ 800,00 |

No menu, ela aparece como a tarifa `3` ao realizar um aluguel. O menu conta quantos alugueis cada cliente ja fez e cria a estrategia com esse numero.

## Decorator: taxas com outras formas de cobranca

O `AdicionalDecorator` original sempre cobra `valor diario x dias`. A extensao cria um segundo decorator base, `TaxaDecorator`, que tambem implementa `ComponenteAluguel`, mas deixa cada subclasse definir como cobra (`calcularValorTaxa()`):

- `TaxaLimpezaDecorator`: R$ 80,00 de valor unico, independente dos dias.
- `TaxaAeroportoDecorator` (retirada no aeroporto): 10% sobre o total do componente que ele envolve.

Por implementarem a mesma interface, as taxas empilham livremente com GPS e seguro:

```java
ComponenteAluguel aluguel = new Aluguel(cliente, veiculo, 7, new TarifaLongaDuracao()); // 595,00
aluguel = new GpsDecorator(aluguel);              // + 140,00
aluguel = new SeguroAdicionalDecorator(aluguel);  // + 210,00
aluguel = new TaxaLimpezaDecorator(aluguel);      // +  80,00
aluguel = new TaxaAeroportoDecorator(aluguel);    // + 10% de 1.025,00 = 102,50
aluguel.calcularTotal();                          // 1.127,50
```

**A ordem passa a importar.** Com a taxa percentual, a ordem dos decorators muda o total: a retirada no aeroporto so incide sobre o que ja estava contratado quando ela foi adicionada. Por exemplo, com base de R$ 300,00, `Limpeza(Aeroporto(aluguel))` da R$ 410,00 e `Aeroporto(Limpeza(aluguel))` da R$ 418,00.

**Decoracao depois do aluguel.** A nova opcao `4` do menu escolhe um aluguel ja realizado e o envolve com as taxas, mostrando que os decorators podem ser acrescentados em tempo de execucao a um objeto existente. Cada taxa so pode ser contratada uma vez por aluguel.

## Menu apos a extensao

1. Listar veiculos disponiveis.
2. Cadastrar cliente.
3. Realizar aluguel: agora com a tarifa `3 - Fidelidade`. Ao final, informa o codigo do aluguel registrado.
4. **(novo)** Contratar servicos extras para um aluguel: lista os alugueis realizados, pergunta pela taxa de limpeza e pela retirada no aeroporto e mostra o novo total.
0. Sair.

## Como testar a extensao

Compilacao, testes e execucao continuam como na Parte 1:

```powershell
.\mvnw.cmd clean package
java -jar target/locadora-veiculos-1.0-SNAPSHOT.jar
```

Roteiro manual sugerido:

1. Opcao `2`: cadastre "Ana".
2. Opcao `3`: cliente `1`, veiculo `1` (Fiat Argo), `3` dias, tarifa `3`, sem adicionais. Resultado: fidelidade com 0% de desconto e total de R$ 300,00.
3. Opcao `3`: cliente `1`, veiculo `2` (Chevrolet Onix), `3` dias, tarifa `3`, sem adicionais. Resultado: 5% de desconto e total de R$ 342,00.
4. Opcao `4`: aluguel `1`, `s` para limpeza e `s` para aeroporto. Resultado: limpeza de R$ 80,00, aeroporto de R$ 38,00 (10% de 380,00) e total de R$ 418,00.
5. Opcao `4` novamente com o aluguel `1`: o sistema avisa que todos os servicos ja foram contratados.

Testes automatizados da extensao (o build passa a executar 71 testes):

- `TarifaFidelidadeTest`: desconto por quantidade de alugueis, limite de 20%, equivalencia com a tarifa padrao sem historico, troca de estrategia no `Aluguel` e validacoes.
- `TaxaDecoratorTest`: valor fixo da limpeza, percentual do aeroporto, empilhamento com GPS e seguro, ordem dos decorators, troca de estrategia refletindo no percentual e componente nulo.
- `MenuExtensaoTest`: fidelidade crescendo a cada aluguel e contada por cliente, servicos extras sobre um aluguel ja realizado, bloqueio de servico repetido e opcao 4 sem alugueis.

## Arquivos da extensao

- Novos: `tarifa/TarifaFidelidade.java`, `adicional/TaxaDecorator.java`, `adicional/TaxaLimpezaDecorator.java`, `adicional/TaxaAeroportoDecorator.java` e os tres testes acima.
- Alterado: `console/MenuLocadora.java`, apenas para acrescentar a tarifa 3, a opcao 4 e o registro dos alugueis realizados. As perguntas originais foram mantidas.
- Alterado: `README.md`, com o acrescimo desta secao. O conteudo da equipe dona foi preservado.

## Contribuicoes da extensao

- [preencher integrante]: [preencher contribuicao].
