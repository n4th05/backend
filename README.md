# 🛒 MoMo E-commerce - Projeto Preparatório para Desenvolvedor Júnior

> **Objetivo**: Este projeto foi criado especificamente para desenvolvedores em nível de estágio/iniciante que desejam construir um portfólio sólido e se preparar para vagas de desenvolvedor júnior Java/Spring Boot.

## 📋 Sobre o Projeto

O **MoMo E-commerce** é uma API REST completa de um sistema de e-commerce simplificado, desenvolvida com as tecnologias mais demandadas pelo mercado:

- **Java 25** (versão LTS mais recente)
- **Spring Boot 4.0.3** (LTS)
- **PostgreSQL** (banco de dados relacional)
- **Docker** (containerização)
- **Maven** (gerenciamento de dependências)
- **JUnit 5 + Mockito** (testes)
- **Spring Security + JWT** (autenticação/autorização)
- **Swagger/OpenAPI** (documentação de API)

## 🎯 O Que Você Vai Aprender

Este projeto é **progressivo** - você vai construir funcionalidades incrementalmente, aprendendo novos conceitos em cada fase:

### ✅ Fundamentos
- Configuração completa do ambiente de desenvolvimento no Windows
- Uso profissional do Git (Conventional Commits, branch strategy)
- Conceitos de Spring Boot (IoC, DI, annotations)
- Arquitetura em camadas (Controller → Service → Repository)

### 💻 Desenvolvimento Backend
- Criação de APIs REST seguindo boas práticas
- Modelagem de banco de dados com JPA/Hibernate
- Relacionamentos entre entidades (OneToMany, ManyToOne, ManyToMany)
- Validação de dados com Bean Validation
- Tratamento global de exceções

### 🧪 Qualidade e Documentação
- Testes unitários e de integração
- TestContainers para testes com PostgreSQL real
- Documentação automática com Swagger
- Code review e boas práticas de clean code

### 🔒 Segurança
- Implementação de autenticação com Spring Security
- Tokens JWT para autorização
- Controle de acesso baseado em roles (ADMIN/CUSTOMER)

## 🗺️ Roadmap de Desenvolvimento

O projeto está dividido em **5 fases progressivas**. Cada fase é desenvolvida em uma branch específica:

| Fase | Descrição | Branch | Conceitos Principais |
|------|-----------|--------|---------------------|
| **Fase 1** | CRUD de Produtos | `feat/product-crud` | Entidade básica, Repository, Service, Controller, Testes |
| **Fase 2** | Categorias de Produtos | `feat/add-categories` | Relacionamento ManyToOne, DTOs, Validações |
| **Fase 3** | Clientes e Pedidos | `feat/add-orders` | Relacionamentos complexos, OrderItem, Agregações |
| **Fase 4** | Carrinho de Compras | `feat/add-cart` | Lógica de negócio complexa, Transações, Cálculos |
| **Fase 5** | Autenticação e Autorização | `feat/add-auth` | Spring Security, JWT, Roles, Endpoints protegidos |

## 📚 Documentação Completa

### 🚀 Guias de Setup (Comece aqui!)

1. **[Setup do Ambiente Windows](docs/01-setup-windows.md)**
   - Instalação do VS Code
   - Extensões essenciais para Java/Spring Boot
   - Configurações recomendadas

2. **[Java com SDKMAN](docs/02-java-sdkman-setup.md)**
   - Instalação do SDKMAN no Windows
   - Gerenciamento de múltiplas versões do Java
   - Configuração do Java 25

3. **[Git - Fundamentos](docs/03-git-fundamentals.md)**
   - Instalação do Git no Windows
   - Comandos básicos essenciais
   - Fluxo de trabalho básico

4. **[Git - Avançado](docs/04-git-advanced.md)**
   - Conventional Commits (padrão de mensagens)
   - Branch Strategy (Gitflow simplificado)
   - Boas práticas profissionais

### 🏗️ Guias Técnicos

5. **[Conceitos do Spring Boot](docs/05-spring-boot-concepts.md)**
   - O que é Spring Boot e por que usar
   - Inversion of Control (IoC) e Dependency Injection (DI)
   - Principais annotations e como funcionam

6. **[Inicialização do Projeto](docs/06-project-initialization.md)**
   - Usando Spring Initializr
   - Estrutura de pastas Maven
   - Configuração do application.properties

7. **[PostgreSQL com Docker](docs/07-postgresql-docker.md)**
   - Instalação do Docker Desktop no Windows
   - Configuração do docker-compose.yml
   - Uso do pgAdmin para visualizar dados

8. **[JPA e Hibernate](docs/08-jpa-hibernate-guide.md)**
   - O que é ORM (Object-Relational Mapping)
   - Criação de entidades
   - Tipos de relacionamentos
   - Migrations com Flyway

9. **[Design de APIs REST](docs/09-rest-api-design.md)**
   - Princípios REST
   - DTOs (Data Transfer Objects)
   - Códigos de status HTTP
   - Boas práticas de endpoints

10. **[Validação e Tratamento de Exceções](docs/10-validation-exceptions.md)**
    - Bean Validation annotations
    - @ControllerAdvice
    - Criação de exceções customizadas
    - Padronização de respostas de erro

11. **[Guia de Testes](docs/11-testing-guide.md)**
    - Testes unitários com JUnit 5
    - Mocks com Mockito
    - Testes de integração com @SpringBootTest
    - TestContainers

12. **[Documentação com Swagger](docs/12-swagger-documentation.md)**
    - Configuração do SpringDoc OpenAPI
    - Annotations para documentação
    - Swagger UI

