package com.jborza.chip8me;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

public class About {
    private About() {}

    public static void showAbout(Display display){
        Alert alert = new Alert("About emuchip8me");
        alert.setTimeout(Alert.FOREVER);

        alert.setString("Lorem ipsum dolor sit amet");

        display.setCurrent(alert);
    }
}
