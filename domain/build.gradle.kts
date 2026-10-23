import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    id("jacoco")
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.famessa.deli_antojito.domain"
    compileSdk = 35

    defaultConfig {
        minSdk = 25
    }

    buildTypes {
        debug { enableUnitTestCoverage = true }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Domain should be pure Kotlin, avoiding Android dependencies
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

// Include domain behavior exercised through the presentation unit tests as well as domain unit tests.
jacoco { toolVersion = libs.versions.jacoco.get() }
tasks.register<JacocoReport>("ordersDomainCoverage") {
    dependsOn(":domain:testDebugUnitTest", ":feature:orders:testDebugUnitTest")
    executionData.setFrom(files(
        layout.buildDirectory.file("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"),
        rootProject.layout.projectDirectory.file("feature/orders/build/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    ))
    classDirectories.setFrom(fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
        include("**/model/Money*", "**/model/Pedido*", "**/model/EstadoPedido*", "**/model/Resumen*",
            "**/usecase/pedido/**", "**/time/**")
    })
    sourceDirectories.setFrom(files("src/main/java"))
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

// Dedicated report: historical query domain behavior exercised directly and through its ViewModel.
tasks.register<JacocoReport>("batchClosingDomainCoverage") {
    dependsOn(":domain:testDebugUnitTest", ":feature:batch-closing:testDebugUnitTest")
    executionData.setFrom(files(
        layout.buildDirectory.file("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"),
        rootProject.layout.projectDirectory.file("feature/batch-closing/build/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    ))
    classDirectories.setFrom(fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
        include("**/model/cierre/**", "**/usecase/cierre/**", "**/time/Cierre*")
    })
    sourceDirectories.setFrom(files("src/main/java"))
    reports { xml.required.set(true); html.required.set(true) }
}
