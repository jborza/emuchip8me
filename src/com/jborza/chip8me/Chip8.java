package com.jborza.chip8me;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class Chip8 extends MIDlet {
    Chip8Canvas canvas;
    RomStorage romStorage;
    List romList;

    public Chip8(){
        canvas = new Chip8Canvas(this);
        romStorage = new RomStorage();
        romList = new List("Choose ROM", Choice.IMPLICIT);
        String[] roms = romStorage.getRoms();
        for(int i = 0; i <  roms.length; i++)
            romList.append(roms[i], null);

    }

    protected void startApp() throws MIDletStateChangeException {
//        Display.getDisplay(this).setCurrent(canvas);
        Display.getDisplay(this).setCurrent(romList);
    }

    protected void pauseApp() {

    }

    protected void destroyApp(boolean b) throws MIDletStateChangeException {

    }
}
