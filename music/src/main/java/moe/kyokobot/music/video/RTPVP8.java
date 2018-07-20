package moe.kyokobot.music.video;

/**
 * Encodes/Decodes RTP/VP8 packets
 *
 * http://tools.ietf.org/html/draft-ietf-payload-vp8-01
 *
 * @author pquiring
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RTPVP8 {
    private static final Logger log = LoggerFactory.getLogger(RTPVP8.class);

    public RTPVP8(int ssrc) {
        this.ssrc = ssrc;
    }

    public RTPVP8() {
        ssrc = new Random().nextInt();
    }

    /** Encodes raw VP8 data into multiple RTP packets. */
    public List<byte[]> encode(byte data[]) {
        ArrayList<byte[]> packets = new ArrayList<>();
        int len = data.length;
        int packetLength;
        int offset = 0;
        byte packet[];
        while (len > 0) {
            if (len > mtu) {
                packetLength = mtu;
            } else {
                packetLength = len;
            }
            packet = new byte[packetLength + 1];  //1=VP8 header, 12=RTP.length
            packet[0] = (byte)(packets.isEmpty() ? 0x10 : 0);  //X R N S PartID
            System.arraycopy(data, offset, packet, 1, packetLength);
            packets.add(packet);
            offset += packetLength;
            len -= packetLength;
        }
        timestamp += 100;  //??? 10 fps ???
        return packets;
    }

    //mtu = 1500 - 14(ethernet) - 20(ip) - 8(udp) - 12(rtp) - 1 (VP8) = 1445 bytes payload per packet
    private static final int mtu = 1350;
    private int seqnum;
    private int timestamp;
    private final int ssrc;
    private byte partial[];
    private int lastseqnum = -1;
}
