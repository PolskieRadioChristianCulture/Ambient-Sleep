plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
}

// Gradle Dynamic Asset Syncing
val syncAssets = project.objects.property(Boolean::class.java).getOrElse(true)
if (syncAssets) {
    val targetRes = file("src/main/res/drawable")
    if (targetRes.exists()) {
        println("=== Syncing User Assets ===")
        val rootDir = project.rootDir
        val rootFiles = rootDir.listFiles() ?: emptyArray()
        
        var foundBanner: File? = null
        var foundLogo: File? = null
        
        // 1. Search for specific files first (exact match)
        for (f in rootFiles) {
            val name = f.name
            if (f.isFile && f.length() > 0) {
                if (name == "grafika apki Ambient  (2).jpg") {
                    foundBanner = f
                }
                if (name == "Logo ambient  sleep.jpg" && foundLogo == null) {
                    foundLogo = f
                }
            }
        }
        
        // 2. Fallbacks for banner
        if (foundBanner == null) {
            for (f in rootFiles) {
                if (f.isFile && f.length() > 0 && f.name == "grafika apki Ambient .jpg") {
                    foundBanner = f
                    break
                }
            }
        }
        if (foundBanner == null) {
            for (f in rootFiles) {
                val lower = f.name.lowercase()
                if (f.isFile && f.length() > 0 && lower.contains("grafika") && lower.endsWith(".jpg")) {
                    foundBanner = f
                    break
                }
            }
        }
        
        // 3. Fallbacks for logo
        if (foundLogo == null) {
            for (f in rootFiles) {
                if (f.isFile && f.length() > 0 && f.name == "Ambient Sleep.jpg") {
                    foundLogo = f
                    break
                }
            }
        }
        if (foundLogo == null) {
            for (f in rootFiles) {
                val lower = f.name.lowercase()
                if (f.isFile && f.length() > 0 && lower.contains("logo") && lower.endsWith(".jpg") && !lower.contains("original")) {
                    foundLogo = f
                    break
                }
            }
        }
        
        if (foundBanner != null) {
            val dest = file("src/main/res/drawable/ambient_sleep_hero.jpg")
            println("Syncing banner graphic from: ${foundBanner.absolutePath} -> ambient_sleep_hero.jpg")
            foundBanner.copyTo(dest, overwrite = true)
        } else {
            println("No banner graphic matching 'grafika' found in project root.")
        }
        
        if (foundLogo != null) {
            val dest = file("src/main/res/drawable/logo_ambient_sleep.jpg")
            println("Syncing app logo from: ${foundLogo.absolutePath} -> logo_ambient_sleep.jpg")
            foundLogo.copyTo(dest, overwrite = true)
        } else {
            println("No logo matching 'logo' or 'Ambient Sleep.jpg' found in project root.")
        }
        
        println("=== User Assets Sync Completed ===")
    }
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.ambientsleep.yzkcjw"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation("androidx.media:media:1.7.0")
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  // implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  // implementation(libs.firebase.ai)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)

  // Media3 (ExoPlayer) and Google AdMob
  implementation(libs.androidx.media3.exoplayer)
  implementation(libs.androidx.media3.session)
  implementation(libs.androidx.media3.common)
  implementation(libs.play.services.ads)
}




