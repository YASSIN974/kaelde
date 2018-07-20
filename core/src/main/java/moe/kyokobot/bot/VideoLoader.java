package moe.kyokobot.bot;

import com.google.common.io.LittleEndianDataInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class VideoLoader {
    public static void main(String... args) throws IOException {
        File file = new File("data.ivf");

        FileInputStream fis = new FileInputStream(file);
        byte[] magic = new byte[4];
        fis.read(magic);
        //System.out.printf("%x%x%x%x%n", magic[0], magic[1], magic[2], magic[3]);
        if (magic[0] == (byte) 0x44
                && magic[1] == (byte) 0x4B
                && magic[2] == (byte) 0x49
                && magic[3] == (byte) 0x46) {
            LittleEndianDataInputStream is = new LittleEndianDataInputStream(fis);

            int version = is.readUnsignedShort();
            int headersize = is.readUnsignedShort();
            is.read(magic);
            int width = is.readUnsignedShort();
            int height = is.readUnsignedShort();
            int framerate = is.readInt();
            int timescale = is.readInt();
            int frames = is.readInt();
            System.out.println("Version: " + version);
            System.out.println("Header size: " + headersize);
            System.out.println("Codec: " + new String(magic));
            System.out.println("Width: " + width);
            System.out.println("Height: " + height);
            System.out.println("Frame rate: " + framerate);
            System.out.println("Time scale: " + timescale);
            System.out.println("Number of frames: " + frames);
            is.readInt(); // unused

            int size;
            long timestamp;

            for (int frame = 0; frame < frames; frame++) {
                System.out.println("Reading frame " + frame + "...");
                size = is.readInt();
                timestamp = is.readLong();
                if (size > is.available()) {
                    System.out.println("Frame size is bigger than file size!");
                    return;
                }
                System.out.println("Size: " + size);
                System.out.println("Timestamp: " + timestamp);
                magic = new byte[size];
                is.read(magic);

                FileOutputStream fos = new FileOutputStream(new File("frame" + frame + ".bin"));
                fos.write(magic);
                fos.close();
            }

        } else {
            System.out.println("Invalid IVF file!");
        }
    }
}
