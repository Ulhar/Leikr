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

import leikr.controls.LeikrController;
import leikr.controls.ButtonCodes;
import leikr.loaders.SpriteLoader;
import leikr.loaders.ImageLoader;
import leikr.loaders.MapLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.utils.Array;
import leikr.loaders.AudioLoader;
import leikr.screens.EngineScreen;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;
import org.mini2Dx.core.graphics.viewport.FitViewport;

/**
 *
 * @author tor
 */
public abstract class Engine implements InputProcessor {

    //Mini2DX specific classes for managing the screen state and drawing.
    Graphics g;
    FitViewport viewport;
    FPSLogger logger;

    //used by the Engine Screen to determine if the game should be actively running.
    boolean active;

    /**
     * LeikrController is actually the custom controller listener and the pNc
     * objects are the controllers.
     */
    LeikrController p1Controller;
    LeikrController p2Controller;
    Controller p1c;
    Controller p2c;

    /**
     * Loaders
     *
     * The loaders are used to load the custom assets for a game at startup.
     */
    MapLoader mapLoader;
    ImageLoader imageLoader;
    SpriteLoader spriteLoader;
    AudioLoader audioLoader;

    /**
     * Properties set by Custom Properties
     *
     * These can be overwritten for a more custom experience.
     */
    private int MAX_SPRITES;
    private int USED_SPRITES;

    //codes for the buttons for readability
    public ButtonCodes BTN;

    //custom prop functions
    final int getUsedSprites() {
        return USED_SPRITES;
    }
    //end custom prop functions

    public final void preCreate(int mSprites) {
        MAX_SPRITES = mSprites;
        viewport = new FitViewport(GameRuntime.WIDTH, GameRuntime.HEIGHT);
        logger = new FPSLogger();
        BTN = new ButtonCodes();
        spriteLoader = new SpriteLoader();
        imageLoader = new ImageLoader();
        mapLoader = new MapLoader();
        audioLoader = new AudioLoader();
        active = true;

        try {
            Array<Controller> nmc = Controllers.getControllers();
            if (null != nmc.get(0)) {
                p1c = nmc.get(0);
                p1Controller = new LeikrController();
                p1c.addListener(p1Controller);
            }
            if (null != nmc.get(1)) {
                p2c = nmc.get(1);
                p2Controller = new LeikrController();
                p2c.addListener(p2Controller);
            }
        } catch (Exception ex) {
            System.out.println("Controllers not active: " + ex.getMessage());
        }

    }

    public final void preUpdate(float delta) {
        if (null == mapLoader.getMap()) {
            return;//don't update the map if it is null
        }
        mapLoader.getMap().update(delta);
    }

    public final void preRender(Graphics g) {
        this.g = g;
        viewport.apply(this.g);
        //set to 0 before drawing anything
        USED_SPRITES = 0;
    }

    /**
     * Override functions for game scripting. These are abstract and are
     * required to be implemented in the game code.
     */
    abstract public void create();

    abstract public void update(float delta);

    abstract public void render();
    // end override functions

    //disposes the game objects on exit
    public final void dispose() {
        audioLoader.disposeAudioLoader();
        mapLoader.disposeMap();
        spriteLoader.disposeSprites();
        imageLoader.disposeImages();
        if (null != p1c) {
            p1c.removeListener(p1Controller);
        }
        if (null != p2c) {
            p2c.removeListener(p2Controller);
        }
    }
    //dispose

    //Start Helper methods
    //Not a very helpful method, but I like to see how things perform.
    final void FPS() {
        logger.log();
    }

    public final boolean getActive() {
        return active;
    }

    public final void setActive(boolean state) {
        this.active = state;
    }
    //End helper methods.

    //Image methods
    final void loadImages() {
        imageLoader.load();
    }

    final void image(String name, float x, float y) {
        g.drawTexture(imageLoader.getImage(name), x, y);
    }

    final void image(String name, float x, float y, float w, float h) {
        g.drawTexture(imageLoader.getImage(name), x, y, w, h);
    }

    final void image(String name, float x, float y, float w, float h, boolean flipv) {
        g.drawTexture(imageLoader.getImage(name), x, y, w, h, flipv);
    }
    //end Image methods

    //Map methods
    final void loadMap(String map) {
        mapLoader.loadMap(map);
    }

    final void map() {
        mapLoader.drawMap(g);
    }

    final void map(int x, int y) {
        mapLoader.drawMap(g, x, y);
    }

    final void map(int x, int y, int layer) {
        mapLoader.drawMap(g, x, y, layer);
    }

    final void map(int x, int y, int sx, int sy, int w, int h) {
        mapLoader.drawMap(g, x, y, sx, sy, w, h);
    }

    final void map(int x, int y, int sx, int sy, int w, int h, int layer) {
        mapLoader.drawMap(g, x, y, sx, sy, w, h, layer);
    }

