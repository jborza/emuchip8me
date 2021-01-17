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

    int scale;
    private int offsetX, offsetY;

    //commands
    private final static Command CMD_EXIT = new Command("Exit", Command.EXIT, 1);
    private final static Command CMD_ABOUT = new Command("About", Command.HELP, 9);
    private final static Command CMD_STEP = new Command("Step", Command.OK, 0);
    private final static Command CMD_STEP100 = new Command("Step x100", Command.OK, 2);
    private final static Command CMD_STEP1000 = new Command("Step x1000", Command.OK, 3);
    private final static Command CMD_RESET = new Command("Reset", Command.SCREEN, 9);

    private String rom;

    public Chip8Canvas(MIDlet midlet) {
        this.midlet = midlet;
        display = Display.getDisplay(midlet);
        font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        random = new Random();

        romStorage = new RomStorage();

        setCommandListener(this);
        //TODO we could also modify commands at runtime
        addCommand(CMD_RESET);
        addCommand(CMD_STEP);
        addCommand(CMD_STEP100);
        addCommand(CMD_STEP1000);

        configureScale();
    }

    private void reset() {
        cpu = new CPU();
        cpu.loadRom(romStorage.getRom(rom));
        cpu.loadFont();
        cpu.reset();
    }

    int lastKeyCode = -1;

    public void keyPressed(int code) {
        //paint the code later
        lastKeyCode = code;
        repaint();
    }

    private void configureScale(){
        int scaleWidth = getWidth() / Chip8HW.DISPLAY_WIDTH;
        int scaleHeight = getHeight() / Chip8HW.DISPLAY_HEIGHT;
        scale = Math.min(scaleWidth,scaleHeight);
        offsetX = (getWidth() - Chip8HW.DISPLAY_WIDTH * scale) / 2;
        offsetY = (getHeight() - Chip8HW.DISPLAY_HEIGHT * scale) / 2;
    }

    public void paint(Graphics g) {
        g.setColor(0x0);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(0);
        g.setFont(font);

        g.setColor(0xFFFFFF);
        g.fillRect(offsetX, offsetY, Chip8HW.DISPLAY_WIDTH * scale, Chip8HW.DISPLAY_HEIGHT*scale);

        g.setColor(0x0);
        //draw the graphics rom
        if(scale == 1) {
            for (int y = 0; y < Chip8HW.DISPLAY_HEIGHT; y++) {
                for (int x = 0; x < Chip8HW.DISPLAY_WIDTH; x++) {
                    if (cpu.state.display[y * Chip8HW.DISPLAY_WIDTH + x] != 0)
                        g.drawLine(x+offsetX, y+offsetY, x+offsetX, y+offsetY);
                }
            }
        }
        else{
            for (int y = 0; y < Chip8HW.DISPLAY_HEIGHT; y++) {
                for (int x = 0; x < Chip8HW.DISPLAY_WIDTH; x++) {
                    if (cpu.state.display[y * Chip8HW.DISPLAY_WIDTH + x] != 0)
                        g.drawRect(x*scale + offsetX,y*scale + offsetY,1,1);
//                        g.drawLine(x, y, x, y);
                }
            }
        }

        //3410: 96x55
        System.out.println("canvas: "+getWidth()+"x"+getHeight());
        g.drawString("k:" + lastKeyCode, 0, getHeight(), Graphics.BOTTOM | Graphics.LEFT);
        g.drawString("v0:" + cpu.state.V[0] + " v1:" + cpu.state.V[1], 0, 42, Graphics.TOP | Graphics.LEFT);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == CMD_STEP) {
            cpu.emulate_op();
            repaint();
        } else if (command == CMD_STEP100) {
            for (int i = 0; i < 100; i++)
                cpu.emulate_op();
            repaint();
        } else if (command == CMD_STEP1000) {
            for (int i = 0; i < 1000; i++)
                cpu.emulate_op();
            repaint();
        } else if (command == CMD_EXIT) {
            midlet.notifyDestroyed();
        } else if (command == CMD_ABOUT) {
            About.showAbout(display);
        } else if (command == CMD_RESET) {
            reset();
            repaint();
        }
    }

    public void setRom(String rom) {
        this.rom = rom;
        reset();
    }
}