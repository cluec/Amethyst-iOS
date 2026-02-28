package net.kdt.pojavlaunch;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.beans.Beans;
import net.kdt.pojavlaunch.uikit.UIKit;

public class PojavLauncher {

    public static void main(String[] args) throws Throwable {
        // 1. RESTORE ORIGINAL UI BEHAVIOR (Fixes broken side menu & controls)
        Beans.setDesignTime(true);
        try {
            com.apple.eawt.Application.getApplication();
            Class clazz = Class.forName("com.apple.eawt.Application");
            Field field = clazz.getDeclaredField("sApplication");
            field.setAccessible(true);
            field.set(null, null);
            sun.font.FontUtilities.isLinux = true;
            // WE REMOVED the broken "PreferencesFactory" line here to fix the chmod crash!
        } catch (Throwable th) { }

        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable th) {
                th.printStackTrace();
                System.exit(1);
            }
        });

        try {
            Class.forName("com.github.caciocavallosilano.cacio.ctc.CTCPreloadClassLoader");
        } catch (ClassNotFoundException e) {}

        launchMinecraft(args);
    }

    public static void launchMinecraft(String[] args) throws Throwable {
        String gameDir = System.getProperty("user.dir");

        // 2. iOS Bridge Properties
        String sizeStr = System.getProperty("cacio.managed.screensize");
        if (sizeStr == null) sizeStr = "1024x768";
        System.setProperty("glfw.windowSize", sizeStr);
        System.setProperty("UIScreen.maximumFramesPerSecond", "60");

        // 3. HARD DISABLE ALL SHADERS (Fixes graphics & performance)
        System.setProperty("com.threerings.opengl.no_shaders", "true");
        System.setProperty("com.threerings.projectx.no_vertex_shaders", "true");
        System.setProperty("com.threerings.projectx.no_fragment_shaders", "true");
        System.setProperty("com.threerings.projectx.low_spec", "true");
        System.setProperty("com.threerings.opengl.force_low_spec", "true");
        
        // 4. Identity & Paths
        System.setProperty("os.name", "Mac OS X");
        System.setProperty("appdir", gameDir);
        System.setProperty("resource_dir", gameDir + "/rsrc");
        System.setProperty("crucible.dir", gameDir + "/crucible");
        System.setProperty("com.threerings.getdown", "true");
        System.setProperty("no_update", "true");
        System.setProperty("jinput.useDefaultPlugin", "false");
        
        System.setProperty("pojav.internal.skipSetIcon", "true");
        System.setProperty("org.lwjgl.opengl.disableStaticInit", "true");
        System.setProperty("org.lwjgl.vulkan.libname", "libMoltenVK.dylib");

        String skMainClass = "com.threerings.projectx.client.ProjectXApp";
        System.out.println("Launching Spiral Knights...");
        Tools.launchSpiral(skMainClass, new String[0]);
    }
}