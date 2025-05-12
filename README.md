# AudioPlayerComEqualizador

## Equipe

*   Alisson Freitas
*   Eduardo Perez Uanús
*   João Gabriel A. Gomes Alves
*   Rayanne Andrade

## Objetivo do Projeto

Este projeto implementa os métodos do ciclo de vida de um serviço de áudio no Android, criando um serviço de reprodução de áudio robusto com capacidades de equalização aprimoradas por código nativo. O objetivo é desenvolver um serviço que lide com a reprodução de áudio (formato MP3), forneça recursos de equalização utilizando integração nativa, permita a troca dinâmica entre diferentes faixas de áudio, **e garantir a qualidade e o funcionamento correto do código através de testes unitários e de integração.**

## Descrição do Projeto

Esta aplicação Android fornece um serviço de reprodução de áudio que permite aos usuários reproduzir, pausar e parar arquivos de áudio MP3. Também inclui um recurso de equalização, **parcialmente implementado com código nativo em C++ via JNI**, para ajustar as frequências de áudio. A aplicação utiliza um `AudioService` para gerenciar a reprodução de áudio em segundo plano utilizando o `MediaPlayer`. A comunicação entre a Activity e o Serviço é feita utilizando AIDL. Agora, os usuários podem selecionar diferentes faixas de áudio para reprodução, alterando dinamicamente a música que está sendo tocada. **Além disso, o projeto inclui uma suite de testes unitários e de integração para validar as funcionalidades do serviço e a correta interação com a camada nativa.**

## Visão Geral da Interface

Aqui está uma captura de tela da interface principal da aplicação:

<div style="display: flex; flex-direction: 'row'; align-items: 'center';">
    <img src="images/appcomplete.png" width="325px">
</div>

Notificações:

<div style="display: flex; flex-direction: row; justify-content: space-between; align-items: center;">
    <img src="images/playing.png" width="225px" alt="Playing">
    <img src="images/pause.png" width="225px" alt="Pause">
</div>

## Principais Funcionalidades

*   **Reprodução de Áudio:** Reproduz arquivos de áudio MP3.
*   **Controles de Reprodução:** Inclui funcionalidades de reproduzir, pausar e parar.
*   **Serviço em Segundo Plano:** Utiliza um `AudioService` para lidar com a reprodução em segundo plano.
*   **Equalização (Parcial - com Implementação Nativa):** Inclui um recurso de equalização utilizando código C++ via JNI.
*   **Notificação Persistente:** Exibe uma notificação persistente com status básicos da reprodução (tocando/pausado).
*   **Comunicação AIDL:** Utiliza AIDL para comunicar entre a Activity e o Serviço.
*   **Seek Bar:** Permite avançar ou retroceder na faixa de áudio.
*   **Controle de Volume:** Permite ajustar o volume da reprodução.
*   **Troca de Faixa Dinâmica:** Permite aos usuários selecionar diferentes faixas de áudio de uma lista, alterando a música em tempo real.
*   **Testes Unitários:** Validação isolada das funcionalidades do serviço utilizando JUnit, Mockito e Robolectric.
*   **Testes de Integração:** Verificação da comunicação entre o código Java/Kotlin e a biblioteca nativa C++ para equalização.

## Arquitetura

A aplicação segue uma arquitetura modular:

*   **`AidlServiceManager`:** Gerencia a vinculação e desvinculação do serviço AIDL.
*   **`AudioManager`:** Gerencia a configuração de áudio, como a troca de faixas, comunicando-se com o serviço AIDL.
*   **`AudioSettingsManager`:** Configura os listeners para os botões da interface do usuário (reproduzir, pausar, parar) e gerencia as animações correspondentes.
*   **`AudioService`:** Um serviço que estende `android.app.Service` e implementa a interface AIDL `IMessageService`. Ele lida com a reprodução de áudio utilizando `MediaPlayer` e inclui equalização básica **através de módulos auxiliares, incluindo um módulo com integração nativa**. Agora também suporta a troca dinâmica de faixas de áudio.
*   **`MainActivity`:** A activity principal que se vincula ao `AudioService` e fornece a interface do usuário para controlar a reprodução de áudio. Inclui a lógica para exibir uma lista de faixas e permitir que o usuário selecione uma nova faixa.
*   **`PermissionManager`:** Gerencia as permissões de tempo de execução para gravação de áudio e acesso à mídia.
*   **`BottomSheetTrackList`**: Mostra uma lista de músicas disponiveis.
*   **`SelectedMusicSingleton`**: Salva a música selecionada.

## Módulos

1.  **Módulo de Reprodução:**
    *   Responsável por reproduzir, pausar e parar arquivos de áudio utilizando `MediaPlayer`.
    *   Localizado dentro do `AudioService`.
    *   Atualizado para suportar a troca dinâmica de faixas.
2.  **Módulo de Equalização:**
    *   Responsável pela aplicação de efeitos de equalização no áudio.
    *   **Inclui a inicialização do `Equalizer` do Android e a chamada a uma função implementada em código nativo (C++) via JNI para processamento de áudio.**
    *   Localizado dentro do `AudioService`.
