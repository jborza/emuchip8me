package com.jborza.chip8me;

public class State {

    public static final int V_REGISTER_COUNT = 16;
    public static final int STACK_DEPTH = 16;
    public static final int CHIP8_MEMORY_SIZE = 4096;
    public static final int CHIP8_KEY_COUNT = 16;
    public static final int FONT_OFFSET = 0x0;
    public static final int PROGRAM_OFFSET = 0x200;

    public byte[] memory;
    public char PC;
    public char I;
    public byte[] V;
    public char[] stack;
    public byte stack_pointer;
    public byte delay_timer;
    public byte sound_timer;
    public byte[] display;
    public boolean draw_flag;
    public boolean[] keys;
    public long total_cycles;

    public State(){
        memory = new byte[CHIP8_MEMORY_SIZE];
        stack = new char[STACK_DEPTH];
        V = new byte[V_REGISTER_COUNT];
        // TODO reduce to memory-mapped bytes
        display = new byte[Chip8HW.CHIP8_DISPLAY_SIZE];
        keys = new boolean[CHIP8_KEY_COUNT];
    }
}
