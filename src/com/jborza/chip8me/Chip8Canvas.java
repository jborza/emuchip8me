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
    boolean scale15x = true;
    private int offsetX, offsetY;

    int lastKeyCode = -1;

    //commands
    private final static Command CMD_EXIT = new Command("Exit", Command.EXIT, 1);
    private final static Command CMD_ABOUT = new Command("About", Command.HELP, 9);
    private final static Command CMD_STEP = new Command("Step", Command.BACK, 0);
    private final static Command CMD_STEP100 = new Command("Step x100", Command.OK, 2);
    private final static Command CMD_STEP1000 = new Command("Step x1000", Command.OK, 3);
    private final static Command CMD_RESET = new Command("Reset", Command.SCREEN, 9);
    private final static Command CMD_SCALE15 = new Command("Toggle 1.5X", Command.SCREEN, 9);

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
        //add 1.5X scaling for low-res devices
        if (getWidth() < 128)
            addCommand(CMD_SCALE15);

        configureScale();
    }

    private void reset() {
        cpu = new CPU();
        cpu.loadRom(romStorage.getRom(rom));
        cpu.loadFont();
        cpu.reset();
    }

    public void keyPressed(int code) {
        //paint the code later
        lastKeyCode = code;
        //decode keys into CHIP-8 keys
        //123C
        //456D
        //789E
        //A0BF
        int chip8KeyCode = Keymap.getChip8Key(code);
        if(chip8KeyCode != Keymap.KEY_INVALID)
            cpu.state.keys[chip8KeyCode] = true;
        repaint();
    }

    public void keyReleased(int code){
        int chip8KeyCode = Keymap.getChip8Key(code);
//        if(chip8KeyCode != Keymap.KEY_INVALID)
//            cpu.state.keys[chip8KeyCode] = false;
//        repaint();
    }

    private void configureScale() {
        int scaleWidth = getWidth() / Chip8HW.DISPLAY_WIDTH;
        int scaleHeight = getHeight() / Chip8HW.DISPLAY_HEIGHT;
        scale = Math.min(scaleWidth, scaleHeight);
        offsetX = (getWidth() - Chip8HW.DISPLAY_WIDTH * scale) / 2;
        offsetY = (getHeight() - Chip8HW.DISPLAY_HEIGHT * scale) / 2;

        scale15Lookup = new int[]{0, 1, 3, 4, 6, 7, 9, 10, 12, 13, 15, 16, 18, 19, 21, 22, 24, 25, 27, 28, 30, 31, 33, 34, 36, 37, 39, 40, 42, 43, 45, 46, 48, 49, 51, 52, 54, 55, 57, 58, 60, 61, 63, 64, 66, 67, 69, 70, 72, 73, 75, 76, 78, 79, 81, 82, 84, 85, 87, 88, 90, 91, 93, 94};
    }

    private void paintDisplayRam(Graphics g){
        if (scale == 1) {
            if (scale15x) {
                //experimental 1.5x scale
                for (int y = 0; y < Chip8HW.DISPLAY_HEIGHT; y++) {
                    for (int x = 0; x < Chip8HW.DISPLAY_WIDTH; x++) {
                        if (cpu.state.display[y * Chip8HW.DISPLAY_WIDTH + x] != 0)

                            g.fillRect(scale15Lookup[x], scale15Lookup[y], 2, 2);
                    }
                }
            } else {
                for (int y = 0; y < Chip8HW.DISPLAY_HEIGHT; y++) {
                    for (int x = 0; x < Chip8HW.DISPLAY_WIDTH; x++) {
                        if (cpu.state.display[y * Chip8HW.DISPLAY_WIDTH + x] != 0)
                            g.drawLine(x + offsetX, y + offsetY, x + offsetX, y + offsetY);
                    }
                }
            }
//
        } else {
            for (int y = 0; y < Chip8HW.DISPLAY_HEIGHT; y++) {
                for (int x = 0; x < Chip8HW.DISPLAY_WIDTH; x++) {
                    if (cpu.state.display[y * Chip8HW.DISPLAY_WIDTH + x] != 0)
                        g.fillRect(x * scale + offsetX, y * scale + offsetY, scale, scale);
                }
            }
        }
    }

    public void paint(Graphics g) {
        g.setColor(0x0);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(0);
        g.setFont(font);

        g.setColor(0xFFFFFF);
        g.fillRect(0, 0, getWidth(), getHeight());
//        g.fillRect(offsetX, offsetY, Chip8HW.DISPLAY_WIDTH * scale, Chip8HW.DISPLAY_HEIGHT * scale);

        g.setColor(0x0);
        //draw the graphics rom
        paintDisplayRam(g);

        //3410: 96x55
        g.setColor(0x0);
        g.drawString("k:" + lastKeyCode + " scale:" + scale + " PC:" + CPU.charToHex(cpu.state.PC), 0, getHeight(), Graphics.BOTTOM | Graphics.LEFT);
    }

    int scale15Lookup[];

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
        } else if (command == CMD_SCALE15) {
            scale15x = !scale15x;
            repaint();
        }
    }

    public void setRom(String rom) {
        this.rom = rom;
        reset();
    }
}