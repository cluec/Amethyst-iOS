package net.kdt.pojavlaunch;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.beans.Beans;
import net.kdt.pojavlaunch.uikit.UIKit;

public class PojavLauncher {
    public static void main(String[] args) throws Throwable {
        System.out.println("SK-Engine: Initializing...");
        Beans.setDesignTime(true);

        // 1. SAFE RESOLUTION LOADING
        // This order ensures we never hit that split() NullPointerException
        String sizeStr = System.getProperty("cacio.managed.screensize");
        if (sizeStr == null) sizeStr = System.getenv("CACIOCAVALLO_SCREEN_SIZE");
        if (sizeStr == null) sizeStr = "1132x744"; // iPad mini 6 fallback

        System.setProperty("cacio.managed.screensize", sizeStr);
        System.setProperty("glfw.windowSize", sizeStr);
        System.setProperty("UIScreen.maximumFramesPerSecond", "60");

        // 2. PREFERENCE FIX (chmod error bypass)
        System.setProperty("java.util.prefs.userRoot", System.getProperty("user.dir"));
        System.setProperty("java.util.prefs.PreferencesFactory", "java.util.prefs.FileSystemPreferencesFactory");

        try {
            // UI Bridge Identity
            System.setProperty("cacio.toolkit.package", "com.github.caciocavallosilano.cacio.ctc");
            Class.forName("com.github.caciocavallosilano.cacio.ctc.CTCPreloadClassLoader");
        } catch (Throwable th) {
            System.out.println("Bridge Warning: " + th.getMessage());
        }

        launchMinecraft(args);
    }

    public static void launchMinecraft(String[] args) throws Throwable {
        String gameDir = System.getProperty("user.dir");

        // Set properties exactly from your PC command line
        System.setProperty("os.name", "Mac OS X");
        System.setProperty("appdir", gameDir);
        System.setProperty("resource_dir", gameDir + "/rsrc");
        System.setProperty("crucible.dir", gameDir + "/crucible");
        System.setProperty("com.threerings.getdown", "true");
        System.setProperty("no_update", "true");
        System.setProperty("org.lwjgl.util.NoChecks", "true");
        System.setProperty("sun.java2d.d3d", "false"); 
        System.setProperty("jinput.useDefaultPlugin", "false");
        
        // Final Mobile Fixes
        System.setProperty("pojav.internal.skipSetIcon", "true");
        System.setProperty("com.threerings.opengl.no_shaders", "true");
        System.setProperty("org.lwjgl.opengl.disableStaticInit", "true");
        System.setProperty("org.lwjgl.vulkan.libname", "libMoltenVK.dylib");

        String skMainClass = "com.threerings.projectx.client.ProjectXApp";
        System.out.println("Launching SK via Tools...");
        
        Tools.launchSpiral(skMainClass, new String[0]);
    }
}