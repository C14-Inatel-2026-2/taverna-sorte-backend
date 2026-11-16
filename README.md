# Taverna da Sorte — Backend

Backend de uma taverna medieval com moedas, inventário, loja e minigames, desenvolvido para a disciplina C14 — Engenharia de Software do Inatel. A API recebe requisições HTTP em JSON e concentra as regras de negócio e a persistência.

O frontend oficial é desenvolvido em Flutter, em outro repositório: [taverna-sorte-frontend](https://github.com/C14-Inatel-2026-2/taverna-sorte-frontend).

## Navegação

- [Equipe e papéis](#equipe-e-papéis)
- [Escopo e funcionalidades](#escopo-e-funcionalidades)
- [Tecnologias e arquitetura](#tecnologias-e-arquitetura)
- [Instalação e execução local](#instalação-e-execução-local)
- [Uso da API](#uso-da-api)
- [Testes automatizados](#testes-automatizados)
- [CI/CD com Jenkins e Render](#cicd-com-jenkins-e-render)
- [Histórias de usuário e rastreabilidade](#histórias-de-usuário-e-rastreabilidade)
- [Metodologia de desenvolvimento](#metodologia-de-desenvolvimento)
- [Dinâmica de desenvolvimento](#dinâmica-de-desenvolvimento)
- [Exemplos de Refatorações e motivos](#exemplos-de-refatorações-e-motivos)
- [Uso de IA](#uso-de-ia)

## Equipe e papéis

- [David Fernandes Barbon](https://github.com/DavidFBar) (Dev Backend e Devops)
- [João Victor Samuel de Faria](https://github.com/joaovsfaria) (Dev Backend e DevOps)
- [João Vitor Souza Aldet](https://github.com/joaoaldet) (Dev Backend e Devops)
- [Lucas Emanuel da Silva Godoi](https://github.com/luscagodoi) (Dev Frontend e Devops)
- [Luiz Gustavo Guimarães Santos](https://github.com/LuizGuimaraesz) ( Dev Backend e Devops)

## Escopo e funcionalidades

O escopo atual inclui jogador sem login, loja, Dice, Baú Encantado e Roleta. A remoção de usuários e histórico da roleta foi integrada à main pelo [PR #39](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/pull/39), a partir do [commit 45d98c6](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/commit/45d98c6).

### Requisitos funcionais

Descrevem as ações e regras de negócio que o sistema deve oferecer ao jogador.

| Identificador | Requisito                           | Descrição                                                                                                                                    |
| ------------- | ----------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| RF01          | Criar e consultar jogador sem login | Permitir criar um jogador e consultar seu identificador, username, saldo e inventário.                                                       |
| RF02          | Recuperar moedas                    | Conceder 100 moedas ao dançar para o gnomo somente quando o saldo do jogador for zero.                                                       |
| RF03          | Consultar a loja e comprar itens    | Listar e consultar itens, debitar o preço da compra e adicionar o item ao inventário, verificando saldo suficiente e o limite de três itens. |
| RF04          | Jogar Dice                          | Receber uma aposta LOW, SEVEN ou HIGH, sortear dois dados, calcular o prêmio e atualizar o saldo, permitindo consumir um item opcional.      |
| RF05          | Jogar Baú Encantado                 | Receber uma aposta, sortear três símbolos, calcular o prêmio da combinação e atualizar o saldo, permitindo consumir um item opcional.        |
| RF06          | Jogar Roleta                        | Receber uma aposta em número ou cor, sortear um número entre 0 e 36 e retornar o resultado e o prêmio da rodada.                             |

Cadastro de usuários, login, JWT e histórico de partidas ficam fora desse escopo. O identificador de jogador não comprova identidade nem oferece controle de acesso.

### Requisitos não funcionais

Descrevem as características de qualidade e as restrições técnicas da implementação.

| Identificador | Requisito                     | Descrição                                                                                                                                                       |
| ------------- | ----------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| RNF01         | Organização em camadas        | Separar controllers, services, repositories, entidades, DTOs, modelos de domínio e exceções, mantendo as regras de negócio fora dos controllers.                |
| RNF02         | Persistência                  | Utilizar MySQL com Spring Data JPA para armazenar jogadores, inventários e itens.                                                                               |
| RNF03         | Padronização da API           | Utilizar HTTP e JSON, DTOs em records, códigos HTTP adequados e respostas de erro centralizadas no formato `{"error":"mensagem"}`.                              |
| RNF04         | Integridade das operações     | Executar compras, danças e partidas de Dice/Baú em transações, permitindo reverter alterações persistidas quando a operação falhar.                             |
| RNF05         | Testabilidade                 | Permitir o controle dos sorteios e a substituição de repositories por mocks, com testes automatizados de regras de negócio, validações e contratos HTTP.        |
| RNF06         | Gerenciamento de dependências | Utilizar Maven e o pom.xml para definir dependências, plugins e compilação para Java 21.                                                                        |
| RNF07         | Integração contínua           | Utilizar Jenkins para executar build e testes e publicar relatórios JUnit e JaCoCo.                                                                             |
| RNF08         | Empacotamento e implantação   | Empacotar a aplicação em Docker e configurar o disparo de deploy no Render pela pipeline, com porta e conexão ao banco configuráveis por variáveis de ambiente. |

### Regras de saldo, apostas e itens

- Todos os valores de moedas são inteiros.
- O jogador é criado com saldo zero. A dança para o gnomo permite iniciar o fluxo com 100 moedas.
- O saldo deve permanecer entre 0 e 1.000.000 de moedas.
- Dice e Baú aceitam apostas entre 1 e 10.000 moedas e exigem saldo suficiente.
- O inventário comporta até três itens. Uma compra debita o preço e adiciona uma unidade ao inventário.
- Os multiplicadores dos itens variam de 1 a 20.
- Em Dice e Baú, o jogador pode escolher um item por rodada. O item precisa pertencer ao inventário e é consumido mesmo na derrota; quando há prêmio, aplica seu multiplicador.
- Antes de debitar a aposta, os jogos verificam se o maior prêmio possível ultrapassaria o saldo máximo. Uma aposta recusada preserva as moedas e os itens.

### Dice

Dois dados de seis faces são sorteados. O prêmio representa o retorno total da rodada, e o saldo final é calculado por `saldo anterior - aposta + prêmio`.

| Aposta  | Soma vencedora | Multiplicador do jogo |
| ------- | -------------- | --------------------- |
| `LOW`   | 2 a 6          | 2                     |
| `SEVEN` | 7              | 4                     |
| `HIGH`  | 8 a 12         | 2                     |

Na vitória: `prêmio = aposta × multiplicador do jogo × multiplicador do item`. Sem item, o multiplicador do item é 1. Na derrota, o prêmio é zero.

### Baú Encantado

São sorteados três símbolos: `COIN`, `POTION`, `SWORD`, `CROWN` ou `DRAGON`, com multiplicadores 3, 5, 8, 12 e 20, respectivamente.

| Combinação                      | Prêmio antes do multiplicador do item     |
| ------------------------------- | ----------------------------------------- |
| Três símbolos iguais            | Aposta multiplicada pelo valor do símbolo |
| Exatamente dois símbolos iguais | Valor da aposta                           |
| Três símbolos diferentes        | Zero                                      |

Quando um item é utilizado, seu multiplicador incide sobre o prêmio dessa combinação, inclusive sobre o retorno de um par.

### Roleta

A roleta europeia sorteia um número entre 0 e 36. O zero é verde; os demais seguem uma tabela fixa de vermelho e preto.

| Aposta   | Escolha              | Retorno total na vitória |
| -------- | -------------------- | ------------------------ |
| `NUMBER` | Número entre 0 e 36  | 36 vezes a aposta        |
| `RED`    | Sem número escolhido | 2 vezes a aposta         |
| `BLACK`  | Sem número escolhido | 2 vezes a aposta         |

Apostas em cor perdem quando sai zero. A aposta deve ser positiva, e prêmios que ultrapassariam a capacidade de um `int` são recusados antes do sorteio. O limite de 10.000 moedas de Dice/Baú não está aplicado à roleta.

A rota de aposta valida a existência do jogador e retorna o resultado. O prêmio informado ainda não é creditado, e o valor apostado não é debitado.

## Tecnologias e arquitetura

- Java 21 e Spring Boot.
- Maven para dependências, build e testes.
- Spring Web, Bean Validation, Spring Data JPA, Hibernate e MySQL.
- JUnit 5, Mockito e MockMvc para testes.
- JaCoCo para relatório de cobertura.
- Docker, Jenkins e Render para empacotamento, CI/CD e implantação.

As versões das dependências e dos plugins são definidas no [pom.xml](pom.xml). O projeto usa o gerenciamento de versões do Spring Boot.

| Pacote em `src/main/java/com/tavernasorte` | Responsabilidade                                                       |
| ------------------------------------------ | ---------------------------------------------------------------------- |
| `controller`                               | Receber HTTP, validar a entrada e delegar aos serviços                 |
| `service`                                  | Aplicar regras, coordenar dependências e controlar transações          |
| `database/entity`                          | Representar Player, Inventory e Item e seus relacionamentos JPA        |
| `database/repository`                      | Acessar os dados com Spring Data JPA                                   |
| `dto`                                      | Definir os contratos da API por meio de records                        |
| `model`                                    | Representar conceitos de domínio, como BetType, Symbol e ChestBetValue |
| `exception`                                | Definir exceções de negócio e centralizar suas respostas HTTP          |

As dependências são recebidas por construtor. Os sorteios podem ser controlados nos testes por `DiceRoller`, `SymbolPicker` e `Random`. Controllers não executam cálculos de jogo nem acessam repositories diretamente.

## Instalação e execução local

### Pré-requisitos

- JDK 21.
- Maven disponível no terminal.
- Docker Desktop com containers Linux e Docker Compose, ou MySQL configurado manualmente.
- Portas 8080 para a API e 3307 para o MySQL do Compose. O Jenkins utiliza 8081.

### Iniciar o banco e a API

Clone o repositório e entre na pasta:

```powershell
git clone https://github.com/C14-Inatel-2026-2/taverna-sorte-backend.git
cd taverna-sorte-backend
```

Prepare o arquivo local de ambiente sem sobrescrever um arquivo existente:

```powershell
if (-not (Test-Path .env)) {
    Copy-Item .env.example .env
}
```

O `.env` é usado pela configuração do Jenkins no Compose e não deve ser versionado. A conexão local da aplicação é definida em [application.yml](src/main/resources/application.yml), não pela leitura automática desse arquivo.

Inicie o MySQL:

```powershell
docker compose up -d mysql
docker compose logs -f mysql
```

Aguarde o banco ficar disponível. Saia do acompanhamento dos logs com `Ctrl+C` e inicie a API:

```powershell
mvn spring-boot:run
```

A URL local é `http://localhost:8080`. A configuração usa o banco `taverna_sorte` na porta 3307, com as credenciais de desenvolvimento definidas no [docker-compose.yml](docker-compose.yml).

O Hibernate utiliza `ddl-auto: update` para atualizar o esquema. Isso não substitui migrações de banco nem apaga automaticamente tabelas de módulos removidos.

## Uso da API

### Endpoints

| Método | Rota                                 | Retorno de sucesso                                     |
| ------ | ------------------------------------ | ------------------------------------------------------ |
| POST   | `/players`                           | 201 com os dados do jogador criado                     |
| GET    | `/players/{id}`                      | 200 com jogador, saldo e inventário                    |
| POST   | `/players/{id}/dance`                | 200 sem corpo                                          |
| GET    | `/shop/items`                        | 200 com a lista de itens                               |
| GET    | `/shop/items/{id}`                   | 200 com os dados do item                               |
| POST   | `/shop/purchase/{playerId}/{itemId}` | 200 sem corpo                                          |
| POST   | `/dice/play`                         | 200 com dados, resultado, prêmio e saldo               |
| POST   | `/chest/open`                        | 200 com símbolos, prêmio e saldo                       |
| GET    | `/roleta/girar`                      | 200 com um número JSON entre 0 e 36                    |
| POST   | `/roleta/{playerId}/jogar`           | 200 com aposta, número sorteado, cor, vitória e prêmio |

### Fluxo de jogador sem login

1. Criar o jogador com `POST /players`, enviando `{"username":"Aventureiro"}`.
2. Guardar o `id` recebido. No frontend, o identificador pode ser mantido no armazenamento local; saldo e inventário devem ser consultados na API.
3. Chamar `POST /players/{id}/dance` para receber 100 moedas enquanto o saldo estiver zerado.
4. Consultar os itens da loja e comprar um item, se desejado.
5. Jogar Dice ou Baú, com ou sem `itemId`.
6. Consultar novamente `GET /players/{id}` para verificar saldo e inventário.

### Exemplo de Dice

Requisição para `POST /dice/play`:

```json
{ "playerId": 1, "betType": "LOW", "betAmount": 10 }
```

O campo opcional `itemId` identifica o item escolhido. Uma possível resposta, com saldo anterior de 100 moedas e sem item, é:

```json
{
  "die1": 2,
  "die2": 3,
  "sum": 5,
  "betType": "LOW",
  "won": true,
  "prize": 20,
  "balance": 110,
  "consumedItemId": null,
  "itemMultiplier": 1
}
```

### Exemplo de Baú

Requisição para `POST /chest/open`:

```json
{ "playerId": 1, "betAmount": 10, "itemId": 3 }
```

Com saldo anterior de 1000 moedas e um item de multiplicador 2, uma possível resposta é:

```json
{ "symbols": ["COIN", "COIN", "POTION"], "prize": 20, "balance": 1010 }
```

O item 3 precisa existir no inventário desse jogador. Os exemplos representam resultados possíveis; os sorteios reais variam.

### Exemplo de Roleta

Requisição para `POST /roleta/1/jogar`:

```json
{ "betType": "NUMBER", "chosenNumber": 7, "betAmount": 10 }
```

Uma possível resposta é:

```json
{
  "betType": "NUMBER",
  "chosenNumber": 7,
  "betAmount": 10,
  "drawnNumber": 7,
  "color": "RED",
  "won": true,
  "prize": 360
}
```

Esse resultado não altera o saldo do jogador.

### Validações e erros

As respostas de erro são centralizadas no `GlobalExceptionHandler` e utilizam o formato:

```json
{ "error": "Saldo insuficiente para a aposta" }
```

| Status | Exemplos                                                                                                                     |
| ------ | ---------------------------------------------------------------------------------------------------------------------------- |
| 400    | Entrada inválida, saldo insuficiente, inventário cheio, item fora do inventário ou prêmio potencial acima do limite de saldo |
| 404    | Jogador, item ou recurso não encontrado                                                                                      |
| 405    | Método HTTP não permitido                                                                                                    |
| 415    | Tipo de conteúdo não suportado                                                                                               |
| 500    | Falha inesperada; detalhes são registrados no servidor                                                                       |

Dice e Baú rejeitam campos JSON desconhecidos. Valores com casas decimais em campos inteiros são recusados, sem truncamento silencioso.

## Testes automatizados

Os testes verificam regras e contratos do domínio: vitórias e derrotas, cálculo de prêmios, limites de saldo e aposta, consumo de itens, compras recusadas e erros HTTP.

| Camada              | Como é testada                                              | Exemplos                                                                          |
| ------------------- | ----------------------------------------------------------- | --------------------------------------------------------------------------------- |
| Entidades e valores | Objetos reais e verificação de invariantes                  | PlayerTest, InventoryTest e ItemTest                                              |
| Services            | Serviço real, repositories e sorteios mockados              | DiceServiceTest, EnchantedChestServiceTest, ShopServiceTest e RouletteServiceTest |
| Controllers         | MockMvc standalone, serviço mockado e advice real           | DiceControllerTest, PlayerControllerTest e ShopControllerTest                     |
| Fluxo               | Controller e serviço reais, repositories e sorteio mockados | EnchantedChestFlowTest                                                            |

Esses testes não dependem de MySQL nem de servidor HTTP em execução. O teste de fluxo não comprova a integração com o banco ou com o frontend.

```powershell
mvn test
mvn clean verify
```

O último comando compila, executa os testes, gera o JAR e o relatório JaCoCo. Consulte os resultados de cada execução em:

- `target/surefire-reports/`: relatórios JUnit.
- `target/site/jacoco/index.html`: cobertura.
- `target/taverna-sorte-backend-1.0-SNAPSHOT.jar`: aplicação empacotada.

A quantidade de testes e a cobertura devem ser obtidas da execução correspondente ao commit apresentado.

## CI/CD com Jenkins e Render

O projeto utiliza Jenkins, sem GitHub Actions. A configuração está no [Jenkinsfile](Jenkinsfile), no [docker-compose.yml](docker-compose.yml) e na pasta [jenkins](jenkins/).

### Executar o Jenkins

1. Prepare o `.env` a partir de `.env.example`.
2. Preencha `JENKINS_ADMIN_ID`, `JENKINS_ADMIN_PASSWORD`, `GITHUB_REPO_URL` e `RENDER_DEPLOY_HOOK`.
3. Inicie o serviço:

```powershell
docker compose up -d --build jenkins
docker compose logs -f jenkins
```

A imagem do Jenkins inclui Java 21 e Maven. Quando aparecer `Jenkins is fully up and running`, acesse `http://localhost:8081` com as credenciais configuradas.

O job multibranch `taverna-sorte` é criado por [jenkins/casc.yaml](jenkins/casc.yaml). Execute **Scan Multibranch Pipeline Now** para descobrir as branches e abra o build para consultar o console, os resultados JUnit e a cobertura.

O Jenkins busca o código no remoto: alterações apenas locais não entram na pipeline. A descoberta é configurada para verificação periódica de um minuto. A origem Git atual não configura credenciais; preencher `GITHUB_USER` e `GITHUB_TOKEN` no `.env` não habilita acesso a repositórios privados por si só.

### Estágios e evidências no Git

| Estágio | Execução                                                              | Commit de introdução                                                                                  |
| ------- | --------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------- |
| Build   | `mvn clean package -DskipTests` e arquivamento do JAR                 | [377379c — LuizGuimaraesz](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/commit/377379c) |
| Tests   | `mvn verify`, publicação de JUnit e JaCoCo                            | [3ce4fac — DavidFBar](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/commit/3ce4fac)      |
| Deploy  | POST no hook do Render, somente na main e após os estágios anteriores | [055d6ef — joaovsfaria](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/commit/055d6ef)    |

### Deploy e banco de produção

Configure o Render como Web Service Docker a partir do [Dockerfile](Dockerfile), e mantenha o Auto-Deploy desligado caso o disparo seja feito exclusivamente pelo Jenkins. O hook fica na credencial `render-deploy-hook`, alimentada por `RENDER_DEPLOY_HOOK`.

A aplicação lê a porta pela variável `PORT`. Para substituir a conexão local, configure no serviço:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Após alterar as variáveis locais do Jenkins, recrie seu container:

```powershell
docker compose up -d --build --force-recreate jenkins
```

Uma resposta bem-sucedida do hook comprova o disparo do deploy. A conclusão e a saúde da aplicação precisam ser verificadas no Render. URLs de builds, evidências de execução e endereço publicado devem ser anexados pelo grupo; não são presumidos por este README.

## Histórias de usuário e rastreabilidade

As histórias abaixo descrevem as funcionalidades verificáveis no código. Os identificadores HU01–HU06 são locais a este README; não substituem os IDs das issues no Jira.

### HU01 — Criar e consultar jogador — entregue

Como jogador sem login, eu quero criar meu personagem e consultar seus dados para acompanhar minhas moedas e meu inventário.

- Dado um username válido, quando eu criar um jogador, então devo receber seu identificador, saldo zero e inventário vazio.
- Dado um identificador inexistente, quando eu consultar o jogador, então devo receber 404 com mensagem de erro.
- Rastreabilidade: [commit 9ca89ee](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/commit/9ca89ee) → [PlayerServiceTest](src/test/java/com/tavernasorte/service/PlayerServiceTest.java), cenários `shouldCreatePlayerWithValidUsername` e `shouldRejectLookupWhenPlayerDoesNotExist`.

### HU02 — Recuperar moedas — entregue

Como jogador sem moedas, eu quero dançar para o gnomo para poder voltar a jogar.

- Dado saldo zero, quando eu dançar, então meu saldo deve receber 100 moedas.
- Dado saldo positivo, quando eu tentar dançar, então a operação deve ser recusada e o saldo preservado.
- Rastreabilidade: [commit 67c47d6](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/commit/67c47d6) → [PlayerServiceTest](src/test/java/com/tavernasorte/service/PlayerServiceTest.java), cenários `shouldAdd100BalanceWhenPlayerHasNoBalance` e `shouldRejectDanceWhenPlayerHasBalance`.

### HU03 — Comprar itens — entregue

Como jogador, eu quero comprar itens na loja para aumentar o prêmio de uma rodada.

- Dado saldo suficiente e espaço no inventário, quando eu comprar, então o preço deve ser debitado e o item adicionado.
- Dado saldo insuficiente ou inventário cheio, quando eu tentar comprar, então devo receber uma recusa sem perder moedas nem alterar o inventário.
- Rastreabilidade: [PR #35](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/pull/35) e [commit a30f9e5](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/commit/a30f9e5) → [ShopServiceTest](src/test/java/com/tavernasorte/service/ShopServiceTest.java), cenários `shouldAllowPurchaseWithExactBalance` e `shouldPreserveBalanceWithFullInventory`.

### HU04 — Jogar Dice com item opcional — entregue

Como jogador, eu quero apostar na soma dos dados e escolher um item para tentar aumentar meu retorno.

- Dado saldo suficiente e uma aposta válida, quando os dados forem sorteados, então o resultado deve respeitar LOW, SEVEN ou HIGH e o saldo deve considerar débito e prêmio.
- Dado um item do meu inventário, quando a rodada terminar, então uma unidade deve ser consumida, inclusive na derrota, aplicando o multiplicador quando houver prêmio.
- Dado um prêmio potencial acima do limite de saldo, quando eu apostar, então a operação deve ser recusada sem consumir moedas ou item.
- Rastreabilidade: [PR #32](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/pull/32) e [PR #38](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/pull/38) → [DiceServiceTest](src/test/java/com/tavernasorte/service/DiceServiceTest.java), cenários `shouldCalculateDiceResultAndBalance`, `shouldConsumeItemAfterLosing` e `shouldRejectBetExceedingMaximumBalance`; [DiceControllerTest](src/test/java/com/tavernasorte/controller/DiceControllerTest.java) verifica o contrato HTTP.

### HU05 — Abrir Baú Encantado — entregue

Como jogador, eu quero apostar na combinação de três símbolos para receber o prêmio correspondente e utilizar um item opcional.

- Dada uma aposta válida, quando o baú abrir, então três símbolos iguais devem pagar seu multiplicador, um par deve retornar a aposta e símbolos distintos não devem pagar prêmio.
- Dado um item válido, quando a rodada terminar, então seu multiplicador deve ser aplicado ao prêmio e uma unidade deve ser consumida.
- Dado saldo insuficiente, quando eu tentar abrir o baú, então nenhum símbolo deve ser sorteado e o saldo deve permanecer igual.
- Rastreabilidade: [PR #34](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/pull/34) → [EnchantedChestServiceTest](src/test/java/com/tavernasorte/service/EnchantedChestServiceTest.java), cenários `shouldPaySymbolMultiplierForThreeMatchingSymbols`, `shouldReturnBetForOnePair`, `shouldConsumeItemEvenWithoutPrize` e `shouldRejectBetAboveBalanceBeforePickingSymbols`; [EnchantedChestFlowTest](src/test/java/com/tavernasorte/controller/EnchantedChestFlowTest.java) cobre o fluxo HTTP entre camadas.

### HU06 — Apostar na roleta — entregue

Como jogador, eu quero apostar em um número ou cor para conhecer o resultado e o prêmio da rodada.

- Dada uma aposta NUMBER entre 0 e 36, quando o número escolhido sair, então o retorno deve ser 36 vezes a aposta.
- Dada uma aposta RED ou BLACK, quando sair a cor escolhida, então o retorno deve ser duas vezes a aposta; o zero deve perder em ambas.
- Dada uma seleção inválida ou um prêmio que não caiba em int, quando eu apostar, então a jogada deve ser recusada antes do sorteio.
- Rastreabilidade: [PR #28](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/pull/28) → [RouletteServiceTest](src/test/java/com/tavernasorte/service/RouletteServiceTest.java), cenários `shouldResolveBetsWithIntegerCoins` e `shouldRejectPrizeOverflowBeforeSpinning`.

### HU07 — Fazer login — entregue parcialmente depois descartada

Como usuário cadastrado no Taverna Sorte,
quero realizar login utilizando meu email e senha,
para acessar de forma autenticada as funcionalidades do jogo.

- Implementar autenticação utilizando JWT (JSON Web Token), permitindo que usuários previamente cadastrados realizem login com suas credenciais.

- A aplicação deverá validar a senha utilizando BCrypt e gerar um token JWT quando as credenciais forem válidas.

- Rastreabilidade: [PR #10](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/pull/10) (cadastro de usuários, entrega parcial) → [UserServiceTest antes da remoção](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/blob/97164a1/src/test/java/com/tavernasorte/service/UserServiceTest.java), cenários `shouldCreateUserSuccessfully` e `shouldRejectUserCreationWhenEmailIsAlreadyInUse` (testes de cadastro; login e JWT não foram implementados) → [commit 45d98c6](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/commit/45d98c6) (descarte e remoção do módulo).

## Metodologia de desenvolvimento

O grupo adotou inicialmente **Scrum**, utilizando **Jira** para organizar os requisitos e as tarefas. A escolha buscava dividir o desenvolvimento em incrementos, acompanhar o trabalho e revisar as prioridades.

Antes da entrega parcial de metodologia ágil, o grupo já tinha definido o que precisava ser implementado. A continuidade do projeto passou a ser organizada principalmente por branches, pull requests e revisão de código. Assim, Scrum e Jira representam a organização inicial, enquanto o fluxo de PRs descreve a condução das etapas seguintes.

Link para a organização no Jira: https://luizgustavogs123123.atlassian.net/jira/software/projects/C14/boards/2

## Dinâmica de desenvolvimento

### Organização do trabalho

As responsabilidades foram distribuídas entre backend, frontend e DevOps, conforme a seção de equipe e papéis. O backend concentrou as regras dos jogos, saldo, inventário e API; o frontend ficou em um repositório separado; e as atividades de DevOps envolveram a configuração do ambiente e da pipeline.

Com as tarefas já definidas, as alterações foram desenvolvidas em branches e reunidas por pull requests. A revisão permitiu discutir a implementação e solicitar ajustes antes da integração. O histórico desse fluxo pode ser consultado com `git log` e `git shortlog -sn`.

### Integração e ajustes

Durante a evolução do projeto, mudanças em uma parte do sistema exigiram adaptações nas demais:

- A conversão dos DTOs em records exigiu atualizar o uso dos contratos nos controllers e nos testes: [PR #37](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/pull/37).
- A integração entre branches deixou referências e testes incompatíveis, corrigidos após o merge: [PR #38](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/pull/38).
- O escopo foi revisto com a remoção do cadastro de usuários e do histórico da roleta, ajustando as dependências para preservar as funcionalidades restantes: [PR #39](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/pull/39).

Esses casos mostram que a continuidade por PRs também envolveu revisar contratos, adaptar testes e corrigir problemas de integração. O Jenkinsfile define as verificações de build e testes e a publicação dos relatórios para apoiar esse processo.

### Lições aprendidas

Os ajustes de integração evidenciaram a importância de comunicar mudanças de contrato entre as partes do projeto e revisar o resultado do merge. Para um próximo ciclo, as melhorias propostas são manter o Jira atualizado também durante a implementação, registrar os critérios de aceitação antes de programar e executar `mvn clean verify` na branch integrada.

### Branches, commits e revisão

O histórico contém nomes como `feat/...`, `fix/...`, `chore/...` e `docs/...`. A convenção documentada para novos commits é `tipo: descrição`, com tipo em inglês e descrição em português; o escopo pode ser omitido.

Exemplo: `fix(dice): preservar inventário ao recusar aposta`.

## Exemplos de Refatorações e motivos

| Smell                                                               | Refactoring aplicado                                                            | Por quê                                                                                                                             | Link do PR                                                                   |
| ------------------------------------------------------------------- | ------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------- |
| Código repetitivo nos DTOs e exposição de entidades nos controllers | **Replace Class with Record** e mapeamento de entidades para DTOs               | Reduzir código repetitivo, padronizar as respostas da API e evitar a exposição direta das entidades, preservando os contratos JSON. | [PR #37](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/pull/37) |
| **Long Method** no DiceService                                      | **Extract Method** para separar busca do jogador, seleção de itens e validações | Melhorar a legibilidade, separar responsabilidades e facilitar a manutenção das regras do jogo.                                     | [PR #37](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/pull/37) |
| **Magic Numbers** no sorteio da roleta                              | **Replace Magic Number with Symbolic Constant**                                 | Substituir valores literais por constantes nomeadas, facilitando a compreensão e futuras alterações dos limites do sorteio.         | [PR #28](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/pull/28) |

**Observação:** a alteração da roleta de 1–10 para 0–36 foi uma mudança de funcionalidade, não uma refatoração. Apenas a substituição dos valores literais por constantes é considerada refactoring.

## Uso de IA

O grupo utilizou **Claude** e **ChatGPT**, nesta interação, para gerar e ajustar código, esclarecer dúvidas, apoiar refatorações, corrigir testes, depurar problemas e revisar documentação. As versões exatas dos modelos não foram registradas neste README.

A configuração do projeto e o levantamento de requisitos foram feitos manualmente

### Exemplos de prompts e tratamento das respostas

Os prompts abaixo sintetizam os temas discutidos e as decisões documentadas no projeto.

#### Exemplo 1: Conversão dos DTOs para records

**Prompt:**

> "A conversão dos DTOs para records altera o JSON da API ou exige mudanças nos testes? Quais vantagens essa alteração traz para o projeto?"

**Resultado:**

A análise apontou que records reduzem o código de construtores e getters e permitem preservar os campos JSON da API. Também identificou a necessidade de adaptar os testes que dependiam dos construtores e métodos de acesso anteriores.

**Ação:**

**Aceito com adaptações.** A conversão para records foi adotada, preservando os contratos JSON e as regras dos jogos. Os testes afetados foram adaptados com autorização para esses ajustes, conforme os commits [d563dfc](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/commit/d563dfc) e [daaec71](https://github.com/C14-Inatel-2026-2/taverna-sorte-backend/commit/daaec71).

#### Exemplo 2: Sugestão de histórico de transações

**Prompt:**

> "Considerando as funcionalidades já implementadas no Taverna Sorte, o que mais poderia ser acrescentado para melhorar a experiência do jogador?"

**Resultado:**

Proposta de um histórico de transações por jogador, registrando compras de itens, apostas e prêmios recebidos. A funcionalidade permitiria consultar as movimentações de moedas e acompanhar a evolução do saldo.

**Ação:**

**Recusado.** O histórico de transações ficou fora do escopo, priorizando os jogos, a loja e o inventário. A funcionalidade exigiria novas estruturas de persistência, endpoints e testes, ampliando o trabalho necessário para a entrega.

#### Exemplo 3: Revisão do fluxo de itens e dos multiplicadores

**Prompt:**

> "O fluxo de uso dos itens nos jogos está correto? Falta alguma validação ou algo do tipo?

**Resultado:**

Recomendação de verificar se o item pertence ao inventário do jogador e de limitar seu multiplicador. Essa restrição, combinada aos limites de aposta e saldo, permite controlar os valores usados no cálculo dos prêmios. O item deve continuar sendo consumido ao final de uma rodada válida, mesmo na derrota, e uma aposta recusada deve preservar moedas e itens.

**Ação:**

**Aceito.** A entidade Item valida multiplicadores entre 1 e 20, inclusive ao obter o multiplicador para calcular o prêmio. Dice e Baú validam o item escolhido e o prêmio máximo possível antes de debitar a aposta, preservando as regras de consumo e os limites de saldo.

As sugestões de IA precisam ser compreendidas e revisadas pelo grupo. Os testes executados ajudam a verificar o comportamento, mas não substituem a leitura do código e validação com banco real

Última atualização: Novembro 2026
