# 🤝 Guia de Contribuição

Este documento define os padrões e processos para contribuir com o projeto MoMo E-commerce.

## 📋 Índice

1. [Git Workflow](#git-workflow)
2. [Padrões de Código](#padrões-de-código)
3. [Conventional Commits](#conventional-commits)
4. [Como Fazer um Pull Request](#como-fazer-um-pull-request)
5. [Code Review](#code-review)
6. [Testes](#testes)

---

## 1. Git Workflow

### Estrutura de Branches

```
main           (produção - protegida)
  └── develop  (integração - protegida)
        ├── feat/product-crud
        ├── feat/add-categories
        ├── feat/add-orders
        ├── feat/add-cart
        └── feat/add-auth
```

### Criando uma Feature

```bash
# 1. Atualizar develop
git checkout develop
git pull origin develop

# 2. Criar branch da feature
git checkout -b feat/nome-da-feature

# 3. Trabalhar na feature (múltiplos commits)
git add .
git commit -m "feat: descrição clara"

# 4. Push da branch
git push -u origin feat/nome-da-feature

# 5. Abrir Pull Request no GitHub
# (develop ← feat/nome-da-feature)

# 6. Após aprovação e merge, deletar branch
git checkout develop
git pull origin develop
git branch -d feat/nome-da-feature
```

### Nomenclatura de Branches

**Padrão**: `<tipo>/<descrição-com-hifens>`

**Tipos permitidos**:
- `feat/` - Nova funcionalidade
- `fix/` - Correção de bug
- `refactor/` - Refatoração
- `docs/` - Documentação
- `test/` - Testes
- `chore/` - Tarefas gerais

**Exemplos válidos** ✅:
```
feat/product-crud
feat/add-jwt-authentication
fix/product-price-validation
refactor/extract-service-layer
test/add-integration-tests
docs/update-api-documentation
```

**Exemplos inválidos** ❌:
```
feature                # Sem descrição
nova-feature           # Sem tipo
feat/Feature1          # Com maiúsculas
fix_bug                # Usar hífen, não underscore
```

---

## 2. Padrões de Código

### Estrutura de Pacotes

```
src/main/java/com/momo/ecommerce/
├── controller/      # REST Controllers
├── service/         # Lógica de negócio
├── repository/      # Acesso a dados
├── model/           # Entidades JPA
├── dto/             # Data Transfer Objects
├── exception/       # Exceções customizadas
├── config/          # Configurações
└── util/            # Classes utilitárias
```

### Nomenclatura

#### Classes

```java
// Controllers
@RestController
public class ProductController { }  // "Controller" no final

// Services
@Service
public class ProductService { }     // "Service" no final

// Repositories
public interface ProductRepository extends JpaRepository<Product, Long> { }
                                    // "Repository" no final

// DTOs
public class ProductDTO { }         // "DTO" no final
public class CreateProductRequest { }  // Ou Request/Response

// Entidades
@Entity
public class Product { }            // Nome singular da entidade
```

#### Métodos

```java
// CRUD - Repository
findById()
findAll()
save()
deleteById()

// CRUD - Service
create()
update()
delete()
findById()
findAll()

// Controller endpoints
@GetMapping
public ResponseEntity<List<ProductDTO>> listProducts() { }

@GetMapping("/{id}")
public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) { }

@PostMapping
public ResponseEntity<ProductDTO> createProduct(@RequestBody CreateProductRequest request) { }

@PutMapping("/{id}")
public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody UpdateProductRequest request) { }

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) { }
```

### Formatação

**Indentação**: 4 espaços (não tabs)

**Chaves**:
```java
// BOM ✅
public void method() {
    // código
}

// RUIM ❌
public void method()
{
    // código
}
```

**Imports**:
- Organizar alfabeticamente
- Remover imports não utilizados
- Não usar wildcard (`*`)

```java
// BOM ✅
import java.util.List;
import com.momo.ecommerce.model.Product;

// RUIM ❌
import java.util.*;
```

### Comentários

**Quando comentar**:
- ✅ Lógica complexa/não óbvia
- ✅ Javadoc em métodos públicos
- ✅ Explicação de decisões técnicas

**Quando NÃO comentar**:
- ❌ Código óbvio
- ❌ Código comentado (deletar!)

```java
// BOM ✅
/**
 * Calcula desconto progressivo baseado na quantidade.
 * 10-50 unidades: 5%
 * 51-100 unidades: 10%
 * 100+: 15%
 */
public BigDecimal calculateDiscount(int quantity, BigDecimal price) {
    // ...
}

// RUIM ❌
// Método que salva produto
public void save(Product product) {  // Óbvio!
    // repository.save(product);  // Não deixe código comentado!
}
```

---

## 3. Conventional Commits

### Estrutura

```
<tipo>[escopo opcional]: <descrição>

[corpo opcional]

[rodapé opcional]
```

### Tipos

| Tipo | Uso | Exemplo |
|------|-----|---------|
| `feat` | Nova funcionalidade | `feat: adiciona endpoint de produtos` |
| `fix` | Correção de bug | `fix: corrige validação de preço negativo` |
| `docs` | Documentação | `docs: atualiza README` |
| `style` | Formatação | `style: formata código com Prettier` |
| `refactor` | Refatoração | `refactor: extrai lógica para service` |
| `test` | Testes | `test: adiciona testes de ProductService` |
| `chore` | Tarefas gerais | `chore: atualiza dependências` |

### Exemplos

```bash
# Feature
feat(products): adiciona entidade Product com validações
feat(auth): implementa autenticação JWT

# Fix
fix(products): corrige validação de preço negativo
fix(orders): resolve NullPointerException ao buscar pedido vazio

# Refactor
refactor(products): extrai validações para classe separada
refactor(services): melhora legibilidade do código

# Test
test(products): adiciona testes unitários para ProductService
test(integration): adiciona testes de integração para orders

# Docs
docs(api): adiciona documentação Swagger para endpoints
docs(readme): atualiza instruções de setup
```

---

## 4. Como Fazer um Pull Request

### Checklist Antes de Abrir PR

```bash
# 1. Garantir que develop está atualizado
git checkout develop
git pull origin develop
git checkout feat/sua-feature
git merge develop
# Resolver conflitos se houver

# 2. Rodar testes
mvn test

# 3. Verificar build
mvn clean install

# 4. Remover código de debug
# Procurar por System.out.println, logs desnecessários, etc

# 5. Verificar formatação
mvn spring-javaformat:apply

# 6. Push
git push origin feat/sua-feature
```

### Template de Pull Request

```markdown
## 📝 Descrição

Breve descrição do que foi implementado e por quê.

## 🎯 Tipo de Mudança

- [ ] Nova funcionalidade (feat)
- [ ] Correção de bug (fix)
- [ ] Refatoração (refactor)
- [ ] Documentação (docs)
- [ ] Testes (test)

## ✅ Checklist

- [ ] Código implementado e funcional
- [ ] Testes implementados e passando
- [ ] Sem código comentado ou debug logs
- [ ] Documentação atualizada (se aplicável)
- [ ] Sem conflitos com develop
- [ ] Build passando (`mvn clean install`)

## 🧪 Como Testar

Passo a passo para testar as mudanças:

1. Iniciar aplicação: `mvn spring-boot:run`
2. Testar endpoint: `GET http://localhost:8080/api/products`
3. Verificar resposta esperada

## 📸 Screenshots (se aplicável)

Adicionar prints do Swagger UI, Postman, etc.

## 🔗 Issues Relacionadas

Closes #123
Relates to #456
```

### Abrir Pull Request no GitHub

1. Push da branch: `git push -u origin feat/sua-feature`
2. Ir no repositório no GitHub
3. Clicar em **"Compare & pull request"**
4. Preencher template acima
5. **Base**: `develop`
6. **Compare**: `feat/sua-feature`
7. **Reviewers**: Adicionar pelo menos 1 revisor
8. Clicar em **"Create pull request"**

---

## 5. Code Review

### O que Revisar

#### ✅ Funcionalidade
- Código faz o que deveria fazer?
- Casos de borda tratados?
- Não quebra funcionalidades existentes?

#### ✅ Qualidade
- Código legível e fácil de entender?
- Nomes claros e descritivos?
- Sem código duplicado?
- Segue padrões do projeto?

#### ✅ Testes
- Cobertura adequada?
- Testes passando?
- Testa casos de erro?

#### ✅ Performance
- Sem queries N+1?
- Algoritmos eficientes?

#### ✅ Segurança
- Validação de inputs?
- Sem exposição de dados sensíveis?
- Proteção contra SQL injection?

### Como Fazer Review

**Comentário construtivo** ✅:
```markdown
Ótima implementação! Apenas algumas sugestões:

**Linha 45**: Considere extrair essa constante para uma classe Config:
```java
private static final int MAX_ITEMS = 100;
```

**Linha 78**: Esse método está grande. Que tal extrair a validação para um método privado?

Nada bloqueante, pode mergear após essas pequenas melhorias! 🚀
```

**Comentário ruim** ❌:
```markdown
Código ruim. Refaz.
```

### Aprovar PR

Após review, use um destes labels:

- ✅ **LGTM** (Looks Good To Me) - Aprovar
- 🔄 **Request Changes** - Pedir alterações
- 💬 **Comment** - Comentar sem bloquear

---

## 6. Testes

### Estrutura de Testes

```
src/test/java/com/momo/ecommerce/
├── controller/          # Testes de controller (MockMvc)
├── service/             # Testes unitários de service
├── repository/          # Testes de repository (TestContainers)
└── integration/         # Testes de integração end-to-end
```

### Nomenclatura de Testes

**Padrão**: `shouldDoSomethingWhenCondition()`

```java
@Test
void shouldReturnProductWhenIdExists() { }

@Test
void shouldThrowExceptionWhenProductNotFound() { }

@Test
void shouldCreateProductWhenDataIsValid() { }

@Test
void shouldRejectProductWhenPriceIsNegative() { }
```

### Cobertura Mínima

- **Service**: 80%+ de cobertura
- **Controller**: Todos os endpoints testados
- **Repository**: Queries customizadas testadas

### Rodar Testes

```bash
# Todos os testes
mvn test

# Testes de uma classe específica
mvn test -Dtest=ProductServiceTest

# Testes com relatório de cobertura
mvn test jacoco:report
# Ver relatório: target/site/jacoco/index.html
```

---

## ✅ Checklist Final

Antes de considerar uma feature completa:

- [ ] Código implementado seguindo padrões
- [ ] Testes unitários criados e passando
- [ ] Testes de integração (se aplicável)
- [ ] Documentação atualizada
- [ ] Conventional commits utilizados
- [ ] Sem código comentado ou TODOs
- [ ] Build passando (`mvn clean install`)
- [ ] Pull Request aberto e aprovado
- [ ] Merge realizado
- [ ] Branch deletada

---

## 📚 Recursos

- [Conventional Commits](https://www.conventionalcommits.org/)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Spring Boot Best Practices](https://www.baeldung.com/spring-boot-start)
- [Effective Java (Joshua Bloch)](https://www.oreilly.com/library/view/effective-java/9780134686097/)

---

## 🆘 Dúvidas?

- Consulte a [documentação do projeto](../README.md)
- Veja exemplos de PRs anteriores
- Pergunte no Slack/Discord do time
- Abra uma issue com a tag `question`

---

**Lembre-se**: Código é lido muito mais vezes do que é escrito. Escreva pensando em quem vai ler depois! 📖✨
