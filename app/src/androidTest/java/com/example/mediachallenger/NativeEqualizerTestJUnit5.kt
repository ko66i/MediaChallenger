package com.example.mediachallenger

import androidx.test.ext.junit.runners.AndroidJUnit4 // Importe o runner para testes Android (instrumentação)
import org.junit.jupiter.api.Assertions.assertEquals // Importe as asserções do JUnit 5
import org.junit.jupiter.api.Assertions.assertNotNull // Importe as asserções do JUnit 5
import org.junit.jupiter.api.Assertions.assertArrayEquals // Importe as asserções do JUnit 5 para arrays
import org.junit.jupiter.api.BeforeEach // Importe a anotação BeforeEach do JUnit 5
import org.junit.jupiter.api.Test // Importe a anotação Test do JUnit 5
import org.junit.runner.RunWith // Necessário para @RunWith

// Importe a classe EqualizationModule (Java ou Kotlin, no pacote correto)
import com.example.mediachallenger.equalization.EqualizationModule

// Se precisar mockar o MediaPlayer para o construtor do EqualizationModule:
// import org.mockito.Mockito.mock
// import org.mockito.MockitoAnnotations
// import org.mockito.Mock
// import android.media.MediaPlayer


/**
 * Testes de instrumentação para a funcionalidade de equalização nativa (applyEqualizationNative).
 * Estes testes rodam em um dispositivo Android ou emulador, usando JUnit 5.
 * Eles verificam se o código nativo C++ é executado corretamente através do wrapper Java/Kotlin.
 */
@RunWith(AndroidJUnit4::class) // Use o runner de testes de instrumentação para rodar em ambiente Android
class NativeEqualizerTestJUnit5 { // Nome da classe de teste (pode ser o que você preferir)

    // Instância da classe que contém a lógica nativa JNI
    private lateinit var equalizationModule: EqualizationModule

    // Se você precisar mockar o MediaPlayer para o construtor do EqualizationModule:
    // @Mock
    // private lateinit var mockMediaPlayer: MediaPlayer


    // Use @BeforeEach para inicializar a instância antes de cada teste, assim como no seu AudioServiceTest
    @BeforeEach
    fun setUp() {
        // Se você precisar mockar o MediaPlayer:
        // MockitoAnnotations.openMocks(this)
        // equalizationModule = EqualizationModule(mockMediaPlayer) // Passe o mock

        // Se o construtor do seu EqualizationModule Java aceitar null para MediaPlayer (simplificado para testar apenas o nativo):
        equalizationModule = EqualizationModule(null) // Se o construtor aceitar null

        // Verifique se a instância foi criada e, implicitamente, se a biblioteca nativa carregou
        // Se a biblioteca não carregar, o construtor pode lançar uma exceção, ou a instância pode ser nula
        // dependendo de como você trata o erro de carregamento na sua classe EqualizationModule.
        // A asserção assertNotNull aqui ajuda a verificar se a instância foi criada com sucesso.
        assertNotNull(equalizationModule, "A instância de EqualizationModule não deve ser nula. A biblioteca nativa pode não ter carregado.")
    }


    /**
     * Testa a aplicação de um ganho básico usando o código nativo.
     */
    @Test
    fun testApplyEqualizationNative_basicGain() {
        // Arrange (Preparar): Crie os dados de entrada controlados
        val initialAudioData = shortArrayOf(100, 200, 50, -150, 0, 300) // Dados de áudio de exemplo
        val gains = intArrayOf(2000) // Ganho de 2000 (multiplica por 2.0)

        // Crie uma CÓPIA dos dados iniciais porque a função nativa irá modificá-los IN-PLACE.
        val audioDataToProcess = initialAudioData.copyOf()

        // Calcule o resultado esperado APÓS a aplicação do ganho.
        // Baseado no seu código C++: cada sample é multiplicado por (gains[0] / 1000.0).
        val gainMultiplier = gains[0] / 1000.0
        val expectedAudioData = initialAudioData.map { (it * gainMultiplier).toInt().toShort() }.toShortArray()
        // Nota: A conversão .toShort() pode causar overflow/underflow se o resultado exceder o range de Short.
        // Certifique-se de que seus dados de teste e ganhos não causem isso, ou adicione testes específicos para overflow.

        // Act (Executar): Chame o método wrapper que interage com o código nativo.
        // O método applyEqualization na sua classe EqualizationModule Java retorna void.
        // A função nativa C++ retorna um int (o número de samples).
        // Se você quiser testar o retorno da função nativa, modifique seu método applyEqualization
        // em Java/Kotlin para retornar o int da chamada nativa.
        // Por enquanto, testamos apenas a modificação do array, já que o método wrapper retorna void.
        equalizationModule.applyEqualization(audioDataToProcess, gains)


        // Assert (Verificar): Compare os dados modificados com o resultado esperado.

        // Verifique se o array audioDataToProcess foi modificado corretamente pela função nativa.
        // assertArrayEquals do JUnit 5 é ideal para comparar arrays.
        assertArrayEquals(expectedAudioData, audioDataToProcess, "O array de áudio processado não corresponde ao valor esperado após um ganho básico.")

        // Se você modificar applyEqualization para retornar o int nativo, adicione esta asserção:
        // assertEquals(initialAudioData.size, processedCount, "O número de samples processados deve ser igual ao número de samples de entrada.")


        println("Teste testApplEqualizationNative_basicGain executado e passou!")
    }

