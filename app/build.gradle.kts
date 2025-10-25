import java.util.Properties

plugins {
    id("com.android.application")
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}
val geminiApiKey: String = localProps.getProperty("GEMINI_API_KEY") ?: ""
val n8nBaseUrl: String = localProps.getProperty("N8N_BASE_URL") ?: ""
val sharedSecret: String = localProps.getProperty("SHARED_SECRET") ?: ""
val favoritesWebhookUrl: String = localProps.getProperty("FAVORITES_WEBHOOK_URL") ?: ""

android {
    namespace = "com.compufire.recomendacionesdeproductosyservicios"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.compufire.recomendacionesdeproductosyservicios"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Exponer la API key y configuración de n8n a BuildConfig para TODAS las variantes
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        buildConfigField("String", "N8N_BASE_URL", "\"$n8nBaseUrl\"")
        buildConfigField("String", "SHARED_SECRET", "\"$sharedSecret\"")
        buildConfigField("String", "FAVORITES_WEBHOOK_URL", "\"$favoritesWebhookUrl\"")
    }

    // Generar BuildConfig.java (necesario para GEMINI_API_KEY)
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            // si quieres logs de red, el interceptor está en dependencies
            isMinifyEnabled = false
            // también exponer explícitamente para debug (redundante pero claro)
            buildConfigField("String", "N8N_BASE_URL", "\"$n8nBaseUrl\"")
            buildConfigField("String", "SHARED_SECRET", "\"$sharedSecret\"")
            buildConfigField("String", "FAVORITES_WEBHOOK_URL", "\"$favoritesWebhookUrl\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "N8N_BASE_URL", "\"$n8nBaseUrl\"")
            buildConfigField("String", "SHARED_SECRET", "\"$sharedSecret\"")
            buildConfigField("String", "FAVORITES_WEBHOOK_URL", "\"$favoritesWebhookUrl\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Compat bajado a 1.6.1 para evitar incompatibilidades
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")

    // Fragments/Activity (útil para Navigation en Java)
    implementation("androidx.fragment:fragment:1.8.2")
    implementation("androidx.activity:activity:1.9.2")

    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.4")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.4")
    implementation("androidx.work:work-runtime:2.9.1")

    // Room (Java)
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Retrofit / OkHttp (Gson + logging)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Glide (imágenes)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Jetpack Navigation (Java)
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
