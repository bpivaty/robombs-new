package robombs.game.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Extracts bundled LWJGL/JInput/OpenAL native libraries to a temporary folder and configures system properties
 * before the first LWJGL access.
 */
public final class NativeLibraryBootstrap {

    private static volatile boolean initialized = false;

    private NativeLibraryBootstrap() {
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }

        String os = System.getProperty("os.name", "").toLowerCase();
        String[] nativeFiles = nativeFilesFor(os);
        if (nativeFiles.length == 0) {
            initialized = true;
            return;
        }

        try {
            Path nativeDir = Files.createTempDirectory("robombs-lwjgl-natives-");
            nativeDir.toFile().deleteOnExit();
            int copied = copyExistingNativeResources(nativeDir, nativeFiles);
            if (copied > 0) {
                String path = nativeDir.toAbsolutePath().toString();
                System.setProperty("org.lwjgl.librarypath", path);
                System.setProperty("net.java.games.input.librarypath", path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        initialized = true;
    }

    private static int copyExistingNativeResources(Path nativeDir, String[] nativeFiles) throws IOException {
        int copied = 0;
        ClassLoader loader = NativeLibraryBootstrap.class.getClassLoader();
        for (String file : nativeFiles) {
            InputStream in = resourceStream(loader, file);
            if (in == null) {
                continue;
            }
            try (InputStream stream = in) {
                Path target = nativeDir.resolve(file);
                Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
                target.toFile().deleteOnExit();
                copied++;
            }
        }
        return copied;
    }

    private static InputStream resourceStream(ClassLoader loader, String file) {
        String[] prefixes = {"", "windows/", "linux/", "macosx/", "native/windows/", "native/linux/", "native/macosx/",
                "natives/windows/", "natives/linux/", "natives/macosx/"};
        for (String prefix : prefixes) {
            InputStream stream = loader.getResourceAsStream(prefix + file);
            if (stream != null) {
                return stream;
            }
        }
        return null;
    }

    private static String[] nativeFilesFor(String os) {
        if (os.contains("win")) {
            return new String[]{"lwjgl64.dll", "lwjgl.dll", "OpenAL64.dll", "OpenAL32.dll", "jinput-dx8_64.dll",
                    "jinput-dx8.dll", "jinput-raw_64.dll", "jinput-raw.dll"};
        }
        if (os.contains("mac")) {
            return new String[]{"liblwjgl.jnilib", "openal.dylib", "libjinput-osx.jnilib"};
        }
        if (os.contains("nux") || os.contains("nix")) {
            return new String[]{"liblwjgl64.so", "liblwjgl.so", "libopenal64.so", "libopenal.so",
                    "libjinput-linux64.so", "libjinput-linux.so"};
        }
        return new String[0];
    }
}
