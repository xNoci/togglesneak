plugins {
    id("java-library")
    id("net.labymod.gradle")
    id("net.labymod.gradle.addon")
    id("org.cadixdev.licenser") version ("0.6.1")
}

group = "org.example"
version = "1.0.0"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

labyMod {
    defaultPackageName = "net.labymod.addons.togglesneak" //change this to your main package name (used by all modules)
    addonInfo {
        namespace = "togglesneak"
        displayName = "ToggleSneak"
        author = "LabyMedia GmbH"
        version = System.getenv().getOrDefault("VERSION", "0.0.1")
    }

    minecraft {
        registerVersions("1.8.9", "1.12.2", "1.20.1") { version, provider ->
            configureRun(provider, version)
        }

        subprojects.forEach {
            if (it.name != "game-runner") {
                filter(it.name)
            }
        }
    }

    addonDev {
        //localRelease()
        internalRelease()
    }
}

subprojects {
    plugins.apply("java-library")
    plugins.apply("net.labymod.gradle")
    plugins.apply("net.labymod.gradle.addon")
    plugins.apply("org.cadixdev.licenser")

    repositories {
        maven("https://libraries.minecraft.net/")
        maven("https://repo.spongepowered.org/repository/maven-public/")
        mavenLocal()
    }

    license {
        header(rootProject.file("gradle/LICENSE-HEADER.txt"))
        newLine.set(true)
    }
}

fun configureRun(provider: net.labymod.gradle.core.minecraft.provider.VersionProvider, gameVersion: String) {
    provider.runConfiguration {
        mainClass = "net.minecraft.launchwrapper.Launch"
        jvmArgs("-Dnet.labymod.running-version=${gameVersion}")
        jvmArgs("-Dmixin.debug=true")
        jvmArgs("-Dnet.labymod.debugging.all=true")
        jvmArgs("-Dmixin.env.disableRefMap=true")

        if (org.gradle.internal.os.OperatingSystem.current() == org.gradle.internal.os.OperatingSystem.MAC_OS) {
            jvmArgs("-XstartOnFirstThread")
        }

        args("--tweakClass", "net.labymod.core.loader.vanilla.launchwrapper.LabyModLaunchWrapperTweaker")
        args("--labymod-dev-environment", "true")
        args("--addon-dev-environment", "true")
    }

    provider.javaVersion = when (gameVersion) {
        else -> {
            JavaVersion.VERSION_17
        }
    }

    provider.mixin {
        val mixinMinVersion = when (gameVersion) {
            "1.8.9", "1.12.2", "1.16.5" -> {
                "0.6.6"
            }

            else -> {
                "0.8.2"
            }
        }

        minVersion = mixinMinVersion
    }
}
