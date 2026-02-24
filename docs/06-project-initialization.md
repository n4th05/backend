# 🚀 Guia: Inicialização do Projeto Spring Boot

Este guia mostra como criar o projeto Spring Boot do zero usando Spring Initializr e configurá-lo corretamente.

---

## 📋 Pré-requisitos

Antes de iniciar, certifique-se de que completou:

- ✅ [Setup do VS Code](01-setup-windows.md)
- ✅ [Instalação do Java 25 via SDKMAN](02-java-sdkman-setup.md)
- ✅ [Git Fundamentals](03-git-fundamentals.md)
- ✅ [Docker Desktop instalado](07-postgresql-docker.md)

Verifique no terminal:
```bash
# Java instalado
java -version
# Deve mostrar: openjdk version "25"

# Maven disponível (vem com Java)
mvn -version

# Git configurado
git --version
```

---

## 🌐 Passo 1: Criar Projeto no Spring Initializr

### 1.1 Acesse o Spring Initializr

Abra seu navegador e vá para: **https://start.spring.io/**

### 1.2 Configure o Projeto

Preencha os campos da seguinte forma:

#### **Project Metadata:**
```
Project:       Maven
Language:      Java
Spring Boot:   4.0.3 (ou versão LTS mais recente)
```

#### **Project Metadata:**
```
Group:         com.momo.ecommerce
Artifact:      momo-ecommerce
Name:          MoMo E-commerce
Description:   E-commerce project for learning Spring Boot
Package name:  com.momo.ecommerce
Packaging:     Jar
Java:          25
```

> 💡 **Explicação:**
> - **Group**: Identificador único da organização (convenção: domínio reverso)
> - **Artifact**: Nome do projeto (minúsculo, sem espaços)
> - **Name**: Nome amigável do projeto
> - **Packaging**: `Jar` inclui servidor embutido (Tomcat)
> - **Java**: Versão do Java que você instalou

### 1.3 Adicione as Dependências

Clique em **"ADD DEPENDENCIES"** e adicione:

#### **Essenciais para Fase 1:**
1. **Spring Web** - Para criar REST APIs
2. **Spring Data JPA** - Para acesso a banco de dados
3. **PostgreSQL Driver** - Driver do PostgreSQL
4. **Lombok** - Reduz código boilerplate
5. **Validation** - Para validações de dados
6. **Spring Boot DevTools** - Reinício automático durante desenvolvimento

#### **Como adicionar:**
- Digite o nome da dependência na busca
- Clique para adicionar
- Verifique que aparecem na lista "Dependencies"

### 1.4 Gere o Projeto

1. Clique no botão **"GENERATE"** (Ctrl+Enter)
2. Um arquivo `.zip` será baixado: `momo-ecommerce.zip`

---

## 📂 Passo 2: Extrair e Abrir no VS Code

### 2.1 Extraia o Projeto

1. Navegue até a pasta **Downloads**
2. **Clique com botão direito** em `momo-ecommerce.zip`
3. Selecione **"Extrair tudo..."** ou **"Extract Here"**
4. Mova a pasta extraída para um local apropriado:
   ```
   Exemplo: C:\Users\SeuNome\projects\momo-ecommerce
   ```

> 💡 **Dica:** Evite pastas com espaços ou caracteres especiais no caminho!

### 2.2 Abra no VS Code

#### **Opção 1: Via Interface**
1. Abra o **VS Code**
2. **File → Open Folder...**
3. Navegue até a pasta `momo-ecommerce`
4. Clique em **"Selecionar pasta"**

#### **Opção 2: Via Terminal**
```bash
cd C:\Users\SeuNome\projects\momo-ecommerce
code .
```

### 2.3 Estrutura do Projeto

Após abrir, você verá esta estrutura:

