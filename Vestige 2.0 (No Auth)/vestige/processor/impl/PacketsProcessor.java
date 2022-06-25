package vestige.processor.impl;

import java.util.ArrayList;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.status.client.C01PacketPing;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventPostMotionUpdate;
import vestige.event.impl.EventReceivePacket;
import vestige.event.impl.EventRender;
import vestige.event.impl.EventSendPacket;
import vestige.event.impl.EventStrafing;
import vestige.event.impl.EventUpdate;
import vestige.util.base.BaseUtil;
import vestige.util.network.PacketSleepThread;
import vestige.util.network.PacketUtils;

public class PacketsProcessor implements BaseUtil {
	
	private final ArrayList<Packet> blinkingPackets = new ArrayList<>();
	private final ArrayList<Packet> pingPacketsQueue = new ArrayList<>();
	private final ArrayList<Packet> movePacketsQueue = new ArrayList<>();
	
	private boolean blinking;
	private long movePacketsDelay;
	private long pingPacketsDelay;

	private boolean clearedPackets;
	
	public void onEvent(Event event) {
		if(mc.isSingleplayer()) {
			return;
		}
		
		if(mc.thePlayer.ticksExisted < 3) {
			if(!clearedPackets) {
				clearPackets();
				pingPacketsQueue.clear();
				movePacketsQueue.clear();
				blinkingPackets.clear();
				blinking = false;
				clearedPackets = true;
			}
			movePacketsDelay = 0;
			pingPacketsDelay = 0;
			return;
		} else {
			clearedPackets = false;
		}
		
		if(event instanceof EventUpdate) {
			if(pingPacketsDelay > 0) {
				while(pingPacketsQueue.size() > pingPacketsDelay) {
					PacketUtils.sendPacketNoEvent(pingPacketsQueue.get(0));
					pingPacketsQueue.remove(0);
				}
			}
			if(movePacketsDelay > 0) {
				while(movePacketsQueue.size() > movePacketsDelay) {
					PacketUtils.sendPacketNoEvent(movePacketsQueue.get(0));
					movePacketsQueue.remove(0);
				}
			}
		}
		
		if(event instanceof EventSendPacket) {
			EventSendPacket e = (EventSendPacket) event;
			if(blinking && !e.isCancelled()) {
				e.setCancelled(true);
				blinkingPackets.add(e.getPacket());
				return;
			} else {
				sendPackets();
			}
			
			if(e.getPacket() instanceof C03PacketPlayer) {
				if(movePacketsDelay > 0 && !e.isCancelled()) {
					e.setCancelled(true);
					movePacketsQueue.add(e.getPacket());
					//PacketSleepThread thread = new PacketSleepThread(e.getPacket(), movePacketsDelay);
					//thread.run();
				}
			} else if(e.getPacket() instanceof C0FPacketConfirmTransaction || e.getPacket() instanceof C00PacketKeepAlive || e.getPacket() instanceof C01PacketPing) {
				if(pingPacketsDelay > 0 && !e.isCancelled()) {
					e.setCancelled(true);
					pingPacketsQueue.add(e.getPacket());
					//PacketSleepThread thread = new PacketSleepThread(e.getPacket(), movePacketsDelay);
					//thread.run();
				}
			}
		}
	}

	public ArrayList<Packet> getBlinkingPackets() {
		return blinkingPackets;
	}

	public boolean isBlinking() {
		return blinking;
	}

	public void setBlinking(boolean blinking) {
		this.blinking = blinking;
	}

	public long getMovePacketsDelay() {
		return movePacketsDelay;
	}

	public void setMovePacketsDelay(long movePacketsDelay) {
		this.movePacketsDelay = movePacketsDelay;
	}

	public long getPingPacketsDelay() {
		return pingPacketsDelay;
	}

	public void setPingPacketsDelay(long pingPacketsDelay) {
		this.pingPacketsDelay = pingPacketsDelay;
	}

	private void sendPackets() {
		if(!blinkingPackets.isEmpty()) {
			for(Packet p : blinkingPackets) {
				/*
				if(movePacketsDelay > 0 && p instanceof C03PacketPlayer) {
					PacketSleepThread thread = new PacketSleepThread(p, movePacketsDelay);
					thread.run();
				} else if(pingPacketsDelay > 0 && PacketUtils.isPingRelatedPacket(p)) {
					PacketSleepThread thread = new PacketSleepThread(p, pingPacketsDelay);
					thread.run();
				} else {
					PacketUtils.sendPacketNoEvent(p);
				}
				*/
				PacketUtils.sendPacketNoEvent(p);
			}
			clearPackets();
		}
	}
	
	private void clearPackets() {
		if(!blinkingPackets.isEmpty()) {
			blinkingPackets.clear();
		}
	}
	
	public ArrayList<Packet> getPingPacketsQueue() {
		return pingPacketsQueue;
	}
	
	public ArrayList<Packet> getMovePacketsQueue() {
		return movePacketsQueue;
	}
	
}