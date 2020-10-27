import javassist.ClassPool
import javassist.CtClass
import org.zeroturnaround.zip.ZipUtil

class GetuiSdkInject {
    private static ClassPool classPool = new ClassPool(true)

    static void removeJniCheck(File jar) {
//        File jar = new File(jarInput.getFile().getPath())
        println("path : " + jar.getPath())

        if(jar.getPath().contains("GetuiSDK")) {
            classPool.insertClassPath(jar.getPath())
            classPool.insertClassPath("/Users/rayworks/Library/Android/sdk/platforms/android-30/android.jar")

            // com.igexin.push.util.b.a(context)
            def ctClass = classPool.getCtClass("com.igexin.push.util.b")
            if (ctClass.isFrozen()) {
                println(">>> defrost class ")
                ctClass.defrost()
            }

            def contextCtClass = classPool.getCtClass("android.content.Context")
            def ctMethod = ctClass.getDeclaredMethod("a", [contextCtClass] as CtClass[])

            // NB: There is a fixed logic in SDK to check whether the so files are in
            // `nativeLibraryDir` in debug mode.

            // However this becomes unnecessary for OS 6.0+ :
            // https://stackoverflow.com/questions/42998083/setting-androidextractnativelibs-false-to-reduce-app-size/47541129#47541129

            // "New approach introduced by Google in Marshmallow (Android 6) is enabled by setting
            // extractNativeLibs to "false". It expects the libraries stored uncompressed in the APK
            // (STORE method) and zipaligned. There's no need to extract them during installation.
            // On app startup, the libraries can be loaded directly from the APK."

            // Just override the method body
            ctMethod.setBody("{return;}")

            ZipUtil.replaceEntry(jar, "com/igexin/push/util/b.class", ctClass.toBytecode())

            println(jar.getPath() + " updated")
        }
    }
}