package com.example.mediachallenger

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mediachallenger.equalization.EqualizationModule

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.jupiter.api.Assertions

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AudioPlayerEqualizerTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.mediachallenger", appContext.packageName)
    }

    private lateinit var equalizationModule: EqualizationModule

    @Test
    fun testapplyEqualization() {

        equalizationModule = EqualizationModule(null)

        Assertions.assertNotNull(
            equalizationModule,
            "A instância de EqualizationModule não deve ser nula. A biblioteca nativa pode não ter carregado."
        )

        val initialAudioData = shortArrayOf(100, 200)
        val gains = intArrayOf() // Array de ganhos vazio
        val audioDataToProcess = initialAudioData.copyOf()

        // Calcule o esperado: se não há ganhos, o loop interno no C++ não roda.
        val expectedAudioData = initialAudioData.copyOf() // Espera-se que seja igual ao original

        // Act
        // Chamando o método applyEqualization que chama o nativo.
        // O teste verifica o comportamento do método wrapper applyEqualization.
        equalizationModule.applyEqualization(audioDataToProcess, gains)

        // Assert
        // Verificar se o array de áudio permanece inalterado.
        Assertions.assertArrayEquals(
            expectedAudioData,
            audioDataToProcess,
            "O array de áudio não deve ser modificado quando não há ganhos (wrapper não chama nativo)."
        )

        println("Teste testApplyEqualizationNative_zeroGains executado e passou!")
    }
}