```
momo-ecommerce/
├── .mvn/                          # Wrapper do Maven
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/momo/ecommerce/
│   │   │       └── MomoEcommerceApplication.java  # Classe principal
│   │   └── resources/
│   │       ├── application.properties  # Configurações
│   │       ├── static/                 # Arquivos estáticos (CSS, JS)
│   │       └── templates/              # Templates (se usar Thymeleaf)
│   └── test/
│       └── java/
│           └── com/momo/ecommerce/
│               └── MomoEcommerceApplicationTests.java
├── .gitignore
├── mvnw                          # Maven Wrapper (Unix)
├── mvnw.cmd                      # Maven Wrapper (Windows)
├── pom.xml                       # Dependências do projeto
└── README.md
```

> 💡 **Arquivo importante:** `pom.xml` - Gerenciador de dependências Maven

---

## ⚙️ Passo 3: Configurar Banco de Dados

### 3.1 Inicie o PostgreSQL com Docker

No terminal do VS Code (`` Ctrl+` ``):

```bash
# Copie o docker-compose.yml do projeto
# Já deve estar na raiz do projeto (se seguiu os guias anteriores)

# Se não tiver, copie de: /docs/docker-compose.yml para raiz

# Inicie o banco de dados
docker-compose up -d

# Verifique se está rodando
docker ps
```

Você deve ver:
```
CONTAINER ID   IMAGE              STATUS          PORTS
abc123...      postgres:16-alpine Up 10 seconds   0.0.0.0:5432->5432/tcp
def456...      dpage/pgadmin4     Up 10 seconds   0.0.0.0:5050->80/tcp
```

### 3.2 Configure application.properties

Abra o arquivo: `src/main/resources/application.properties`

Substitua TODO o conteúdo por:

```properties
# =========================================
# APPLICATION
# =========================================
spring.application.name=momo-ecommerce

# =========================================
# DATABASE - PostgreSQL
# =========================================
spring.datasource.url=jdbc:postgresql://localhost:5432/momo_ecommerce
spring.datasource.username=momo_user
spring.datasource.password=momo_pass
spring.datasource.driver-class-name=org.postgresql.Driver

# =========================================
# JPA / HIBERNATE
# =========================================
# ddl-auto options:
# - none: Não faz nada
# - update: Atualiza schema automaticamente (DEV)
# - create: Recria schema toda vez (CUIDADO!)
# - create-drop: Cria e deleta ao parar app
# - validate: Só valida, não altera
spring.jpa.hibernate.ddl-auto=update

# Mostrar SQL executado no console (útil para aprender)
spring.jpa.show-sql=true

# Formatar SQL para melhor legibilidade
spring.jpa.properties.hibernate.format_sql=true

# Dialeto do PostgreSQL
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Mostrar tipos de bind do SQL (avançado)
# logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# =========================================
# SERVER
# =========================================
server.port=8080

# Contexto da aplicação (opcional)
# server.servlet.context-path=/api

# =========================================
# LOGGING
# =========================================
# Níveis: TRACE, DEBUG, INFO, WARN, ERROR
logging.level.root=INFO
logging.level.com.momo.ecommerce=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG

# =========================================
# DEVTOOLS (desenvolvimento)
# =========================================
# Reiniciar automaticamente quando código mudar
spring.devtools.restart.enabled=true

# LiveReload (atualiza navegador automaticamente)
spring.devtools.livereload.enabled=true
```

> 💡 **Importante:** Esses valores devem corresponder ao `docker-compose.yml`!

### 3.3 Verifique o pom.xml

Abra o arquivo `pom.xml` e confirme que contém estas dependências:

```xml
<dependencies>
    <!-- Spring Web: Para REST APIs -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Data JPA: Para acesso a dados -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok: Reduz boilerplate -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Validation: Validações de dados -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- DevTools: Desenvolvimento -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>

    <!-- Test: Para testes -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Se algo estiver faltando, adicione manualmente e salve. O VS Code baixará automaticamente.

---

## ▶️ Passo 4: Executar a Aplicação

### 4.1 Primeira Execução

No terminal do VS Code:

```bash
# Via Maven Wrapper (recomendado)
./mvnw spring-boot:run

# Ou se tiver Maven instalado globalmente
mvn spring-boot:run
```

> 💡 **No Windows:** Use `mvnw.cmd` se `./mvnw` não funcionar

### 4.2 O que Esperar

Você verá logs similares a:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v4.0.3)

