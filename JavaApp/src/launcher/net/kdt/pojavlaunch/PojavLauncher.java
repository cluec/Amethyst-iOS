package net.kdt.pojavlaunch;

import java.beans.Beans;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;

import org.lwjgl.glfw.CallbackBridge;
import org.lwjgl.glfw.GLFW;

import net.kdt.pojavlaunch.uikit.*;
import net.kdt.pojavlaunch.utils.*;
import net.kdt.pojavlaunch.value.*;

public class PojavLauncher {
    private static float currProgress, maxProgress;

    public static void main(String[] args) throws Throwable {
        // Skip calling to com.apple.eawt.Application.nativeInitializeApplicationDelegate()
        Beans.setDesignTime(true);
        try {
            // Some places use macOS-specific code, which is unavailable on iOS
            // In this case, try to get it to use Linux-specific code instead.
            com.apple.eawt.Application.getApplication();
            Class clazz = Class.forName("com.apple.eawt.Application");
            Field field = clazz.getDeclaredField("sApplication");
            field.setAccessible(true);
            field.set(null, null);
            sun.font.FontUtilities.isLinux = true;
            System.setProperty("java.util.prefs.PreferencesFactory", "java.util.prefs.FileSystemPreferencesFactory");
        } catch (Throwable th) {
            // Not on JRE8, ignore exception
            //Tools.showError(th);
        }

        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            public void uncaughtException(Thread t, Throwable th) {
                th.printStackTrace();
                System.exit(1);
            }
        });

        try {
            // Try to initialize Caciocavallo17
            Class.forName("com.github.caciocavallosilano.cacio.ctc.CTCPreloadClassLoader");
        } catch (ClassNotFoundException e) {}

        // Safety check: If no args are provided (like when we skip login), 
        // just go straight to Spiral Knights.
        if (args != null && args.length > 0 && args[0].equals("-jar")) {
            UIKit.callback_JavaGUIViewController_launchJarFile(args[1], Arrays.copyOfRange(args, 2, args.length));
        } else {
            launchMinecraft(args);
        }
    }

    public static void launchMinecraft(String[] args) throws Throwable {
        // 1. Get the current directory (where you will put your SK files)
        String gameDir = System.getProperty("user.dir");

        // 2. Set the exact properties from your PC command line
        System.setProperty("appdir", gameDir);
        System.setProperty("resource_dir", gameDir + "/rsrc");
        System.setProperty("com.threerings.getdown", "true");
        System.setProperty("org.lwjgl.util.NoChecks", "true");
        System.setProperty("sun.java2d.d3d", "false"); 
        System.setProperty("Djinput.useDefaultPlugin", "false");
        
        // This is needed for the rendering bridge on iOS
        System.setProperty("org.lwjgl.vulkan.libname", "libMoltenVK.dylib");

        // 3. Define the Spiral Knights Entry Point
        String skMainClass = "com.threerings.projectx.client.ProjectXApp";

        System.out.println("Launching Spiral Knights...");

        // 4. Redirect to the custom loader we are putting in Tools.java
        Tools.launchSpiral(skMainClass, args);
    }
}
