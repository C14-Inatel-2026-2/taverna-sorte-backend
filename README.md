# Taverna da Sorte - Backend

Backend do projeto Taverna da Sorte desenvolvido em Java com Spring Boot.

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

Edite `.env` e defina `JENKINS_ADMIN_ID`, `JENKINS_ADMIN_PASSWORD` e `GITHUB_REPO_URL`. O `.env` é local e não deve ser publicado.

A configuração atual acessa o repositório sem autenticação. Para um repositório privado, configure uma credencial no Jenkins e associe seu `credentialsId` à origem Git em `jenkins/casc.yaml`; preencher `GITHUB_USER` e `GITHUB_TOKEN` no `.env` sozinho não habilita a autenticação.

### 2. Iniciar o Jenkins

Execute na raiz do projeto:

```powershell
docker compose up -d --build jenkins
docker compose logs -f jenkins
```

Aguarde a mensagem `Jenkins is fully up and running`. Pressione `Ctrl+C` para sair dos logs; o Jenkins continuará ativo. O MySQL não é necessário para esses estágios de build e testes. Para subir tudo use docker-compose up -d.

### 3. Executar e acompanhar a pipeline

1. Acesse [http://localhost:8081](http://localhost:8081) e entre com as credenciais definidas no `.env`.
2. Abra o job multibranch **taverna-sorte**, criado pela configuração em `jenkins/casc.yaml`.
3. Clique em **Scan Multibranch Pipeline Now** para localizar branches com um `Jenkinsfile` na raiz. A descoberta pode iniciar o primeiro build; nas execuções seguintes, selecione uma branch e clique em **Build Now**.
4. Abra a execução para ver o **Console Output**, os resultados dos testes e o link **Cobertura de Testes**.

O Jenkins busca o código no repositório remoto, então publique suas alterações antes de iniciar o build. Uma nova varredura também pode ser disparada manualmente; a configuração periódica atual é de um minuto.

O estágio **Build** executa `mvn clean package -DskipTests` e arquiva `target/*.jar`. O estágio **Tests** executa `mvn verify`; o Maven roda os testes e gera o relatório HTML de cobertura com JaCoCo. Jenkins publica os resultados JUnit e o relatório de cobertura. A pipeline não inicia a aplicação nem realiza deploy.

## Arquitetura de Camadas (Package-by-Layer)

A aplicação segue o padrão arquitetural em camadas, onde cada pacote possui responsabilidade única e bem definida:

### 1. `controller` (Camada de Apresentação / Web)

- **O que faz:** É a porta de entrada da sua aplicação. Escuta as requisições HTTP (`GET`, `POST`, `PUT`, `DELETE`) vindas do frontend, do Postman ou de outros sistemas.
- **Responsabilidades:**
  - Validar dados básicos que chegam na requisição.
  - Definir as rotas (ex: `/roleta/girar`, `/items`).
  - Definir os códigos de status HTTP corretos (`200 OK`, `201 Created`, `404 Not Found`).
  - Repassar o trabalho pesado para a camada `service`.
- **Regra de ouro:** Controllers devem ser simples — nunca coloque regras de negócio ou comandos de banco de dados diretamente aqui!

### 2. `service` (Camada de Negócio / Domínio)

- **O que faz:** Reside toda a inteligência e as regras de negócio da Taverna da Sorte.
- **Responsabilidades:**
  - Fazer cálculos e validações de regras (ex: verificar se o jogador tem moedas suficientes para girar a roleta).
  - Orquestrar chamadas a um ou mais repositórios.
  - Controlar transações de banco (`@Transactional`).

### 3. `repository` (Camada de Acesso a Dados)

- **O que faz:** É a camada que conversa diretamente com o banco de dados.
- **Responsabilidades:**
  - Executar operações de CRUD (_Create, Read, Update, Delete_).
  - O Spring Data JPA permite criar uma interface que herda de `JpaRepository<Item, Long>`, gerando os comandos SQL automaticamente sem precisar escrever SQL na mão.

### 4. `entity` (Camada de Entidades de Banco de Dados)

- **O que faz:** Representa o esquema das tabelas do banco de dados relacional mapeado em classes Java (via JPA/Hibernate).
- **Responsabilidades:**
  - Cada atributo da classe representa uma coluna na tabela.
  - Mapear chaves primárias (`@Id`), auto-incrementos (`@GeneratedValue`) e relacionamentos de chave estrangeira (`@ManyToOne`, `@OneToMany`).

### 5. `dto` (Data Transfer Object / Tráfego de Dados)

- **O que faz:** São objetos simples que servem apenas para transportar dados entre o cliente e a API.
- **Por que ela existe se já temos a `entity`?**
  - **Segurança:** Evita expor dados confidenciais ou colunas internas do banco (ex: senha criptografada, flags de auditoria).
  - **Controle:** Quando o usuário cadastra um item, ele não envia o `id` (pois o banco gera). Então podemos ter um `ItemRequestDTO(String nome, double preco)` para entrada e um `ItemResponseDTO(Long id, String nome, double preco)` para saída.
