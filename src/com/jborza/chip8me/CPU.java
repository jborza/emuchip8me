package com.jborza.chip8me;

import java.util.Random;

public class CPU {
    public State state;
    private Random rand;
    public boolean debug = false;

    public CPU() {
        state = new State();
        rand = new Random();
    }

    public void loadRom(byte[] rom) {
        System.arraycopy(rom, 0, state.memory, State.PROGRAM_OFFSET, rom.length);
    }

    public void loadFont() {
        System.arraycopy(SpriteFont.makeFont(), 0, state.memory, State.FONT_OFFSET, SpriteFont.FONT_SIZE);
    }

    public void reset() {
        state.PC = State.PROGRAM_OFFSET;
    }

    void update_timers() {
        if (state.delay_timer > 0)
            state.delay_timer--;
        if (state.sound_timer > 0)
            state.sound_timer--;
    }

    void draw(char opcode, byte vx, byte vy) {
        //DXYN
        //Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N+1 pixels.
        //Each row of 8 pixels is read as bit-coded starting from memory location I;
        //I value doesn’t change after the execution of this instruction.
        //VF is set to 1 if any screen pixels are flipped from set to unset
        //when the sprite is drawn, and to 0 if that doesn’t happen
        byte rx = state.V[vx]; //sprite x
        byte ry = state.V[vy]; //sprite y
        char sprite_height = (char) (opcode & 0x000F);
        byte sprite_row;
        //reset collision bit
        state.V[0xF] = 0;

        for (int y = 0; y < sprite_height; y++) {
            sprite_row = state.memory[state.I + y];
            for (int x = 0; x < 8; x++) {
                //get the x-th pixel from the sprite row
                if ((sprite_row & (0x80 >> x)) != 0) {
                    int display_pixel_address = ((ry + y) * Chip8HW.DISPLAY_WIDTH + rx + x) % (Chip8HW.CHIP8_DISPLAY_SIZE);
                    if (state.display[display_pixel_address] == 1) {
                        state.V[0xF] = 1;
                    }
                    state.display[display_pixel_address] ^= 1;
                }
            }
        }
//
        state.draw_flag = true;
    }

