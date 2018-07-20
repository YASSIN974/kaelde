package moe.kyokobot.music.video;

import com.github.hoary.javaav.Encoder;
import net.dv8tion.jda.core.audio.VideoSendHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class TestVideoSender implements VideoSendHandler {
    private static final Logger logger = LoggerFactory.getLogger(TestVideoSender.class);
    private static int MAX_FRAME_SIZE = 1350;
    private static BufferedImage image;
    private static List<byte[]> packets = null;

    //private Encoder encoder;
    private byte[] frame = null;
    private int partnum = 0;

    static {
        /*image = new BufferedImage(640,360, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        g.drawString("YAY IT WORKS! owo", 20,20);
        g.drawString("https://kyokobot.moe", 20,50);
        g.drawString("IS THIS IT THE FIRST VIDEO TRANSMISSION", 20,80);
        g.drawString("IN THE WORLD BY A DISCORD BOT?", 20,100);*/

        try {
            FileInputStream fis = new FileInputStream(new File("frame0.bin"));
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            packets = new RTPVP8(2).encode(data);
            logger.info("Created {} VP8 packets!", packets.size());

        } catch (IOException e) {
            logger.error("Error loading vpx frame!", e);
        }
    }

    public TestVideoSender() {
        /*try {
            Options options = new Options();
            options.put("tune", "zerolatency");
            options.put("preset", "ultrafast");

            encoder = new Encoder(CodecID.VP8);
            encoder.setPixelFormat(PixelFormat.YUV420P);
            encoder.setImageWidth(640);
            encoder.setImageHeight(360);
            encoder.setGOPSize(25);
            encoder.setBitrate(2000000);
            encoder.setFramerate(25);
            encoder.open(options);

            VideoFrame videoFrame = VideoFrame.create(image);
            logger.debug("encoding video...");
            MediaPacket packet = encoder.encodeVideo(videoFrame);
            logger.debug("creating packets...");
            packets = new RTPVP8(2).encode(packet.getData().array());
            encoder.close();
        } catch (JavaAVException e) {
            e.printStackTrace();
        }*/

    }

    @Override
    public boolean canProvide() {
        return true;
    }

    @Override
    public byte[] provideVP8Frame() {
        //logger.debug("provide video frame!");
        getFrame();
        return frame;
    }

    private void getFrame() {
        if (packets != null) {
            if (partnum >= packets.size())
                partnum = 0;

            frame = packets.get(partnum++);
        }
    }
}
