# Nox Launcher

Launcher Android nativo em Java, com tema escuro, relógio, gaveta de aplicativos, busca por nome, abertura de apps e opções simples de personalização.

## Compilar automaticamente
Cada push para `main` executa o workflow **Build Android APK**. Também é possível iniciá-lo manualmente em **Actions**. O APK de teste fica em **Actions → execução concluída → Artifacts → NoxLauncher-debug-apk**.

## Instalar
Baixe `app-debug.apk` no artefato do workflow e abra-o no Android. Se necessário, autorize a instalação para o navegador/gerenciador de arquivos. Depois, pressione Home e escolha **Nox** como app de tela inicial.

## Projeto
- Android Gradle Plugin 8.7.3
- Java 17
- Min SDK 26 (Android 8.0)
- O APK gerado é de depuração, não uma versão assinada para publicação na Play Store.

## Observações
Este é um projeto inicial funcional, não um substituto completo para launchers comerciais. Teste em seu aparelho. A escolha de papel de parede ainda abre o seletor de imagem e salva a URI; aplicar automaticamente a imagem como papel de parede do sistema pode exigir uma etapa extra.
