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

        externalNativeBuild {
            cmake {
                arguments.add("-DANDROID_STL=c++_shared")
                cppFlags.add("-std=c++11")
                cppFlags.add("-fexceptions")
                // cppFlags.remove("-nostdinc++")
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
            path = File("src/main/jni/CMakeLists.txt")
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.lottie)
    implementation(libs.material)

    implementation(libs.androidx.junit.ktx)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.robolectric)
    androidTestImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.androidx.test.core)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(kotlin("test"))


}

tasks.withType<Test> {
    useJUnitPlatform() // Isso garante que o Gradle sabe para usar JUnit 5
}
