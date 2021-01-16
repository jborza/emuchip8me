package com.jborza.chip8me;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import java.util.Random;

public class Chip8Canvas extends Canvas implements CommandListener {
    MIDlet midlet;
    Display display;

    Font font;
    Random random;

    RomStorage romStorage;

    //commands
    static final int CMD_ABOUT = 0;
    static final int CMD_EXIT = 1;
    static final int CMD_RESET = 2;
    static final int CMD_ZLAST = 3; // must be ze last, of course
    Command cmd[];
    private String rom;

    public Chip8Canvas(MIDlet midlet){
        this.midlet = midlet;
        display = Display.getDisplay(midlet);
        font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        random = new Random();

        romStorage = new RomStorage();

        cmd= new Command[CMD_ZLAST];
        cmd[CMD_ABOUT] = new MyCommand("About", Command.HELP, 9, CMD_ABOUT);
        cmd[CMD_EXIT] = new MyCommand("Exit", Command.EXIT, 8, CMD_EXIT);
        cmd[CMD_RESET] = new MyCommand("Reset", Command.SCREEN, 1, CMD_RESET);

        setCommandListener(this);
        //TODO we could also modify commands at runtime
        addCommand(cmd[CMD_ABOUT]);
        addCommand(cmd[CMD_EXIT]);
        addCommand(cmd[CMD_RESET]);
    }

    int lastKeyCode = -1;

    public void keyPressed(int code){
        //paint the code later
        lastKeyCode = code;
        repaint();
    }

    public void paint(Graphics g){
        //draw diagnostic info
        g.setColor(0xFFFFFF);
        g.fillRect(0,0, getWidth(), getHeight());
        g.setColor(0);
        g.setFont(font);
        g.drawString("w:"+getWidth()+ " h:"+getHeight(), 0,10, Graphics.BOTTOM| Graphics.LEFT);
        g.drawString("keycode:"+lastKeyCode, 0,30, Graphics.BOTTOM| Graphics.LEFT);

        g.drawString("HELLO J2ME IN 2021", getWidth()/2,50, Graphics.BOTTOM| Graphics.HCENTER);

    }

    public void commandAction(Command command, Displayable displayable) {
        switch(((MyCommand)command).tag){
            case CMD_ABOUT:
                About.showAbout(display);
                break;
            case CMD_EXIT:
                midlet.notifyDestroyed();
                break;
            case CMD_RESET:
                //TODO restart CHIP-8
                repaint();
                break;
        }
    }

    public void setRom(String rom) {
        this.rom = rom;
    }


    class MyCommand extends Command{
        int tag;

        MyCommand(String label, int type, int pri, int tag){
            super(label,type,pri);
            this.tag = tag;
        }
    }
}