    final int mapGet(float x, float y) {
        return mapLoader.getMapTileId(x, y);
    }

    final void mapSet(float x, float y, int id) {
        mapLoader.setMapTile(x, y, id);
    }
    
    final void mapRemove(float x, float y){
        mapLoader.removeMapTile(x,y);
    }

    final int getMapHeight() {
        return mapLoader.getMap().getHeight();
    }

    final int getMapWidth() {
        return mapLoader.getMap().getWidth();
    }
    //end Map methods

    //start color methods
    final void drawColor(int color) {
        g.setColor(getDrawColor(color));
    }

    final void drawColor(float r, float gr, float b) {
        Color tmp = new Color(r, gr, b, 1f);
        g.setColor(tmp);
    }

    final Color getDrawColor(int color) {
        switch (color) {
            case 0:
                return (Color.BLACK);
            case 1:
                return (Color.WHITE);
            case 2:
                return (Color.RED);
            case 3:
                return (Color.GREEN);
            case 4:
                return (Color.BLUE);
            case 5:
                return (Color.YELLOW);
            case 6:
                return (Color.BROWN);
            case 7:
                return (Color.MAGENTA);
            case 8:
                return (Color.CYAN);
            case 9:
                return (Color.TEAL);
            case 10:
                return (Color.TAN);
            case 11:
                return (Color.FOREST);
            case 12:
                return (Color.PINK);
            case 13:
                return (Color.ORANGE);
            case 14:
                return (Color.PURPLE);
            case 15:
                return (Color.CORAL);
            default:
                return Color.BLACK;
        }
    }

    final void bgColor(int color) {
        drawColor(color);
        g.fillRect(-1, -1, GameRuntime.WIDTH + 1, GameRuntime.HEIGHT + 1);
    }

    final void bgColor(float r, float gr, float b) {
        drawColor(r, gr, b);
        g.fillRect(-1, -1, GameRuntime.WIDTH + 1, GameRuntime.HEIGHT + 1);
    }

    final void bgColor(float[] color) {
        drawColor(color[0], color[1], color[2]);
        g.fillRect(-1, -1, GameRuntime.WIDTH + 1, GameRuntime.HEIGHT + 1);
    }
    //end color methods

    //text methods
    final void text(String text, float x, float y, int color) {
        drawColor(color);
        g.drawString(text, x, y);
    }

    final void text(String text, float x, float y, float[] color) {
        drawColor(color[0], color[1], color[2]);
        g.drawString(text, x, y);
    }

    final void text(String text, float x, float y, float width, int color) {
        drawColor(color);
        g.drawString(text, x, y, width);
    }

    final void text(String text, float x, float y, float width, float[] color) {
        drawColor(color[0], color[1], color[2]);
        g.drawString(text, x, y, width);
    }
    //end text methods