    /**
     * Testa a aplicação de múltiplos ganhos sequenciais usando o código nativo.
     */
    @Test
    fun testApplyEqualizationNative_multipleGains() {
        // Arrange
        val initialAudioData = shortArrayOf(100, 200, -50, 120)
        val gains = intArrayOf(2000, 500, 1000) // Ganho 1=2.0, Ganho 2=0.5, Ganho 3=1.0

        val audioDataToProcess = initialAudioData.copyOf()

        // Calcule o resultado esperado: cada sample é multiplicado sequencialmente por TODOS os ganhos.
        // Multiplicador total = (gains[0]/1000.0) * (gains[1]/1000.0) * ...
        var totalGainMultiplier = 1.0
        for (gain in gains) {
            totalGainMultiplier *= (gain / 1000.0)
        }
        // Neste exemplo: 2.0 * 0.5 * 1.0 = 1.0
        // Então, o array esperado deve ser igual ao original.

        val expectedAudioData = initialAudioData.map {
            (it * totalGainMultiplier).toInt().toShort()
        }.toShortArray()


        // Act
        // Chamando o método applyEqualization que chama o nativo (assume que retorna void)
        equalizationModule.applyEqualization(audioDataToProcess, gains)


        // Assert
        // Verificar se o array foi modificado corretamente (ou não modificado, neste caso específico)
        assertArrayEquals(expectedAudioData, audioDataToProcess, "O array de áudio processado não corresponde ao valor esperado com múltiplos ganhos.")

        // Se applyEqualization retornasse o int nativo:
        // assertEquals(initialAudioData.size, processedCount, "O número de samples processados deve ser igual ao número de samples de entrada.")

        println("Teste testApplyEqualizationNative_multipleGains executado e passou!")
    }

    /**
     * Testa o comportamento do código nativo quando o array de ganhos está vazio.
     * O loop de ganhos no C++ não deve rodar, e o array de áudio não deve ser modificado.
     */
    @Test
    fun testApplyEqualizationNative_zeroGains() {
        // Arrange
        val initialAudioData = shortArrayOf(100, 200)
        val gains = intArrayOf() // Array de ganhos vazio

        val audioDataToProcess = initialAudioData.copyOf()

        // Calcule o esperado: se não há ganhos, o loop interno no C++ não roda.
        // O array de áudio NÃO deve ser modificado pela função nativa.
        val expectedAudioData = initialAudioData.copyOf() // Espera-se que seja igual ao original

        // Act
        // Chamando o método applyEqualization que chama o nativo.
        // Pela sua implementação Java, ele tem um check para gains.length == 0 e retorna cedo.
        // Isso significa que a chamada nativa applyEqualizationNative *não* ocorrerá.
        // O teste verifica o comportamento do método wrapper applyEqualization.
        equalizationModule.applyEqualization(audioDataToProcess, gains)


        // Assert
        // Verificar se o array de áudio permanece inalterado.
        assertArrayEquals(expectedAudioData, audioDataToProcess, "O array de áudio não deve ser modificado quando não há ganhos (wrapper não chama nativo).")

        // Nota: Se você modificar o método applyEqualization no EqualizationModule
        // para *sempre* chamar o nativo, mesmo com arrays vazios, e testar o retorno,
        // o retorno esperado da função nativa C++ com gains vazios seria o numSamples
        // original (pois o loop j não roda). O teste precisaria ser ajustado para isso.


        println("Teste testApplyEqualizationNative_zeroGains executado e passou!")
    }

    /**
     * Testa o comportamento do código nativo quando o array de áudio está vazio.
     * O loop de samples no C++ não deve rodar, e o retorno deve ser 0.
     */
    @Test
    fun testApplyEqualizationNative_emptyAudioData() {
        // Arrange
        val initialAudioData = shortArrayOf() // Array de áudio vazio
        val gains = intArrayOf(2000) // Ganhos

        val audioDataToProcess = initialAudioData.copyOf() // Cópia de um array vazio é um array vazio

        // Calcule o esperado: se não há samples, o loop externo no C++ não roda.
        // O retorno esperado da função nativa C++ é 0 (o tamanho do array vazio).
        // Pela sua implementação Java de applyEqualization, ele tem um check para audioData.length == 0 e retorna cedo.
        // Isso significa que a chamada nativa applyEqualizationNative *não* ocorrerá.
        // O teste verifica o comportamento do método wrapper applyEqualization.
        val expectedAudioData = shortArrayOf() // Espera-se que continue vazio


        // Act
        // Chamando o método applyEqualization que chama o nativo.
        // Pela sua implementação Java, ele vai retornar cedo se audioData estiver vazio.
        // A chamada nativa *não* ocorrerá neste caso.
        equalizationModule.applyEqualization(audioDataToProcess, gains)


        // Assert
        // Verificar se o array de áudio permanece vazio.
        assertArrayEquals(expectedAudioData, audioDataToProcess, "O array de áudio deve permanecer vazio quando a entrada está vazia (wrapper não chama nativo).")

        // Nota: Se você modificar o método applyEqualization no EqualizationModule
        // para *sempre* chamar o nativo, mesmo com arrays vazios, e testar o retorno,
        // o retorno esperado da função nativa C++ com audioData vazio seria 0.
        // O teste precisaria ser ajustado para isso.

        println("Teste testApplyEqualizationNative_emptyAudioData executado e passou!")
    }

    // Adicione mais testes para cobrir outros cenários, como:
    // - Ganhos negativos
    // - Ganho zero (multiplicador 0.0)
    // - Valores de áudio que resultem em overflow/underflow após a multiplicação (se aplicável)
    // - Arrays com tamanhos grandes

}
