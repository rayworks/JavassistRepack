package com.rayworks.tools.javassist

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project


class DroidAssistPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        def androidPlugin = ['android', 'com.android.application', 'android-library', 'com.android.library',
                             'com.android.test', 'com.android.feature']
                .collect { project.plugins.findPlugin(it) as Plugin }
                .find { it != null }

        println('Found Plugin: ' + androidPlugin)

        if (androidPlugin == null) {
            throw new GradleException('You must apply the Android plugin or the Android library plugin before using the groovy-android plugin')
        }

        if (project.plugins.hasPlugin(AppPlugin.class)) {
            AppExtension extension = project.extensions.getByType(AppExtension)
            extension.registerTransform(
                    new JniTransform(project, true))

            println(">>> AppPlugin registered")
        }
        if (project.plugins.hasPlugin(LibraryPlugin.class)) {
            LibraryExtension extension = project.extensions.getByType(LibraryExtension)
            extension.registerTransform(
                    new JniTransform(project, false))
        }

    }
}