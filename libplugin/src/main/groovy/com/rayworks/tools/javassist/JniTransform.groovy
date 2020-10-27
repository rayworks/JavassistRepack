package com.rayworks.tools.javassist

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project

class JniTransform extends Transform {

    private final Project project;
    private final boolean application;

    public JniTransform(Project project, boolean isApplication) {
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
//        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)

        println(">>> transform entry")

        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput dirInput ->

                MyInject.injectOnCreate(dirInput.file.absolutePath, project)

                def dest = outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes,
                        dirInput.scopes, Format.DIRECTORY)

                FileUtils.copyDirectory(dirInput.file, dest)
            }
            println(">>> jar size : " + input.jarInputs.size())

            input.jarInputs.each { JarInput jarInput ->

                def jarName = jarInput.name
                println("jar name : $jarName")

//                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
//                if (jarName.endsWith(".jar")) {
//                    jarName = jarName.substring(0, jarName.length() - 4)
//                }
//                def dest = outputProvider.getContentLocation(jarName + md5Name,
//                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
//                FileUtils.copyFile(jarInput.file, dest)
            }
        }

    }
}