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
        
        // 1. Mandatary Graphics Bridge Fix
        String sizeStr = System.getProperty("cacio.managed.screensize");
        if (sizeStr == null) sizeStr = "1024x768";
        System.setProperty("glfw.windowSize", sizeStr);
        System.setProperty("UIScreen.maximumFramesPerSecond", "60");

        // 2. Keep Mac OS X Identity (Required for Java 21 library loading)
        System.setProperty("os.name", "Mac OS X");
        
        // 3. Spiral Knights Logic
        System.setProperty("appdir", gameDir);
        System.setProperty("resource_dir", gameDir + "/rsrc");
        System.setProperty("crucible.dir", gameDir + "/crucible");
        System.setProperty("com.threerings.getdown", "true");
        System.setProperty("no_update", "true");
        System.setProperty("org.lwjgl.util.NoChecks", "true");
        System.setProperty("sun.java2d.d3d", "false"); 
        System.setProperty("jinput.useDefaultPlugin", "false");

        System.setProperty("com.threerings.projectx.no_vertex_shaders", "true");
        System.setProperty("com.threerings.projectx.no_fragment_shaders", "true");
        System.setProperty("sun.java2d.opengl", "true"); // Ensure Java uses the OGL pipeline
        System.setProperty("com.threerings.opengl.no_shaders", "true");
        System.setProperty("com.threerings.projectx.low_spec", "true");
        System.setProperty("com.threerings.opengl.force_low_spec", "true"); 
        System.setProperty("com.threerings.opengl.num_layers", "1"); // Reduce overdraw
        //System.setProperty("com.threerings.projectx.low_spec", "true");
        System.setProperty("org.lwjgl.opengl.Display.noResizable", "true");

        System.setProperty("com.threerings.opengl.no_vbos", "true"); // STOP the game from managing buffers
        System.setProperty("com.threerings.opengl.no_pbos", "true"); // Prevent pixel buffer crashes

      
        System.setProperty("com.threerings.opengl.no_arrays", "true"); // FORCES immediate-style pointers


// AWT/UI Stability
System.setProperty("sun.java2d.opengl", "false"); 
System.setProperty("org.lwjgl.opengl.Display.noResizable", "true");
        
    // Performance/Stability Tweaks
    System.setProperty("com.threerings.opengl.force_low_spec", "true");
    System.setProperty("com.threerings.projectx.low_spec", "true");
    System.setProperty("sun.java2d.opengl", "false"); 
    System.setProperty("org.lwjgl.opengl.Display.noResizable", "true");

        // Important: Java2D can conflict with GL4ES if not handled.
        System.setProperty("sun.java2d.opengl", "false");
        System.setProperty("sun.java2d.noddraw", "true");
        
        // 4. THE CRITICAL FIXES FOR THE LOG ERRORS YOU SENT:
        // This stops the ArrayIndexOutOfBounds in Display.setIcon
        System.setProperty("pojav.internal.skipSetIcon", "true");
        
        // This tells the Mouse system to use the package found in libs_caciocavallo
        System.setProperty("cacio.toolkit.package", "net.java.openjdk.cacio.ctc");
        // ---------------------------------------------
        
        System.setProperty("org.lwjgl.opengl.disableStaticInit", "true");
        System.setProperty("org.lwjgl.vulkan.libname", "libMoltenVK.dylib");

        String skMainClass = "com.threerings.projectx.client.ProjectXApp";
        String[] skArgs = new String[0]; 

        System.out.println("Starting Spiral Knights (Bridge Redirection Active)...");
        Tools.launchSpiral(skMainClass, skArgs);
    }
}