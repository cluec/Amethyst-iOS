package net.kdt.pojavlaunch;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.beans.Beans;
import net.kdt.pojavlaunch.uikit.UIKit;

public class PojavLauncher {
    public static void main(String[] args) throws Throwable {
        // 1. SET SCREEN SIZE FIRST (Fixes the NullPointerException and Frozen UI)
        String sizeStr = System.getProperty("cacio.managed.screensize");
        if (sizeStr == null) sizeStr = "1132x744"; // iPad mini 6 default
        System.setProperty("glfw.windowSize", sizeStr);
        System.setProperty("UIScreen.maximumFramesPerSecond", "60");

        // 2. Set Stability and Graphics Properties
        System.setProperty("os.name", "Mac OS X");
        System.setProperty("pojav.internal.skipSetIcon", "true");
        System.setProperty("lwjgl.util.NoChecks", "true");
        System.setProperty("org.lwjgl.opengl.disableStaticInit", "true");
        System.setProperty("com.threerings.opengl.no_shaders", "true");
        
        // 3. Initialize UI Bridge
        Beans.setDesignTime(true);
        try {
            System.setProperty("java.util.prefs.PreferencesFactory", "java.util.prefs.FileSystemPreferencesFactory");
            // Force the specific Java 21 package name
            System.setProperty("cacio.toolkit.package", "com.github.caciocavallosilano.cacio.ctc");
            Class.forName("com.github.caciocavallosilano.cacio.ctc.CTCPreloadClassLoader");
        } catch (Throwable th) {
            th.printStackTrace();
        }

        // 4. Start the game logic
        launchMinecraft(args);
    }

    public static void launchMinecraft(String[] args) throws Throwable {
        String gameDir = System.getProperty("user.dir");

        System.setProperty("appdir", gameDir);
        System.setProperty("resource_dir", gameDir + "/rsrc");
        System.setProperty("crucible.dir", gameDir + "/crucible");
        System.setProperty("com.threerings.getdown", "true");
        System.setProperty("no_update", "true");
        System.setProperty("jinput.useDefaultPlugin", "false");
        System.setProperty("org.lwjgl.vulkan.libname", "libMoltenVK.dylib");

        String skMainClass = "com.threerings.projectx.client.ProjectXApp";
        System.out.println("Launching SK via Main Entry Point...");
        
        Tools.launchSpiral(skMainClass, new String[0]);
    }
}