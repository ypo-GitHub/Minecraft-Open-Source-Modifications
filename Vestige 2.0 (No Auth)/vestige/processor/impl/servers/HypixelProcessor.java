package vestige.processor.impl.servers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventMotionUpdate;
import vestige.event.impl.EventReceivePacket;
import vestige.event.impl.EventSendPacket;
import vestige.module.impl.combat.Killaura;
import vestige.util.base.BaseUtil;
import vestige.util.misc.MathUtils;
import vestige.util.movement.MovementUtils;
import vestige.util.network.PacketUtils;

public class HypixelProcessor implements BaseUtil {
	
	public void onEvent(Event event) {
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S08PacketPlayerPosLook) {
				if(mc.getNetHandler().doneLoadingTerrain && mc.thePlayer.ticksExisted < 100) {
					S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
		            e.setCancelled(true);
		            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.getX(), packet.getY(), packet.getZ(), false));
				}
	        }
		} else if(event instanceof EventSendPacket) {
			EventSendPacket e = (EventSendPacket) event;
			if(e.getPacket() instanceof C03PacketPlayer) {
				C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
	            if(mc.thePlayer.ticksExisted <= Math.round(MathUtils.randomNumber(15, 20))) {
	            	packet.setX(packet.getPositionX() + MathUtils.randomNumber(-MathUtils.randomNumber(1, 300), MathUtils.randomNumber(1, 300)));
	            	packet.setY(packet.getPositionY() + MathUtils.randomNumber(-MathUtils.randomNumber(1, 300), MathUtils.randomNumber(1, 300)));
	            	packet.setZ(packet.getPositionZ() + MathUtils.randomNumber(-MathUtils.randomNumber(1, 300), MathUtils.randomNumber(1, 300)));
	            	packet.setYaw((float) MathUtils.randomNumber(-180, 180));
	            	packet.setPitch((float) MathUtils.randomNumber(-90, 90));
	            	packet.setMoving(false);
	            } else if(PacketUtils.isPacketNoMoveNoRotate(packet) && !mc.thePlayer.isUsingItem()) {
	            	e.setCancelled(true);
	            }
	        } else if(e.getPacket() instanceof C17PacketCustomPayload) {
	            C17PacketCustomPayload packet = (C17PacketCustomPayload) e.getPacket();
	            if (packet.getChannelName().equals("MC|Brand")) {
	            	e.setPacket(new C17PacketCustomPayload(packet.getChannelName(), new PacketBuffer(Unpooled.buffer()).writeString("vanilla")));
	            }
	        }
		} else if(event instanceof EventMotionUpdate) {
			if(mc.thePlayer.ticksExisted < 10) {
				Vestige.getPacketsProcessor().setMovePacketsDelay(0);
				Vestige.getPacketsProcessor().setPingPacketsDelay(0);
			}
		}
		
		/*
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S08PacketPlayerPosLook) {
				S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
				if(mc.getNetHandler().doneLoadingTerrain) {
					if(mc.thePlayer.ticksExisted < 100) {
						for(int i = 0; i < 10; i++) {
							PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false));
						}
						e.setCancelled(true);
						Vestige.addChatMessage("test");
					} else {
						packet.setX(packet.getX() - Double.MIN_VALUE);
						packet.setZ(packet.getZ() + Double.MIN_VALUE);
					}
				}
			}
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			if(Vestige.getModuleManager().getModuleByName("Speed").isEnabled()) {
				e.setYaw((float) MovementUtils.getHypixelDirection());
			}
			
			if(mc.thePlayer.ticksExisted < 10) {
				Vestige.getPacketsProcessor().setMovePacketsDelay(0);
				Vestige.getPacketsProcessor().setPingPacketsDelay(0);
			}
		}
		*/
	}
	
}