2024-01-15 10:30:45.123  INFO 12345 --- [main] c.m.e.MomoEcommerceApplication : Starting MomoEcommerceApplication
2024-01-15 10:30:47.456  INFO 12345 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2024-01-15 10:30:48.789  INFO 12345 --- [main] o.hibernate.dialect.Dialect            : HHH000400: Using dialect: org.hibernate.dialect.PostgreSQLDialect
2024-01-15 10:30:49.012  INFO 12345 --- [main] c.m.e.MomoEcommerceApplication : Started MomoEcommerceApplication in 4.5 seconds
```

**Procure por:**
- ✅ `Started MomoEcommerceApplication` - **Sucesso!**
- ✅ `Tomcat started on port(s): 8080` - Servidor web rodando
- ✅ HHH000400 - Hibernate conectado ao PostgreSQL

### 4.3 Teste a Aplicação

Abra o navegador e acesse: **http://localhost:8080**

Você verá um erro **Whitelabel Error Page** - **Isso é NORMAL!**
```json
{
  "timestamp": "2024-01-15T10:35:00.123+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/"
}
```

> ✅ **Por quê?** Ainda não criamos nenhum endpoint! O servidor está funcionando.

### 4.4 Parar a Aplicação

No terminal onde está rodando:
- Pressione **Ctrl + C**

---

## 📁 Passo 5: Criar Estrutura de Pacotes

Vamos organizar o código ANTES de começar a programar.

### 5.1 Criar Pacotes no VS Code

1. No explorador de arquivos, navegue até:
   ```
   src/main/java/com/momo/ecommerce/
   ```

2. **Clique com botão direito** na pasta `ecommerce`

3. Selecione **"New Folder"** e crie:
   - `controller`
   - `service`
   - `repository`
   - `model`
   - `dto`
   - `exception`

4. Dentro da pasta `dto`, crie:
   - `request`
   - `response`

### 5.2 Estrutura Final

```
src/main/java/com/momo/ecommerce/
├── MomoEcommerceApplication.java  ← Classe principal
├── controller/                     ← REST controllers
├── service/                        ← Lógica de negócio
├── repository/                     ← Acesso a dados
├── model/                          ← Entidades JPA
├── dto/                            ← Data Transfer Objects
│   ├── request/                   ← DTOs de entrada
│   └── response/                  ← DTOs de saída
└── exception/                      ← Exceções customizadas
```

---

## 🧪 Passo 6: Primeiro Endpoint (Hello World)

Vamos criar um endpoint simples para validar que tudo está funcionando.

### 6.1 Criar HelloController

Crie o arquivo: `src/main/java/com/momo/ecommerce/controller/HelloController.java`

```java
package com.momo.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from MoMo E-commerce! 🚀";
    }

    @GetMapping("/health")
    public String health() {
        return "Application is running! ✅";
    }
}
```

### 6.2 Teste o Endpoint

1. **Inicie a aplicação** (se não estiver rodando):
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Abra o navegador** em: **http://localhost:8080/api/hello**

Você deve ver:
```
Hello from MoMo E-commerce! 🚀
```

3. Teste também: **http://localhost:8080/api/health**
```
Application is running! ✅
```

### 6.3 Teste via Terminal (curl)

```bash
curl http://localhost:8080/api/hello
curl http://localhost:8080/api/health
```

**Parabéns! 🎉 Seu primeiro endpoint está funcionando!**

---

## 🔧 Passo 7: Configurar Git

### 7.1 Inicializar Repositório

No terminal:

```bash
# Inicializar Git (se ainda não foi feito)
git init

