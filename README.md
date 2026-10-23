# JARVIS AI — Android

Assistente pessoal para Android, inspirado em interfaces de ficção científica, com foco em voz em português e automações permitidas pelo sistema.

## Primeira versão (MVP)
- Interface inicial com estado do assistente.
- Botão para iniciar reconhecimento de fala usando os recursos do Android.
- Resposta falada com TextToSpeech.
- Base preparada para integrar um provedor de IA futuramente (a chave de API nunca deve ser colocada no aplicativo ou no GitHub).
- Ações locais limitadas às APIs e permissões oficiais do Android.

## Estrutura
- `app/`: aplicativo Android em Kotlin.
- Jetpack Compose: interface.
- Android Speech Recognizer: entrada de voz.
- Android TextToSpeech: saída de voz.

## Compilar
Abra o repositório no Android Studio atualizado, aguarde a sincronização do Gradle e execute o módulo `app` em um dispositivo/emulador Android. O projeto inicial não inclui o Gradle Wrapper; o Android Studio pode importar e configurar o projeto, ou podemos adicionar o wrapper na próxima etapa.

## Privacidade e segurança
O aplicativo não tem acesso irrestrito ao telefone. Ações sensíveis exigem permissões explícitas do Android e confirmação do usuário. Não inclua tokens, senhas ou chaves de API em arquivos versionados.

## Estado atual
Protótipo inicial de interface e voz. Ainda não é um assistente completo, não contém integração com IA configurada e não controla o celular inteiro.

## Próximas etapas
1. Validar compilação e permissões de microfone.
2. Testar fala para texto e resposta falada em português.
3. Adicionar configuração segura de um provedor de IA.
4. Implementar comandos Android individuais, com confirmação quando necessário.


## Arquitetura inspirada no HuggingGPT

A primeira versão do JARVIS pode adotar um ciclo de controlador + ferramentas: reconhecer a fala, interpretar a intenção, selecionar uma ferramenta permitida, executar e responder por voz. Veja [a proposta de arquitetura](docs/ARQUITETURA_HUGGINGGPT.md). Esta é uma adaptação simplificada, não a implementação completa do artigo.
