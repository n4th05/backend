# 🌿 Git - Fundamentos

Este guia ensina os conceitos básicos do Git e os comandos essenciais que você vai usar diariamente.

## 📋 Índice

1. [O que é Git?](#o-que-é-git)
2. [Instalação e Configuração Inicial](#instalação-e-configuração-inicial)
3. [Conceitos Fundamentais](#conceitos-fundamentais)
4. [Comandos Básicos](#comandos-básicos)
5. [Fluxo de Trabalho Básico](#fluxo-de-trabalho-básico)
6. [Trabalhando com GitHub](#trabalhando-com-github)
7. [Exercícios Práticos](#exercícios-práticos)

---

## 1. O que é Git?

### Definição

**Git** é um sistema de **controle de versão distribuído** criado por Linus Torvalds (o mesmo criador do Linux) em 2005.

### O que isso significa na prática?

Imagine que você está escrevendo um trabalho da faculdade:

```
trabalho_final.docx
trabalho_final_v2.docx
trabalho_final_v3.docx
trabalho_final_VERSAO_FINAL.docx
trabalho_final_VERSAO_FINAL_AGORA_VAI.docx
```

😱 **Caos total, certo?**

Git faz isso de forma profissional:
- ✅ Salva **todo o histórico** de mudanças automaticamente
- ✅ Permite **voltar no tempo** para qualquer versão anterior
- ✅ Permite **colaborar** com múltiplas pessoas no mesmo código
- ✅ Permite **trabalhar em paralelo** em features diferentes (branches)
- ✅ Mostra **quem mudou o quê** e **quando**

### Git vs GitHub - Qual a diferença?

| **Git** | **GitHub** |
|---------|------------|
| Software instalado no seu computador | Site/plataforma na internet |
| Sistema de controle de versão | Serviço de hospedagem de repositórios Git |
| Funciona offline | Requer internet |
| Gratuito e open-source | Gratuito para projetos públicos |

**Analogia**: Git é como o Word (programa), GitHub é como o OneDrive (nuvem onde você salva).

### Por que todo desenvolvedor usa Git?

- ✅ **Padrão da indústria** - 99% das empresas usam
- ✅ **Open source** - Linux, VS Code, React, Spring Boot... tudo usa Git
- ✅ **Segurança** - nunca perca código
- ✅ **Colaboração** - trabalhe em equipe sem conflitos
- ✅ **Portfólio** - GitHub é seu currículo como desenvolvedor

---

## 2. Instalação e Configuração Inicial

### 2.1. Instalação

Se você seguiu o guia anterior de [Java com SDKMAN](./02-java-sdkman-setup.md), o Git já está instalado!

**Verificar se está instalado**:

```bash
git --version
```

Deve mostrar algo como: `git version 2.XX.X`

Se não estiver instalado, volte ao [guia de Setup Windows](./01-setup-windows.md) e instale o Git Bash.

### 2.2. Configuração Inicial (OBRIGATÓRIA)

O Git precisa saber quem você é para registrar suas alterações.

**Abra o Git Bash** (ou terminal integrado do VS Code com Git Bash) e execute:

```bash
# Seu nome completo
git config --global user.name "Seu Nome Completo"

# Seu email (use o mesmo do GitHub depois)
git config --global user.email "seuemail@exemplo.com"

# Editor padrão (VS Code)
git config --global core.editor "code --wait"

# Nome da branch padrão (main ao invés de master)
git config --global init.defaultBranch main
```

**Exemplo real**:

```bash
git config --global user.name "João Silva"
git config --global user.email "joao.silva@gmail.com"
git config --global core.editor "code --wait"
git config --global init.defaultBranch main
```

### 2.3. Verificar Configuração

```bash
git config --list
```

Deve mostrar suas configurações, incluindo:
```
user.name=Seu Nome
user.email=seuemail@exemplo.com
core.editor=code --wait
init.defaultbranch=main
```

✅ **Git configurado!**

---

## 3. Conceitos Fundamentais

### 3.1. Repositório (Repository)

Um **repositório** é uma pasta que o Git está monitorando. Contém:
- Seus arquivos de código
- Histórico completo de mudanças
- Configurações do Git (pasta `.git/`)

### 3.2. As Três Áreas do Git

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│  Working        │       │  Staging Area   │       │  Repository     │
│  Directory      │──────▶│  (Index)        │──────▶│  (.git)         │
│                 │  add  │                 │ commit│                 │
│  Arquivos que   │       │  Arquivos       │       │  Histórico      │
│  você edita     │       │  preparados     │       │  permanente     │
└─────────────────┘       └─────────────────┘       └─────────────────┘
```

**Explicação**:

1. **Working Directory**: Sua pasta normal onde você edita arquivos
2. **Staging Area**: Área de preparação - arquivos que você marcou para salvar
3. **Repository**: Histórico permanente após o commit

**Analogia com fotografia**:
- **Working Directory**: Você posicionando pessoas para a foto
- **Staging Area**: Pessoas posicionadas, prontas para foto (`git add`)
- **Repository**: Foto tirada e salva (`git commit`)

### 3.3. Commits

Um **commit** é um ponto na história do seu código:
- 📸 Foto do estado de todos os arquivos naquele momento
- 👤 Autor (quem fez)
- 📅 Data e hora
- 💬 Mensagem explicando o que mudou

**Exemplo de um commit**:
```
commit abc123def456...
Author: João Silva <joao.silva@gmail.com>
Date:   Mon Feb 24 10:30:00 2025

    Adiciona validação de email no formulário de cadastro
```

### 3.4. Branches (Ramificações)

**Branch** é uma linha independente de desenvolvimento:

```
main:      A---B---C---D
                 \
feature:          E---F
```

- `main`: Branch principal (produção)
- `feature`: Branch para desenvolver nova funcionalidade

**Por que usar branches?**
- ✅ Trabalhar em features sem afetar o código principal
- ✅ Múltiplas pessoas trabalhando em paralelo
- ✅ Experimentar ideias sem quebrar o que funciona

---

## 4. Comandos Básicos

### 4.1. Inicializar um Repositório

**Criar um novo repositório**:

```bash
# Criar pasta do projeto
mkdir meu-projeto
cd meu-projeto

# Inicializar Git
git init
```

Isso cria a pasta `.git/` (oculta) que contém todo o histórico.

✅ **Repositório criado!**

### 4.2. Ver Status

**Ver o estado atual** (arquivos modificados, staging area, etc.):

```bash
git status
```

Exemplo de saída:
```
On branch main
Changes not staged for commit:
  modified:   arquivo.txt

Untracked files:
  novo-arquivo.txt
```

### 4.3. Adicionar Arquivos (Staging)

**Adicionar um arquivo específico**:

```bash
git add arquivo.txt
```

**Adicionar todos os arquivos modificados**:

```bash
git add .
```

> 💡 **Dica**: O `.` significa "todos os arquivos da pasta atual".

### 4.4. Fazer Commit

**Commitar as mudanças** (salvar no histórico):

```bash
git commit -m "Mensagem descrevendo a mudança"
```

Exemplo:

```bash
git commit -m "Adiciona página de login"
```

> ⚠️ **Importante**: A mensagem deve ser clara e descrever **o que** você mudou.

### 4.5. Ver Histórico

**Ver lista de commits**:

```bash
git log
```

Saída:
```
commit abc123...
Author: João Silva
Date:   Mon Feb 24 10:30

    Adiciona página de login

commit def456...
Author: João Silva
Date:   Mon Feb 24 09:15

    Cria estrutura inicial do projeto
```

**Versão resumida** (mais legível):

```bash
git log --oneline
```

Saída:
```
abc123 Adiciona página de login
def456 Cria estrutura inicial do projeto
```

### 4.6. Ver Diferenças

**Ver o que mudou** (antes de adicionar ao staging):

```bash
git diff
```

**Ver o que está no staging**:

```bash
git diff --staged
```

### 4.7. Desfazer Mudanças

**Remover arquivo do staging** (mas manter as alterações):

```bash
git restore --staged arquivo.txt
```

**Descartar mudanças** (CUIDADO: perde as alterações):

```bash
git restore arquivo.txt
```

**Voltar para um commit anterior**:

```bash
git reset --hard abc123
```

> ⚠️ **ATENÇÃO**: `reset --hard` **apaga commits**. Use com cuidado!

---

## 5. Fluxo de Trabalho Básico

### Fluxo Diário

```bash
# 1. Ver o que mudou
git status

# 2. Adicionar arquivos ao staging
git add .

# 3. Ver o que vai ser commitado
git status

# 4. Fazer commit
git commit -m "Descrição clara da mudança"

# 5. Ver histórico
git log --oneline
```

### Exemplo Prático Completo

Vamos criar um projeto de exemplo:

```bash
# 1. Criar projeto
mkdir exemplo-git
cd exemplo-git

# 2. Inicializar Git
git init

# 3. Criar um arquivo
echo "# Meu Projeto" > README.md

# 4. Ver status
git status
# Saída: Untracked files: README.md

# 5. Adicionar ao staging
git add README.md

# 6. Ver status novamente
git status
# Saída: Changes to be committed: new file: README.md

# 7. Fazer commit
git commit -m "Adiciona README inicial"

# 8. Criar mais um arquivo
echo "function hello() { console.log('Hello!'); }" > app.js

# 9. Adicionar e commitar
git add app.js
git commit -m "Adiciona função hello"

# 10. Ver histórico
git log --oneline
```

Saída do log:
```
def456 Adiciona função hello
abc123 Adiciona README inicial
```

✅ **Você acabou de criar seu primeiro repositório Git!**

---

## 6. Trabalhando com GitHub

### 6.1. Criar Conta no GitHub

1. Acesse: https://github.com
2. Clique em **"Sign up"**
3. Use o **mesmo email** configurado no Git local
4. Escolha um username profissional (ex: `joao-silva-dev`)
5. Complete a verificação

### 6.2. Criar um Repositório no GitHub

1. Clique no `+` no canto superior direito
2. Selecione **"New repository"**
3. Preencha:
   - **Repository name**: `meu-projeto`
   - **Description**: "Meu primeiro projeto"
   - **Public** (para portfólio)
   - **NÃO** marque "Add README" (já temos um localmente)
4. Clique em **"Create repository"**

### 6.3. Conectar Repositório Local com GitHub

Após criar o repositório, GitHub mostra instruções. Execute:

```bash
# Adicionar remote (conexão com GitHub)
git remote add origin https://github.com/SEU-USUARIO/meu-projeto.git

# Enviar código para GitHub
git push -u origin main
```

> 💡 **Nota**: Na primeira vez, GitHub vai pedir suas credenciais.

**Autenticação**: 
- Use **Personal Access Token** (PAT) ao invés de senha
- Vá em: GitHub → Settings → Developer settings → Personal access tokens → Generate new token
- Dê permissões de `repo`
- Use esse token como senha

### 6.4. Comandos para Sincronização

**Enviar commits para GitHub**:

```bash
git push
```

**Baixar mudanças do GitHub**:

```bash
git pull
```

**Clonar um repositório existente**:

```bash
git clone https://github.com/usuario/repositorio.git
```

---

## 7. Exercícios Práticos

### Exercício 1: Criar e Versionar um Projeto

```bash
# 1. Criar pasta do projeto
mkdir portfolio
cd portfolio

# 2. Inicializar Git
git init

# 3. Criar arquivo index.html
echo "<h1>Meu Portfolio</h1>" > index.html

# 4. Adicionar e commitar
git add index.html
git commit -m "Adiciona página inicial"

# 5. Modificar o arquivo
echo "<p>Desenvolvedor Java</p>" >> index.html

# 6. Ver diferenças
git diff

# 7. Adicionar e commitar a mudança
git add index.html
git commit -m "Adiciona descrição na página inicial"

# 8. Ver histórico
git log --oneline
```

### Exercício 2: Desfazer Mudanças

```bash
# 1. Modificar arquivo
echo "<p>Erro!</p>" >> index.html

# 2. Ver status
git status

# 3. Descartar mudança (antes de adicionar ao staging)
git restore index.html

# 4. Verificar que a mudança foi descartada
cat index.html
```

### Exercício 3: Trabalhar com GitHub

```bash
# 1. Criar repositório no GitHub chamado "portfolio"

# 2. Conectar
git remote add origin https://github.com/SEU-USUARIO/portfolio.git

# 3. Enviar código
git push -u origin main

# 4. Acessar https://github.com/SEU-USUARIO/portfolio
#    e verificar que o código está lá!
```

---

## ✅ Checklist de Verificação

Marque o que você aprendeu:

- [✅] Instalei e configurei Git (`git config`)
- [✅] Entendi os conceitos: repositório, commit, staging area
- [✅] Sei inicializar repositório (`git init`)
- [✅] Sei adicionar arquivos (`git add`)
- [✅] Sei fazer commit (`git commit`)
- [✅] Sei ver status (`git status`)
- [✅] Sei ver histórico (`git log`)
- [✅] Sei desfazer mudanças (`git restore`)
- [✅] Criei conta no GitHub
- [✅] Conectei repositório local com GitHub (`git remote add`)
- [✅] Enviei código para GitHub (`git push`)

---

## 🆘 Problemas Comuns

### "Author identity unknown"

**Causa**: Não configurou `user.name` e `user.email`.

**Solução**:
```bash
git config --global user.name "Seu Nome"
git config --global user.email "seuemail@exemplo.com"
```

### "fatal: not a git repository"

**Causa**: Você não está em uma pasta com Git inicializado.

**Solução**:
```bash
git init
```

### "Permission denied (publickey)"

**Causa**: GitHub não reconhece sua autenticação.

**Solução**: Use HTTPS ao invés de SSH, ou configure SSH keys (avançado).

### "Updates were rejected because the remote contains work that you do not have locally"

**Causa**: Alguém enviou commits antes de você.

**Solução**:
```bash
git pull --rebase
git push
```

---

## 🎯 Próximos Passos

Agora que você domina o básico, vamos aprender práticas profissionais:

➡️ **[Próximo: Git - Avançado (Conventional Commits, Branch Strategy)](./04-git-advanced.md)**

---

## 📚 Recursos Adicionais

- [Git Official Documentation](https://git-scm.com/doc)
- [GitHub Guides](https://guides.github.com/)
- [Git Cheat Sheet (PDF)](https://education.github.com/git-cheat-sheet-education.pdf)
- [Visualizing Git](https://git-school.github.io/visualizing-git/)
- [Learn Git Branching (Interactive)](https://learngitbranching.js.org/)

---

## 💡 Comandos de Referência Rápida

```bash
# Configuração inicial
git config --global user.name "Nome"
git config --global user.email "email@exemplo.com"

# Criar repositório
git init

# Ciclo básico
git status                    # Ver estado
git add arquivo.txt           # Adicionar arquivo específico
git add .                     # Adicionar todos
git commit -m "Mensagem"      # Fazer commit
git log --oneline             # Ver histórico

# Desfazer
git restore arquivo.txt       # Descartar mudanças
git restore --staged arquivo  # Remover do staging

# GitHub
git remote add origin URL     # Conectar com GitHub
git push -u origin main       # Enviar código (primeira vez)
git push                      # Enviar código (demais vezes)
git pull                      # Baixar mudanças
git clone URL                 # Clonar repositório
```