3.  **Módulo de Notificação:**
    *   Cria e gerencia uma notificação persistente na barra de status.
    *   Localizado dentro do `AudioService`.

## Tecnologias Utilizadas

*   **Android SDK:** Utilizado para construir a aplicação.
*   **Java:** Linguagem de programação para a lógica de alguns componentes da aplicação.
*   **Kotlin:** Linguagem de programação para a Activity e outros componentes.
*   **C++:** Linguagem de programação utilizada para a implementação da lógica de equalização nativa.
*   **JNI (Java Native Interface):** Interface para comunicação entre o código Java/Kotlin e o código nativo C++.
*   **AIDL (Android Interface Definition Language):** Utilizado para a comunicação entre a Activity e o Serviço.
*   **MediaPlayer:** Utilizado para a reprodução de áudio.
*   **Equalizer:** Utilizado para a equalização de áudio (API do Android).
*   **CMake:** Ferramenta utilizada para configurar e compilar o código nativo C++.
*   **JUnit 5 / JUnit 4:** Frameworks utilizados para escrita e execução de testes unitários e instrumentados.
*   **Mockito:** Framework para criação de mocks e simulação de dependências em testes unitários.
*   **Robolectric:** Framework para execução de testes unitários Android na JVM local.
*   **AndroidJUnit4:** Runner para execução de testes instrumentados em ambiente Android.
*   **Android Studio:** IDE para o desenvolvimento.

## Instruções de Configuração

1.  **Pré-requisitos:**
    *   Android Studio instalado.
    *   Android SDK configurado.
    *   Android NDK e CMake instalados (podem ser instalados via SDK Manager no Android Studio).
    *   Um dispositivo Android físico ou emulador.

2.  **Clonando o Repositório:**

    ```bash
    git clone [https://github.com/ko66i/MediaChallenger.git]
    cd [diretório do projeto]
    ```

3.  **Abrindo o Projeto no Android Studio:**
    *   Abra o Android Studio.
    *   Selecione "Open an Existing Project".
    *   Navegue até o diretório do projeto clonado e selecione o arquivo `build.gradle`.
    *   Aguarde o Android Studio sincronizar o projeto e baixar as dependências (incluindo as libs de teste e configurando o CMake).

4.  **Construindo e Executando a Aplicação:**
    *   Conecte seu dispositivo Android ou inicie um emulador.
    *   Clique no botão "Run" no Android Studio.
    *   Selecione seu dispositivo/emulador e clique em "OK".

## Explicação do Código

*   **`AidlServiceManager.kt`:**
    *   Gerencia a vinculação e desvinculação ao `AudioService` usando AIDL.
    *   Estabelece uma `ServiceConnection` para lidar com a conexão e desconexão do serviço.
    *   Fornece métodos para verificar se o serviço está vinculado e para obter a interface `IMessageService`.

*   **`AudioManager.kt`:**
    *   Gerencia a configuração de áudio, como a troca de faixas, comunicando-se com o serviço AIDL.
    *   Registra mensagens de sucesso ou falha com base no resultado das chamadas AIDL.

*   **`AudioSettingsManager.kt`:**
    *   Configura os listeners de clique para os botões da interface do usuário (reproduzir, pausar, parar).
    *   Chama os métodos de controle de áudio correspondentes (`playAudio`, `pauseAudio`, `stopAudio`) do serviço AIDL.
    *   Gerencia a animação Lottie com base no estado da reprodução.

*   **`MainActivity.kt`:**
    *   A activity principal que inicializa o `AidlServiceManager`, `AudioManager` e `AudioSettingsManager`.
    *   Vincula-se ao `AudioService` quando a activity é criada.
    *   Solicita permissões de áudio utilizando o `PermissionManager`.
    *   Implementa a lógica para as SeekBars de progresso e volume.
    *   Implementa a lógica para exibir uma lista de faixas e permitir que o usuário selecione uma nova faixa.

*   **`AudioService.java`:**
    *   Um serviço que estende `android.app.Service` e implementa a interface AIDL `IMessageService`.
    *   Lida com a reprodução de áudio utilizando `MediaPlayer`.
    *   Utiliza `PlaybackModule`, `EqualizationModule` (com integração nativa) e `NotificationModule` para gerenciar as funcionalidades.
    *   Agora suporta a troca dinâmica de faixas de áudio através do método `setAudioResource`.
    *   Cria uma notificação persistente com controles de reprodução.

*   **`equalizer.cpp`:** Contém a implementação em C++ da lógica para aplicar ganhos às amostras de áudio (`applyEqualizationNative`). Este código é compilado em uma biblioteca nativa (`libequalizer.so`) usando CMake.

*   **`EqualizationModule.java/kt`:** Módulo utilizado pelo `AudioService` que gerencia o `Equalizer` do Android e **chama a função nativa `applyEqualizationNative` implementada em C++ via JNI** para processamento de áudio.

