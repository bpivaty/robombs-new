package robombs.game;

import robombs.game.startup.ResolutionFrame;
import robombs.game.util.NativeLibraryBootstrap;

/**
 * Starts the game's client which offers a dialog to start the server as well.
 */
public class RunGame {
    public static void main(String[] args) throws Exception {
        NativeLibraryBootstrap.init();
        if (args.length > 0 && args[0].equals("compile")) {
            Globals.compiledObjects = true;
        }
        new ResolutionFrame(new BlueThunderClient());
    }
}
