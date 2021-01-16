package com.jborza.chip8me;

public class CPU {
    public State state;

    public CPU(){
        state = new State();
    }

    public void loadRom(byte[] rom){
        System.arraycopy(rom, 0, state.memory, State.PROGRAM_OFFSET, rom.length);
    }

    public void loadFont() {
        System.arraycopy(SpriteFont.makeFont(), 0, state.memory, State.FONT_OFFSET, SpriteFont.FONT_SIZE);
    }

    public void reset(){
        state.PC = State.PROGRAM_OFFSET;
    }

    void draw(short opcode, byte vx, byte vy)
    {
        //DXYN
        //Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N+1 pixels.
        //Each row of 8 pixels is read as bit-coded starting from memory location I;
        //I value doesn’t change after the execution of this instruction.
        //VF is set to 1 if any screen pixels are flipped from set to unset
        //when the sprite is drawn, and to 0 if that doesn’t happen
        short rx = state.V[vx]; //sprite x
        short ry = state.V[vy]; //sprite y
        short sprite_height = (short)(opcode & 0x000F);
        short sprite_row;
        //reset collision bit
        state.V[0xF] = 0;

        for (int y = 0; y < sprite_height; y++)
        {
            sprite_row = state.memory[state.I + y];
            for (int x = 0; x < 8; x++)
            {
                //get the x-th pixel from the sprite row
                if ((sprite_row & (0x80 >> x)) != 0)
                {
                    int display_pixel_address = ((ry + y) * state.DISPLAY_WIDTH + rx + x) % (state.CHIP8_DISPLAY_SIZE);
                    if (state.display[display_pixel_address] == 1)
                    {
                        state.V[0xF] = 1;
                    }
                    state.display[display_pixel_address] ^= 1;
                }
            }
        }
//
        state.draw_flag = true;
    }

    public void emulate_op(){
        //annn
        //dxyn
        short opcode = state.memory[state.PC];
        opcode <<= 8;
        opcode |= state.memory[state.PC+1];
        state.PC += 2;
        byte vx,vy;
        vx = (byte)((opcode & 0x0F00) >> 8);
        vy = (byte)((opcode & 0x00F0) >> 4);
        switch (opcode & 0xF000) {
            case 0x0000:
                switch (opcode & 0x00FF) {
                    case 0x00E0:
                        //clear display
                        state.display = new byte[State.CHIP8_DISPLAY_SIZE];
                        break;
                    case 0x00EE:
                        state.PC = state.stack[state.stack_pointer];
                        state.stack_pointer--;
                        break;
                }
                break;
            case 0x1000:
                //jump to NNN
                state.PC = (short) (opcode & 0x0FFF);
                break;
            case 0x2000:
                //call subroutine at NNN
                state.stack_pointer++;
                state.stack[state.stack_pointer] = state.PC;
                state.PC = (short) (opcode & 0x0FFF);
                break;
            case 0x6000:
                //set register X to NN (6XNN)
                state.V[vx] = (byte) (opcode & 0x00FF);
                break;
            case 0x7000:
                //add NN to VX
                state.V[vx] += (byte) (opcode & 0x00FF);
                break;
            case 0xA000:
                //set index register to NNN
                state.I = (short) (opcode & 0x0FFF);
                break;
            case 0xD000:
                draw(opcode, vx, vy);
                break;
        }
    }
}