    //start 8x8 sprites
    final void sprite(int id, float x, float y) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        g.drawSprite(spriteLoader.getSprite(id, 0), x, y);
        USED_SPRITES++;
    }

    final void sprite(int id, float x, float y, float degr) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        Sprite t = spriteLoader.getSprite(id, 0);
        t.rotate(degr);
        g.drawSprite(t, x, y);
        t.rotate(-degr);
        USED_SPRITES++;
    }

    final void sprite(int id, float x, float y, boolean clockwise) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        Sprite t = spriteLoader.getSprite(id, 0);
        t.rotate90(clockwise);
        g.drawSprite(t, x, y);
        t.rotate90(!clockwise);
        USED_SPRITES++;
    }

    final void sprite(int id, float x, float y, boolean flipX, boolean flipY) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        Sprite t = spriteLoader.getSprite(id, 0);
        t.setFlip(flipX, flipY);
        g.drawSprite(t, x, y);
        t.setFlip(false, false);
        USED_SPRITES++;
    }
    //end 8x8 sprites

    //start 16x16 sprites
    final void sprite16(int id, float x, float y) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        g.drawSprite(spriteLoader.getSprite(id, 1), x, y);
        USED_SPRITES++;
    }

    final void sprite16(int id, float x, float y, float degr) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        Sprite t = spriteLoader.getSprite(id, 1);
        t.rotate(degr);
        g.drawSprite(t, x, y);
        t.rotate(-degr);
        USED_SPRITES++;
    }

    final void sprite16(int id, float x, float y, boolean clockwise) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        Sprite t = spriteLoader.getSprite(id, 1);
        t.rotate90(clockwise);
        g.drawSprite(t, x, y);
        t.rotate90(!clockwise);
        USED_SPRITES++;
    }

    final void sprite16(int id, float x, float y, boolean flipX, boolean flipY) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        Sprite t = spriteLoader.getSprite(id, 1);
        t.setFlip(flipX, flipY);
        g.drawSprite(t, x, y);
        t.setFlip(false, false);
        USED_SPRITES++;
    }
    //end 16x16 sprites

    //start 32x32 sprites
    final void sprite32(int id, float x, float y) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        g.drawSprite(spriteLoader.getSprite(id, 2), x, y);
        USED_SPRITES++;
    }

    final void sprite32(int id, float x, float y, float degr) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        Sprite t = spriteLoader.getSprite(id, 2);
        t.rotate(degr);
        g.drawSprite(t, x, y);
        t.rotate(-degr);
        USED_SPRITES++;
    }

    final void sprite32(int id, float x, float y, boolean clockwise) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        Sprite t = spriteLoader.getSprite(id, 2);
        t.rotate90(clockwise);
        g.drawSprite(t, x, y);
        t.rotate90(!clockwise);
        USED_SPRITES++;
    }

    final void sprite32(int id, float x, float y, boolean flipX, boolean flipY) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        Sprite t = spriteLoader.getSprite(id, 2);
        t.setFlip(flipX, flipY);
        g.drawSprite(t, x, y);
        t.setFlip(false, false);
        USED_SPRITES++;
    }
    //end 32x32 sprites

    //start 64x64 sprites
    final void sprite64(int id, float x, float y) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        g.drawSprite(spriteLoader.getSprite(id, 3), x, y);
        USED_SPRITES++;
    }

    final void sprite64(int id, float x, float y, float degr) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        Sprite t = spriteLoader.getSprite(id, 3);
        t.rotate(degr);
        g.drawSprite(t, x, y);
        t.rotate(-degr);
        USED_SPRITES++;
    }

    final void sprite64(int id, float x, float y, boolean clockwise) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        Sprite t = spriteLoader.getSprite(id, 3);
        t.rotate90(clockwise);
        g.drawSprite(t, x, y);
        t.rotate90(!clockwise);
        USED_SPRITES++;
    }

    final void sprite64(int id, float x, float y, boolean flipX, boolean flipY) {
        if (USED_SPRITES >= MAX_SPRITES) {
            return;
        }
        Sprite t = spriteLoader.getSprite(id, 3);
        t.setFlip(flipX, flipY);
        g.drawSprite(t, x, y);
        t.setFlip(false, false);
        USED_SPRITES++;
    }
    //end 64x64 sprites

    //start shape drawing methods
    final void rect(float x, float y, float w, float h) {
        g.drawRect(x, y, w, h);
    }

    final void rect(float x, float y, float w, float h, boolean fill) {
        if (fill) {
            g.fillRect(x, y, w, h);
        } else {
            g.drawRect(x, y, w, h);
        }
    }

    final void circle(float x, float y, float r) {
        g.drawCircle(x, y, r);
    }

    final void circle(float x, float y, float r, boolean fill) {
        if (fill) {
            g.fillCircle(x, y, r);
        } else {
            g.drawCircle(x, y, r);
        }
    }

    final void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        g.drawTriangle(x1, y1, x2, y2, x3, y3);
    }

    final void triangle(float x1, float y1, float x2, float y2, float x3, float y3, boolean fill) {
        if (fill) {
            g.fillTriangle(x1, y1, x2, y2, x3, y3);
        } else {
            g.drawTriangle(x1, y1, x2, y2, x3, y3);
        }
    }

    final void line(float x1, float y1, float x2, float y2) {
        g.drawLineSegment(x1, x2, y1, y2);
    }
    //end shape drawing methods

    //start Audio handling
    final void sfx(String name) {
        audioLoader.sound(name);
    }

    final void sfx(String name, float vol, float pit, float pan) {
        audioLoader.sound(name, vol, pit, pan);
    }

    final void music(String name) {
        audioLoader.music(name);
    }

    final void music(String name, boolean loop) {
        audioLoader.music(name, loop);
    }

    final void stopAllMusic() {
        audioLoader.stopMusic();
    }

    final void stopMusic(String fileName) {
        audioLoader.stopMusic(fileName);
    }
    //end audio handling

    //start input handling
    final boolean button(int button) {
        //assume single player game, only p1Controller
        return (null != p1Controller) ? p1Controller.button(button) : false;
    }

    final boolean button(int button, int player) {
        if (null != p1Controller && player == 0) {
            return p1Controller.button(button);
        }
        if (null != p2Controller && player == 1) {
            return p2Controller.button(button);
        }
        //default search is false, in case there are no controllers.
        return false;
    }
    
    final boolean getButton(int code){
        return p1c.getButton(code);
    }

    //detect keyboard key presses
    final boolean key(String key) {
        return Gdx.input.isKeyPressed(Keys.valueOf(key));
    }

    @Override
    public boolean keyDown(int keyCode) {
        return false;
    }

    @Override
    public boolean keyUp(int keyCode) {
        if (keyCode == Keys.ESCAPE) {
            EngineScreen.setBack(true);
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
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

    //end input handling
}
