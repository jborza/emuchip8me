package com.jborza.chip8me;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class RomStorage {
    public byte[] getRom(String name){
        InputStream is = this.getClass().getResourceAsStream("/roms/"+name);

        try{
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int read;
            //limit ROM size at 4k
            byte[] buffer = new byte[4096];
            read = is.read(buffer, 0, buffer.length);
            os.write(buffer,0,read);
            return os.toByteArray();
        }
        catch(Exception e){
            System.out.println(e);
            return new byte[0];
        }
    }

    public String[] getRoms(){
        String[] roms = {
                "logo.ch8",
                "demo-poo.ch8",
                "brix.ch8",
                "test_opcode.ch8",
                "sierpinski.ch8",
                "lunar.ch8",
                "particle.ch8",
                "random-flip.ch8",
                "scrolling-dot.ch8",
                "mondrian.ch8",
                "astrododge.ch8",
                "chip8.ch8",
                "keypad-test.ch8",
                "15-puzzle.ch8"
        };
        return roms;
    }
}
