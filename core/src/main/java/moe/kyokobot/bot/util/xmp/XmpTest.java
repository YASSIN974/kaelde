package moe.kyokobot.bot.util.xmp;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.nio.file.Files;

public class XmpTest {
    public static void main(String... args) throws Exception {
        AudioFormat af = new AudioFormat(44100, 16, 2, true, false);
        SourceDataLine line = AudioSystem.getSourceDataLine(af);

        // 24584
        byte[] buffer = new byte[4096];

        Player p = new Player(44100);
        byte[] fileContent = Files.readAllBytes(new File("drunk_razor_girl.xm").toPath());

        System.out.println("file len: " + fileContent.length);

        p.loadModule(fileContent);
        System.out.println("name: " + p.getModule().getName() + " type: " + p.getModule().getType());

        line.open(af);
        line.start();

        p.startPlayer();

        int o = 0;
        while (o == 0) {
            //System.out.println("write");
            if (line.available() >= buffer.length) {
                o = p.getBuffer(buffer, 0, buffer.length);
                line.write(buffer, 0, buffer.length);
            }
        }

        p.endPlayer();

        line.drain();
        line.stop();
        line.close();
    }
}
