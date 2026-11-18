# Supernova Colheita

Global Solution 2026/2 — CitizenTech | FIAP  
Disciplina: Java Advanced  
Repositório: https://github.com/kaique-masc/SUPERNOVA_COLHEITA_JAVA  
Deploy: não há; execução local

## Integrantes

| Nome | RM | Turma |
|---|---|---|
| Felipe Augusto Lopes Ferreira | RM563982 | 2TDSPG |
| Kaique Mascarenhas dos Santos | RM565802 | 2TDSPG |

## Projeto

O Supernova Colheita é uma API REST desenvolvida para conectar produtores da agricultura familiar a compradores da região, principalmente escolas públicas. A proposta é facilitar a divulgação de alimentos disponíveis, a realização de pedidos e o acompanhamento das compras.

O sistema também permite que escolas gerem sugestões de cardápios com inteligência artificial, considerando as ofertas cadastradas, a quantidade de alunos, o orçamento e as restrições alimentares. Depois de gerar o cardápio, é possível transformá-lo em um pedido.

O projeto está relacionado aos ODS 2, 10 e 11.

## Tecnologias utilizadas

- Java 21 ou superior
- Spring Boot 3.5.14
- Spring Web e Spring Data JPA
- Bean Validation
- Spring Security e JWT
- Spring AI 1.1.8 e Google Gemini
- RabbitMQ e Spring AMQP
- Banco H2
- Swagger/OpenAPI
- Maven, Docker Compose, JUnit 5 e Mockito

## Funcionalidades

- Cadastro e autenticação de usuários
- Perfis de produtor, comprador e gestor
- Cadastro e consulta de produtores, compradores e produtos
- Cadastro, atualização e consulta de ofertas
- Criação e acompanhamento de pedidos
- Geração de cardápios com IA
- Conversão de cardápios em pedidos
- Indicadores de compras relacionados ao PNAE
- Notificações de pedidos por meio do RabbitMQ
- Validação dos dados e tratamento de erros
- Listagens com paginação

## Estrutura do projeto

O backend está organizado em controllers, services, repositories, models e DTOs.

As principais entidades são Usuario, Produtor, Comprador, Produto, Oferta, Pedido, ItemPedido, Cardapio, ItemCardapio e Notificacao.

O acesso ao banco é feito com Spring Data JPA. A autenticação utiliza JWT, com permissões específicas para PRODUTOR, COMPRADOR e GESTOR.

## Como executar

É necessário ter o JDK 21 ou superior e o Docker Desktop instalado e em execução.

Clone o repositório:

```bash
git clone https://github.com/kaique-masc/SUPERNOVA_COLHEITA_JAVA.git
cd SUPERNOVA_COLHEITA_JAVA
```

Inicie o RabbitMQ:

```bash
docker compose up -d
```

Configure a chave do Gemini no PowerShell:

```powershell
$env:GEMINI_API_KEY="SUA_CHAVE_GEMINI"
```

Execute a aplicação:

```powershell
.\mvnw.cmd spring-boot:run
```

Também é possível executar pelo IntelliJ, abrindo a pasta que contém o `pom.xml` e configurando `GEMINI_API_KEY` nas variáveis de ambiente da execução.

No Linux ou macOS, a configuração e execução são feitas com:

```bash
export GEMINI_API_KEY="SUA_CHAVE_GEMINI"
./mvnw spring-boot:run
```

A chave real deve ser configurada apenas no ambiente local, sem ser adicionada ao repositório. Sem uma chave Gemini válida, a geração de cardápios não funciona; os demais recursos podem ser utilizados normalmente.

### Variáveis de ambiente

| Variável | Finalidade |
|---|---|
| `GEMINI_API_KEY` | Chave de acesso à API Gemini |
| `IA_MODELO` | Modelo Gemini, com padrão `gemini-3.5-flash-lite` |
| `JWT_SECRET` | Segredo usado para assinatura dos tokens JWT |
| `RABBITMQ_HOST` | Servidor RabbitMQ; padrão `localhost` |
| `RABBITMQ_PORT` | Porta RabbitMQ; padrão `5672` |
| `RABBITMQ_USER` | Usuário RabbitMQ; padrão local `guest` |
| `RABBITMQ_PASSWORD` | Senha RabbitMQ; padrão local `guest` |
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | Conexão com Oracle no perfil `oracle` |

O projeto utiliza H2 na execução padrão. Também existe um perfil Oracle em `src/main/resources/application-oracle.properties`, que pode ser utilizado com a configuração de banco correspondente.

## Acessos locais

| Serviço | Endereço |
|---|---|
| Swagger | http://localhost:8080/swagger-ui/index.html |
| OpenAPI | http://localhost:8080/v3/api-docs |
| H2 Console | http://localhost:8080/h2-console |
| RabbitMQ | http://localhost:15672 |

O RabbitMQ utiliza `guest` como usuário e senha no ambiente local. O banco H2 é executado em memória e recebe dados de exemplo ao iniciar a aplicação.

## Usuários para teste

A senha dos usuários de exemplo é `123456`.

| E-mail | Perfil |
|---|---|
| gestor@supernova.com | GESTOR |
| escola@supernova.com | COMPRADOR |
| restaurante@supernova.com | COMPRADOR |
| jose@supernova.com | PRODUTOR |
| maria@supernova.com | PRODUTOR |
| terraviva@supernova.com | PRODUTOR |
| benedita@supernova.com | PRODUTOR |

