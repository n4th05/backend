# 🔧 Guia de Troubleshooting

Este guia ajuda a resolver problemas comuns durante o desenvolvimento do projeto MoMo E-commerce.

---

## 📑 Índice

1. [Problemas de Ambiente](#problemas-de-ambiente)
2. [Problemas com Docker e Banco de Dados](#problemas-com-docker-e-banco-de-dados)
3. [Problemas com Spring Boot](#problemas-com-spring-boot)
4. [Problemas com JPA/Hibernate](#problemas-com-jpahibernate)
5. [Problemas com Maven](#problemas-com-maven)
6. [Problemas com VS Code](#problemas-com-vs-code)
7. [Problemas com Git](#problemas-com-git)
8. [Problemas de Performance](#problemas-de-performance)
9. [Erros HTTP Comuns](#erros-http-comuns)

---

## 🖥️ Problemas de Ambiente

### Java não reconhecido

**Erro:**
```bash
'java' is not recognized as an internal or external command
```

**Soluções:**

1. **Verificar instalação:**
   ```bash
   sdk list java
   sdk current java
   ```

2. **Reinstalar via SDKMAN:**
   ```bash
   sdk uninstall java 25
   sdk install java 25-open
   ```

3. **Verificar PATH (no PowerShell):**
   ```powershell
   $env:PATH -split ';' | Select-String -Pattern 'java'
   ```

4. **Reiniciar terminal** após instalação

---

### SDKMAN não funciona no PowerShell

**Solução:**

Use **Git Bash** ou **WSL** para SDKMAN.

Ou instale Java manualmente:
1. Baixe OpenJDK 25 de https://adoptium.net/
2. Configure variável de ambiente `JAVA_HOME`
3. Adicione `%JAVA_HOME%\bin` ao PATH

---

## 🐳 Problemas com Docker e Banco de Dados

### Docker Compose: "Cannot start service postgres"

**Erro:**
```
Error response from daemon: Ports are not available
```

**Causa:** Porta 5432 ou 5050 já está em uso.

**Soluções:**

1. **Verificar o que está usando a porta:**
   ```bash
   netstat -ano | findstr :5432
   netstat -ano | findstr :5050
   ```

2. **Matar processo:**
   ```bash
   taskkill /PID <número_do_processo> /F
   ```

3. **Ou mudar porta no docker-compose.yml:**
   ```yaml
   ports:
     - "5433:5432"  # Porta externa diferente
   ```

---

### PostgreSQL não inicia

**Verificar logs:**
```bash
docker logs momo-postgres
```

**Solções comuns:**

1. **Remover volumes corrompidos:**
   ```bash
   docker-compose down -v
   docker-compose up -d
   ```
   ⚠️ **ATENÇÃO:** Isso deleta todos os dados!

2. **Verificar Docker Desktop está rodando**

3. **Verificar WSL 2 está ativo** (se usar Docker Desktop no Windows)

---

### Não consigo conectar no pgAdmin

**URL:** http://localhost:5050

**Login padrão:**
- Email: `admin@momo.com`
- Senha: `admin123`

**Soluções:**

1. **Container está rodando?**
   ```bash
   docker ps | findstr pgadmin
   ```

2. **Reiniciar container:**
   ```bash
   docker-compose restart pgadmin
   ```

3. **Verificar logs:**
   ```bash
   docker logs momo-pgadmin
   ```

4. **Resetar senha (se esqueceu):**
   ```bash
   docker-compose down
   # Edite docker-compose.yml e mude PGADMIN_DEFAULT_PASSWORD
   docker-compose up -d
   ```

---

### Erro: "Connection refused" ao acessar banco

**Erro na aplicação:**
```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```

**Checklist:**

1. **PostgreSQL está rodando?**
   ```bash
   docker ps
   ```

2. **application.properties correto?**
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/momo_ecommerce
   spring.datasource.username=momo_user
   spring.datasource.password=momo_pass
   ```

3. **Firewall bloqueando?**
   - Temporariamente desative para testar

4. **Tente IP ao invés de localhost:**
   ```properties
   spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/momo_ecommerce
   ```

---

## 🍃 Problemas com Spring Boot

### Aplicação não inicia

**Erro genérico:**
```
APPLICATION FAILED TO START
```

**Passo a passo:**

1. **Leia a mensagem de erro completa** no console

2. **Erros comuns:**

   **a) Porta em uso:**
   ```
   Web server failed to start. Port 8080 was already in use.
   ```

   **Solução:**
   ```bash
   # Encontrar processo
   netstat -ano | findstr :8080
   
   # Matar processo
   taskkill /PID <número> /F
   
   # Ou mudar porta
   # application.properties:
   server.port=8081
   ```

   **b) Bean não encontrado:**
   ```
   Field repository in com.momo.ecommerce.service.ProductService required a bean of type 'ProductRepository' that could not be found.
   ```

   **Solução:**
   - Adicione `@Repository` no ProductRepository
   - Verifique que classe está no pacote correto
   - Verifique componentScan

   **c) Erro de sintaxe no application.properties:**
   ```
   Could not resolve placeholder 'spring.datasource.url'
   ```

   **Solução:**
   - Verifique sintaxe (sem espaços extras)
   - Use `=` não `:`
   - Sem aspas nos valores

---

### DevTools não reinicia automaticamente

**Solução:**

1. **Verificar dependência no pom.xml:**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-devtools</artifactId>
       <scope>runtime</scope>
       <optional>true</optional>
   </dependency>
   ```

2. **Ativar no application.properties:**
   ```properties
   spring.devtools.restart.enabled=true
   ```

3. **Configurar VS Code:**
   - Ative "Auto Save" (File → Auto Save)
   - Ou salve manualmente com Ctrl+S

---

### Endpoint não encontrado (404)

**Erro:**
```json
{
  "status": 404,
  "error": "Not Found",
  "path": "/api/products"
}
```

**Checklist:**

1. **Controller tem @RestController?**
   ```java
   @RestController
   @RequestMapping("/api/products")
   public class ProductController {
   ```

2. **Método tem annotation de mapeamento?**
   ```java
   @GetMapping  // ou @PostMapping, etc
   public ResponseEntity<List<ProductResponseDTO>> getAll() {
   ```

3. **URL está correta?**
   - Não esqueça `/api` se definido em @RequestMapping

4. **Verifique logs de startup:**
   ```
   Mapped "{[/api/products],methods=[GET]}"
   ```

---

## 💾 Problemas com JPA/Hibernate

### LazyInitializationException

**Erro:**
```
org.hibernate.LazyInitializationException: could not initialize proxy - no Session
```

**Causa:** Tentou acessar relacionamento lazy fora de uma transação.

**Soluções:**

1. **Use @Transactional no método:**
   ```java
   @Transactional(readOnly = true)
   public ProductResponseDTO findById(Long id) {
       // ...
   }
   ```

2. **Use JOIN FETCH:**
   ```java
   @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
   Optional<Product> findByIdWithCategory(@Param("id") Long id);
   ```

3. **Mude para EAGER (não recomendado):**
   ```java
   @ManyToOne(fetch = FetchType.EAGER)
   private Category category;
   ```

---

### Tabela não é criada

**Problema:** Aplicação inicia mas tabela não existe no banco.

**Soluções:**

1. **Verificar ddl-auto:**
   ```properties
   spring.jpa.hibernate.ddl-auto=update
   ```
   Opções:
   - `update` - Atualiza schema (DEV)
   - `create` - Recria sempre (CUIDADO!)
   - `none` - Não faz nada
   - `validate` - Só valida

2. **Verificar que entidade tem @Entity:**
   ```java
   @Entity
   @Table(name = "products")
   public class Product {
   ```

3. **Verificar package scan:**
   - Entidades devem estar em `com.momo.ecommerce.model` ou subpacote

4. **Verificar logs:**
   - Ative SQL logs:
     ```properties
     spring.jpa.show-sql=true
     logging.level.org.hibernate.SQL=DEBUG
     ```

---

### N+1 Query Problem

**Sintoma:** Muitas queries executadas para buscar dados relacionados.

**Exemplo:**
```
SELECT * FROM products;        -- 1 query
SELECT * FROM categories WHERE id = 1;  -- +1
SELECT * FROM categories WHERE id = 2;  -- +1
SELECT * FROM categories WHERE id = 3;  -- +1
... (para cada produto)
```

**Soluções:**

1. **JOIN FETCH:**
   ```java
   @Query("SELECT p FROM Product p JOIN FETCH p.category")
   List<Product> findAllWithCategory();
   ```

2. **@EntityGraph:**
   ```java
   @EntityGraph(attributePaths = {"category"})
   List<Product> findAll();
   ```

3. **DTO Projection:**
   ```java
   @Query("SELECT new com.momo.dto.ProductDTO(p.id, p.name, c.name) " +
          "FROM Product p JOIN p.category c")
   List<ProductDTO> findAllProjected();
   ```

---

### Erro: "could not execute statement"

**Erro completo:**
```
org.springframework.dao.DataIntegrityViolationException: could not execute statement; 
SQL [n/a]; constraint [uk_product_name]; nested exception is 
org.hibernate.exception.ConstraintViolationException
```

**Causa:** Violação de constraint (UNIQUE, NOT NULL, FOREIGN KEY).

**Soluções:**

1. **UNIQUE violation:**
   - Produto com mesmo nome já existe
   - Verifique antes de inserir

2. **NOT NULL violation:**
   - Campo obrigatório está null
   - Adicione validação @NotNull

3. **FOREIGN KEY violation:**
   - Referência a entidade que não existe
   - Verifique se categoria/user existe antes

---

## 📦 Problemas com Maven

### Dependências não baixam

**Solução:**

1. **Forçar download:**
   ```bash
   ./mvnw clean install -U
   ```
   `-U` força atualização de dependências

2. **Limpar cache do Maven:**
   ```bash
   # Windows
   rmdir /s %USERPROFILE%\.m2\repository
   
   # Depois
   ./mvnw clean install
   ```

3. **Verificar internet/proxy**

4. **Verificar pom.xml está válido:**
   - XML bem formatado
   - Tags fechadas corretamente

---

### Build falha com erro de compilação

**Erro:**
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin
```

**Soluções:**

1. **Java version mismatch:**
   ```xml
   <properties>
       <java.version>25</java.version>
   </properties>
   ```

2. **Limpar e recompilar:**
   ```bash
   ./mvnw clean compile
   ```

3. **Verificar imports:**
   - Classes importadas existem?
   - Dependências no pom.xml?

---

## 📝 Problemas com VS Code

### Lombok não funciona

**Sintomas:**
- Getters/Setters não reconhecidos
- Erros de compilação mesmo com @Getter/@Setter

**Soluções:**

1. **Instalar extensão:**
   - Extensions (Ctrl+Shift+X)
   - Buscar: "Lombok Annotations Support"
   - Instalar + Reload Window

2. **Verificar pom.xml:**
   ```xml
   <dependency>
       <groupId>org.projectlombok</groupId>
       <artifactId>lombok</artifactId>
       <optional>true</optional>
   </dependency>
   ```

3. **Clean workspace:**
   - Ctrl+Shift+P → "Java: Clean Java Language Server Workspace"
   - Reiniciar VS Code

---

### VS Code não reconhece projeto Maven

**Solução:**

1. **Instalar extensões:**
   - Extension Pack for Java
   - Spring Boot Extension Pack

2. **Reload window:**
   - Ctrl+Shift+P → "Developer: Reload Window"

3. **Maven sync:**
   - Clique direito em `pom.xml` → "Update Project"

---

### Autocompletion não funciona

**Soluções:**

1. **Aguardar indexação** (primeira vez demora)

2. **Verificar Java Language Server:**
   - Ctrl+Shift+P → "Java: Clean Java Language Server Workspace"

3. **Verificar configuração:**
   - Settings → Java: Configuration: Update Build Configuration → Automatic

---

## 🔀 Problemas com Git

### Conflito de merge

**Erro:**
```
CONFLICT (content): Merge conflict in src/main/java/...
Automatic merge failed; fix conflicts and then commit the result.
```

**Solução:**

1. **Abrir arquivos com conflito:**
   ```
   <<<<<<< HEAD
   // Seu código
   =======
   // Código da branch que está mergeando
   >>>>>>> feature-branch
   ```

2. **Escolher versão ou combinar**

3. **Remover marcadores de conflito** (`<<<<<<<`, `=======`, `>>>>>>>`)

4. **Adicionar e commitar:**
   ```bash
   git add .
   git commit -m "fix: resolve merge conflicts"
   ```

---

### Commit rejeitado

**Erro:**
```
! [rejected]        main -> main (fetch first)
```

**Solução:**

```bash
# Baixar mudanças
git pull origin main

# Resolver conflitos se houver

# Tentar push novamente
git push origin main
```

---

### Desfazer último commit

**Mantendo alterações:**
```bash
git reset --soft HEAD~1
```

**Descartando alterações:**
```bash
git reset --hard HEAD~1
```
⚠️ **CUIDADO:** Perde código!

---

## ⚡ Problemas de Performance

### Aplicação lenta

**Checklist:**

1. **Ativar logs de SQL:**
   ```properties
   spring.jpa.show-sql=true
   logging.level.org.hibernate.SQL=DEBUG
   ```

2. **Verificar N+1 queries** (ver seção JPA)

3. **Usar paginação:**
   ```java
   Page<Product> findAll(Pageable pageable);
   ```

4. **Indexar colunas de busca frequente:**
   ```java
   @Table(name = "products", indexes = {
       @Index(name = "idx_product_name", columnList = "name")
   })
   ```

5. **Considerar cache:**
   ```java
   @Cacheable("products")
   public Product findById(Long id) {
   ```

---

## 🌐 Erros HTTP Comuns

### 400 Bad Request

**Causa:** Dados inválidos na requisição.

**Soluções:**

1. **Verificar validações:**
   ```java
   @NotBlank(message = "Nome é obrigatório")
   private String name;
   ```

2. **Adicionar @Valid:**
   ```java
   public ResponseEntity create(@Valid @RequestBody ProductRequestDTO request) {
   ```

3. **Verificar JSON:**
   - Sintaxe correta?
   - Tipos corretos (number, string)?

---

### 404 Not Found

**Causas:**

1. **Recurso não existe** → Normal, lance ResourceNotFoundException
2. **URL errada** → Verificar @RequestMapping/@GetMapping
3. **Endpoint não mapeado** → Verificar se controller está com @RestController

---

### 500 Internal Server Error

**Causa:** Erro não tratado na aplicação.

**Solução:**

1. **Verificar logs do console**

2. **Implementar @ControllerAdvice:**
   ```java
   @ControllerAdvice
   public class GlobalExceptionHandler {
       @ExceptionHandler(Exception.class)
       public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
           ex.printStackTrace();  // Ver stack trace
           return ResponseEntity.status(500).body(...);
       }
   }
   ```

---

## 🆘 Quando Pedir Ajuda

Se ainda não resolver:

1. **Copie erro completo** (não só primeira linha)
2. **Compartilhe código relevante**
3. **Descreva o que tentou**
4. **Indique versões:**
   ```bash
   java -version
   mvn -version
   docker --version
   ```

**Onde pedir ajuda:**
- Stack Overflow (em inglês)
- GitHub Issues do projeto
- Comunidades Spring Boot (Discord/Telegram)

---

## 📚 Recursos Úteis

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Stack Overflow - Spring Boot](https://stackoverflow.com/questions/tagged/spring-boot)
- [Baeldung - Spring Tutorials](https://www.baeldung.com/spring-tutorial)

---

**Mantenha a calma e debug! 🐛🔍**
