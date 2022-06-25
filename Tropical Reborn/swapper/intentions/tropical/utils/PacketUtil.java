package swapper.intentions.tropical.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketUtil {
	static Minecraft mc = Minecraft.getMinecraft();
	
    public static void sendPacket(Packet packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void sendPacket(int sendTimes, Packet packet) {
        for (int i = 0; i < sendTimes; i++) {
            mc.getNetHandler().addToSendQueue(packet);
        }
    }

    public static void sendPacketNoEvent(Packet packet) {
        if(mc.getNetHandler() != null)
            mc.getNetHandler().addToSendQueueNoEvent(packet);
    }
    
    public static void sendPacketNoEventDelayed(Packet packet, long delay) {
    	Runnable gg =
    	        () -> {
	    	        try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						System.out.println("Thread interrupted");
					}
	    	        PacketUtil.sendPacketNoEvent(packet);
    	        };
    	Thread bliss = new Thread(gg);
    	bliss.start();
    }

    public static void sendPacketNoEvent(int sendTimes, Packet packet) {
        for (int i = 0; i < sendTimes; i++) {
            mc.getNetHandler().addToSendQueueNoEvent(packet);
        }
    }
}
