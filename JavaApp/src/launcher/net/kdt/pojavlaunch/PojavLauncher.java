package net.kdt.pojavlaunch;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.beans.Beans;
import net.kdt.pojavlaunch.uikit.UIKit;

public class PojavLauncher {
    public static void main(String[] args) throws Throwable {
        Beans.setDesignTime(true);
        try {
            // Fix for Java 21 UI initialization
            com.apple.eawt.Application.getApplication();
            Class clazz = Class.forName("com.apple.eawt.Application");
            Field field = clazz.getDeclaredField("sApplication");
            field.setAccessible(true);
            field.set(null, null);

            // Redirect preferences to avoid the 'chmod' crash
            System.setProperty("java.util.prefs.userRoot", System.getProperty("user.dir"));
            System.setProperty("java.util.prefs.PreferencesFactory", "java.util.prefs.FileSystemPreferencesFactory");
        } catch (Throwable th) {
            // Ignore UI prep errors
        }

        Thread.currentThread().setUncaughtExceptionHandler((t, th) -> {
            th.printStackTrace();
            System.exit(1);
        });

        // Initialize the bridge
        try {
            Class.forName("com.github.caciocavallosilano.cacio.ctc.CTCPreloadClassLoader");
        } catch (ClassNotFoundException e) {}

        // Start Spiral Knights
        launchMinecraft(args);
    }

    public static void launchMinecraft(String[] args) throws Throwable {
        String gameDir = System.getProperty("user.dir");

        // Mandatory Graphics Bridge Fix
        String sizeStr = System.getProperty("cacio.managed.screensize");
        if (sizeStr == null) sizeStr = "1132x744"; 
        System.setProperty("glfw.windowSize", sizeStr);

        // Core Properties
        System.setProperty("os.name", "Mac OS X");
        System.setProperty("appdir", gameDir);
        System.setProperty("resource_dir", gameDir + "/rsrc");
        System.setProperty("crucible.dir", gameDir + "/crucible");
        System.setProperty("com.threerings.getdown", "true");
        System.setProperty("no_update", "true");
        
        // Crash/Graphics Fixes
        System.setProperty("pojav.internal.skipSetIcon", "true");
        System.setProperty("lwjgl.util.NoChecks", "true");
        System.setProperty("org.lwjgl.opengl.disableStaticInit", "true");
        System.setProperty("com.threerings.opengl.no_shaders", "true");
        System.setProperty("org.lwjgl.vulkan.libname", "libMoltenVK.dylib");

        String skMainClass = "com.threerings.projectx.client.ProjectXApp";
        Tools.launchSpiral(skMainClass, new String[0]);
    }
}