13. **[Spring Security e JWT](docs/13-security-jwt.md)**
    - Conceitos de autenticação vs autorização
    - Implementação de JWT
    - Configuração de roles e permissions
    - Proteção de endpoints

### 📝 Implementação por Fases

Cada fase tem um guia detalhado com:
- ✅ Objetivos de aprendizado
- 📖 Conceitos explicados
- 💻 Implementação passo-a-passo
- 🧪 Testes a serem criados
- 🔍 Checklist de verificação

- **[Fase 1: CRUD de Produtos](docs/phases/PHASE-1-product-crud.md)**
- **[Fase 2: Categorias de Produtos](docs/phases/PHASE-2-categories.md)**
- **[Fase 3: Clientes e Pedidos](docs/phases/PHASE-3-customers-orders.md)**
- **[Fase 4: Carrinho de Compras](docs/phases/PHASE-4-shopping-cart.md)**
- **[Fase 5: Autenticação e Autorização](docs/phases/PHASE-5-authentication.md)**

### 🛠️ Recursos Adicionais

- **[Templates de Código](docs/templates/)** - Estruturas prontas para copiar e adaptar
- **[Exemplos Práticos](docs/examples/)** - Exemplos de commits, PRs, code reviews
- **[Checklists de Verificação](docs/checklists/)** - O que validar antes de cada merge
- **[Troubleshooting](docs/troubleshooting.md)** - Soluções para problemas comuns no Windows
- **[Boas Práticas](docs/best-practices.md)** - Clean code e padrões profissionais
- **[Recursos de Estudo](docs/resources.md)** - Links, documentação oficial, tutoriais

## 🚦 Como Começar

### Passo 1: Preparação do Ambiente
```bash
# Siga os guias na ordem:
1. docs/01-setup-windows.md      # VS Code e extensões
2. docs/02-java-sdkman-setup.md  # Java 25 com SDKMAN
3. docs/03-git-fundamentals.md   # Git básico
4. docs/04-git-advanced.md       # Git profissional
```

### Passo 2: Estude os Conceitos
```bash
5. docs/05-spring-boot-concepts.md    # Entenda o Spring Boot
6. docs/06-project-initialization.md  # Inicialize o projeto
7. docs/07-postgresql-docker.md       # Configure o banco de dados
```

### Passo 3: Implemente as Fases (Mão na Massa!)
```bash
# Cada fase é uma branch nova a partir de develop
8. docs/phases/PHASE-1-product-crud.md        # Comece aqui!
9. docs/phases/PHASE-2-categories.md
10. docs/phases/PHASE-3-customers-orders.md
11. docs/phases/PHASE-4-shopping-cart.md
12. docs/phases/PHASE-5-authentication.md
```

### Passo 4: Prepare-se para Entrevistas
```bash
# Revise e consolide conhecimento
- Leia docs/best-practices.md
- Complete docs/checklists/pre-interview-checklist.md
- Pratique explicar o que fez em cada fase
```

## 🎓 Habilidades que Você Vai Desenvolver

Ao completar este projeto, você terá demonstrado capacidade de:

- ✅ Configurar um ambiente profissional de desenvolvimento Java
- ✅ Criar APIs REST completas e bem documentadas
- ✅ Modelar bancos de dados relacionais
- ✅ Escrever código limpo e testável
- ✅ Usar Git de forma profissional em workflow de equipe
- ✅ Implementar autenticação e autorização
- ✅ Trabalhar com Docker e containers
- ✅ Seguir padrões e convenções do mercado
- ✅ Resolver problemas de forma autônoma
- ✅ Documentar código e decisões técnicas

## 💼 Este Projeto no Seu Portfólio

### O que adicionar no README do seu GitHub:
- ✅ Tecnologias utilizadas
- ✅ Funcionalidades implementadas
- ✅ Desafios técnicos superados
- ✅ Screenshots do Swagger UI
- ✅ Como rodar o projeto (docker-compose up)
- ✅ Exemplos de requisições (Postman/curl)

### O que mostrar em entrevistas:
- ✅ Arquitetura do projeto (diagrama de classes)
- ✅ Decisões técnicas tomadas e por quê
- ✅ Testes implementados (cobertura)
- ✅ Conhecimento dos conceitos aplicados
- ✅ Capacidade de explicar o código

## 🤝 Contribuindo

Este é um projeto de aprendizado, mas seguimos padrões profissionais. Leia o [CONTRIBUTING.md](CONTRIBUTING.md) para entender:
- Padrão de commits (Conventional Commits)
- Fluxo de branches (Gitflow)
- Como fazer code review
- Padrões de código do projeto

## 📞 Precisa de Ajuda?

- 📖 **Consulte primeiro**: [docs/troubleshooting.md](docs/troubleshooting.md)
- 🔍 **Documentação oficial**: [docs/resources.md](docs/resources.md)
- 💬 **Comunidades**: Stack Overflow, Reddit r/java, Discord de programação

## 📊 Status do Projeto

- [ ] Fase 1: CRUD de Produtos
- [ ] Fase 2: Categorias
- [ ] Fase 3: Clientes e Pedidos
- [ ] Fase 4: Carrinho de Compras
- [ ] Fase 5: Autenticação e Autorização

---

**💡 Lembre-se**: Este projeto é sobre **aprender fazendo**. Não tenha pressa. Entenda cada conceito antes de avançar. A qualidade do aprendizado é mais importante que a velocidade.

**🚀 Boa sorte na sua jornada para desenvolvedor júnior!**
