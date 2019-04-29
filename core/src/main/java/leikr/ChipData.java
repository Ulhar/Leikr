/*
 * Copyright 2019 torbuntu.
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
package leikr;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import leikr.customProperties.CustomProgramProperties;

/**
 *
 * @author tor
 */
public class ChipData {

    public CustomProgramProperties cpp;
    private String ICON_PATH;
    AssetManager assetManager;

    public ChipData(String programTitle, AssetManager am) {
        this.assetManager = am;
        cpp = new CustomProgramProperties(GameRuntime.PROGRAM_PATH + programTitle);
        ICON_PATH = GameRuntime.PROGRAM_PATH + programTitle + "/Art/icon.png";
        assetManager.load(ICON_PATH, Texture.class);
        assetManager.finishLoading();
    }
    
    public Texture getIcon(){
        return assetManager.get(ICON_PATH, Texture.class);
    }
    public String getTitle(){
        return cpp.TITLE;
    }
    public String getAuthor(){
        return cpp.AUTHOR;
    }
    public String getType(){
        return cpp.TYPE;
    }
    public String getEngine(){
        return cpp.ENGINE;
    }
    public int getPlayers(){
        return cpp.PLAYERS;
    }
}