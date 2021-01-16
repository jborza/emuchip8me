package com.jborza.chip8me;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Vector;

public class RomStorage {
    public byte[] getRom(String name){
        InputStream is = this.getClass().getResourceAsStream("/roms/"+name);

        try{
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int read;
            //pretend max ROM size is 4k
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
                "particle.ch8"
        };
        return roms;
    }
}
