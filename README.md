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
- **RF04 — Roleta:** sortear números de 0 a 36 e resolver apostas em número, vermelho ou preto no serviço. A integração da rodada com aposta pela API, saldo e histórico permanece pendente.
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

A API também aceita o nome anterior do campo, `{"valor": 10.0}`, por meio de `@JsonAlias`. Envie apenas um dos nomes em cada requisição.

Valores de aposta e prêmio usam `BigDecimal`, em unidades da moeda do jogo. `ChestBetValue` centraliza as regras de validação: valor obrigatório, positivo e com até duas casas decimais.

**Limite funcional da aposta:** conforme o critério de aceite da história C14-20, o valor máximo permitido deve ser R$ 10.000,00. Essa regra deve ser aplicada e testada pelo backend antes da realização do sorteio. A validação técnica de quantidade de dígitos e casas decimais não substitui esse limite funcional.

A entrada é validada com Bean Validation em `EnchantedChestRequest` (`@NotNull`, `@Positive` e `@Digits`), e os erros são tratados pelo `GlobalExceptionHandler` no mesmo formato usado em usuários (`{"error": "..."}`).

| Requisição | Status esperado | Resultado esperado |
|---|---|---|
| `{"valorAposta": 0}` ou negativo | 400 | O valor da aposta deve ser maior que zero |
| `{}` ou `{"valorAposta": null}` | 400 | O valor da aposta é obrigatório |
| `{"valorAposta": 10000.01}` | 400 | A aposta não pode ultrapassar R$ 10.000,00 |
| Mais de 2 casas decimais | 400 | A aposta deve ter no máximo 2 casas decimais |
| `{"valorAposta": "abc"}` ou JSON malformado | 400 | Valor inválido na requisição |

O `EnchantedChestService` também deve validar o valor por meio de `ChestBetValue` antes de sortear e lançar `InvalidBetException`, pois pode ser chamado sem passar pelo controller. O cálculo do prêmio é privado e recebe somente os dados preparados por `open`.

Esta refatoração se limita ao baú. Dice, roleta, usuários e loja mantêm a implementação da `main`, incluindo seus pacotes e contratos.

Para executar a suíte com Java 21 e Maven:

```shell
mvn clean test
```

---

## Refactorings

### Roleta — constantes do intervalo de sorteio

| Smell | Refactoring aplicado | Por quê | Link do PR |
|---|---|---|---|
| Magic Numbers: `random.nextInt(10) + 1` não explicita o significado dos valores. | Replace Magic Number with Symbolic Constant: definir `MIN_RESULT`, `MAX_RESULT` e derivar `RESULT_COUNT` em `RouletteService`. | Tornar explícitos os limites e a quantidade de resultados, preservando o sorteio de 1 a 10. | Pendente: inserir a URL real deste PR. |

O refactoring original foi isolado na branch `refactor/roulette-range-constants`, preservando o comportamento de 1 a 10. A implementação posterior da lógica europeia, descrita abaixo, é funcionalidade nova e altera o intervalo para 0 a 36.

Validação local: `mvn clean package -DskipTests` e `mvn verify` passaram antes e depois, com **116 testes**, sem falhas, erros ou testes ignorados. Ambiente: JDK 25, compilando com `release 21`. Os logs locais estão em `target/roulette-refactor-evidence/` e são removidos por `mvn clean`.

Para concluir a entrega, substituir o campo pendente pelo link real do PR e anexar ao PR as evidências das execuções verdes do Jenkins antes e depois, com Java 21. A execução no Jenkins ainda não foi confirmada.

## Lógica da roleta europeia

O serviço sorteia uniformemente um dos **37 números de 0 a 36**. O zero é verde; os demais possuem as cores fixas da roleta europeia, mapeadas em `RouletteColor`.

| Tipo (`RouletteBetType`) | Escolha | Condição de vitória | Retorno total |
|---|---|---|---|
| `NUMBER` | Número de 0 a 36 | Número escolhido igual ao sorteado | 36 vezes a aposta |
| `RED` | Sem número (`null`) | Número sorteado vermelho | 2 vezes a aposta |
| `BLACK` | Sem número (`null`) | Número sorteado preto | 2 vezes a aposta |

O retorno inclui a aposta original: apostar 10 em um número e acertar retorna 360 (350 de lucro); acertar uma cor retorna 20 (10 de lucro). Uma derrota retorna `0.00`. O zero perde nas apostas em vermelho ou preto; pode vencer quando escolhido explicitamente em `NUMBER`. Não há aposta em verde nem regras especiais de devolução no zero.

`RouletteBet` valida tipo, escolha e valor antes de qualquer sorteio. O valor deve ser obrigatório, positivo, de até `10000.00` unidades da moeda do jogo e ter no máximo duas casas decimais. Valores usam `BigDecimal` e são normalizados para duas casas, sem arredondamento. Apostas em cor rejeitam um número informado.

Exemplos de chamada interna ao serviço:

```java
service.play(RouletteBetType.NUMBER, 0, new BigDecimal("10.00"));
service.play(RouletteBetType.RED, null, new BigDecimal("10.00"));
```

`RoulettePlayResponse` retorna a aposta validada (`bet`), o número sorteado (`drawnNumber`), a cor (`color`), a vitória (`won`) e o retorno total (`prize`). Não usa `@JsonProperty`. A operação anterior `play(int)` foi substituída por essa operação completa.

O controller permanece com `GET /roleta/girar`, retornando apenas o número; seu intervalo agora é **0 a 36**, o que exige ajuste de consumidores que assumam 1 a 10. A rodada com aposta ainda não é exposta pela API. Saldo, autenticação do jogador, histórico e frontend ficam para a próxima integração.

Validação local desta implementação: `mvn clean package -DskipTests` e `mvn verify` passaram antes (**120 testes**) e depois (**176 testes**), sem falhas, erros ou testes ignorados. São **64 casos da roleta** (incluindo os 37 números da tabela de cores) e 112 dos demais módulos, cujos testes não foram alterados. Ambiente: JDK 25 com `release 21`; Jenkins com Java 21 continua pendente. Logs em `target/roulette-european-evidence/`, removidos por `mvn clean`.

## Equipe

- [David Fernandes Barbon](https://github.com/DavidFBar)
- [João Victor Samuel de Faria](https://github.com/joaovsfaria)
- [João Vitor Souza Aldet](https://github.com/joaoaldet)
- [Lucas Emanuel da Silva Godoi](https://github.com/luscagodoi)
- [Luiz Gustavo Guimarães Santos](https://github.com/LuizGuimaraesz)