# Verificar status
git status
```

### 7.2 Copiar .gitignore

O Spring Initializr já criou um `.gitignore`, mas vamos melhorar:

Substitua o conteúdo de `.gitignore` pelo arquivo do projeto:
- Copie de: `/docs/.gitignore` (se disponível)
- Ou use o conteúdo abaixo:

```gitignore
# Maven
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

# IntelliJ IDEA
.idea/
*.iws
*.iml
*.ipr

# VS Code
.vscode/
*.code-workspace

# Eclipse
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

# NetBeans
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/

# OS
.DS_Store
Thumbs.db

# Logs
*.log
logs/

# Sensitive data
application-local.properties
application-dev.properties
src/main/resources/application-secrets.properties
```

### 7.3 Primeiro Commit

```bash
# Adicionar todos os arquivos
git add .

# Commit inicial
git commit -m "chore: initial Spring Boot project setup"
```

---

## ✅ Checklist de Validação

Antes de prosseguir para a Fase 1, confirme:

- [ ] Projeto criado no Spring Initializr com configurações corretas
- [ ] Projeto extraído e aberto no VS Code
- [ ] Docker Compose rodando PostgreSQL e pgAdmin
- [ ] `application.properties` configurado corretamente
- [ ] Aplicação inicia sem erros
- [ ] Endpoint `/api/hello` responde corretamente
- [ ] Estrutura de pacotes criada
- [ ] Git inicializado com commit inicial
- [ ] VS Code reconhece o projeto Maven (ícone M nos arquivos Java)

---

## 🚨 Troubleshooting

### Problema: "Port 8080 already in use"

**Solução:**
```bash
# Encontrar processo usando porta 8080
netstat -ano | findstr :8080

# Matar processo (substitua PID pelo número encontrado)
taskkill /PID <número> /F

# Ou mude a porta no application.properties
server.port=8081
```

### Problema: "Cannot connect to database"

**Solução:**
```bash
# Verificar se PostgreSQL está rodando
docker ps

# Se não estiver, inicie:
docker-compose up -d

# Verificar logs do container
docker logs momo-postgres
```

### Problema: "Maven dependencies not downloading"

**Solução:**
1. Abra o Command Palette (Ctrl+Shift+P)
2. Digite: `Java: Clean Java Language Server Workspace`
3. Reinicie VS Code
4. Ou force download:
   ```bash
   ./mvnw clean install
   ```

### Problema: "Lombok annotations not working"

**Solução:**
1. Instale extensão: **Lombok Annotations Support for VS Code**
2. Reinicie VS Code
3. Verifique que Lombok está no `pom.xml`

---

## 📚 Próximos Passos

Agora que seu projeto está configurado:

1. ✅ **Leia:** [Spring Boot Concepts](05-spring-boot-concepts.md) - Entenda IoC, DI, etc.
2. ✅ **Pratique:** [Git Advanced](04-git-advanced.md) - Conventional Commits
3. 🚀 **Implemente:** [PHASE-1: Product CRUD](phases/PHASE-1-product-crud.md)

---

## 💡 Dicas Finais

### DevTools - Reinício Automático

Com DevTools, quando você **salvar** um arquivo Java:
- Aplicação reinicia automaticamente
- Não precisa parar e iniciar manualmente
- **Muito útil durante desenvolvimento!**

### Organize Workspace

Mantenha VS Code organizado:
- Abra terminal integrado (`` Ctrl+` ``)
- Use Explorer (Ctrl+Shift+E) para navegar arquivos
- Use Search (Ctrl+Shift+F) para buscar no projeto

### Próximas Dependências (Fase 5)

Eventualmente adicionará:
- **Spring Security** - Autenticação
- **JWT** - Tokens
- **Swagger/OpenAPI** - Documentação de API

**Mas foco agora na Fase 1!** 🎯

---

**Projeto inicializado! Hora de codificar! 🚀**
