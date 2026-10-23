# Arquitetura do JARVIS inspirada no HuggingGPT

Este documento adapta a ideia geral do artigo HuggingGPT (arXiv:2303.17580) para um assistente Android pequeno e seguro. Não é uma implementação oficial do artigo nem reproduz o sistema de pesquisa completo.

## Ciclo do assistente
1. **Entrada:** Android Speech Recognizer converte a fala em texto.
2. **Entendimento e planejamento:** o controlador interpreta a intenção e escolhe uma ação entre ferramentas registradas.
3. **Política:** cada ferramenta tem nome, descrição, argumentos permitidos e nível de risco.
4. **Execução:** somente ferramentas explícitas e autorizadas podem ser chamadas.
5. **Resposta:** o resultado volta para texto e TextToSpeech.

## Ferramentas locais do protótipo
- `get_time`: informa a hora local.
- `get_date`: informa a data local.
- `assistant_identity`: informa que é o JARVIS protótipo.
- `fallback`: explica que o pedido ainda não é suportado.

Esses comandos são roteamento local simples, não um modelo de IA generativo. Uma integração com modelo remoto deve ser adicionada por uma camada de servidor, sem colocar chaves secretas no APK ou no GitHub.

## Segurança
- Não executar texto arbitrário como código ou comando de shell.
- Não abrir, enviar ou apagar dados sem autorização específica.
- Pedir confirmação antes de ações sensíveis.
- Solicitar apenas permissões Android necessárias para cada função.
- Registrar claramente qual ferramenta foi escolhida e qual resultado foi retornado.

## Próxima fase
Criar uma interface `AssistantTool` e um `CommandRouter` testável; depois adicionar um provedor de IA por meio de um backend com autenticação e limites de uso.
