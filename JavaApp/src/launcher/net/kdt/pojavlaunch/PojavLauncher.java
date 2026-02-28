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
        String gameDir = System.getProperty("user.dir");

        // Mandatory Graphics Bridge Fix
        String sizeStr = System.getProperty("cacio.managed.screensize");
        if (sizeStr == null) sizeStr = "1024x768";
        System.setProperty("glfw.windowSize", sizeStr);
        System.setProperty("UIScreen.maximumFramesPerSecond", "60");

        // Identity & Version Fixes
        System.setProperty("os.name", "Mac OS X");
        System.setProperty("gl4es.version", "2.1"); // Force gl4es to report 2.1
        
        // DISABLE SHADERS 
        System.setProperty("com.threerings.opengl.no_shaders", "true");
        System.setProperty("com.threerings.projectx.no_vertex_shaders", "true");
        System.setProperty("com.threerings.projectx.no_fragment_shaders", "true");
        
        // CRASH & UI FIXES:
        System.setProperty("pojav.internal.skipSetIcon", "true");
        System.setProperty("lwjgl.util.NoChecks", "true");
        System.setProperty("cacio.toolkit.package", "com.github.caciocavallosilano.cacio.ctc");

        
        System.setProperty("appdir", gameDir);
        System.setProperty("resource_dir", gameDir + "/rsrc");
        System.setProperty("crucible.dir", gameDir + "/crucible");
        System.setProperty("com.threerings.getdown", "true");
        System.setProperty("no_update", "true");
        System.setProperty("jinput.useDefaultPlugin", "false");
        
        System.setProperty("org.lwjgl.opengl.disableStaticInit", "true");
        System.setProperty("org.lwjgl.vulkan.libname", "libMoltenVK.dylib");

        String skMainClass = "com.threerings.projectx.client.ProjectXApp";
        String[] skArgs = new String[0]; 

        System.out.println("Starting SK (Final Graphics Compatibility Mode)...");
        Tools.launchSpiral(skMainClass, skArgs);
    }
}