*   **`NotificationModule.java/kt`:** Módulo responsável por criar e gerenciar a notificação persistente do serviço em primeiro plano.

*   **`PermissionManager.kt`:**
    *   Verifica e solicita permissões de áudio em tempo de execução.
    *   Utiliza a permissão `READ_MEDIA_AUDIO` para Android 13 e superior, e `RECORD_AUDIO` para versões mais antigas.

*   **`IMessageService.aidl`:** Define a interface para o `AudioService`, permitindo que a Activity chame métodos no Serviço. Inclui métodos para `playAudio`, `pauseAudio`, `stopAudio`, `seekAudio`, `getDuration`, `getCurrentPosition`, `setVolume`, e `setAudioResource`.

*   **`AudioServiceTest.kt`:** Classe com testes unitários que validam as funcionalidades do `AudioService`, `IMessageServiceConnector` e `NotificationModule` utilizando mocks (Mockito) e simulação do ambiente Android (Robolectric). Localizado em `src/test/java/`.

*   **`AudioPlayerEqualizerTest.kt`:** Classe com teste de integração instrumentado que verifica o carregamento da biblioteca nativa e a chamada ao método `applyEqualization` do `EqualizationModule` em um ambiente Android. Localizado em `src/androidTest/java/`.

*   **`AndroidManifest.xml`:** Declara as permissões necessárias, o serviço e a activity. Inclui a declaração da permissão personalizada para o serviço de mensagens e especifica o `foregroundServiceType` como `mediaPlayback`.

## Utilização

1.  **Executando a Aplicação:**
    *   Inicie a aplicação em um dispositivo Android ou emulador.
    *   Conceda as permissões de áudio necessárias quando solicitado.
    *   Utilize os botões de reproduzir, pausar e parar na interface do usuário para controlar a reprodução de áudio.
    *   Utilize a SeekBar para controlar o ponto da musica.
    *   Utilize a SeekBar de volume para controlar o volume da musica.
    *   Selecione uma nova faixa de áudio na lista para alterar a música que está sendo tocada.

2.  **Interagindo com a Notificação:**
    *   Quando o áudio estiver sendo reproduzido, uma notificação persistente será exibida na barra de status.
    *   Utilize os controles na notificação para gerenciar a reprodução sem abrir a aplicação.

## Testes

O projeto inclui testes unitários e de integração para garantir a qualidade do código, validar as funcionalidades do serviço e verificar a correta integração com a camada nativa C++.

*   **Testes Unitários:**
    *   Os testes unitários (`AudioServiceTest.kt`) rodam na JVM local e utilizam mocks para isolar as unidades de código.
    *   Para executar no Android Studio: Clique com o botão direito no arquivo `AudioServiceTest.kt` (em `src/test/java/`) e selecione `Run 'AudioServiceTest'`.
    *   Para executar via linha de comando: Utilize o comando `./gradlew test` na raiz do projeto.

*   **Testes de Integração:**
    *   Os testes de integração instrumentados (`AudioPlayerEqualizerTest.kt`) rodam em um dispositivo ou emulador Android, necessários para testar a integração JNI e o carregamento da biblioteca nativa.
    *   Para executar no Android Studio: Certifique-se de que um emulador ou dispositivo esteja conectado. Clique com o botão direito no arquivo `AudioPlayerEqualizerTest.kt` (em `src/androidTest/java/`) e selecione `Run 'AudioPlayerEqualizerTest'`.
    *   Para executar via linha de comando: Utilize o comando `./gradlew connectedCheck` na raiz do projeto (executa todos os testes instrumentados em dispositivos conectados).

## Melhorias Futuras

*   **Implementar Lógica de Equalização Nativa Completa:** Finalizar e refinar a lógica de processamento de áudio em C++ para aplicar a equalização de forma eficaz.
*   **Expandir Testes de Integração Nativa:** Adicionar testes que validem a aplicação da equalização nativa com diferentes conjuntos de ganhos, verificando se os dados de áudio são processados corretamente pela função C++.
*   **Aumentar Cobertura de Testes:** Escrever mais testes unitários para cobrir cenários adicionais, tratamento de erros e outros módulos ou funcionalidades não totalmente testadas.
*   **Implementar Testes de Performance/Carga:** Avaliar o desempenho do serviço, especialmente da parte nativa, sob diferentes condições de uso.
*   **Testes de Ciclo de Vida do Serviço:** Adicionar testes para verificar o comportamento do serviço durante seu ciclo de vida e o gerenciamento de recursos.
*   **Adicionar Seleção de Arquivo de Áudio:** Implementar um seletor de arquivos para permitir que os usuários escolham arquivos de áudio de seus dispositivos.
*   **Melhorar a Interface do Usuário:** Adicionar mais opções de customização e melhorar o design geral da interface.
*   **Controles na Notificação:** Adicionar botões de controle (play/pause/stop) à notificação usando `MediaStyle` para uma melhor experiência do usuário.
*   **Controle remoto via `MediaSession`:** Permitir controlar a reprodução de áudio através de dispositivos externos, como fones de ouvido Bluetooth.

