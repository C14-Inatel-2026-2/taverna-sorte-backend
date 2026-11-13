# Orientações para agentes — Taverna da Sorte

Instruções explícitas do usuário têm precedência sobre este documento. Requisitos,
contratos da API, pipeline e deploy estão descritos no `README.md`.

## Antes de alterar

- Ler o `README.md`, os testes e as classes relacionadas à tarefa. Os testes
  documentam o comportamento esperado.
- Conferir `git status` e o diff. Preservar alterações locais e não mexer em
  funcionalidades fora do pedido.
- Esclarecer ambiguidades que mudem regras de negócio ou contratos públicos antes
  de implementar. Não inventar métricas, SLAs, prazos ou story points.

## Projeto

- Java 21, Spring Boot, Maven, Spring Data JPA e MySQL. Testes com JUnit 5,
  Mockito e MockMvc.
- Pacote base `com.tavernasorte`: código em `src/main/java`, configurações em
  `src/main/resources` e testes em `src/test/java`.
- Documentação e explicações em português. Identificadores em inglês, seguindo o
  estilo do código próximo.

| Pacote | Responsabilidade |
| --- | --- |
| `controller` | Receber HTTP, validar a entrada, delegar ao serviço e montar a resposta. |
| `service` | Regras de negócio, coordenação de dependências e transações. |
| `database/repository` | Acesso a dados. |
| `database/entity` | Entidades persistidas e relacionamentos. |
| `dto` | Contrato de entrada e saída da API. |
| `model` | Conceitos do domínio sem persistência (ex.: `ChestBetValue`, `Symbol`). |
| `exception` | Falhas traduzidas em respostas pelo `GlobalExceptionHandler`. |

- Controllers não fazem cálculos de jogo nem acessam o banco diretamente.
- Usar injeção por construtor. Sorteios entram pelo construtor (`SymbolPicker`,
  `DiceRoller` ou `Random`) para serem fixados por mock nos testes.
- Usar DTOs para não expor dados internos.
- Fazer só melhorias pequenas ligadas à tarefa. Refatorações profundas ou
  mudanças de arquitetura têm escopo próprio.
- Gerenciar dependências e plugins pelo `pom.xml` (bibliotecas de teste com
  escopo `test`), sem atualizações alheias à tarefa.

## Testes

- Preferir TDD em passos pequenos. Bugs: criar um teste de
  regressão que reproduza a falha antes de corrigir.
- Convenções: sufixo `Test`, métodos sem `public`, Arrange–Act–Assert,
  `@ExtendWith(MockitoExtension.class)` com `@Mock` e testes parametrizados
  quando úteis.
- Testes unitários não dependem de ordem, estado global, rede, banco real ou
  sorteio não controlado.
- Mockar colaboradores, nunca a regra sob teste. DTOs, enums e valores simples
  são objetos reais.
- Priorizar regras, exceções, limites e contratos. Não escrever testes triviais
  de getters/setters nem testes apenas para subir a cobertura.
- Separar os testes por camada:
  - serviço: serviço real, com sorteio e repositórios mockados; verifica regras
    e cálculos;
  - controller: `MockMvc` com `standaloneSetup` e serviço mockado; verifica
    status, campos JSON e mensagens de erro;
  - fluxo: controller e serviço reais, com sorteio e repositórios mockados;
    verifica a integração entre as camadas. Não cobre banco nem servidor real.

  Os testes `EnchantedChest*Test` são o exemplo completo desse padrão.

## Verificação

| Comando | Uso |
| --- | --- |
| `mvn "-Dtest=EnchantedChestServiceTest,EnchantedChestControllerTest,EnchantedChestFlowTest" test` | Cenários do baú durante uma alteração localizada. |
| `mvn test` | Suíte completa. |
| `mvn clean verify` | Validar a entrega e gerar a cobertura JaCoCo em `target/site/jacoco/index.html`. |

- O `Jenkinsfile` roda Build (`mvn clean package -DskipTests`), Tests
  (`mvn verify`, publica JUnit/JaCoCo) e Deploy (hook do Render, só na `main`).
  Não remover nem enfraquecer o estágio de testes.
- Mudança só de documentação não exige testes: revisar conteúdo, caminhos e
  comandos.
- Hook do Render aceito confirma apenas o disparo, não que a aplicação subiu
  saudável.

## Commits

- Criar commits somente quando solicitado, coesos e com arquivos selecionados
  explicitamente. Uma funcionalidade ou correção pode levar seus testes e
  documentação no mesmo commit.
- Conventional Commits: `tipo(escopo): descrição`. Tipo em inglês (`feat`, `fix`,
  `refactor`, `test`, `docs`, `build`, `ci`, `style`, `perf`, `chore`, `revert`);
  descrição em português, verbo no infinitivo, sem ponto final, até ~72
  caracteres. Ex.: `fix(bau): rejeitar aposta acima do limite`.
- Quebra de contrato solicitada: `!` após o tipo/escopo e rodapé
  `BREAKING CHANGE: ...` explicando a migração.
- **IAs não constam como coautoras.** Nenhum trailer `Co-authored-by:` ou
  atribuição equivalente a Claude, Codex, ChatGPT, Copilot ou outro assistente.
  Revisar a mensagem completa, inclusive trailers inseridos automaticamente por
  ferramentas, e não alterar a identidade Git configurada.
- Não versionar `target/`, `.env`, credenciais, tokens ou hooks.

## Entrega

Explicar brevemente o que mudou, quais comandos foram executados e seus
resultados, e quais limitações restam (testes ausentes, build quebrada, débito
técnico). Não afirmar aprovação do usuário nem deploy não observado.
