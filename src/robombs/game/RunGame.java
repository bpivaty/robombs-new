package robombs.game;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import robombs.game.startup.ResolutionFrame;

/**
 * Starts the game's client which offers a dialog to start the server as well.
 */
public class RunGame {

    private static final String[] WINDOWS_NATIVE_LIBS = {
            "lwjgl.dll",
            "lwjgl64.dll",
            "OpenAL32.dll",
            "OpenAL64.dll",
            "jinput-dx8.dll",
            "jinput-dx8_64.dll",
            "jinput-raw.dll",
            "jinput-raw_64.dll"
    };

    public static void main(String[] args) throws Exception {
        configureWindowsNatives();
        if (args.length > 0 && args[0].equals("compile")) {
            Globals.compiledObjects = true;
        }
        new ResolutionFrame(new BlueThunderClient());
    }

    private static void configureWindowsNatives() throws IOException {
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (!osName.contains("win")) {
            return;
        }

        Path nativeDir = Files.createTempDirectory("robombs-lwjgl-natives");
        nativeDir.toFile().deleteOnExit();

        int extracted = 0;
        for (String nativeLib : WINDOWS_NATIVE_LIBS) {
            try (InputStream input = RunGame.class.getClassLoader().getResourceAsStream(nativeLib)) {
                if (input == null) {
                    continue;
                }
                Path target = nativeDir.resolve(nativeLib);
                Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
                target.toFile().deleteOnExit();
                extracted++;
            }
        }

        if (extracted == 0) {
            throw new IOException("No Windows LWJGL native libraries were found in the packaged JAR.");
        }

        String nativePath = nativeDir.toAbsolutePath().toString();
        System.setProperty("org.lwjgl.librarypath", nativePath);
        System.setProperty("net.java.games.input.librarypath", nativePath);
        System.setProperty("java.library.path", nativePath);
    }
}
