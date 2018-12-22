package leikr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mini2Dx.core.game.BasicGame;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.miniscript.core.GameScriptingEngine;
import org.mini2Dx.miniscript.core.ScriptBindings;
import org.mini2Dx.miniscript.core.exception.InsufficientCompilersException;
import org.mini2Dx.miniscript.groovy.GroovyGameScriptingEngine;
import org.mini2Dx.miniscript.kotlin.KotlinGameScriptingEngine;
import org.mini2Dx.miniscript.lua.LuaGameScriptingEngine;
import org.mini2Dx.miniscript.python.PythonGameScriptingEngine;
import org.mini2Dx.miniscript.ruby.RubyGameScriptingEngine;

public class GameRuntime extends BasicGame {

    public static final String GAME_IDENTIFIER = "torbuntu.leikr";

    public static int WIDTH = 320;
    public static int HEIGHT = 240;

    GameScriptingEngine scriptEngine;
    ScriptBindings scriptBindings;
    File dir;
    LeikrEngine engine;

    File libraryDir;
    String[] libraryList;

    public GameRuntime() {
        libraryDir = new File("./Games");
        libraryList = libraryDir.list();
        for (String file : libraryList) {
            System.out.println(file);
        }
            
        dir = new File("./Code/main.groovy");
    }

    GameScriptingEngine getEngine() {
        Properties prop = new Properties();
        InputStream stream;
        try {
            stream = new FileInputStream(new File("./game.properties"));
            prop.load(stream);
            switch (prop.getProperty("runtime").toLowerCase()) {
                case "kotlin":
                    return new KotlinGameScriptingEngine();
                case "lua":
                    return new LuaGameScriptingEngine();
                case "python":
                    return new PythonGameScriptingEngine();
                case "ruby":
                    return new RubyGameScriptingEngine();                    
                case "groovy":
                default:
                    return new GroovyGameScriptingEngine();
            }
        } catch (IOException ex) {
            Logger.getLogger(GameRuntime.class.getName()).log(Level.SEVERE, null, ex);
        }
        // if all fails, return groovy scripting engine.
        return new GroovyGameScriptingEngine();
    }

    @Override
    public void initialise() {
        scriptEngine = getEngine();
        scriptBindings = new ScriptBindings();
        scriptBindings.put("game", engine);
        try {
            int scriptId = scriptEngine.compileScript(new FileInputStream(dir));
            scriptEngine.invokeCompiledScriptLocally(scriptId, scriptBindings);
            engine = (LeikrEngine) scriptBindings.get("game");
            engine.init();
        } catch (InsufficientCompilersException | IOException ex) {
            Logger.getLogger(GameRuntime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(float delta) {
        scriptEngine.update(delta);
        engine.update();
        engine.update(delta);
    }

    @Override
    public void interpolate(float alpha) {

    }

    @Override
    public void render(Graphics g) {
        engine.preRender(g);
        engine.render();
    }
}