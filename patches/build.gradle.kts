group = "dev.dyno0311"

patches {
    // This name is what shows up as the patch source in Morphe Manager.
    // Keep it clearly distinct from "Morphe" itself and from other bundles.
    about {
        name = "Dyno Patches"
        description = "Personal Android patches, compatible with Morphe."
        source = "https://github.com/Dyno0311/dyno-patches.git"
        author = "Dyno0311"
        contact = "https://github.com/Dyno0311/dyno-patches/issues"
        website = "https://github.com/Dyno0311/dyno-patches"
        license = "GPLv3"
    }
}

// Separate configuration so gson is available at runtime for the
// generatePatchesList task but never bundled into the APK.
val patchListGeneratorClasspath = configurations.create("patchListGeneratorClasspath")

dependencies {
    compileOnly(libs.gson)
    patchListGeneratorClasspath(libs.gson)
}

tasks {
    register<JavaExec>("generatePatchesList") {
        description = "Build patch with patch list"

        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath + patchListGeneratorClasspath
        mainClass.set("util.PatchListGeneratorKt")
    }

    // Used by gradle-semantic-release-plugin.
    publish {
        dependsOn("generatePatchesList")
    }
}
