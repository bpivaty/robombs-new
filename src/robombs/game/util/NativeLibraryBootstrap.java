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

    private enum OsType {
        WINDOWS, MAC, LINUX, UNKNOWN
    }

    private static volatile boolean initialized = false;

    private NativeLibraryBootstrap() {
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }

        String osName = System.getProperty("os.name", "");
        OsType osType = detectOsType(osName);
        String[] nativeFiles = nativeFilesFor(osType);
        String[] prefixes = prefixesFor(osType);
        if (nativeFiles.length == 0) {
            initialized = true;
            return;
        }

        try {
            Path nativeDir = Files.createTempDirectory("robombs-lwjgl-natives-");
            nativeDir.toFile().deleteOnExit();
            int copied = extractAvailableNativeResources(nativeDir, nativeFiles, prefixes);
            if (copied == 0) {
                throw new IllegalStateException("No bundled LWJGL native libraries found for OS: " + osName);
            }
            String path = nativeDir.toAbsolutePath().toString();
            System.setProperty("org.lwjgl.librarypath", path);
            System.setProperty("net.java.games.input.librarypath", path);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create/extract LWJGL native libraries for OS: " + osName, e);
        }

        initialized = true;
    }

    private static int extractAvailableNativeResources(Path nativeDir, String[] nativeFiles, String[] prefixes) throws IOException {
        int copied = 0;
        ClassLoader loader = NativeLibraryBootstrap.class.getClassLoader();
        for (String file : nativeFiles) {
            InputStream in = resourceStream(loader, file, prefixes);
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

    private static InputStream resourceStream(ClassLoader loader, String file, String[] prefixes) {
        for (String prefix : prefixes) {
            InputStream stream = loader.getResourceAsStream(prefix + file);
            if (stream != null) {
                return stream;
            }
        }
        return null;
    }

    private static String[] nativeFilesFor(OsType osType) {
        if (osType == OsType.WINDOWS) {
            return new String[]{"lwjgl64.dll", "lwjgl.dll", "OpenAL64.dll", "OpenAL32.dll", "jinput-dx8_64.dll",
                    "jinput-dx8.dll", "jinput-raw_64.dll", "jinput-raw.dll"};
        }
        if (osType == OsType.MAC) {
            return new String[]{"liblwjgl.jnilib", "openal.dylib", "libjinput-osx.jnilib"};
        }
        if (osType == OsType.LINUX) {
            return new String[]{"liblwjgl64.so", "liblwjgl.so", "libopenal64.so", "libopenal.so",
                    "libjinput-linux64.so", "libjinput-linux.so"};
        }
        return new String[0];
    }

    private static String[] prefixesFor(OsType osType) {
        if (osType == OsType.WINDOWS) {
            return new String[]{"", "windows/", "native/windows/", "natives/windows/"};
        }
        if (osType == OsType.MAC) {
            return new String[]{"", "macosx/", "native/macosx/", "natives/macosx/"};
        }
        if (osType == OsType.LINUX) {
            return new String[]{"", "linux/", "native/linux/", "natives/linux/"};
        }
        return new String[]{""};
    }

    private static OsType detectOsType(String osName) {
        String os = osName.toLowerCase();
        if (os.contains("windows")) {
            return OsType.WINDOWS;
        }
        if (os.contains("mac")) {
            return OsType.MAC;
        }
        if (os.contains("linux") || os.contains("unix")) {
            return OsType.LINUX;
        }
        return OsType.UNKNOWN;
    }
}
