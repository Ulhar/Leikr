/*
 * Copyright 2019 See AUTHORS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package leikr.managers;

import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import leikr.GameRuntime;
import leikr.commands.AboutCommand;
import leikr.commands.CleanCommand;
import leikr.commands.Command;
import leikr.commands.ExitCommand;
import leikr.commands.ExportCommand;
import leikr.commands.FindCommand;
import leikr.commands.InstallCommand;
import leikr.commands.NewProgramCommand;
import leikr.commands.PackageCommand;
import leikr.commands.PrintDirectoryCommand;
import leikr.commands.PrintWorkspaceCommand;
import leikr.commands.RemoveCommand;
import leikr.commands.RunCommand;
import leikr.commands.ToolCommand;
import leikr.commands.WikiCommand;
import org.mini2Dx.core.Mdx;
import org.mini2Dx.gdx.Input.Keys;
import org.mini2Dx.gdx.InputProcessor;

/**
 *
 * @author tor
 */
public class TerminalManager implements InputProcessor {

    public String prompt = "";
    public String historyText = "";
    public ArrayList<String> history;
    int index;

    Map<String, Command> commandList;

    ArrayList<String> programList;
    int prIdx;

    public static enum TerminalState {
        PROCESSING,
        RUN_PROGRAM,
        NEW_PROGRAM,
        RUN_UTILITY
    }

    public static TerminalState terminalState;

    /**
     * The list of available commands. displayed when "help" with no params is
     * run.
     */
    public TerminalManager() {
        history = new ArrayList<>();
        terminalState = TerminalState.PROCESSING;
        commandList = new HashMap<>();
        programList = new ArrayList<>();
        try {
            Arrays.stream(Mdx.files.local("Programs/").list()).filter(e -> e.isDirectory()).forEach(game -> programList.add(game.nameWithoutExtension()));
            if (programList.size() > 0) {
                prIdx = programList.size();
            }
        } catch (Exception ex) {
            Logger.getLogger(TerminalManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        commandList.put("about", new AboutCommand());
        commandList.put("ls", new PrintDirectoryCommand());
        commandList.put("new", new NewProgramCommand());
        commandList.put("exit", new ExitCommand());
        commandList.put("run", new RunCommand());
        commandList.put("find", new FindCommand());
        commandList.put("clean", new CleanCommand());
        commandList.put("pwd", new PrintWorkspaceCommand());
        commandList.put("wiki", new WikiCommand());
        commandList.put("export", new ExportCommand());
        commandList.put("install", new InstallCommand());
        commandList.put("tool", new ToolCommand());
        commandList.put("uninstall", new RemoveCommand());
        commandList.put("package", new PackageCommand());

    }

    public static void setState(TerminalState state) {
        terminalState = state;
    }

    public TerminalState getState() {
        return terminalState;
    }

    public void init() {
        terminalState = TerminalState.PROCESSING;
        prompt = "";
        index = history.size() - 1;
        if (GameRuntime.GAME_NAME.length() < 2) {
            historyText = "No program loaded.";
        } else {
            historyText = "Closed program: [" + GameRuntime.GAME_NAME + "]";
        }
    }

    public void update() {
        if (programList.size() <= 0) {
            return;
        }
        if (Mdx.input.isKeyJustPressed(Keys.PAGE_UP)) {
            if (prIdx > 0) {
                prIdx--;
            } else {
                prIdx = programList.size() - 1;
            }
            prompt = "run " + programList.get(prIdx);
        }
        if (Mdx.input.isKeyJustPressed(Keys.PAGE_DOWN)) {
            if (prIdx < programList.size()-1) {
                prIdx++;
            } else {
                prIdx = 0;
            }
            prompt = "run " + programList.get(prIdx);
        }

    }

    String getAllHelp() {
        ArrayList<String> output = new ArrayList<>();
        commandList.keySet().forEach((h) -> {
            output.add(commandList.get(h).name);
        });
        output.sort(String::compareToIgnoreCase);
        return output.stream().collect(Collectors.joining(", "));
    }

    String getSpecificHelp(String name) {
        if (!commandList.containsKey(name)) {
            return "No help for unknown command: [ " + name + " ]";
        }
        return commandList.get(name).help();
    }

    public String processCommand() {
        history.add(prompt);
        if (history.size() > 20) {
            history.remove(0);
        } else {
            index = history.size() - 1;
        }
        String[] command = prompt.split(" ");
        if (command[0].equalsIgnoreCase("help")) {
            if (command.length > 1) {
                return getSpecificHelp(command[1]);
            } else {
                return getAllHelp();
            }
        }
        if (!commandList.containsKey(command[0])) {
            return "Unknown command [" + command[0] + "]";
        }
        try {
            Command c = commandList.get(command[0]);
            return c.execute(command);
        } catch (Exception ex) {
            Logger.getLogger(TerminalManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Unknown command [" + command[0] + "]";
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Mdx.input.isKeyDown(Keys.CONTROL_LEFT)) {
            if (keycode == Keys.NUM_1) {
                Gdx.graphics.setWindowedMode(240, 160);
            }
            if (keycode == Keys.NUM_2) {
                Gdx.graphics.setWindowedMode(240 * 2, 160 * 2);
            }
            if (keycode == Keys.NUM_3) {
                Gdx.graphics.setWindowedMode(240 * 3, 160 * 3);
            }
            return true;
        }
        if (keycode == Keys.ESCAPE) {
            Mdx.platformUtils.exit(true);
        }
        if (keycode == Keys.ENTER) {
            historyText = processCommand() + "\n\n";
            prompt = "";
            return true;
        }
        if (keycode == Keys.UP) {
            if (history.size() > 0) {
                prompt = history.get(index);
                if (index > 0) {
                    index--;
                }
            }
        }
        if (keycode == Keys.DOWN) {
            if (history.size() > 0) {
                if (index < history.size() - 1) {
                    index++;
                    prompt = history.get(index);
                } else {
                    prompt = "";
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        if (Mdx.input.isKeyDown(Keys.CONTROL_LEFT)) {
            return true;
        }
        if ((int) c >= 32 && (int) c <= 126) {
            prompt = prompt + c;
            return true;
        }
        if ((int) c == 8 && prompt.length() > 0) {
            prompt = prompt.substring(0, prompt.length() - 1);
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
