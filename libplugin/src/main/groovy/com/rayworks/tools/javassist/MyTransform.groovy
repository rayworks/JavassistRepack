package com.rayworks.tools.javassist

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.Project

class MyTransform extends Transform {

    private final Project project;
    private final boolean application;

    public MyTransform(Project project, boolean isApplication) {
        this.project = project;
        this.application = isApplication;
    }

    @Override
    String getName() {
        return "libplugin";
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS //all classes
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return application ?
                TransformManager.SCOPE_FULL_PROJECT
                : Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT)
    }

    /**
     * classpath needed
     */
    @Override
    Set<? super QualifiedContent.Scope> getReferencedScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false; //gradleExtension.
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {

        println(">>> transform entry")

        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput dirInput ->

                MyInject.injectOnCreate(dirInput.file.absolutePath, project)

                def dest = outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes,
                        dirInput.scopes, Format.DIRECTORY)

                FileUtils.copyDirectory(dirInput.file, dest)
            }

            input.jarInputs.each { JarInput jarInput ->
                File jar = new File(jarInput.getFile().getPath())
                GetuiSdkInject.removeJniCheck(jar)
            }
        }

    }
}