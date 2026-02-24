# 🪟 Setup do Ambiente de Desenvolvimento no Windows

Este guia vai te ajudar a configurar um ambiente profissional de desenvolvimento Java/Spring Boot no Windows.

## 📋 Índice

1. [Instalação do Visual Studio Code](#instalação-do-visual-studio-code)
2. [Extensões Essenciais](#extensões-essenciais)
3. [Configurações Recomendadas](#configurações-recomendadas)
4. [Terminal Integrado](#terminal-integrado)
5. [Verificação da Instalação](#verificação-da-instalação)

---

## 1. Instalação do Visual Studio Code

### O que é o VS Code?

O **Visual Studio Code** (VS Code) é um editor de código gratuito, leve e poderoso criado pela Microsoft. É a escolha mais popular entre desenvolvedores atualmente porque:

- ✅ É **gratuito e open-source**
- ✅ Tem **extensões para qualquer linguagem**
- ✅ É **leve e rápido** (diferente de IDEs pesadas como Eclipse)
- ✅ Tem **terminal integrado**
- ✅ Excelente suporte para Git e debugging

### Passo a Passo da Instalação

#### 1.1. Baixar o Instalador

1. Acesse: https://code.visualstudio.com/
2. Clique em **"Download for Windows"**
3. O arquivo baixado será algo como: `VSCodeUserSetup-x64-1.XX.X.exe`

#### 1.2. Executar o Instalador

1. Execute o arquivo baixado (duplo clique)
2. Aceite o contrato de licença
3. **IMPORTANTE**: Na tela "Selecione Tarefas Adicionais", marque **TODAS** as opções:
   - ✅ Adicionar a ação "Abrir com Code" ao menu de contexto de arquivo do Windows Explorer
   - ✅ Adicionar a ação "Abrir com Code" ao menu de contexto de diretório do Windows Explorer
   - ✅ Registrar Code como um editor para tipos de arquivos suportados
   - ✅ Adicionar ao PATH (isso permite abrir o VS Code pelo terminal)

4. Clique em **"Instalar"**
5. Aguarde a instalação concluir
6. Marque **"Executar Visual Studio Code"** e clique em **"Concluir"**

#### 1.3. Primeira Execução

Quando o VS Code abrir pela primeira vez:
1. Pode aparecer uma mensagem perguntando se você deseja instalar extensões recomendadas - **clique em "Instalar"**
2. Escolha um tema de sua preferência (pode mudar depois)

---

## 2. Extensões Essenciais

Extensões são plugins que adicionam funcionalidades ao VS Code. Para desenvolvimento Java/Spring Boot, você precisa de algumas extensões específicas.

### Como Instalar Extensões

1. Clique no ícone de **Extensions** na barra lateral esquerda (ou pressione `Ctrl+Shift+X`)
2. Digite o nome da extensão na barra de busca
3. Clique em **"Install"** na extensão correta

### 2.1. Extension Pack for Java (Microsoft)

**O que faz?**: Pacote completo com tudo que você precisa para Java

**Como instalar**:
1. Buscar por: `Extension Pack for Java`
2. Procure pela que tem a Microsoft como autor
3. Clique em **"Install"**

Essa extensão instala automaticamente:
- ✅ Language Support for Java (RedHat)
- ✅ Debugger for Java (Microsoft)
- ✅ Test Runner for Java (Microsoft)
- ✅ Maven for Java (Microsoft)
- ✅ Project Manager for Java (Microsoft)
- ✅ IntelliCode (Microsoft) - sugestões de código com IA

### 2.2. Spring Boot Extension Pack (VMware)

**O que faz?**: Ferramentas específicas para Spring Boot

**Como instalar**:
1. Buscar por: `Spring Boot Extension Pack`
2. Procure pela que tem VMware/Tanzu como autor
3. Clique em **"Install"**

Essa extensão instala:
- ✅ Spring Boot Tools
- ✅ Spring Initializr Java Support
- ✅ Spring Boot Dashboard

### 2.3. REST Client (Huachao Mao)

**O que faz?**: Permite testar APIs REST direto do VS Code (alternativa ao Postman)

**Como instalar**:
1. Buscar por: `REST Client`
2. Autor: Huachao Mao
3. Clique em **"Install"**

**Como usar**: Crie um arquivo `.http` e escreva requisições HTTP. Exemplo:

```http
### Testar endpoint de produtos
GET http://localhost:8080/api/products
```

### 2.4. GitLens (GitKraken)

**O que faz?**: Superpoderes para Git (ver histórico, blame, etc.)

**Como instalar**:
1. Buscar por: `GitLens`
2. Autor: GitKraken
3. Clique em **"Install"**

### 2.5. Docker (Microsoft)

**O que faz?**: Gerenciar containers Docker direto do VS Code

**Como instalar**:
1. Buscar por: `Docker`
2. Autor: Microsoft
3. Clique em **"Install"**

### 2.6. Error Lens (Alexander)

**O que faz?**: Mostra erros inline no código (muito útil!)

**Como instalar**:
1. Buscar por: `Error Lens`
2. Autor: Alexander
3. Clique em **"Install"**

### 2.7. Material Icon Theme (Philipp Kief)

**O que faz?**: Ícones bonitos para arquivos e pastas (opcional, mas recomendado)

**Como instalar**:
1. Buscar por: `Material Icon Theme`
2. Autor: Philipp Kief
3. Clique em **"Install"**
4. Quando perguntar se deseja ativar, clique em **"Activate"**

### 2.8. Portuguese (Brazil) Language Pack (Microsoft)

**O que faz?**: Interface do VS Code em Português (se preferir)

**Como instalar**:
1. Buscar por: `Portuguese (Brazil) Language Pack`
2. Autor: Microsoft
3. Clique em **"Install"**
4. Reinicie o VS Code quando solicitado

> ⚠️ **Nota**: Mantenha a interface em inglês se quiser se acostumar com os termos técnicos em inglês (útil para carreira)

---

## 3. Configurações Recomendadas

### 3.1. Abrir Configurações

Pressione `Ctrl+,` ou vá em: **File → Preferences → Settings**

### 3.2. Configurações Úteis

Pesquise e configure o seguinte:

#### Auto Save
- Buscar: `Auto Save`
- Mudar de `off` para `afterDelay`
- **Por quê?**: Salva arquivos automaticamente após 1 segundo de inatividade

#### Format On Save
- Buscar: `Format On Save`
- Marcar a checkbox ✅
- **Por quê?**: Formata o código automaticamente quando salvar (Ctrl+S)

#### Tab Size para Java
- Buscar: `Java > Format > Settings: Profile`
- Selecionar `GoogleStyle` ou `Eclipse`
- **Por quê?**: Padrão de indentação profissional

#### Font Size
- Buscar: `Font Size`
- Ajustar para um tamanho confortável (padrão é 14)
- Experimente: 16 se tela grande, 12 se tela pequena

#### Font Ligatures (Opcional)
- Buscar: `Font Ligatures`
- Ativar com `true`
- Instalar fonte: [Fira Code](https://github.com/tonsky/FiraCode) ou [JetBrains Mono](https://www.jetbrains.com/lp/mono/)
- **Por quê?**: Símbolos como `=>`, `!=`, `>=` ficam mais bonitos

### 3.3. Configuração via JSON (Avançado)

Você pode configurar tudo de uma vez via JSON:

1. Pressione `Ctrl+Shift+P`
2. Digite: `Preferences: Open User Settings (JSON)`
3. Adicione (ou substitua) com estas configurações:

```json
{
  "workbench.colorTheme": "Default Dark+",
  "workbench.iconTheme": "material-icon-theme",
  "editor.fontSize": 14,
  "editor.fontFamily": "Consolas, 'Courier New', monospace",
  "editor.tabSize": 4,
  "editor.insertSpaces": true,
  "editor.formatOnSave": true,
  "editor.formatOnPaste": true,
  "editor.minimap.enabled": true,
  "editor.bracketPairColorization.enabled": true,
  "editor.guides.bracketPairs": true,
  "files.autoSave": "afterDelay",
  "files.autoSaveDelay": 1000,
  "terminal.integrated.defaultProfile.windows": "Git Bash",
  "git.autofetch": true,
  "git.confirmSync": false,
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.saveActions.organizeImports": true,
  "spring-boot.ls.checkJVM": false,
  "[java]": {
    "editor.defaultFormatter": "redhat.java"
  }
}
```

---

## 4. Terminal Integrado

### 4.1. O que é?

O VS Code tem um terminal integrado onde você pode executar comandos sem sair do editor.

### 4.2. Como Abrir

- **Atalho**: `` Ctrl+` `` (Ctrl + tecla de acento grave)
- **Menu**: View → Terminal

### 4.3. Escolher o Terminal Padrão

Por padrão, o Windows usa PowerShell ou CMD, mas é melhor usar **Git Bash** (instalaremos Git no próximo guia).

**Depois de instalar o Git**:
1. Abra o terminal (`` Ctrl+` ``)
2. Clique na setinha para baixo ao lado do `+`
3. Clique em **"Select Default Profile"**
4. Escolha **"Git Bash"**

---

## 5. Verificação da Instalação

### 5.1. Verificar VS Code

Abra o terminal (`` Ctrl+` ``) e digite:

```bash
code --version
```

Deve mostrar algo como:
```
1.XX.X
abc123def456
x64
```

### 5.2. Verificar Extensões Java

1. Crie uma pasta de teste: `C:\teste-java`
2. Abra essa pasta no VS Code: **File → Open Folder**
3. Crie um arquivo chamado `Teste.java`
4. Digite:

```java
public class Teste {
    public static void main(String[] args) {
        System.out.println("VS Code configurado!");
    }
}
```

5. Se você ver **números de linha**, **syntax highlighting** (cores no código) e **nenhum erro de extensão**, está tudo certo!

> ⚠️ **Nota**: Ainda não vamos executar esse código. Primeiro precisamos instalar o Java (próximo guia).

---

## ✅ Checklist de Verificação

Marque o que você completou:

- [ ] VS Code instalado e abrindo corretamente
- [ ] Extension Pack for Java instalado
- [ ] Spring Boot Extension Pack instalado
- [ ] REST Client instalado
- [ ] GitLens instalado
- [ ] Docker extensão instalado
- [ ] Error Lens instalado
- [ ] Material Icon Theme instalado (opcional)
- [ ] Auto Save configurado
- [ ] Format On Save configurado
- [ ] Terminal integrado abrindo corretamente
- [ ] `code --version` funcionando no terminal

---

## 🆘 Problemas Comuns

### "O comando 'code' não funciona no terminal"

**Solução**: Reinstale o VS Code e certifique-se de marcar a opção **"Adicionar ao PATH"** durante a instalação.

### "As extensões Java não estão funcionando"

**Solução**: 
1. Vá em Extensions (`Ctrl+Shift+X`)
2. Procure por extensões Java desabilitadas
3. Clique em **"Enable"**
4. Reinicie o VS Code

### "O VS Code está em inglês e quero em português"

**Solução**: Instale a extensão **Portuguese (Brazil) Language Pack** e reinicie.

### "O terminal integrado não abre"

**Solução**:
1. Pressione `Ctrl+Shift+P`
2. Digite: `Terminal: Select Default Profile`
3. Escolha qualquer opção (PowerShell, CMD, ou Git Bash se já instalado)
4. Tente abrir novamente com `` Ctrl+` ``

---

## 🎯 Próximos Passos

Agora que seu VS Code está configurado, vamos instalar e configurar o Java:

➡️ **[Próximo: Java com SDKMAN](./02-java-sdkman-setup.md)**

---

## 📚 Recursos Adicionais

- [Documentação oficial do VS Code](https://code.visualstudio.com/docs)
- [VS Code Tips and Tricks](https://code.visualstudio.com/docs/getstarted/tips-and-tricks)
- [Java in VS Code](https://code.visualstudio.com/docs/languages/java)
