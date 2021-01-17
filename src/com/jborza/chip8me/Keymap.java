package com.jborza.chip8me;

import javax.microedition.lcdui.Canvas;

public class Keymap {
    public static final int KEY_INVALID = -1;

    public static int getChip8Key(int midpKeyCode) {
        switch (midpKeyCode) {
            case Canvas.KEY_NUM1:
                return 1;
            case Canvas.KEY_NUM2:
                return 2;
            case Canvas.KEY_NUM3:
                return 3;
            case Canvas.KEY_NUM4:
                return 4;
            case Canvas.KEY_NUM5:
                return 5;
            case Canvas.KEY_NUM6:
                return 6;
            case Canvas.KEY_NUM7:
                return 7;
            case Canvas.KEY_NUM8:
                return 8;
            case Canvas.KEY_NUM9:
                return 9;
            case Canvas.KEY_NUM0:
                return 0;
            case Canvas.KEY_STAR:
                return 0xA;
            case Canvas.KEY_POUND:
                return 0xB;
            case Canvas.UP:
                return 0xC;
            case Canvas.DOWN:
                return 0xD;
            default:
                return KEY_INVALID;
        }
    }
}
