package com.jborza.chip8me;

public class State {

    public static final int V_REGISTER_COUNT = 16;
    public static final int STACK_DEPTH = 16;
    public static final int DISPLAY_WIDTH = 64;
    public static final int DISPLAY_HEIGHT = 32;
    public static final int CHIP8_DISPLAY_SIZE = DISPLAY_WIDTH * DISPLAY_HEIGHT;
    public static final int CHIP8_MEMORY_SIZE = 4096;
    public static final int CHIP8_KEY_COUNT = 16;
    public static final int FONT_OFFSET = 0x10;
    public static final int PROGRAM_OFFSET = 0x200;

    public byte[] memory;
    public short PC;
    public short I;
    public byte[] V;
    public short[] stack;
    public byte stack_pointer;
    public byte delay_timer;
    public byte sound_timer;
    public byte[] display;
    public boolean draw_flag;
    public byte[] keys;

    public State(){
        memory = new byte[CHIP8_MEMORY_SIZE];
        stack = new short[STACK_DEPTH];
        V = new byte[V_REGISTER_COUNT];
        // TODO reduce to memory-mapped bytes
        display = new byte[CHIP8_DISPLAY_SIZE];
        keys = new byte[CHIP8_KEY_COUNT];
    }
}
