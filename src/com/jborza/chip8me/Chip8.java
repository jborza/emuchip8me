package com.jborza.chip8me;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class Chip8 extends MIDlet {
    Chip8Canvas canvas;

    public Chip8(){
        canvas = new Chip8Canvas(this);
    }

    protected void startApp() throws MIDletStateChangeException {
        Display.getDisplay(this).setCurrent(canvas);
    }

    protected void pauseApp() {

    }

    protected void destroyApp(boolean b) throws MIDletStateChangeException {

    }
}
