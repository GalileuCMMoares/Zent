<div align="center">

# 📚 Zent

### Estude com foco. Revise no momento certo.

Um app Android para organizar seus estudos, transformar materiais em questões e acompanhar revisões com repetição espaçada.

<p>
  <img src="https://img.shields.io/badge/Android-API%2026%2B-3DDC84?logo=android&logoColor=white" alt="Android API 26 ou superior">
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin 2.0.21">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose e Material 3">
</p>

</div>

---

## ✨ Funcionalidades

| Organize | Aprenda | Acompanhe |
| --- | --- | --- |
| Crie matérias e assuntos | Gere questões com Gemini a partir de texto, imagem ou PDF | Faça quizzes com níveis de dificuldade variados |
| Consulte biblioteca e perfil | Revise questões sobre o material estudado | Planeje revisões com repetição espaçada baseada no desempenho |
| Acesse sua conta por e-mail | Salve dados localmente e no Firestore | Veja estatísticas de estudo |

**Fluxo de estudo:** material → questões → quiz → próxima revisão.

## 🧰 Tecnologias

| Área | Tecnologia |
| --- | --- |
| Interface | Kotlin, Jetpack Compose e Material 3 |
| Navegação e estado | Navigation Compose, ViewModel, Coroutines e Flow |
| Injeção de dependências | Koin |
| Dados locais | Room |
| Conta e nuvem | Firebase Authentication e Cloud Firestore |
| Geração de questões | Gemini API |

## 🚀 Comece

### Requisitos

- Android Studio com suporte ao Android Gradle Plugin 8.12.
- JDK 17 e Android SDK Platform 36.
- Gradle Wrapper 8.13, incluído no repositório.

O Android Gradle Plugin 8.12 requer JDK 17 e Gradle 8.13. Consulte as [notas oficiais de compatibilidade](https://developer.android.com/build/releases/agp-8-12-0-release-notes).

### 1. Configure o Firebase

1. Crie um projeto no Firebase e registre um app Android com o application ID `com.example.zent`.
2. Ative a autenticação por e-mail e senha e configure o Cloud Firestore.
3. Baixe o arquivo `google-services.json` e coloque-o em `app/google-services.json`.
4. Configure regras do Firestore para que cada pessoa autenticada só acesse os próprios dados.

O arquivo `google-services.json` é específico do seu projeto e fica fora do Git. Cada pessoa que clonar o Zent deve obter o próprio arquivo. Antes de publicar, troque `com.example.zent` pelo application ID definitivo e registre esse identificador no Firebase.

### 2. Configure a API Gemini

Defina `GEMINI_API_KEY` no ambiente usado pelo Gradle. No macOS ou Linux:

```bash
export GEMINI_API_KEY="sua-chave"
./gradlew assembleDebug
```

Sem essa variável, o app pode ser compilado, mas a geração de questões com Gemini não estará disponível.

> **Atenção:** o app faz chamadas Gemini diretamente. A variável evita versionar a chave, mas o valor é incluído no APK e pode ser extraído. Use essa configuração apenas para desenvolvimento. Para distribuir o app, encaminhe as chamadas por um backend e mantenha a chave no ambiente seguro do servidor.

## ▶️ Compilar e executar

Abra o projeto no Android Studio, aguarde a sincronização do Gradle e execute-o em um emulador ou dispositivo Android 8.0 (API 26) ou superior.

Também é possível compilar e instalar pelo terminal:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

No Windows, use `gradlew.bat` no lugar de `./gradlew`.

## 🗂️ Organização do código

```text
app/src/main/java/com/example/zent/
├── data/          # Room, Firestore e mapeadores
├── di/            # Injeção de dependências
├── domain/        # Modelos, repositórios, casos de uso e SRS
├── presentation/  # Telas Compose
└── viewmodel/     # Estado e lógica de apresentação
```