    public void emulate_op() {
        char opcode = (char) (state.memory[state.PC] & 0xFF);
        opcode = (char) (opcode << 8);
        opcode |= (char) (state.memory[state.PC + 1] & 0xFF);
        state.PC += 2;
        byte vx, vy;
        vx = (byte) ((opcode & 0x0F00) >> 8);
        vy = (byte) ((opcode & 0x00F0) >> 4);
        switch (opcode & 0xF000) {
            case 0x0000:
                switch ((char) (opcode & 0x00FF)) {
                    case 0x00E0:
                        //clear display
                        state.display = new byte[Chip8HW.CHIP8_DISPLAY_SIZE];
                        break;
                    case 0x00EE:
                        state.PC = state.stack[state.stack_pointer];
                        state.stack_pointer--;
                        break;
                    default:
                        System.out.println("Unknown opcode: " + charToHex(opcode));
                        break;
                }

                break;
            case 0x1000:
                //jump to NNN
                state.PC = (char) (opcode & 0x0FFF);
                break;
            case 0x2000:
                //call subroutine at NNN
                state.stack_pointer++;
                state.stack[state.stack_pointer] = state.PC;
                state.PC = (char) (opcode & 0x0FFF);
                break;
            case 0x3000:
                //skip if vx == NN
                if (state.V[vx] == (byte) (opcode & 0x00FF))
                    state.PC += 2;
                break;
            case 0x4000:
                //skip if vx != NN
                if (state.V[vx] != (byte) (opcode & 0x00FF))
                    state.PC += 2;
                break;
            case 0x5000:
                //skip if VX == VY
                if (state.V[vx] == state.V[vy])
                    state.PC += 2;
                break;
            case 0x6000:
                //set register X to NN (6XNN)
                state.V[vx] = (byte) (opcode & 0x00FF);
                break;
            case 0x7000:
                //add NN to VX
                state.V[vx] += (byte) (opcode & 0x00FF);
                break;
            case 0x8000:
                //arithmetic
                switch (opcode & 0x000F) {
                    case 0x0000:
                        //vx = vy
                        state.V[vx] = state.V[vy];
                        break;
                    case 0x0001:
                        //vx = vx | vy
                        state.V[vx] |= state.V[vy];
                        break;
                    case 0x0002:
                        //vx = vx | vy
                        state.V[vx] &= state.V[vy];
                        break;
                    case 0x0003:
                        //vx = vx ^ vy
                        state.V[vx] ^= state.V[vy];
                        break;
                    case 0x0004:
                        //vx = vx+vy, vf=carry
                    {
                        short result = (short) (state.V[vx] + state.V[vy]);
                        byte overflow = (byte) (result > 0xFF ? 1 : 0);
                        state.V[vx] = (byte) (result & 0xFF);
                        state.V[0xF] = overflow;
                    }
                    break;
                    case 0x0005:
                        //VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
                    {
                        byte overflow = (byte) (state.V[vx] >= state.V[vy] ? 1 : 0);
                        state.V[vx] = (byte) (state.V[vx] - state.V[vy]);
                        state.V[0xF] = overflow;
                        break;
                    }
                    case 0x0006: {
                        //Stores the least significant bit of VX in VF and then shifts VX to the right by 1.
                        byte overflow = (byte) (state.V[vx] & 0x1);
                        state.V[vx] >>= 1;
                        state.V[0xF] = overflow;
                        break;
                    }
                    case 0x0007:
                        //Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
                        //vx = vy - vx
                    {
                        byte overflow = (byte) (state.V[vy] >= state.V[vx] ? 1 : 0);
                        state.V[vx] = (byte) (state.V[vy] - state.V[vx]);
                        state.V[0xF] = overflow;
                        break;
                    }
                    case 0x000E:
                        //Stores the most significant bit of VX in VF and then shifts VX to the left by 1.
                    {
                        byte overflow = (byte) (state.V[vx] >> 7);
                        state.V[vx] <<= 1;
                        state.V[0xF] = overflow;
                        break;
                    }
                    default:
                        System.out.println("Unknown opcode: " + charToHex(opcode));
                }
                break;
            case 0x9000:
                //skips next instruction if VX doesn't equal VY.
                if (state.V[vx] != state.V[vy])
                    state.PC += 2;
                break;
            case 0xA000:
                //set index register to NNN
                state.I = (char) (opcode & 0x0FFF);
                break;
            case 0xB000:
                //jump to NNN plus V0
                state.PC = (char) ((opcode & 0xFFF) + state.V[0]);
                break;
            case 0xC000:
                //Sets VX to the result of a bitwise and operation on a random number (Typically: 0 to 255) and NN.
                state.V[vx] = (byte) (rand.nextInt() & (opcode & 0x00FF));
                break;
            case 0xD000:
                draw(opcode, vx, vy);
                break;
            case 0xE000:
                //key operations
                switch (opcode & 0x00FF) {
                    case 0x009E:
                        //skip next instruction if key at VX is pressed
                        if (state.keys[state.V[vx]])
                            state.PC += 2;
                        break;
                    case 0x00A1:
                        //skip next instruction if key at VX is not pressed
                        if (!(state.keys[state.V[vx]]))
                            state.PC += 2;
                        break;
                }
                break;
            case 0xF000:
                switch (opcode & 0x00FF) {
                    case 0x0007:
                        //set vx = timer delay
                        state.V[vx] = state.delay_timer;
                        break;
                    case 0x000A:
                        //wait for a key press, store it in vx
                    {
                        boolean keypress = false;
                        for (byte i = 0; i < State.CHIP8_KEY_COUNT; i++) {
                            if (state.keys[i]) {
                                state.V[vx] = i;
                                keypress = true;
                                break;
                            }
                        }
                        if (!keypress) {
                            //should be blocking (e.g. we should not advance PC)
                            state.PC -= 2;
                        }
                    }
                    break;
                    case 0x0015:
                        //set delay timer to VX
                        state.delay_timer = state.V[vx];
                        break;
                    case 0x0018:
                        //set sound timer to VX
                        state.sound_timer = state.V[vx];
                        break;
                    case 0x001E:
                        // add vx to I
                        state.I += state.V[vx];
                        break;
                    case 0x0029:
                        //set i to the location of sprite for the character in vx
                        state.I = (char) (state.V[vx] * 5 + State.FONT_OFFSET);
                        break;
                    case 0x0033:
                        //take the decimal representation of VX, place the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.
                        state.memory[state.I] = (byte) (state.V[vx] / 100);
                        state.memory[state.I + 1] = (byte) ((state.V[vx] / 10) % 10);
                        state.memory[state.I + 2] = (byte) ((state.V[vx] % 100) % 10);
                        break;
                    case 0x0055:
                        //fill memory at I to I+x (inclusive!) with values of v0 to vx
                        //I should stay incremented afterwards
                        for (int i = 0; i <= vx; i++) {
                            state.memory[state.I + i] = state.V[i];
                        }
                        break;
                    case 0x0065:
                        //fill v0 to vx with values from memory starting at i
                        //I should stay incremented afterwards
                        for (int i = 0; i <= vx; i++) {
                            state.V[i] = state.memory[state.I + i];
                        }
                        break;
                    default:
                        System.out.println("Unknown opcode: " + charToHex(opcode));
                        break;
                }
                break;
            default:
                System.out.println("Unknown opcode: " + charToHex(opcode));
                break;
        }

        if (state.total_cycles % 9 == 0)
            update_timers();
        state.total_cycles++;

        if(debug) {
            StringBuffer sb = new StringBuffer();
            sb.append("PC:");
            appendHex(sb, state.PC);
            sb.append(" OP:");
            appendHex(sb, opcode);
            sb.append(" I:");
            appendHex(sb, state.I);
            sb.append(" DT:");
            appendByte(sb, state.delay_timer);
            System.out.println(sb.toString());
        }
    }

    static String charToHex(char ch) {
        StringBuffer sb = new StringBuffer();
        appendHex(sb, ch);
        return sb.toString();
    }

    static void appendHex(StringBuffer sb, char val) {
        char hival = (char) (val >> 8);
        sb.append(hex.charAt((hival & 0xf0) >> 4));
        sb.append(hex.charAt((hival & 0x0f)));
        sb.append(hex.charAt((val & 0xf0) >> 4));
        sb.append(hex.charAt(val & 0x0f));
    }

    static void appendByte(StringBuffer sb, byte val) {
        sb.append(hex.charAt((val & 0xf0) >> 4));
        sb.append(hex.charAt(val & 0x0f));
    }

    private static final String hex = "0123456789ABCDEF";
}
