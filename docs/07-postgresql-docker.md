# 🐘 PostgreSQL com Docker no Windows

Este guia ensina como configurar PostgreSQL usando Docker no Windows para desenvolvimento local.

## 📋 Índice

1. [O que é Docker?](#o-que-é-docker)
2. [Instalação do Docker Desktop](#instalação-do-docker-desktop)
3. [Docker Compose](#docker-compose)
4. [PostgreSQL + pgAdmin](#postgresql--pgadmin)
5. [Conexão com Spring Boot](#conexão-com-spring-boot)
6. [Comandos Úteis](#comandos-úteis)

---

## 1. O que é Docker?

### Definição

**Docker** é uma plataforma para **executar aplicações em containers**.

**Container** = Pacote com tudo que a aplicação precisa (código, runtime, bibliotecas, dependências)

### Analogia

**Sem Docker**:
- "Funciona na minha máquina!" 🤷‍♂️
- Cada desenvolvedor instala PostgreSQL diferente
- Versões diferentes, configurações diferentes
- Difícil replicar ambientes

**Com Docker**:
- Todos rodam o **mesmo container**
- Garantia de **ambiente idêntico**
- Roda em **qualquer máquina** (Windows, Mac, Linux)
- **Isola** do sistema operacional

### Por que usar para banco de dados?

✅ **Não "suja" seu Windows** com instalações  
✅ **Fácil de resetar** (deletar container e criar novo)  
✅ **Múltiplas versões** (PostgreSQL 14, 15, 16 simultaneamente)  
✅ **Portátil** (mesmo setup em dev, staging, prod)  
✅ **Padrão do mercado** (toda empresa usa containers)

---

## 2. Instalação do Docker Desktop

### 2.1. Requisitos

- Windows 10 64-bit versão 2004+ **ou** Windows 11
- WSL 2 ou Hyper-V habilitado
- Virtualização habilitada na BIOS

### 2.2. Verificar Virtualização

Pressione `Ctrl+Shift+Esc` (Task Manager) → Aba "Performance" → CPU:

Deve mostrar: **"Virtualization: Enabled"**

Se estiver **Disabled**, precisa habilitar na BIOS (varia por fabricante).

### 2.3. Baixar Docker Desktop

1. Acesse: https://www.docker.com/products/docker-desktop/
2. Clique em **"Download for Windows"**
3. Execute o instalador (`Docker Desktop Installer.exe`)

### 2.4. Instalação

1. Marque: **"Use WSL 2 instead of Hyper-V"** (recomendado)
2. Clique em **"Ok"**
3. Aguarde instalação (pode demorar alguns minutos)
4. **Reinicie o computador** quando solicitado

### 2.5. Primeira Execução

1. Abra **Docker Desktop** (procure no menu Iniciar)
2. Aceite os termos de serviço
3. Tutorial inicial (pode pular)
4. Aguarde Docker **iniciar** (ícone da baleia no system tray ficará quieto)

### 2.6. Verificar Instalação

Abra **Git Bash** ou **PowerShell** e execute:

```bash
docker --version
```

Deve mostrar:
```
Docker version 24.X.X, build xxxxx
```

E também:

```bash
docker-compose --version
```

Deve mostrar:
```
Docker Compose version v2.XX.X
```

✅ **Docker instalado com sucesso!**

---

## 3. Docker Compose

### O que é?

**Docker Compose** é uma ferramenta para **definir e rodar múltiplos containers** via arquivo YAML.

**Exemplo**: Rodar PostgreSQL + pgAdmin juntos.

### Estrutura do docker-compose.yml

No **root do projeto**, crie arquivo `docker-compose.yml`:

```yaml
version: '3.8'

services:
  # Serviço PostgreSQL
  postgres:
    image: postgres:16-alpine       # Imagem oficial do PostgreSQL 16
    container_name: momo-postgres   # Nome do container
    restart: always                 # Reinicia automaticamente
    environment:
      POSTGRES_DB: ecommerce        # Nome do banco
      POSTGRES_USER: postgres       # Usuário
      POSTGRES_PASSWORD: postgres   # Senha (trocar em produção!)
    ports:
      - "5432:5432"                 # Porta host:container
    volumes:
      - postgres_data:/var/lib/postgresql/data  # Persistir dados
    networks:
      - momo-network

  # Serviço pgAdmin (interface web para PostgreSQL)
  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: momo-pgadmin
    restart: always
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@momo.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "5050:80"                   # Acessar via http://localhost:5050
    depends_on:
      - postgres                    # Inicia após o postgres
    networks:
      - momo-network

# Volumes para persistir dados
volumes:
  postgres_data:

# Rede para comunicação entre containers
networks:
  momo-network:
    driver: bridge
```

### Explicação

**postgres service**:
- `image`: Qual imagem Docker usar (PostgreSQL versão 16, Alpine Linux)
- `container_name`: Nome customizado do container
- `environment`: Variáveis de ambiente (config do banco)
- `ports`: Mapeia porta 5432 do container para 5432 do Windows
- `volumes`: Persiste dados (não perde ao reiniciar)

**pgadmin service**:
- Interface gráfica web para gerenciar PostgreSQL
- Acessa via navegador: http://localhost:5050
- Login: admin@momo.com / admin

---

## 4. PostgreSQL + pgAdmin

### 4.1. Iniciar Containers

No **terminal** (Git Bash ou PowerShell), na **raiz do projeto**:

```bash
docker-compose up -d
```

**Flags**:
- `up`: Iniciar serviços
- `-d`: Detached mode (roda em background)

Saída esperada:
```
[+] Running 3/3
 ✔ Network momo-network       Created
 ✔ Container momo-postgres    Started
 ✔ Container momo-pgadmin     Started
```

### 4.2. Verificar Containers Rodando

```bash
docker ps
```

Deve mostrar 2 containers: `momo-postgres` e `momo-pgadmin`

### 4.3. Acessar pgAdmin

1. Abra navegador: http://localhost:5050
2. Login:
   - **Email**: `admin@momo.com`
   - **Password**: `admin`

3. Adicionar servidor PostgreSQL:
   - Clique direito em **"Servers"** → **"Register" → "Server"**
   
   **Aba General**:
   - Name: `momo-local`
   
   **Aba Connection**:
   - Host name/address: `postgres` (nome do service no docker-compose)
   - Port: `5432`
   - Maintenance database: `ecommerce`
   - Username: `postgres`
   - Password: `postgres`
   - ✅ Save password
   
4. Clique em **"Save"**

✅ **Conectado ao PostgreSQL!**

### 4.4. Testar Conexão

No pgAdmin:
1. Expanda: **Servers → momo-local → Databases → ecommerce**
2. Clique direito em ecommerce → **"Query Tool"**
3. Execute:

```sql
SELECT version();
```

Deve mostrar a versão do PostgreSQL!

---

## 5. Conexão com Spring Boot

### 5.1. Adicionar Dependências no pom.xml

```xml
<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### 5.2. Configurar application.properties

Em `src/main/resources/application.properties`:

```properties
# Database Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### 5.3. Explicação das Propriedades

**spring.datasource.***: Conexão com banco
- `url`: JDBC URL (host:porta/database)
- `username/password`: Credenciais

**spring.jpa.hibernate.ddl-auto**:
- `none`: Não faz nada
- `validate`: Valida schema
- `update`: Atualiza schema (cria/altera tabelas conforme Entities)
- `create`: Cria schema do zero (apaga dados!)
- `create-drop`: Cria e apaga ao finalizar app

> ⚠️ **Produção**: SEMPRE use `none` ou `validate`!  
> 💡 **Dev**: `update` é ok.

**spring.jpa.show-sql**: Mostra SQL no console (útil para debug)

### 5.4. Testar Conexão

Crie uma entidade simples:

```java
package com.momo.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Double price;
    
    // Getters e Setters
}
```

Rode a aplicação:

```bash
mvn spring-boot:run
```

Se aparecer no console:
```sql
Hibernate: create table products (...)
```

✅ **Spring Boot conectado ao PostgreSQL!**

---

## 6. Comandos Úteis

### Docker Compose

```bash
# Iniciar todos os serviços
docker-compose up -d

# Ver logs
docker-compose logs -f

# Ver logs de um serviço específico
docker-compose logs -f postgres

# Parar serviços (mantém dados)
docker-compose stop

# Parar e remover containers (mantém volumes)
docker-compose down

# Parar e remover TUDO (inclusive volumes - apaga banco!)
docker-compose down -v

# Reiniciar serviços
docker-compose restart

# Ver status dos serviços
docker-compose ps
```

### Docker Containers

```bash
# Listar containers rodando
docker ps

# Listar TODOS os containers (inclusive parados)
docker ps -a

# Parar container
docker stop momo-postgres

# Iniciar container parado
docker start momo-postgres

# Reiniciar container
docker restart momo-postgres

# Ver logs de um container
docker logs momo-postgres

# Seguir logs em tempo real
docker logs -f momo-postgres

# Executar comando dentro do container
docker exec -it momo-postgres psql -U postgres -d ecommerce

# Remover container
docker rm momo-postgres

# Remover container forçado (mesmo se rodando)
docker rm -f momo-postgres
```

### Docker Images

```bash
# Listar imagens baixadas
docker images

# Baixar imagem
docker pull postgres:16-alpine

# Remover imagem
docker rmi postgres:16-alpine

# Remover imagens não utilizadas
docker image prune
```

### Docker Volumes

```bash
# Listar volumes
docker volume ls

# Inspecionar volume
docker volume inspect momo-project_postgres_data

# Remover volume (apaga dados!)
docker volume rm momo-project_postgres_data

# Remover volumes não utilizados
docker volume prune
```

### Acesso Direto ao PostgreSQL via Terminal

```bash
# Entrar no container e abrir psql
docker exec -it momo-postgres psql -U postgres -d ecommerce
```

Dentro do `psql`:

```sql
-- Listar bancos
\l

-- Conectar a banco
\c ecommerce

-- Listar tabelas
\dt

-- Descrever tabela
\d products

-- Executar query
SELECT * FROM products;

-- Sair
\q
```

---

## ✅ Checklist de Verificação

- [ ] Docker Desktop instalado e rodando
- [ ] `docker --version` funciona
- [ ] `docker-compose.yml` criado
- [ ] Containers iniciados (`docker-compose up -d`)
- [ ] PostgreSQL acessível na porta 5432
- [ ] pgAdmin acessível em http://localhost:5050
- [ ] Servidor configurado no pgAdmin
- [ ] Spring Boot conecta ao PostgreSQL
- [ ] Tabelas sendo criadas automaticamente

---

## 🆘 Problemas Comuns

### "Docker daemon is not running"

**Solução**: 
1. Abra Docker Desktop
2. Aguarde iniciar completamente
3. Tente novamente

### "Port 5432 is already in use"

**Causa**: PostgreSQL já instalado no Windows ou outro container usando porta.

**Solução 1** - Mudar porta no docker-compose.yml:
```yaml
ports:
  - "5433:5432"  # Mudar para 5433 no host
```

Então conectar usando porta 5433.

**Solução 2** - Parar PostgreSQL instalado no Windows:
- Abra "Services" (services.msc)
- Procure "postgresql"
- Clique direito → Stop

### "Cannot connect to Docker daemon"

**Solução**:
- Windows: Reinicie Docker Desktop
- Se não resolver: Reinstale Docker Desktop

### "pgAdmin não abre"

**Solução**:
```bash
# Ver logs do pgadmin
docker logs momo-pgadmin

# Reiniciar container
docker restart momo-pgadmin
```

### "Esqueci senha do pgAdmin"

**Solução**:
```bash
# Recriar container pgadmin
docker-compose down
docker-compose up -d
```

Senha volta para `admin` (do docker-compose.yml)

---

## 🎯 Próximos Passos

Com PostgreSQL rodando, vamos aprender JPA e Hibernate:

➡️ **[Próximo: JPA e Hibernate](./08-jpa-hibernate-guide.md)**

---

## 📚 Recursos Adicionais

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [PostgreSQL Docker Image](https://hub.docker.com/_/postgres)
- [pgAdmin Documentation](https://www.pgadmin.org/docs/)
