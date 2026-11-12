# Taverna da Sorte — Backend

Backend do projeto Taverna da Sorte, desenvolvido em Java com Spring Boot. É responsável pelo processamento das regras de negócio, autenticação, persistência de dados e disponibilização dos serviços utilizados pelo frontend.

Repositório do frontend: [taverna-sorte-frontend](https://github.com/C14-Inatel-2026-2/taverna-sorte-frontend).

---

## Funcionalidades e Requisitos

Esta seção apresenta as funcionalidades do sistema e os requisitos que orientam seu desenvolvimento. Funcionalidades ainda não concluídas são identificadas como em desenvolvimento.

### Requisitos Funcionais

- **RF01 — Autenticação de usuários (em desenvolvimento):** permitir que usuários cadastrados realizem login utilizando e-mail e senha. O sistema deve validar as credenciais e disponibilizar um token JWT para autenticação nas rotas protegidas.
- **RF02 — Compra de itens (em desenvolvimento):** permitir que jogadores comprem itens na loja utilizando seu saldo de moedas, desde que possuam saldo suficiente e espaço disponível no inventário.
- **RF03 — Baú Encantado (em desenvolvimento):** permitir que jogadores realizem apostas e recebam prêmios de acordo com as combinações de três símbolos sorteados.
- **RF04 — Roleta:** permitir que jogadores participem do jogo de roleta e recebam o resultado da rodada.
- **RF05 — Histórico de partidas (em desenvolvimento):** registrar as partidas concluídas e permitir que jogadores consultem seu próprio histórico, incluindo jogo, valor apostado, resultado, prêmio recebido e data.

#### Detalhamento das funcionalidades

**Autenticação de usuários**

- Permitir login utilizando e-mail e senha.
- Validar as senhas utilizando BCrypt.
- Gerar um token JWT após autenticação bem-sucedida.
- Rejeitar credenciais inválidas sem revelar se o e-mail ou a senha estão incorretos.
- Definir validade e assinatura verificável para os tokens.
- Proteger rotas que exigem autenticação, rejeitando tokens ausentes, inválidos ou expirados.
- Não expor senhas ou seus hashes nas respostas da API.

**Compra de itens na loja**

- Permitir a compra de itens utilizando o saldo de moedas do jogador.
- Descontar o preço do item do saldo após uma compra bem-sucedida.
- Adicionar o item adquirido ao inventário.
- Permitir compras quando o saldo for exatamente igual ao preço do item.
- Recusar compras quando o jogador não possuir saldo suficiente.
- Impedir compras quando o inventário estiver cheio, respeitando o limite de 3 itens.
- Recusar compras de itens inexistentes.
- Garantir que compras recusadas não alterem o saldo nem o inventário.

**Baú Encantado**

- Receber o valor da aposta do jogador.
- Sortear exatamente três símbolos.
- Calcular o prêmio conforme a combinação sorteada:
  - Três símbolos iguais: prêmio correspondente ao valor da aposta multiplicado pelo multiplicador do símbolo.
  - Exatamente dois símbolos iguais: devolução do valor apostado.
  - Três símbolos diferentes: nenhum prêmio.
- Rejeitar apostas ausentes, nulas, iguais a zero, negativas, superiores a R$ 10.000,00 ou com mais de duas casas decimais, conforme os critérios de aceite da história C14-20.

**Histórico de partidas**

O sistema deverá:

- Registrar automaticamente cada partida concluída.
- Armazenar o jogador, o tipo de jogo, o valor apostado, o resultado, o prêmio recebido e a data da partida.
- Permitir que o jogador consulte somente seu próprio histórico.
- Exigir autenticação válida para realizar a consulta.
- Exibir as partidas da mais recente para a mais antiga.
- Informar quando não houver partidas registradas.
- Manter os registros armazenados mesmo após o encerramento da sessão.

### Requisitos Não Funcionais

- **RNF01 — Arquitetura:** organizar o backend em camadas, separando responsabilidades entre controllers, services, repositories, entities e DTOs.
- **RNF02 — Persistência:** utilizar MySQL para armazenar os dados da aplicação.
- **RNF03 — Segurança:** utilizar BCrypt para proteção de senhas e JWT para autenticação e proteção de rotas.
- **RNF04 — Validação:** validar os dados recebidos pela API e retornar respostas HTTP adequadas em caso de erros.
- **RNF05 — Privacidade:** restringir o acesso aos dados de cada jogador, impedindo consultas não autorizadas ao histórico de outros usuários.
- **RNF06 — Integridade:** preservar os registros das partidas concluídas e garantir a consistência das operações de compra.
- **RNF07 — Testabilidade:** utilizar testes automatizados para verificar o comportamento dos serviços e endpoints.
- **RNF08 — Integração contínua:** utilizar Jenkins para executar o processo de build e os testes automatizados.
- **RNF09 — Implantação:** disponibilizar a aplicação em ambiente de produção por meio do Render.

---

## Integração com o Frontend

O backend e o frontend são partes do mesmo sistema, mas possuem responsabilidades distintas. O frontend, desenvolvido em Flutter, apresenta as telas e permite a interação do jogador com o aplicativo. O backend, desenvolvido em Java com Spring Boot, processa as requisições, aplica as regras de negócio e realiza a persistência dos dados.

A comunicação entre as duas partes ocorre por meio de requisições HTTP à API, com troca de dados em formato JSON.

### Responsabilidades do Backend

- **Autenticação:** validar as credenciais do jogador, emitir tokens JWT e proteger os endpoints que exigem autenticação.
- **Jogos:** processar apostas, realizar sorteios e calcular os resultados e prêmios dos minijogos.
- **Loja:** consultar itens disponíveis, validar compras, descontar moedas e atualizar o inventário.
- **Saldo:** controlar o saldo de moedas utilizado nas operações do jogador.
- **Inventário:** controlar os itens adquiridos e respeitar as regras de capacidade.
- **Histórico:** registrar partidas concluídas e disponibilizar os registros do jogador autenticado.
- **Persistência:** armazenar os dados necessários para manter as informações do jogador e suas partidas.

### Responsabilidades do Frontend

- Apresentar as telas e os elementos visuais do aplicativo.
- Permitir a navegação entre as funcionalidades.
- Receber os dados informados pelo jogador e enviá-los à API.
- Apresentar os resultados e as mensagens retornadas pelo backend.
- Exibir saldo, itens do inventário e histórico de partidas conforme os dados recebidos da API.

As regras de negócio e as validações que afetam os dados do sistema devem ser aplicadas pelo backend. O frontend não deve ser considerado responsável por garantir a integridade de apostas, compras, saldo ou registros.

### Contratos da API

As requisições e respostas devem seguir os contratos definidos pelos endpoints do backend. Alterações em rotas, nomes de campos, formatos de dados ou códigos HTTP devem ser acompanhadas de ajustes correspondentes no frontend, preservando a compatibilidade entre as aplicações.

Exemplo de requisição para o Baú Encantado:

```json
{
  "valorAposta": 10.0
}
```

Exemplo da estrutura de resposta:

```json
{
  "simbolos": ["..."],
  "premio": 20.0
}
```

O frontend deve utilizar o resultado retornado pela API, sem reproduzir localmente o sorteio ou o cálculo do prêmio.

---

## Executar a pipeline no Jenkins

### Pré-requisitos

- Docker Desktop iniciado e configurado para containers Linux, com Docker Compose disponível.
- Acesso à internet para baixar a imagem do Jenkins, os plugins e as dependências Maven.
- `Jenkinsfile`, pasta `jenkins/` e `docker-compose.yml` publicados no repositório remoto.
- Porta `8081` disponível na máquina.

A imagem do Jenkins já inclui Java 21 e Maven.

### 1. Configurar o ambiente

Na raiz do projeto, crie o `.env` a partir do exemplo se ainda não existir. No PowerShell:

```powershell
if (-not (Test-Path .env)) {
    Copy-Item .env.example .env
}
```

Edite `.env` e defina `JENKINS_ADMIN_ID`, `JENKINS_ADMIN_PASSWORD`, `GITHUB_REPO_URL` e `RENDER_DEPLOY_HOOK` (veja [Deploy no Render](#deploy-no-render)). O `.env` é local e não deve ser publicado.

A configuração atual acessa o repositório sem autenticação. Para um repositório privado, configure uma credencial no Jenkins e associe seu `credentialsId` à origem Git em `jenkins/casc.yaml`; preencher `GITHUB_USER` e `GITHUB_TOKEN` no `.env` sozinho não habilita a autenticação.

### 2. Iniciar o Jenkins

Execute na raiz do projeto:

```powershell
docker compose up -d --build jenkins
docker compose logs -f jenkins
```

Aguarde a mensagem `Jenkins is fully up and running`. Pressione `Ctrl+C` para sair dos logs; o Jenkins continuará ativo. O MySQL não é necessário para esses estágios de build e testes. Para subir tudo use `docker-compose up -d`.

### 3. Executar e acompanhar a pipeline

1. Acesse [http://localhost:8081](http://localhost:8081) e entre com as credenciais definidas no `.env`.
2. Abra o job multibranch **taverna-sorte**, criado pela configuração em `jenkins/casc.yaml`.
3. Clique em **Scan Multibranch Pipeline Now** para localizar branches com um `Jenkinsfile` na raiz. A descoberta pode iniciar o primeiro build; nas execuções seguintes, selecione uma branch e clique em **Build Now**.
4. Abra a execução para ver o **Console Output**, os resultados dos testes e o link **Cobertura de Testes**.

O Jenkins busca o código no repositório remoto, então publique suas alterações antes de iniciar o build. Uma nova varredura também pode ser disparada manualmente; a configuração periódica atual é de um minuto.

O estágio **Build** executa `mvn clean package -DskipTests` e arquiva `target/*.jar`. O estágio **Tests** executa `mvn verify`; o Maven roda os testes e gera o relatório HTML de cobertura com JaCoCo. Jenkins publica os resultados JUnit e o relatório de cobertura. O estágio **Deploy** roda somente na branch `main` e dispara o deploy no Render.

## Deploy no Render

A aplicação é publicada no Render como um Web Service Docker, construído a partir do `Dockerfile` da raiz. O Auto-Deploy do Render fica desligado: quem dispara o deploy é o estágio **Deploy** do Jenkins, que só roda na `main` e depois que os estágios de build e testes passam.

### Como funciona

1. O estágio **Deploy** faz um `POST` no *Deploy Hook* do serviço no Render.
2. O Render clona o repositório, constrói a imagem com o `Dockerfile` e sobe a aplicação.
3. A aplicação usa a porta definida pelo Render na variável `PORT` (localmente continua `8080`).

### Configurar o Jenkins

1. Peça o Deploy Hook a quem administra o serviço no Render. Ele é secreto: não publique no repositório.
2. Defina `RENDER_DEPLOY_HOOK` no `.env`. O `jenkins/casc.yaml` cria a credencial `render-deploy-hook` a partir dessa variável.
3. Recrie o container para o Jenkins ler o `.env` atualizado:

```powershell
docker compose up -d --build --force-recreate jenkins
```

Sem o Deploy Hook no `.env`, o estágio **Deploy** falha nos builds da `main`. O estágio só confirma que o deploy foi disparado; acompanhe a conclusão no painel do Render.

### Banco de dados em produção

O `application.yml` aponta para o MySQL local. No Render, a conexão é definida pelas variáveis de ambiente do serviço, que substituem os valores do arquivo:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

### Testar a imagem localmente

Com o MySQL do `docker-compose.yml` em execução:

```powershell
docker compose up -d mysql
docker build -t taverna-backend .
docker run --rm -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3307/taverna_sorte taverna-backend
```

---

## Arquitetura de Camadas (Package-by-Layer)

A aplicação segue o padrão arquitetural em camadas, onde cada pacote possui responsabilidade única e bem definida.

### 1. `controller` (Camada de Apresentação / Web)

- **O que faz:** é a porta de entrada da aplicação. Recebe as requisições HTTP (`GET`, `POST`, `PUT`, `DELETE`) vindas do frontend, do Postman ou de outros sistemas.
- **Responsabilidades:**
  - Validar dados básicos que chegam na requisição.
  - Definir as rotas (ex.: `/roleta/girar`, `/items`).
  - Definir os códigos de status HTTP corretos (`200 OK`, `201 Created`, `404 Not Found`).
  - Repassar o trabalho para a camada `service`.
- **Regra de ouro:** controllers devem ser simples — nunca coloque regras de negócio ou comandos de banco de dados diretamente aqui.

### 2. `service` (Camada de Negócio / Domínio)

- **O que faz:** contém a inteligência e as regras de negócio da Taverna da Sorte.
- **Responsabilidades:**
  - Fazer cálculos e validações de regras (ex.: verificar se o jogador tem moedas suficientes para uma compra).
  - Orquestrar chamadas a um ou mais repositórios.
  - Controlar transações de banco (`@Transactional`).

### 3. `repository` (Camada de Acesso a Dados)

- **O que faz:** é a camada que conversa diretamente com o banco de dados.
- **Responsabilidades:**
  - Executar operações de CRUD (*Create, Read, Update, Delete*).
  - O Spring Data JPA permite criar uma interface que herda de `JpaRepository<Item, Long>`, gerando os comandos SQL automaticamente sem precisar escrever SQL manualmente.

### 4. `entity` (Camada de Entidades de Banco de Dados)

- **O que faz:** representa o esquema das tabelas do banco de dados relacional mapeado em classes Java (via JPA/Hibernate).
- **Responsabilidades:**
  - Cada atributo da classe representa uma coluna na tabela.
  - Mapear chaves primárias (`@Id`), auto-incrementos (`@GeneratedValue`) e relacionamentos de chave estrangeira (`@ManyToOne`, `@OneToMany`).

### 5. `dto` (Data Transfer Object / Tráfego de Dados)

- **O que faz:** são objetos simples que servem para transportar dados entre o cliente e a API.
- **Por que ela existe se já temos a `entity`?**
  - **Segurança:** evita expor dados confidenciais ou colunas internas do banco (ex.: senha criptografada, flags de auditoria).
  - **Controle:** quando o usuário cadastra um item, ele não envia o `id` (pois o banco gera). Assim, podemos ter um `ItemRequestDTO(String nome, double preco)` para entrada e um `ItemResponseDTO(Long id, String nome, double preco)` para saída.

---

## Organização do Baú Encantado

O baú segue a organização por camada, com suas classes distribuídas assim:

```text
controller/EnchantedChestController.java
dto/EnchantedChestRequest.java
dto/EnchantedChestResponse.java
service/EnchantedChestService.java
service/SymbolPicker.java
service/RandomSymbolPicker.java
model/Symbol.java
model/ChestBetValue.java
```

A interface `SymbolPicker` permite substituir o gerador aleatório por um mock nos testes. `Symbol` fica em `model/` por ser um enum do jogo, sem representar uma entidade persistida.

A rota `POST /bau/abrir` recebe `{"valorAposta": 10.0}` e retorna os campos `simbolos` e `premio`. Três símbolos iguais pagam seu multiplicador, um par devolve a aposta e três símbolos diferentes não pagam prêmio.

Valores de aposta e prêmio usam `BigDecimal`, em unidades da moeda do jogo.
`ChestBetValue` centraliza as regras: valor obrigatório, positivo, com até
5 dígitos inteiros e 2 casas decimais, e no máximo `10000.00`.
Entradas com mais de duas casas decimais são rejeitadas, sem arredondamento
silencioso. Valores aceitos são normalizados para duas casas decimais,
e o prêmio continua sendo um número no JSON.

O corpo da requisição é estrito: o único campo aceito é `valorAposta`, e
qualquer outro campo (inclusive o antigo `valor`) rejeita a requisição.
Isso vale só para o baú; os demais endpoints não foram alterados.

A entrada é validada com Bean Validation em `EnchantedChestRequest` (`@NotNull`,
`@Positive` e `@Digits`), e os erros são tratados pelo `GlobalExceptionHandler` no mesmo
formato usado em usuários (`{"error": "..."}`):

A entrada é validada com Bean Validation em `EnchantedChestRequest` (`@NotNull`, `@Positive` e `@Digits`), e os erros são tratados pelo `GlobalExceptionHandler` no mesmo formato usado em usuários (`{"error": "..."}`).

| Requisição | Status esperado | Resultado esperado |
|---|---|---|
| `{"valorAposta": 0}` ou negativo | 400 | O valor da aposta deve ser maior que zero |
| `{}` ou `{"valorAposta": null}` | 400 | O valor da aposta é obrigatório |
| Mais de 2 casas decimais ou mais de 5 dígitos inteiros | 400 | A aposta deve ter no máximo 5 dígitos inteiros e 2 casas decimais |
| Acima de `10000.00` (ex.: `10000.01`) | 400 | O valor máximo da aposta é 10000.00 |
| `{"valorAposta": "abc"}`, JSON malformado ou campo desconhecido (ex.: `{"valor": 10.0}`) | 400 | Valor inválido na requisição |

O `EnchantedChestService` também deve validar o valor por meio de `ChestBetValue` antes de sortear e lançar `InvalidBetException`, pois pode ser chamado sem passar pelo controller. O cálculo do prêmio é privado e recebe somente os dados preparados por `open`.

Esta refatoração se limita ao baú. Dice, roleta, usuários e loja mantêm a implementação da `main`, incluindo seus pacotes e contratos.

Para executar a suíte com Java 21 e Maven:

```shell
mvn clean test
```

---

## Roleta europeia — lógica da rodada

A roleta sorteia um número de **0 a 36**. O zero é verde; os demais números
seguem a tabela fixa de cores da roleta europeia. Apostas e prêmios são moedas
inteiras (`int`), assim como o saldo de `Player`.

| Tipo de aposta | Escolha | Retorno total em caso de acerto |
|---|---|---|
| `NUMBER` | Número de 0 a 36 | 36 vezes a aposta |
| `RED` | Cor vermelha, sem número | 2 vezes a aposta |
| `BLACK` | Cor preta, sem número | 2 vezes a aposta |

O retorno inclui as moedas apostadas: uma aposta de 10 em número retorna 360
ao acertar; em cor, retorna 20. Derrotas retornam zero. O zero perde em vermelho
e preto, mas vence quando escolhido numa aposta `NUMBER`.

O fluxo fica em `RouletteService`: **validar → sortear → verificar vitória →
calcular retorno → responder**. `RouletteBetType` guarda os três tipos e seus
multiplicadores. `RoulettePlayResponse` é um record com os dados da aposta,
número sorteado, cor, vitória e prêmio, sem `@JsonProperty`.

```java
service.play(RouletteBetType.NUMBER, 7, 10);
service.play(RouletteBetType.RED, null, 10);
```

A aposta deve ser positiva. `NUMBER` exige um número válido; apostas em cor
exigem `chosenNumber` nulo. Todas as validações ocorrem antes do sorteio.
`Math.multiplyExact` verifica se o prêmio potencial cabe em `int`; apostas
que causariam overflow são rejeitadas mesmo que pudessem perder a rodada.
Esse é um limite técnico, sem um teto adicional de negócio.

O controller mantém apenas `GET /roleta/girar`, cujo intervalo muda de 1–10
para **0–36**. Consumidores da rota precisam considerar esse novo intervalo.
A rodada com aposta ainda é chamada internamente pelo serviço. API de apostas,
movimentação de saldo, autenticação, histórico e frontend ficam para outra entrega.

Validação local: `mvn clean verify` passou antes (**121 testes**) e depois
(**143 testes**, sendo 26 da roleta), sem falhas, erros ou testes ignorados.
Ambiente: JDK 25, compilando com `release 21`. A execução no Jenkins com Java 21
ainda precisa ser confirmada. Logs locais em `target/roulette-integer-evidence/`,
removidos por `mvn clean`.

## Refactorings

| Smell | Refactoring aplicado | Por quê | Link do PR |
|---|---|---|---|
| Magic Numbers no sorteio | Replace Magic Number with Symbolic Constant: `MIN_NUMBER`, `MAX_NUMBER` e `NUMBER_COUNT` | Nomear os limites e derivar a quantidade de resultados. A mudança para 0–36 é funcionalidade nova, separada dessa melhoria de clareza. | Pendente: inserir o link do novo PR. |
| Construção repetida do serviço com mock nos testes | Extract Method: `setUp()` com `@BeforeEach` | Centralizar a preparação e manter cada teste isolado. | Pendente: inserir o link do novo PR. |

Os refactorings e testes se limitam à roleta. Para a entrega, substituir os
links pendentes e anexar ao PR as evidências da pipeline verde antes e depois;
a validação local não substitui essas execuções do Jenkins.

## Equipe

- [David Fernandes Barbon](https://github.com/DavidFBar)
- [João Victor Samuel de Faria](https://github.com/joaovsfaria)
- [João Vitor Souza Aldet](https://github.com/joaoaldet)
- [Lucas Emanuel da Silva Godoi](https://github.com/luscagodoi)
- [Luiz Gustavo Guimarães Santos](https://github.com/LuizGuimaraesz)
