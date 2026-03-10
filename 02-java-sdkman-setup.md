# ☕ Instalação do Java 25 com SDKMAN no Windows

Este guia ensina como instalar e gerenciar múltiplas versões do Java no Windows usando o SDKMAN.

## 📋 Índice

1. [Por que SDKMAN?](#por-que-sdkman)
2. [Pré-requisitos](#pré-requisitos)
3. [Instalação do Git Bash / WSL](#instalação-do-git-bash--wsl)
4. [Instalação do SDKMAN](#instalação-do-sdkman)
5. [Instalação do Java 25](#instalação-do-java-25)
6. [Gerenciamento de Versões](#gerenciamento-de-versões)
7. [Verificação e Testes](#verificação-e-testes)

---

## 1. Por que SDKMAN?

### O que é SDKMAN?

**SDKMAN** (Software Development Kit Manager) é uma ferramenta que permite **instalar, gerenciar e alternar** entre múltiplas versões de Java e outras ferramentas (Maven, Gradle, Spring Boot CLI, etc.).

### Por que não baixar o Java direto do site?

| Método | Vantagens | Desvantagens |
|--------|-----------|--------------|
| **Download manual** | Simples para uma versão | ❌ Trocar versões é complicado<br>❌ Gerenciar variáveis de ambiente manualmente<br>❌ Dificulta ter múltiplas versões |
| **SDKMAN** | ✅ Múltiplas versões facilmente<br>✅ Troca de versão com 1 comando<br>✅ Atualização automática<br>✅ Padrão no mercado | Requer Git Bash ou WSL |

### No mercado de trabalho...

Você frequentemente vai precisar de **versões diferentes do Java** para projetos diferentes:
- Projeto antigo: Java 11
- Projeto novo: Java 21
- Projeto experimental: Java 25

SDKMAN facilita isso!

---

## 2. Pré-requisitos

Você precisa de um **terminal Unix-like** no Windows. Temos duas opções:

### Opção A: Git Bash (Mais Simples - Recomendado)

✅ Mais rápido de instalar  
✅ Leve e suficiente para SDKMAN  
✅ Já vamos instalar Git mesmo  

### Opção B: WSL (Windows Subsystem for Linux)

✅ Ambiente Linux completo no Windows  
✅ Mais poderoso  
❌ Instalação mais complexa  
❌ Requer Windows 10 versão 2004+ ou Windows 11  

> 💡 **Recomendação**: Use **Git Bash** se você é iniciante. É mais simples e funciona perfeitamente.

---

## 3. Instalação do Git Bash / WSL

### Opção A: Instalando Git Bash (Recomendado)

#### 3.1. Baixar o Git

1. Acesse: https://git-scm.com/download/win
2. Baixe a versão **64-bit Git for Windows Setup**
3. Execute o instalador

#### 3.2. Configuração da Instalação

Durante a instalação, você verá várias telas. Use estas configurações:

**Tela 1 - Select Components**:
- ✅ Marque tudo (padrão está bom)

**Tela 2 - Choosing the default editor**:
- Selecione: **"Use Visual Studio Code as Git's default editor"**

**Tela 3 - Adjusting your PATH environment**:
- Selecione: **"Git from the command line and also from 3rd-party software"** (padrão)

**Tela 4 - Choosing HTTPS transport backend**:
- Deixe: **"Use the OpenSSL library"** (padrão)

**Tela 5 - Configuring the line ending conversions**:
- Deixe: **"Checkout Windows-style, commit Unix-style line endings"** (padrão)

**Tela 6 - Configuring the terminal emulator**:
- Selecione: **"Use Windows' default console window"**

**Demais telas**:
- Deixe as opções padrão

#### 3.3. Finalizar Instalação

1. Clique em **"Install"**
2. Aguarde a instalação
3. Marque **"Launch Git Bash"** e clique em **"Finish"**

#### 3.4. Verificar Instalação

Uma janela preta (Git Bash) vai abrir. Digite:

```bash
bash --version
```

Deve mostrar algo como:
```
GNU bash, version 5.X.X(1)-release
```

✅ **Git Bash instalado com sucesso!**

### Opção B: Instalando WSL (Avançado)

<details>
<summary>Clique aqui se preferir usar WSL ao invés de Git Bash</summary>

#### 1. Verificar Versão do Windows

Pressione `Win + R`, digite `winver` e pressione Enter.

Você precisa de:
- Windows 10 versão 2004 ou superior, ou
- Windows 11

#### 2. Habilitar WSL

Abra **PowerShell como Administrador**:
1. Pressione `Win + X`
2. Clique em **"Windows PowerShell (Admin)"** ou **"Terminal (Admin)"**

Execute:

```powershell
wsl --install
```

Isso vai:
- Habilitar WSL
- Instalar o kernel Linux
- Instalar Ubuntu por padrão

#### 3. Reiniciar o Computador

**Obrigatório** após instalar WSL.

#### 4. Configurar Ubuntu

Após reiniciar, uma janela Ubuntu vai abrir e pedir:

```
Enter new UNIX username:
```

Digite um nome de usuário (ex: seu nome sem espaços)

```
New password:
```

Digite uma senha (você não vai ver ela sendo digitada - é normal!)

```
Retype new password:
```

Digite a senha novamente.

✅ **WSL instalado com sucesso!**

#### 5. Verificar Instalação

Digite no terminal WSL:

```bash
lsb_release -a
```

Deve mostrar informações do Ubuntu.

</details>

---

## 4. Instalação do SDKMAN

### 4.1. Abrir o Terminal

**Se você instalou Git Bash**:
- Abra o **Git Bash** (procure no menu Iniciar)

**Se você instalou WSL**:
- Abra o **Ubuntu** (procure no menu Iniciar)

### 4.2. Executar o Script de Instalação

Cole este comando no terminal e pressione Enter:

```bash
curl -s "https://get.sdkman.io" | bash
```

Você vai ver uma mensagem de instalação. Aguarde completar.

### 4.3. Carregar o SDKMAN

Após a instalação, execute:

```bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

> 💡 **Nota**: Esse comando só precisa ser executado uma vez. Na próxima vez que abrir o terminal, SDKMAN já estará disponível.

### 4.4. Verificar Instalação

Digite:

```bash
sdk version
```

Deve mostrar algo como:
```
SDKMAN 5.X.X
```

✅ **SDKMAN instalado com sucesso!**

---

## 5. Instalação do Java 25

### 5.1. Listar Versões Disponíveis

Para ver todas as versões de Java disponíveis:

```bash
sdk list java
```

Você vai ver uma lista enorme! Procure por versões **Java 25** (ou 21 LTS se 25 ainda não estiver disponível).

**Distribuidores**:
- `tem` = Eclipse Temurin (OpenJDK - Recomendado)
- `oracle` = Oracle JDK
- `graal` = GraalVM
- `amzn` = Amazon Corretto

> 💡 **Recomendação**: Use **Eclipse Temurin** (tem) - é o OpenJDK oficial, gratuito e mais usado.

### 5.2. Instalar Java 25

Procure na lista por uma linha parecida com:

```
25.0.0-tem
```

Então execute (substitua pela versão exata que você viu):

```bash
sdk install java 25.0.0-tem
```

O SDKMAN vai:
1. Baixar o Java 25
2. Instalar automaticamente
3. Perguntar se deseja tornar essa a versão padrão

**Digite `y` e pressione Enter** para tornar padrão.

### 5.3. Verificar Instalação

Digite:

```bash
java -version
```

Deve mostrar:

```
openjdk version "25.0.0" 2024-XX-XX
OpenJDK Runtime Environment Temurin-25.0.0+X (build 25.0.0+X)
OpenJDK 64-Bit Server VM Temurin-25.0.0+X (build 25.0.0+X, mixed mode, sharing)
```

E também:

```bash
javac -version
```

Deve mostrar:

```
javac 25.0.0
```

✅ **Java 25 instalado e configurado!**

---

## 6. Gerenciamento de Versões

### 6.1. Instalar Múltiplas Versões

Você pode ter várias versões instaladas simultaneamente:

```bash
# Instalar Java 21 LTS
sdk install java 21.0.4-tem

# Instalar Java 17 LTS
sdk install java 17.0.12-tem
```

### 6.2. Listar Versões Instaladas

```bash
sdk list java | grep installed
```

Ou simplesmente:

```bash
sdk current java
```

### 6.3. Alternar Entre Versões

#### Temporariamente (apenas na sessão atual do terminal)

```bash
sdk use java 21.0.4-tem
```

Quando fechar o terminal, volta para a versão padrão.

#### Permanentemente (tornar padrão)

```bash
sdk default java 21.0.4-tem
```

Agora Java 21 é o padrão sempre.

### 6.4. Desinstalar uma Versão

```bash
sdk uninstall java 17.0.12-tem
```

### 6.5. Atualizar SDKMAN

```bash
sdk selfupdate
```

---

## 7. Verificação e Testes

### 7.1. Configurar VS Code para Usar o Java Correto

**No Git Bash**:

1. Execute:

```bash
echo $JAVA_HOME
```

Copie o caminho exibido.

2. Abra VS Code
3. Pressione `Ctrl+,` (Configurações)
4. Busque por: `java.jdt.ls.java.home`
5. Clique em **"Edit in settings.json"**
6. Adicione:

```json
{
  "java.jdt.ls.java.home": "/c/Users/SEU_USUARIO/.sdkman/candidates/java/current"
}
```

> ⚠️ **Nota**: Substitua `SEU_USUARIO` pelo seu nome de usuário do Windows.

**No WSL**: O caminho será algo como `/home/seu_usuario/.sdkman/candidates/java/current`

### 7.2. Criar um Programa de Teste

1. Crie uma pasta: `C:\teste-java`
2. Abra no VS Code: **File → Open Folder**
3. Crie um arquivo: `HelloWorld.java`
4. Cole este código:

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Java " + System.getProperty("java.version"));
        System.out.println("Funcionando perfeitamente! 🚀");
    }
}
```

### 7.3. Compilar e Executar

**No terminal integrado do VS Code** (`` Ctrl+` ``), selecione Git Bash ou WSL:

```bash
# Compilar
javac HelloWorld.java

# Executar
java HelloWorld
```

Você deve ver:

```
Java 25.0.0
Funcionando perfeitamente! 🚀
```

✅ **Tudo funcionando!**

---

## ✅ Checklist de Verificação

Marque o que você completou:

- [✅] Git Bash ou WSL instalado e funcionando
- [✅] SDKMAN instalado (`sdk version` funciona)
- [✅] Java 25 instalado (`java -version` mostra versão 25)
- [✅] Compilador Java funcionando (`javac -version`)
- [✅] VS Code reconhecendo o Java
- [✅] Programa de teste compilando e executando
- [✅] JAVA_HOME configurado (`echo $JAVA_HOME` mostra caminho)

---

## 🆘 Problemas Comuns

### "sdk: command not found"

**Causa**: SDKMAN não foi carregado no terminal.

**Solução**:

```bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

Ou feche e abra novamente o terminal.

### "curl: command not found" (Git Bash)

**Causa**: Git Bash sem curl instalado.

**Solução**: Reinstale o Git e certifique-se de marcar opções de componentes Unix tools.

### "java: command not found" após instalar

**Causa**: Variável de ambiente não configurada.

**Solução**:

```bash
sdk default java 25.0.0-tem
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

### VS Code não encontra o Java

**Solução**:

1. Abra o terminal integrado do VS Code
2. Execute: `which java`
3. Copie o caminho
4. Vá em Settings → `java.configuration.runtimes` → Edit in settings.json
5. Adicione:

```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-25",
      "path": "/c/Users/SEU_USUARIO/.sdkman/candidates/java/25.0.0-tem",
      "default": true
    }
  ]
}
```

### "Permissão negada" ao instalar SDKMAN no WSL

**Solução**:

```bash
chmod +x ~/.sdkman/bin/sdkman-init.sh
source ~/.sdkman/bin/sdkman-init.sh
```

---

## 🎯 Próximos Passos

Agora que o Java está instalado, vamos aprender Git:

➡️ **[Próximo: Git - Fundamentos](./03-git-fundamentals.md)**

---

## 📚 Recursos Adicionais

- [Documentação oficial SDKMAN](https://sdkman.io/)
- [OpenJDK](https://openjdk.org/)
- [Eclipse Temurin (AdoptOpenJDK)](https://adoptium.net/)
- [Oracle Java Documentation](https://docs.oracle.com/en/java/)

---

## 💡 Dicas Profissionais

### Comando úteis do SDKMAN

```bash
# Ver tudo que pode instalar além de Java
sdk list

# Instalar Maven
sdk install maven

# Instalar Gradle
sdk install gradle

# Instalar Spring Boot CLI
sdk install springboot

# Atualizar todas as ferramentas
sdk upgrade
```

### Script de Configuração Automática

Crie um arquivo `~/.bashrc` (ou `~/.bash_profile` no Git Bash) e adicione:

```bash
# SDKMAN
export SDKMAN_DIR="$HOME/.sdkman"
[[ -s "$SDKMAN_DIR/bin/sdkman-init.sh" ]] && source "$SDKMAN_DIR/bin/sdkman-init.sh"
```

Assim SDKMAN carrega automaticamente sempre que abrir o terminal!
