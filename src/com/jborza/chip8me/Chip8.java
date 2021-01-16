package com.jborza.chip8me;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class Chip8 extends MIDlet implements CommandListener {

    Chip8Canvas canvas;
    RomStorage romStorage;
    List romList;

    public Chip8() {
        canvas = new Chip8Canvas(this);
        romStorage = new RomStorage();
        romList = new List("Choose ROM", Choice.IMPLICIT);
        String[] roms = romStorage.getRoms();
        for (int i = 0; i < roms.length; i++)
            romList.append(roms[i], null);
        romList.setCommandListener(this);
    }

    protected void startApp() throws MIDletStateChangeException {
        Display.getDisplay(this).setCurrent(romList);
    }

    protected void pauseApp() {

    }

    protected void destroyApp(boolean b) throws MIDletStateChangeException {

    }

    public void commandAction(Command c, Displayable d) {
        if (c == List.SELECT_COMMAND) {
            int selectedIndex = ((List) d).getSelectedIndex();
            canvas.setRom(romStorage.getRoms()[selectedIndex]);
            Display.getDisplay(this).setCurrent(canvas);
        }
    }
}
