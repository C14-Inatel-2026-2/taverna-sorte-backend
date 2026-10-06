# Taverna da Sorte - Backend

Backend do projeto Taverna da Sorte desenvolvido em Java com Spring Boot.

---

## Arquitetura de Camadas (Package-by-Layer)

A aplicação segue o padrão arquitetural em camadas, onde cada pacote possui responsabilidade única e bem definida:

### 1. `controller` (Camada de Apresentação / Web)
* **O que faz:** É a porta de entrada da sua aplicação. Escuta as requisições HTTP (`GET`, `POST`, `PUT`, `DELETE`) vindas do frontend, do Postman ou de outros sistemas.
* **Responsabilidades:**
  * Validar dados básicos que chegam na requisição.
  * Definir as rotas (ex: `/roleta/girar`, `/items`).
  * Definir os códigos de status HTTP corretos (`200 OK`, `201 Created`, `404 Not Found`).
  * Repassar o trabalho pesado para a camada `service`.
* **Regra de ouro:** Controllers devem ser simples — nunca coloque regras de negócio ou comandos de banco de dados diretamente aqui!

### 2. `service` (Camada de Negócio / Domínio)
* **O que faz:** Reside toda a inteligência e as regras de negócio da Taverna da Sorte.
* **Responsabilidades:**
  * Fazer cálculos e validações de regras (ex: verificar se o jogador tem moedas suficientes para girar a roleta).
  * Orquestrar chamadas a um ou mais repositórios.
  * Controlar transações de banco (`@Transactional`).

### 3. `repository` (Camada de Acesso a Dados)
* **O que faz:** É a camada que conversa diretamente com o banco de dados.
* **Responsabilidades:**
  * Executar operações de CRUD (*Create, Read, Update, Delete*).
  * O Spring Data JPA permite criar uma interface que herda de `JpaRepository<Item, Long>`, gerando os comandos SQL automaticamente sem precisar escrever SQL na mão.

### 4. `entity` (Camada de Entidades de Banco de Dados)
* **O que faz:** Representa o esquema das tabelas do banco de dados relacional mapeado em classes Java (via JPA/Hibernate).
* **Responsabilidades:**
  * Cada atributo da classe representa uma coluna na tabela.
  * Mapear chaves primárias (`@Id`), auto-incrementos (`@GeneratedValue`) e relacionamentos de chave estrangeira (`@ManyToOne`, `@OneToMany`).

### 5. `dto` (Data Transfer Object / Tráfego de Dados)
* **O que faz:** São objetos simples que servem apenas para transportar dados entre o cliente e a API.
* **Por que ela existe se já temos a `entity`?**
  * **Segurança:** Evita expor dados confidenciais ou colunas internas do banco (ex: senha criptografada, flags de auditoria).
  * **Controle:** Quando o usuário cadastra um item, ele não envia o `id` (pois o banco gera). Então podemos ter um `ItemRequestDTO(String nome, double preco)` para entrada e um `ItemResponseDTO(Long id, String nome, double preco)` para saída.

## Organização do Baú Encantado

O baú segue a organização por camada, com suas classes distribuídas assim:

```text
controller/BauEncantadoController.java
dto/ApostaBauRequest.java
dto/ResultadoBau.java
service/jogo/Jogo.java
service/jogo/BauEncantadoService.java
service/jogo/SorteadorSimbolo.java
service/jogo/SorteadorSimboloAleatorio.java
model/jogo/Resultado.java
model/jogo/bau/Simbolo.java
model/jogo/bau/DetalheBauEncantado.java
```

`BauEncantadoService` implementa `Jogo<Void>`, pois o baú não exige escolha
do jogador. A interface `SorteadorSimbolo` permite substituir o gerador
aleatório por um mock nos testes. `Simbolo` é um enum do modelo do jogo,
sem representar uma entidade persistida.

A rota `POST /bau/abrir` continua recebendo `{"valor": 10.0}` e retornando
os campos `simbolos` e `premio`. Três símbolos iguais pagam seu multiplicador,
um par devolve a aposta e três símbolos diferentes não pagam prêmio.

A entrada é validada com Bean Validation em `ApostaBauRequest` (`@NotNull` e
`@Positive`), e os erros são tratados pelo `GlobalExceptionHandler` no mesmo
formato usado em usuários (`{"error": "..."}`):

| Requisição | Status | `error` |
| --- | --- | --- |
| `{"valor": 0}` ou negativo | 400 | O valor da aposta deve ser maior que zero |
| `{}` ou `{"valor": null}` | 400 | O valor da aposta é obrigatório |
| `{"valor": "abc"}` ou JSON malformado | 400 | Valor inválido na requisição |

O `BauEncantadoService` também valida o valor por conta própria e lança
`ApostaInvalidaException`, pois pode ser chamado sem passar pelo controller.

Esta refatoração se limita ao baú. Dice, roleta, usuários e loja mantêm
a implementação da `main`, incluindo seus pacotes e contratos.

Para executar a suíte com Java 21 e Maven:

```shell
mvn clean test
```


