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
package leikr.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import leikr.customProperties.CustomProgramProperties;
import org.mini2Dx.core.Mdx;

/**
 *
 * @author tor
 */
public class AboutCommand extends Command {

    public AboutCommand() {
        super.name = "about";
    }

    @Override
    public String execute(String[] command) {
        if (command.length <= 1) {
            return "Pass a program name to get the program's about info.";
        }
        if (!containsName(command[1])) {
            return "Program [" + command[1] + "] does not exist in Programs directory.";
        }
        try {
            CustomProgramProperties cpp = new CustomProgramProperties("Programs/" + command[1]);
            return "Title: " + cpp.TITLE + "\nType: " + cpp.TYPE + "\nPlayers: " + cpp.PLAYERS + "\nAuthor: " + cpp.AUTHOR + "\nAbout: " + cpp.ABOUT;
        } catch (Exception ex) {
            Logger.getLogger(AboutCommand.class.getName()).log(Level.SEVERE, null, ex);
            return "Failed to load property file for ["+ command[1] +"].";
        }
    }

    boolean containsName(String name) {
        try {
            ArrayList<String> names = new ArrayList<>();
            Arrays.asList(Mdx.files.local("Programs").list()).stream().forEach(e -> names.add(e.nameWithoutExtension()));
            return names.contains(name);
        } catch (IOException ex) {
            Logger.getLogger(AboutCommand.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public String help() {
        return ">about [name]\nReads the property file of the given program name.";
    }

}
