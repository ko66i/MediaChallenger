import java.io.File

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}


android {
    namespace = "com.example.mediachallenger"
    compileSdk = 35

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }

    defaultConfig {
        applicationId = "com.example.mediachallenger"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Configurações específicas para builds que usam código nativo com CMake
        externalNativeBuild {
            cmake {
                // Argumentos passados para o CMake durante a compilação
                arguments.add("-DANDROID_STL=c++_shared")  // Usa a STL compartilhada no Android NDK

                // Flags extras de compilação em C++ para o compilador
                cppFlags.add("-std=c++11")                 // Define o padrão C++11 para o código nativo
                cppFlags.add("-fexceptions")                // Habilita tratamento de exceções em código C++
            }
        }


    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            externalNativeBuild {
                cmake {
                    //Don't need to declare it here again, is declared on the externalNativeBuild
                }
            }
        }

        debug {
            externalNativeBuild {
                cmake {
                    //Don't need to declare it here again, is declared on the externalNativeBuild
                }
            }
        }

    }

    externalNativeBuild {
        cmake {
            // Define o caminho para o arquivo CMakeLists.txt que gerencia a build do código nativo
            path = File("src/main/jni/CMakeLists.txt")
            // Especifica a versão do CMake a ser utilizada para compilar o código nativo
            version = "3.31.6"
        }
    }
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }

    packagingOptions {
        resources {
            // Exclui arquivos específicos dentro da pasta META-INF que causam conflitos
            // O erro mencionou 'META-INF/LICENSE.md', então vamos excluir esse e alguns outros comuns
            excludes += ("META-INF/LICENSE.md")
            excludes += ("META-INF/LICENSE") // Geralmente existe uma versão sem a extensão .md também
            excludes += ("META-INF/NOTICE.md")
            excludes += ("META-INF/NOTICE")
            excludes += ("META-INF/ASL2.0") // Licença Apache
            excludes += ("META-INF/*.txt") // Pode pegar LICENSE.txt, NOTICE.txt, etc.
            excludes += ("META-INF/*.kotlin_module") // Comum em projetos Kotlin com várias libs
            excludes += ("META-INF/LICENSE-notice.md")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        aidl = true
    }
}


dependencies {

        // Dependências essenciais para o funcionamento do app
        implementation(libs.androidx.core.ktx)               // Extensões Kotlin para APIs AndroidX Core
        implementation(libs.androidx.lifecycle.runtime.ktx)  // Components Lifecycle para gerenciar ciclo de vida com Kotlin
        implementation(libs.androidx.activity.compose)       // Suporte para atividades usando Jetpack Compose
        implementation(platform(libs.androidx.compose.bom))  // BOM para sincronizar versões das bibliotecas Compose
        implementation(libs.androidx.ui)                      // Biblioteca principal da UI do Jetpack Compose
        implementation(libs.androidx.ui.graphics)             // Recursos gráficos para Jetpack Compose
        implementation(libs.androidx.ui.tooling.preview)      // Ferramentas para preview layouts no Android Studio
        implementation(libs.androidx.material3)                // Material Design 3 para Compose
        implementation(libs.androidx.appcompat)                // Compatibilidade retroativa para componentes Android
        implementation(libs.lottie)                            // Animações JSON vetoriais com suporte Lottie
        implementation(libs.material)                          // Componentes Material Design (para Views tradicionais)

        // Dependências para testes unitários e instrumentados
        implementation(libs.androidx.junit.ktx)               // Extensões JUnit para AndroidX
        testImplementation(libs.junit.jupiter.api)            // API JUnit 5 para escrever testes
        testImplementation(libs.junit.jupiter.engine)         // Motor de execução para JUnit 5
        testImplementation(libs.robolectric)                  // Testes offline simulando ambiente Android
        androidTestImplementation(libs.junit.jupiter)          // JUnit 5 para testes instrumentados no dispositivo
        testRuntimeOnly(libs.junit.jupiter.engine)            // Motor JUnit 5 para tempo de execução dos testes
        testImplementation(libs.mockito.kotlin)                // Mocking para testes usando Mockito com extensão Kotlin
        testImplementation(libs.androidx.test.core)            // Núcleo para testes AndroidX

        // Dependências para testes instrumentados (rodando em dispositivo/emulador)
        androidTestImplementation(libs.androidx.junit)         // Suporte para JUnit em testes instrumentados
        androidTestImplementation(libs.androidx.espresso.core) // Framework para testes UI Espresso
        androidTestImplementation(platform(libs.androidx.compose.bom)) // BOM para compor versões de dependências Compose em testes
        androidTestImplementation(libs.androidx.ui.test.junit4)          // Integração Compose UI com JUnit4 para testes instrumentados

        // Dependências específicas para builds de debug (exclusivas no modo debug)
        debugImplementation(libs.androidx.ui.tooling)          // Ferramentas de debug para UI Compose
        debugImplementation(libs.androidx.ui.test.manifest)    // Utilitários para testes para debug no Compose

        // Teste padrão Kotlin
        testImplementation(kotlin("test"))                      // Biblioteca padrão de testes do Kotlin

}

tasks.withType<Test> {
    useJUnitPlatform() // Isso garante que o Gradle sabe para usar JUnit 5
}
