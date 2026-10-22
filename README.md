# Nox Launcher — Windows-inspired Android UI

Launcher Android em Java com interface inspirada no Windows 11: fundo azul em gradiente, relógio/data, pesquisa, menu Iniciar, apps fixados e barra inferior centralizada.

## Gerar o APK no GitHub
1. Envie todos os arquivos e pastas deste ZIP para a raiz do repositório GitHub.
2. Confirme que `.github/workflows/build-apk.yml` também foi enviado.
3. Abra **Actions** e selecione **Build Android APK**. O workflow roda automaticamente após um push para `main`; também pode ser iniciado manualmente.
4. Baixe `NoxLauncher-debug-apk` em **Artifacts** quando a execução terminar.

## Requisitos
- Java 17
- Android Gradle Plugin 8.7.3
- Android SDK 35
- Android 8.0 (API 26) ou superior

O app é um launcher para Android com estilo visual inspirado no Windows; não instala nem executa o Windows. Este projeto é uma base inicial e o APK de debug serve para testes. A opção de escolher imagem de fundo atualmente salva a seleção, mas ainda não aplica essa imagem como fundo.
