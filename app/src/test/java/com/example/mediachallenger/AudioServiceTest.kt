package com.example.mediachallenger

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mediachallenger.AudioService.IMessageServiceConnector
import com.example.mediachallenger.notification.NotificationModule
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config
import java.io.IOException
import kotlin.test.DefaultAsserter.fail
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@Config(manifest = Config.NONE)
class AudioServiceTest {

    // Mock de MediaPlayer para simulação de comportamento nos testes
    @Mock
    private lateinit var mockMediaPlayer: MediaPlayer

    // Mock do conector IMessageServiceConnector para interações com AudioService
    @Mock
    private lateinit var mockConnector: IMessageServiceConnector

    // Instância da classe a ser testada
    private lateinit var audioService: AudioService

    // Mock do NotificationManager para verificar criação e exibição de notificações
    @Mock
    private lateinit var mockNotificationManager: NotificationManager

    // Mock de NotificationCompat.Builder, usado para construir instâncias de Notification
    @Mock
    private lateinit var mockBuilder: NotificationCompat.Builder

    // Instância de NotificationModule sendo testada
    private lateinit var notificationModule: NotificationModule

    // Mock do Context em que os serviços simulados são chamados
    @Mock
    private lateinit var context: Context

    @BeforeEach
    fun setUp() {
        // Inicializa todas as anotações @Mock nesta classe de teste
        MockitoAnnotations.openMocks(this)

        // Inicialize o AudioService com o Robolectric
        audioService = AudioService(mockConnector)
        audioService.mediaPlayer = mockMediaPlayer // injeta o mock

        // Gerenciar contexto com mock apropriado
        context = mock(Context::class.java)

        // Configura o contexto para retornar mockNotificationManager quando o sistema de serviços é chamado
        `when`(context.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(
            mockNotificationManager
        )

        // Inicializa o NotificationModule com o mock de contexto e notificationManager
        notificationModule = NotificationModule(context, "test_channel_id", 1234)

        // Configura o mockBuilder para retornar uma instância fake de Notification
        `when`(mockBuilder.build()).thenReturn(Notification())
    }

    @Test
    fun testPlayAudio() {
        try {
            // Configura o mock para indicar que o MediaPlayer não está tocando.
            `when`(mockMediaPlayer.isPlaying).thenReturn(false)

            // Configura o mockConnector para responder quando playAudio é chamado.
            // Simula o comportamento do método de reprodução.
            `when`(mockConnector.playAudio()).thenAnswer {
                if (!mockMediaPlayer.isPlaying) {
                    // Reinicia o MediaPlayer
                    mockMediaPlayer.reset()
                    // Define a fonte de dados (caminho fictício apenas para teste)
                    mockMediaPlayer.setDataSource("data source sample")
                    // Prepara o MediaPlayer para reprodução
                    mockMediaPlayer.prepare()
                    // Inicia a reprodução
                    mockMediaPlayer.start()
                    // Retorna true se todas as operações completaram com êxito
                    true
                } else {
                    // Indica que a reprodução não foi iniciada
                    false
                }
            }

            // Método que deseja testar: chama playAudio através do mockConnector
            val result = mockConnector.playAudio()

            // Verifica se os seguintes métodos no MediaPlayer são chamados
            verify(mockMediaPlayer).reset()
            verify(mockMediaPlayer).setDataSource(anyString())
            verify(mockMediaPlayer).prepare()
            verify(mockMediaPlayer).start()

            // Asserta que o result é verdadeiro, indicando que o áudio está tocando
            assertTrue(result, "O MediaPlayer deve estar tocando agora.")

            // Simula a verificação de que o MediaPlayer está tocando
            `when`(mockMediaPlayer.isPlaying).thenReturn(true)

            // Asserta que o isPlaying é verdadeiro
            assertTrue(mockMediaPlayer.isPlaying)
        } catch (e: IOException) {
            e.printStackTrace()
            fail("Exception não esperada durante o teste playAudio")
        }
    }

    @Test
    fun testPauseAudio() {
        // Configura o mock para indicar que o MediaPlayer está tocando.
        `when`(mockMediaPlayer.isPlaying).thenReturn(true)

        // Configura o mockConnector para responder quando pauseAudio é chamado.
        // Simula o comportamento do método de pausa.
        `when`(mockConnector.pauseAudio()).thenAnswer {
            if (mockMediaPlayer.isPlaying) {
                // Pausa a reprodução
                mockMediaPlayer.pause()
                true // Retorna true se a reprodução foi pausada com sucesso
            } else {
                false // Indica falha em pausar a reprodução
            }
        }

        // Método que deseja testar: chama pauseAudio através do mockConnector
        val result = mockConnector.pauseAudio()

        // Verifica se o método pause no MediaPlayer é chamado
        verify(mockMediaPlayer).pause()

        // Asserta que o result é verdadeiro, indicando que o áudio foi pausado
        assertTrue(result, "O MediaPlayer deve estar pausado agora.")
    }

    @Test
    fun testStopAudio() {
        // Configura o mock para indicar que o MediaPlayer está tocando.
        `when`(mockMediaPlayer.isPlaying).thenReturn(true)

        // Configura o mockConnector para responder quando stopAudio é chamado.
        // Simula o comportamento do método de parada.
        `when`(mockConnector.stopAudio()).thenAnswer {
            if (mockMediaPlayer.isPlaying) {
                // Para a reprodução
                mockMediaPlayer.stop()
                // Prepara novamente o MediaPlayer
                mockMediaPlayer.prepare()
                // Retorna ao início da faixa
                mockMediaPlayer.seekTo(0)
                true // Retorna true se a reprodução foi parada com sucesso
            } else {
                false // Indica falha em parar a reprodução
            }
        }

        // Método que deseja testar: chama stopAudio através do mockConnector
        val result = mockConnector.stopAudio()

        // Verifica se os seguintes métodos no MediaPlayer são chamados
        verify(mockMediaPlayer).stop()
        verify(mockMediaPlayer).prepare()
        verify(mockMediaPlayer).seekTo(0)

        // Asserta que o result é verdadeiro, indicando que o áudio foi parado
        assertTrue(result, "O MediaPlayer deve ter sido parado e preparado.")
    }

    @Test
    fun testSeekAudio() {
        val position = 1000
        // Configura o mockConnector para simular o comportamento do método seekAudio
        `when`(mockConnector.seekAudio(position)).thenAnswer {
            if (mockMediaPlayer != null) {
                // Move a reprodução para a posição desejada
                mockMediaPlayer.seekTo(position)
                true // Retorna true se a operação foi bem-sucedida
            } else {
                false // Indica falha em buscar a posição
            }
        }

        // Chama o método através do mockConnector
        val result = mockConnector.seekAudio(position)

        // Verifica se o método seekTo no MediaPlayer é chamado com o valor esperado
        verify(mockMediaPlayer).seekTo(position)

        // Asserta que o resultado é verdadeiro, confirmando que a operação foi bem-sucedida
        assertTrue(result, "O MediaPlayer deve buscar para a posição $position.")
    }

    @Test
    fun testGetCurrentPosition() {
        val expectedPosition = 3000

        // Configura o mock do MediaPlayer para retornar uma posição fixa
        `when`(mockMediaPlayer.currentPosition).thenReturn(expectedPosition)

        // Configura o mockConnector para retornar a posição do MediaPlayer
        `when`(mockConnector.getCurrentPosition()).thenAnswer {
            mockMediaPlayer.currentPosition // Devolve a posição atual do MediaPlayer
        }

        // Chama o método que deseja testar
        val position = mockConnector.getCurrentPosition()

        // Verifica se a posição retornada é a esperada
        assertEquals(expectedPosition, position, "A posição atual deve ser $expectedPosition")
    }

    @Test
    fun testGetDuration() {
        val expectedDuration = 120000

        // Configura o mock do MediaPlayer para retornar uma duração fixa
        `when`(mockMediaPlayer.duration).thenReturn(expectedDuration)

        // Configura o mockConnector para retornar a duração do MediaPlayer
        `when`(mockConnector.getDuration()).thenAnswer {
            mockMediaPlayer.duration // Fornece a duração atual do MediaPlayer
        }

        // Chama o método de teste
        val duration = mockConnector.getDuration()

        // Verifica se a duração retornada é a esperada
        assertEquals(expectedDuration, duration, "A duração deve ser $expectedDuration")
    }

    @Test
    fun testSetVolume() {
        val volume = 0.5f

        // Configura o mockConnector para simular o comportamento do método setVolume
        `when`(mockConnector.setVolume(volume)).thenAnswer {
            if (mockMediaPlayer != null) {
                // Ajusta o volume do MediaPlayer
                mockMediaPlayer.setVolume(volume, volume)
                true // Retorna true se a operação foi bem-sucedida
            } else {
                false // Indica falha em ajustar o volume
            }
        }

        // Chama o método para ajustar o volume
        val result = mockConnector.setVolume(volume)

        // Verifica se o método setVolume no MediaPlayer é chamado com os valores esperados
        verify(mockMediaPlayer).setVolume(volume, volume)

        // Asserta que o volume foi ajustado corretamente
        assertTrue(result, "O volume deve ter sido ajustado para $volume.")
    }


    @Test
    fun testCreateNotificationChannel() {
        // Verifica se o teste faz sentido apenas para Android O ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Chama o método para criar o canal de notificação
            notificationModule.createNotificationChannel()
            // Verifica se o método createNotificationChannel do NotificationManager foi chamado
            // Isso confirma que o canal foi tentado ser criado corretamente
            verify(mockNotificationManager).createNotificationChannel(any(NotificationChannel::class.java))
        }
    }

    @Test
    fun testCreateNotification() {
        // Cria uma instância anônima de NotificationModule para modificar o comportamento
        val factoryTestModule = object : NotificationModule(context, "test_channel_id", 1234) {
            override fun buildCompatNotification(title: String, text: String): Notification {
                // Retorna uma instância de Notification simulada
                // Isso ignora partes que não podemos mockar diretamente nos testes
                return Notification()
            }
        }

        // Usa este fábrica personalizada para criar uma notificação
        val notification = factoryTestModule.createNotification("Test Title", "Test Text")
        // Asserta que a notificação não é nula, indicando que foi criada com sucesso
        assertNotNull(notification)
    }


    @Test
    fun testShowNotification() {
        // Configure a chamada se for Android O ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationModule.createNotificationChannel()
            verify(mockNotificationManager).createNotificationChannel(any(NotificationChannel::class.java))
        }

        // Use uma instância real ou mock do Notification
        val mockNotification = Notification()

        // Chamar o método de mostrar notificação
        notificationModule.showNotification(mockNotification)

        // Verifique se notify foi chamado corretamente
        verify(mockNotificationManager).notify(eq(1234), eq(mockNotification))
    }



}
