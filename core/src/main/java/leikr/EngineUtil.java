/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leikr;

import groovy.lang.GroovyClassLoader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author tor
 */
public class EngineUtil {

    public static Engine getEngine(String name) {
        Engine engine = null;
        GroovyClassLoader gcl = new GroovyClassLoader();
        try {
            Class game = gcl.parseClass(new File("./Games/" + name + "/Code/main.groovy"));//loads the game code  
            Constructor[] cnst = game.getConstructors();//gets the constructos
            engine = (Engine) cnst[0].newInstance();//instantiates based on first constructor
        }catch(IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException | InvocationTargetException | IOException ex){
            System.out.println(ex);
        }
        return engine;
    } 

}