# 🚀 Git - Avançado (Pro)

Este guia ensina práticas profiss de Git usadas no mercado: Conventional Commits, Branch Strategy e workflows de equipe.

## 📋 Índice

1. [Conventional Commits](#conventional-commits)
2. [Branch Strategy](#branch-strategy)
3. [Gitflow Simplificado](#gitflow-simplificado)
4. [Pull Requests e Code Review](#pull-requests-e-code-review)
5. [Boas Práticas Profissionais](#boas-práticas-profissionais)
6. [Comandos Avançados](#comandos-avançados)
7. [Ferramentas e Automações](#ferramentas-e-automações)

---

## 1. Conventional Commits

### 1.1. O que é?

**Conventional Commits** é uma **convenção para mensagens de commit** que:
- ✅ Padroniza como escrever mensagens
- ✅ Facilita entender o histórico
- ✅ Permite gerar changelogs automaticamente
- ✅ Facilita versionamento automático (semantic versioning)
- ✅ **É padrão em 80% empresa do mercado**

### 1.2. Estrutura do Commit

```
<tipo>[escopo opcional]: <descrição>

[corpo opcional]

[rodapé(s) opcional(is)]
```

**Exemplo simples**:
```
feat: adiciona endpoint de criação de produtos
```

**Exemplo completo**:
```
feat(produtos): adiciona endpoint de criação de produtos

Implementa POST /api/products com validação de dados
usando Bean Validation e tratamento de erros.

Closes #123
```

### 1.3. Tipos de Commit

| Tipo | Descrição | Exemplo |
|------|-----------|---------|
| `feat` | Nova funcionalidade | `feat: adiciona autenticação JWT` |
| `fix` | Correção de bug | `fix: corrige cálculo de total do pedido` |
| `docs` | Documentação | `docs: atualiza README com instruções` |
| `style` | Formatação (não muda código) | `style: formata código com Prettier` |
| `refactor` | Refatoração (não muda comportamento) | `refactor: extrai lógica para service` |
| `perf` | Melhoria de performance | `perf: adiciona índice no banco de dados` |
| `test` | Adiciona/modifica testes | `test: adiciona testes para ProductService` |
| `build` | Sistema de build (Maven, Gradle) | `build: atualiza Spring Boot para 4.0.3` |
| `ci` | CI/CD (GitHub Actions, Jenkins) | `ci: adiciona workflow de testes` |
| `chore` | Tarefas gerais (não afeta código) | `chore: atualiza dependências` |
| `revert` | Reverte commit anterior | `revert: reverte "adiciona feature X"` |

### 1.4. Escopos (Opcional)

O **escopo** indica qual parte do projeto foi afetada:

```bash
feat(auth): adiciona login com Google
fix(products): corrige validação de preço negativo
test(cart): adiciona testes de carrinho vazio
docs(api): documenta endpoint de orders
```

**Escopos comuns neste projeto**:
- `products` - relacionado a produtos
- `categories` - relacionado a categorias  
- `orders` - relacionado a pedidos
- `cart` - relacionado a carrinho
- `auth` - relacionado a autenticação
- `database` - relacionado a banco de dados
- `api` - relacionado a endpoints REST

### 1.5. Breaking Changes

Mudanças que **quebram compatibilidade** devem ter `!` ou `BREAKING CHANGE:`:

```bash
feat!: remove endpoint /api/v1/products

BREAKING CHANGE: endpoint movido para /api/v2/products
```

### 1.6. Exemplos Práticos

**Bons exemplos** ✅:

```bash
feat: adiciona entidade Product com JPA
fix: corrige erro de NullPointer na busca por ID
docs: adiciona documentação do Swagger
test: adiciona testes unitários para ProductService
refactor: move validações para classe separada
chore: adiciona .gitignore para Java/Maven
```

**Maus exemplos** ❌:

```bash
update              # Muito vago
fixed bug           # Não segue padrão
WIP                 # Work In Progress - não commitar!
asdfgh              # Sem sentido
commit final        # "Final" nunca é final
```

### 1.7. Template de Commit

Crie um template para facilitar:

```bash
# Criar arquivo de template
cat > ~/.gitmessage << 'EOF'
# <tipo>[escopo]: <descrição curta>
#
# [Corpo - explicação detalhada do que e por que]
#
# [Rodapé - issues relacionadas, breaking changes]
#
# Tipos: feat, fix, docs, style, refactor, perf, test, build, ci, chore, revert
# Escopo: products, orders, cart, auth, database, api, etc
EOF

# Configurar Git para usar o template
git config --global commit.template ~/.gitmessage
```

Agora ao rodar `git commit` (sem `-m`), o editor abrirá com o template!

---

## 2. Branch Strategy

### 2.1. O que é Branch Strategy?

É a **estratégia de como organizar branches** no projeto:
- Quando criar uma branch?
- Como nomear branches?
- Quando fazer merge?
- O que vai para produção?

### 2.2. Tipos de Branches

#### Main Branch (Principal)
```
main (ou master)
```
- **Código em produção** (ou pronto para produção)
- Sempre **estável e funcionando**
- **Nunca commitar direto** nela (usar PRs)
- **Protegida** (requires reviews)

#### Development Branch
```
develop
```
- **Código em desenvolvimento**
- Onde todas as features são integradas
- Base para criar novas branches de feature
- Quando estável, faz-se merge para `main`

#### Feature Branches
```
feat/nome-da-feature
feature/nome-da-feature
```
- Uma branch para cada **nova funcionalidade**
- Criada a partir de `develop`
- Merge de volta para `develop` via Pull Request
- Deletada após merge

**Exemplos**:
```
feat/product-crud
feat/add-categories
feat/add-cart
feat/add-auth
```

#### Fix Branches
```
fix/nome-do-bug
```
- Para **correções de bugs**
- Criada a partir de `develop` (ou `main` se hotfix)
- Merge de volta para `develop`

**Exemplos**:
```
fix/product-price-validation
fix/null-pointer-in-order
```

#### Hotfix Branches
```
hotfix/urgente
```
- **Correção urgente em produção**
- Criada a partir de `main`
- Merge para `main` E `develop`

**Exemplo**:
```
hotfix/security-vulnerability
```

### 2.3. Nomenclatura de Branches

**Padrão recomendado**:
```
<tipo>/<descrição-curta-com-hifens>
```

**Bons exemplos** ✅:
```
feat/product-crud
feat/add-categories
feat/add-cart-api
fix/product-validation
fix/order-total-calculation
refactor/extract-service-layer
docs/update-readme
test/add-integration-tests
```

**Maus exemplos** ❌:
```
feature            # Muito vago
nova-feature       # Sem tipo
feat/Feature1      # Com maiúsculas
fix_bug            # Usar hífen, não underscore
develop-2          # Não numerar develop
```

### 2.4. Fluxo Visual

```
main       ─────────────●────────────────●──────────▶
                         │                 │
                       v1.0              v2.0
                         │                 │
develop    ────●────●────┼────●────●───────┼────●───▶
               │    │         │    │        │
               │    │         │    └────────┘
               │    │         └─────────────┘
feat/A     ────●────●    
feat/B              ────●────●
feat/C                        ────●────●
```

---

## 3. Gitflow Simplificado

### 3.1. Fluxo Completo para Este Projeto

#### Setup Inicial

```bash
# 1. Clonar/inicializar projeto
git init

# 2. Criar branch develop (uma vez só)
git checkout -b develop

# 3. Fazer commit inicial
git add .
git commit -m "chore: setup inicial do projeto"

# 4. Enviar para GitHub
git push -u origin develop
```

#### Desenvolvendo uma Feature

```bash
# 1. Garantir que está em develop atualizado
git checkout develop
git pull origin develop

# 2. Criar branch da feature
git checkout -b feat/product-crud

# 3. Desenvolver (fazer vários commits)
# ... editar código ...
git add .
git commit -m "feat(products): adiciona entidade Product"

# ... editar mais ...
git add .
git commit -m "feat(products): adiciona ProductRepository"

# ... editar mais ...
git add .
git commit -m "feat(products): adiciona ProductService"

# ... e assim por diante ...

# 4. Enviar branch para GitHub
git push -u origin feat/product-crud

# 5. Abrir Pull Request no GitHub
# (develop ← feat/product-crud)

# 6. Após aprovação, fazer merge via GitHub

# 7. Voltar para develop e atualizar
git checkout develop
git pull origin develop

# 8. Deletar branch local
git branch -d feat/product-crud
```

#### Ciclo de uma Feature (Resumo)

```bash
develop → feat/xxx → commits → push → PR → merge → develop
```

### 3.2. Workflow para Este Projeto (5 Fases)

```bash
# FASE 1: CRUD de Produtos
git checkout develop
git checkout -b feat/product-crud
# ... implementar ...
git push -u origin feat/product-crud
# ... criar PR, review, merge ...

# FASE 2: Categorias
git checkout develop
git pull
git checkout -b feat/add-categories
# ... implementar ...
git push -u origin feat/add-categories
# ... criar PR, review, merge ...

# FASE 3: Pedidos
git checkout develop
git pull
git checkout -b feat/add-orders
# ... implementar ...

# FASE 4: Carrinho
git checkout develop
git pull
git checkout -b feat/add-cart
# ... implementar ...

# FASE 5: Autenticação
git checkout develop
git pull
git checkout -b feat/add-auth
# ... implementar ...

# Depois de todas as fases integradas em develop:
git checkout main
git merge develop
git tag v1.0.0
git push origin main --tags
```

---

## 4. Pull Requests e Code Review

### 4.1. O que é Pull Request (PR)?

Um **Pull Request** é uma **solicitação para fazer merge** de uma branch em outra:
- 🔍 Permite **revisar código** antes de integrar
- 💬 Espaço para **discussão e feedback**
- ✅ Garante **qualidade do código**
- 📝 Documentação de **decisões técnicas**

### 4.2. Como Criar um PR no GitHub

1. Faça push da sua branch:
   ```bash
   git push -u origin feat/product-crud
   ```

2. Vá no repositório no GitHub

3. Clique no botão **"Compare & pull request"** (aparece automaticamente)

4. Preencha:
   - **Title**: `feat(products): adiciona CRUD completo de produtos`
   - **Description**:
     ```markdown
     ## Descrição
     Implementa CRUD completo para entidade Product com:
     - Entidade Product com validações
     - Repository, Service e Controller
     - Testes unitários e de integração
     - Documentação Swagger
     
     ## Checklist
     - [x] Código implementado
     - [x] Testes passando
     - [x] Documentação atualizada
     - [x] Sem conflitos com develop
     
     ## Closes
     Closes #123
     ```

5. **Base branch**: `develop`  
6. **Compare branch**: `feat/product-crud`

7. Clique em **"Create pull request"**

### 4.3. Code Review - O que Revisar

#### Funcionamento
- ✅ Código faz o que deveria fazer?
- ✅ Casos de borda estão tratados?
- ✅ Não quebra funcionalidades existentes?

#### Qualidade
- ✅ Código legível e fácil de entender?
- ✅ Nomes de variáveis e métodos claros?
- ✅ Não há código duplicado?
- ✅ Segue padrões do projeto?

#### Testes
- ✅ Tem testes suficientes?
- ✅ Testes estão passando?
- ✅ Cobertura adequada?

#### Performance
- ✅ Não tem queries N+1?
- ✅ Não carrega dados desnecessários?
- ✅ Algoritmos eficientes?

#### Segurança
- ✅ Validação de inputs?
- ✅ Não expõe dados sensíveis?
- ✅ Proteção contra SQL injection?

### 4.4. Template de Code Review

Para comentar no PR:

**Aprovação** ✅:
```markdown
LGTM! 🎉 (Looks Good To Me)

Código limpo, bem testado e documentado. Apenas algumas sugestões menores:

- Considerar extrair constante `MAX_PRICE` linha 45
- Adicionar log de erro no catch da linha 78

Mas nada bloqueante, pode mergear!
```

**Pedindo alterações** 🔄:
```markdown
Ótimo trabalho! Apenas algumas melhorias necessárias:

**Bloqueantes**:
- [ ] Linha 34: Falta validação de null no campo `name`
- [ ] Linha 67: Query pode causar N+1, usar JOIN FETCH

**Sugestões**:
- Considerar renomear `proc` para `processOrder` (mais claro)
- Adicionar comentário explicando a lógica complexa da linha 89

Após corrigir os bloqueantes, aprovo!
```

### 4.5. Auto Code Review (antes de abrir PR)

Checklist antes de enviar PR:

```bash
# 1. Garantir que develop está atualizado na sua branch
git checkout develop
git pull
git checkout feat/sua-feature
git merge develop  # Resolver conflitos se houver

# 2. Rodar testes
mvn test

# 3. Verificar se não há código comentado/debug
grep -r "System.out.println" src/  # Não deve ter!
grep -r "TODO" src/                # Resolver ou documentar

# 4. Verificar formatação
mvn spring-javaformat:apply

# 5. Verificar se build passa
mvn clean install

# 6. Ver o diff completo
git diff develop

# 7. Fazer último commit se necessário
git add .
git commit -m "fix: remove debug logs"

# 8. Push
git push
```

---

## 5. Boas Práticas Profissionais

### 5.1. Tamanho dos Commits

✅ **BOM - Commits pequenos e focados**:
```bash
git commit -m "feat(products): adiciona entidade Product"
git commit -m "feat(products): adiciona ProductRepository"
git commit -m "feat(products): adiciona ProductService"
git commit -m "test(products): adiciona testes de ProductService"
```

❌ **RUIM - Commit gigante com tudo**:
```bash
git commit -m "adiciona CRUD completo de produtos com testes e documentação"
# 50 arquivos modificados!
```

**Regra prática**: 1 commit = 1 mudança lógica

### 5.2. Quando Commitar?

Commite quando:
- ✅ Completou uma unidade lógica de trabalho
- ✅ Código compila (sem erros)
- ✅ Testes passam
- ✅ Faz sentido se precisar reverter só isso

Não commite:
- ❌ Código com erros de compilação
- ❌ Testes quebrados
- ❌ Código comentado/debug (System.out.println)
- ❌ Trabalho pela metade (use stash se preciso)

### 5.3. Git Stash (Guardar Mudanças Temporariamente)

**Cenário**: Você está no meio de uma feature e precisa urgentemente trocar de branch:

```bash
# Guardar mudanças temporariamente
git stash

# Trocar de branch e fazer o que precisa
git checkout develop
# ... fazer algo ...

# Voltar para sua branch
git checkout feat/sua-feature

# Recuperar as mudanças
git stash pop
```

### 5.4. Manter Branches Atualizadas

**Atualizar sua branch com develop** regularmente (para evitar conflitos grandes):

```bash
# Enquanto trabalha na sua feature
git checkout develop
git pull
git checkout feat/sua-feature
git merge develop  # Ou: git rebase develop (avançado)
```

Faça isso **pelo menos 1x por dia** se a equipe é grande.

### 5.5. Resolução de Conflitos

**Quando acontece**: Duas pessoas modificaram a mesma parte do código.

**Como resolver**:

```bash
# Ao fazer merge/pull, Git avisa conflito
git merge develop
# CONFLICT (content): Merge conflict in src/main/java/Product.java

# Ver quais arquivos tem conflito
git status

# Abrir arquivo com conflito no VS Code
code src/main/java/Product.java
```

No arquivo, você verá:

```java
<<<<<<< HEAD
private String name;  // Sua versão
=======
private String productName;  // Versão do develop
>>>>>>> develop
```

**Resolver manualmente**:
1. Decida qual versão manter (ou misturar ambas)
2. Remova as linhas `<<<<<<<`, `=======`, `>>>>>>>`
3. Teste se funciona
4. Adicione e commite:

```bash
git add src/main/java/Product.java
git commit -m "fix: resolve conflito de merge em Product.java"
```

### 5.6. Não Commitar Arquivos Sensíveis

**NUNCA commite**:
- ❌ Senhas, API keys, tokens
- ❌ `application-prod.properties` com credenciais
- ❌ Arquivos `.env` com secrets
- ❌ Certificados SSL

**Use `.gitignore`** e **variáveis de ambiente**!

---

## 6. Comandos Avançados

### 6.1. Alterar Último Commit

**Esqueceu de adicionar um arquivo**:

```bash
# Adicionar arquivo esquecido
git add arquivo-esquecido.txt

# Alterar último commit (adiciona arquivo sem criar novo commit)
git commit --amend --no-edit
```

**Mudar mensagem do último commit**:

```bash
git commit --amend -m "Nova mensagem corrigida"
```

> ⚠️ **ATENÇÃO**: Só use `--amend` em commits que **ainda não foram pushed**!

### 6.2. Reverter Commit

**Desfazer último commit** (mas manter mudanças):

```bash
git reset --soft HEAD~1
```

**Desfazer último commit** (e descartar mudanças):

```bash
git reset --hard HEAD~1
```

### 6.3. Cherry-pick (Copiar Commit Específico)

**Copiar um commit de outra branch**:

```bash
# Pegar ID do commit
git log --oneline

# Copiar commit específico para branch atual
git cherry-pick abc123
```

### 6.4. Rebase Interativo (Limpar Histórico)

**Reorganizar/editar commits** antes de fazer PR:

```bash
# Editar últimos 3 commits
git rebase -i HEAD~3
```

Abre editor onde você pode:
- `pick` - manter commit
- `reword` - mudar mensagem
- `edit` - editar conteúdo
- `squash` - juntar com commit anterior
- `drop` - remover commit

### 6.5. Buscar no Histórico

**Procurar quando algo foi mudado**:

```bash
# Buscar por palavra no histórico
git log -S "palavra"

# Ver commits que mexeram em arquivo específico
git log -- arquivo.txt

# Ver quem mudou cada linha de um arquivo
git blame arquivo.txt
```

---

## 7. Ferramentas e Automações

### 7.1. Aliases Úteis

Criar atalhos para comandos longos:

```bash
# Adicionar aliases
git config --global alias.co checkout
git config --global alias.br branch
git config --global alias.ci commit
git config --global alias.st status
git config --global alias.lg "log --oneline --graph --all"

# Usar
git co develop        # Igual a: git checkout develop
git br -a             # Igual a: git branch -a
git lg                # Log bonito com grafo
```

### 7.2. Git Hooks

**Executar scripts automaticamente** em eventos Git.

**Exemplo - Validar mensagem de commit**:

Criar arquivo `.git/hooks/commit-msg`:

```bash
#!/bin/bash

# Ler mensagem do commit
commit_msg=$(cat $1)

# Validar padrão: tipo: mensagem
if ! echo "$commit_msg" | grep -qE "^(feat|fix|docs|style|refactor|test|chore)(\(.+\))?: .{10,}"; then
    echo "❌ Mensagem de commit inválida!"
    echo "Use: <tipo>[escopo]: <descrição com pelo menos 10 caracteres>"
    echo "Exemplo: feat: adiciona validação de email"
    exit 1
fi

echo "✅ Mensagem de commit válida!"
```

Dar permissão de execução:

```bash
chmod +x .git/hooks/commit-msg
```

### 7.3. Configurações Recomendadas

```bash
# Cores no terminal
git config --global color.ui auto

# Push só da branch atual
git config --global push.default current

# Pull com rebase por padrão (evita commits de merge desnecessários)
git config --global pull.rebase true

# Mostrar branch no terminal
git config --global pager.branch false
```

---

## ✅ Checklist de Boas Práticas

Marque o que você está seguindo:

- [ ] Uso Conventional Commits em todos os commits
- [ ] Crio uma branch para cada feature/fix
- [ ] Nomenclatura de branches segue padrão: `tipo/descricao`
- [ ] Faço commits pequenos e focados
- [ ] Mensagens de commit são claras e descritivas
- [ ] Nunca commito código quebrado
- [ ] Sempre testo antes de commitar
- [ ] Faço Pull Requests ao invés de merge direto
- [ ] Reviso meu próprio código antes de abrir PR
- [ ] Mantenho branch atualizada com develop
- [ ] Não commito arquivos sensíveis (senhas, tokens)
- [ ] Resolvo conflitos com cuidado
- [ ] Deleto branches após merge

---

## 🆘 Problemas Comuns

### "Accidentally committed to main instead of feature branch"

```bash
# Desfazer commit localmente
git reset --soft HEAD~1

# Criar branch correta
git checkout -b feat/minha-feature

# Commitar novamente
git add .
git commit -m "feat: mensagem correta"
```

### "Need to modify a pushed commit"

```bash
# Fazer mudança
git add .
git commit --amend

# Force push (CUIDADO!)
git push --force-with-lease
```

> ⚠️ **ATENÇÃO**: `--force` reescreve histórico. Só use em branches pessoais!

### "Too many commits, want to squash before PR"

```bash
# Squash últimos 5 commits em 1
git rebase -i HEAD~5

# Marcar commits para squash (s)
# Editar mensagem final
# Force push
git push --force-with-lease
```

---

## 🎯 Aplicação Neste Projeto

### Workflow das 5 Fases

```bash
# FASE 1
git checkout -b feat/product-crud
# ... commits seguindo conventional commits ...
git push -u origin feat/product-crud
# ... abrir PR, review, merge para develop ...

# FASE 2
git checkout develop && git pull
git checkout -b feat/add-categories
# ... repetir processo ...

# ... e assim por diante até FASE 5 ...
```

### Exemplo de Histórico Ideal

```bash
git log --oneline

abc123 feat(auth): adiciona proteção de endpoints
def456 feat(auth): implementa geração de JWT
789ghi feat(cart): adiciona cálculo de total
jkl012 feat(cart): adiciona endpoint de adicionar item
mno345 feat(orders): implementa criação de pedido
pqr678 feat(categories): adiciona relacionamento com produtos
stu901 feat(products): adiciona CRUD completo
vwx234 chore: setup inicial do projeto Spring Boot
```

---

## 📚 Recursos Adicionais

- [Conventional Commits Specification](https://www.conventionalcommits.org/)
- [Git Flow Original](https://nvie.com/posts/a-successful-git-branching-model/)
- [GitHub Flow (Simplified)](https://guides.github.com/introduction/flow/)
- [Semantic Versioning](https://semver.org/)
- [Git Best Practices](https://git-scm.com/book/en/v2/Distributed-Git-Contributing-to-a-Project)

---

## 🎯 Próximos Passos

Com Git dominado, vamos fundo em Spring Boot:

➡️ **[Próximo: Conceitos do Spring Boot](./05-spring-boot-concepts.md)**
