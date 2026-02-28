package net.kdt.pojavlaunch;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.beans.Beans;
import net.kdt.pojavlaunch.uikit.UIKit;

public class PojavLauncher {
    public static void main(String[] args) throws Throwable {
        // RESTORE: This specific logic makes your side menu and touch work
        Beans.setDesignTime(true);
        try {
            com.apple.eawt.Application.getApplication();
            Class clazz = Class.forName("com.apple.eawt.Application");
            Field field = clazz.getDeclaredField("sApplication");
            field.setAccessible(true);
            field.set(null, null);
            // This is the line that fixed your UI before
            sun.font.FontUtilities.isLinux = true;
        } catch (Throwable th) { }

        // FIX: This property stops the 'chmod' crash on iOS 16
        System.setProperty("java.util.prefs.userRoot", System.getProperty("user.dir"));

        Thread.currentThread().setUncaughtExceptionHandler((t, th) -> {
            th.printStackTrace();
            System.exit(1);
        });

        try {
            Class.forName("com.github.caciocavallosilano.cacio.ctc.CTCPreloadClassLoader");
        } catch (ClassNotFoundException e) {}

        launchMinecraft(args);
    }

    public static void launchMinecraft(String[] args) throws Throwable {
        String gameDir = System.getProperty("user.dir");

        // UI & Window setup
        String sizeStr = System.getProperty("cacio.managed.screensize");
        if (sizeStr == null) sizeStr = "1132x744"; 
        System.setProperty("glfw.windowSize", sizeStr);
        System.setProperty("UIScreen.maximumFramesPerSecond", "60");

        // 1.5 PERFORMANCE MODE: Tell the game we are an old GPU
        // This forces the game to use simple code that the iPad can run fast.
        System.setProperty("os.name", "Mac OS X");
        System.setProperty("gl4es.version", "1.5");
        System.setProperty("com.threerings.opengl.no_shaders", "true");
        
        // Crash Fixes
        System.setProperty("pojav.internal.skipSetIcon", "true");
        System.setProperty("lwjgl.util.NoChecks", "true");

        // SK Paths
        System.setProperty("appdir", gameDir);
        System.setProperty("resource_dir", gameDir + "/rsrc");
        System.setProperty("crucible.dir", gameDir + "/crucible");
        System.setProperty("com.threerings.getdown", "true");
        System.setProperty("no_update", "true");
        System.setProperty("jinput.useDefaultPlugin", "false");
        
        System.setProperty("org.lwjgl.opengl.disableStaticInit", "true");
        System.setProperty("org.lwjgl.vulkan.libname", "libMoltenVK.dylib");

        Tools.launchSpiral("com.threerings.projectx.client.ProjectXApp", new String[0]);
    }
}