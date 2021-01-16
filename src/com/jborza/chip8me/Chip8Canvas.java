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

    //CHIP-8 specifics
    CPU cpu;


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
        cmd[CMD_EXIT] = new MyCommand("Step", Command.EXIT, 8, CMD_EXIT);
        cmd[CMD_RESET] = new MyCommand("Reset", Command.SCREEN, 1, CMD_RESET);

        setCommandListener(this);
        //TODO we could also modify commands at runtime
        addCommand(cmd[CMD_ABOUT]);
        addCommand(cmd[CMD_EXIT]);
        addCommand(cmd[CMD_RESET]);
    }

    private void reset(){
        cpu = new CPU();
        cpu.loadRom(romStorage.getRom(rom));
        cpu.loadFont();
        cpu.reset();
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

        //draw the graphics rom?
//        g.drawLine(0,0,64,32);
//        g.drawLine(64,0,64,32);
        for(int y = 0; y < 32; y++){
            for(int x = 0; x < 64; x++){
                if(cpu.state.display[y*64+x] != 0)
                    g.drawLine(x,y,x+1,y+1);
            }
        }


        g.drawString("PC:"+cpu.state.PC+ " I:"+cpu.state.I, 0,32, Graphics.TOP| Graphics.LEFT);
        g.drawString("v0:"+cpu.state.V[0]+" v1:"+cpu.state.V[1], 0,42, Graphics.TOP| Graphics.LEFT);
    }

    public void commandAction(Command command, Displayable displayable) {
        switch(((MyCommand)command).tag){
            case CMD_ABOUT:
                About.showAbout(display);
                break;
            case CMD_EXIT:
                //midlet.notifyDestroyed();
                cpu.emulate_op();
                repaint();
                break;
            case CMD_RESET:
                //TODO restart CHIP-8
                reset();
                repaint();
                break;
        }
    }

    public void setRom(String rom) {
        this.rom = rom;
        reset();
    }

    class MyCommand extends Command{
        int tag;

        MyCommand(String label, int type, int pri, int tag){
            super(label,type,pri);
            this.tag = tag;
        }
    }
}
