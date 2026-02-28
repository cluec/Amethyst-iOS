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

        // Try to get the size from the environment (Set by the slider)
        String sizeStr = System.getenv("CACIOCAVALLO_SCREEN_SIZE");
        
        // If the environment is empty, check the property, then fallback to default
        if (sizeStr == null) sizeStr = System.getProperty("cacio.managed.screensize");
        if (sizeStr == null) sizeStr = "1132x744"; 

        // Set the properties so the rest of the app can see them
        System.setProperty("cacio.managed.screensize", sizeStr);
        System.setProperty("glfw.windowSize", sizeStr);

        // Bypass the chmod error
        System.setProperty("java.util.prefs.PreferencesFactory", "java.util.prefs.MemoryPreferencesFactory");

        try {
            System.setProperty("cacio.toolkit.package", "com.github.caciocavallosilano.cacio.ctc");
            Class.forName("com.github.caciocavallosilano.cacio.ctc.CTCPreloadClassLoader");
        } catch (Throwable th) {
            System.out.println("UI Bridge Warning: " + th.getMessage());
        }

        launchMinecraft(args);
    }

    public static void launchMinecraft(String[] args) throws Throwable {
        String gameDir = System.getProperty("user.dir");

        // Identity and Graphics
        System.setProperty("os.name", "Mac OS X");
        System.setProperty("pojav.internal.skipSetIcon", "true");
        System.setProperty("lwjgl.util.NoChecks", "true");
        System.setProperty("org.lwjgl.opengl.disableStaticInit", "true");
        
        // Spiral Knights Core Properties
        System.setProperty("appdir", gameDir);
        System.setProperty("resource_dir", gameDir + "/rsrc");
        System.setProperty("crucible.dir", gameDir + "/crucible");
        System.setProperty("com.threerings.getdown", "true");
        System.setProperty("no_update", "true");
        System.setProperty("jinput.useDefaultPlugin", "false");
        System.setProperty("org.lwjgl.vulkan.libname", "libMoltenVK.dylib");

        // Force the game to skip its broken shader checks
        System.setProperty("com.threerings.opengl.no_shaders", "true");

        String skMainClass = "com.threerings.projectx.client.ProjectXApp";
        System.out.println("Tools: Starting ProjectXApp...");
        
        // Ensure we pass NO arguments to avoid the updater loop
        Tools.launchSpiral(skMainClass, new String[0]);
    }
}