O usuário `benedita@supernova.com` representa um produtor com CAF vencido e permite testar as regras de elegibilidade das ofertas.

Para entrar, utilize o endpoint `POST /auth/login`:

```json
{
  "email": "escola@supernova.com",
  "senha": "123456"
}
```

O token retornado deve ser informado na opção **Authorize** do Swagger para acessar os endpoints protegidos.

## Principais endpoints

| Método | Rota | Função |
|---|---|---|
| POST | `/auth/login` | Login |
| POST | `/auth/registro/produtor` | Cadastro de produtor |
| POST | `/auth/registro/comprador` | Cadastro de comprador |
| GET | `/auth/me` | Dados do usuário autenticado |
| GET, POST | `/produtos` | Listar e cadastrar produtos |
| PUT, DELETE | `/produtos/{id}` | Atualizar e excluir produtos |
| GET | `/produtores` | Listar produtores |
| GET, POST | `/ofertas` | Listar e cadastrar ofertas |
| GET | `/ofertas/minhas` | Ofertas do produtor |
| PUT, DELETE | `/ofertas/{id}` | Atualizar e cancelar ofertas |
| GET, POST | `/pedidos` | Listar e criar pedidos |
| GET | `/pedidos/{id}` | Consultar pedido |
| PATCH | `/pedidos/{id}/status` | Alterar status do pedido |
| GET | `/pnae/metas` | Indicadores para gestor |
| GET | `/pnae/metas/minha` | Indicadores da escola |
| GET, POST | `/cardapios` | Listar e gerar cardápios |
| GET, DELETE | `/cardapios/{id}` | Consultar e excluir cardápio |
| POST | `/cardapios/{id}/pedido` | Converter cardápio em pedido |
| GET | `/notificacoes` | Consultar notificações |
| PATCH | `/notificacoes/{id}/lida` | Marcar notificação como lida |

As listagens paginadas permitem o uso de `page`, `size` e `sort`.

## Integração com inteligência artificial

Utilizamos o Spring AI com Google Gemini para gerar sugestões de cardápios escolares.

A escola informa a quantidade de alunos, os dias letivos, o orçamento e as restrições. O sistema consulta as ofertas disponíveis, envia os dados ao modelo e valida a resposta antes de salvar o cardápio.

Exemplo de requisição para `POST /cardapios`:

```json
{
  "numeroAlunos": 100,
  "orcamento": 5000,
  "diasLetivos": 5,
  "restricoes": "Nenhuma restrição alimentar"
}
```

O cardápio pode ser convertido em pedido pelo endpoint `POST /cardapios/{id}/pedido`. As sugestões precisam de avaliação nutricional antes do uso real.

## Mensageria com RabbitMQ

Escolhemos o RabbitMQ para a comunicação assíncrona dos eventos de pedidos.

Quando um pedido é criado ou tem seu status alterado, a aplicação publica um evento. O listener recebe a mensagem e registra a notificação correspondente.

- Exchange: `supernova.pedidos`
- Fila: `supernova.pedidos.notificacoes`
- Routing keys: `pedido.pedido_criado` e `pedido.status_alterado`

Essa implementação corresponde à **opção A (RabbitMQ)** do requisito de arquitetura distribuída.

## Segurança e validações

A autenticação é feita por JWT e as senhas são armazenadas com BCrypt. O Spring Security controla o acesso conforme o perfil do usuário.

Os DTOs utilizam Bean Validation e os erros são tratados de forma centralizada com `@RestControllerAdvice`.

A configuração de desenvolvimento utiliza um segredo JWT padrão, console H2 habilitado e CORS permissivo. Para utilização em produção, é necessário configurar um `JWT_SECRET` forte, restringir as origens permitidas e desabilitar o console H2.

## Testes

Para executar os testes automatizados no Windows:

```powershell
.\mvnw.cmd test
```

No Linux ou macOS:

```bash
./mvnw test
```

Foram implementados testes para os serviços de pedidos e PNAE. Também realizamos testes manuais pelo Swagger para autenticação, ofertas, pedidos, cardápios e troca de mensagens com o RabbitMQ.

## Integração com Mobile

A API disponibiliza endpoints REST com respostas JSON e autenticação JWT para o aplicativo da disciplina de Mobile. O fluxo de integração envolve autenticação pelo endpoint `/auth/login`, armazenamento do token no aplicativo e envio do cabeçalho `Authorization: Bearer` nas requisições protegidas.

O aplicativo pode consumir os dados de produtos, ofertas e pedidos, respeitando as permissões de cada perfil. A integração completa entre aplicativo e API ainda precisa ser demonstrada e validada no ambiente de apresentação.

## Uso de inteligência artificial no desenvolvimento

Utilizamos ferramentas de inteligência artificial como apoio na implementação, na identificação de erros e na revisão de código. O projeto também integra o Google Gemini ao fluxo de geração de cardápios por meio do Spring AI.

## Repositório e entrega

https://github.com/kaique-masc/SUPERNOVA_COLHEITA_JAVA

O vídeo de apresentação será disponibilizado junto aos materiais da entrega.
