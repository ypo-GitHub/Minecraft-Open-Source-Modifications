package vestige.module.impl.movement;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.network.play.client.C07PacketPlayerDigging;
import vestige.event.Event;
import vestige.event.impl.EventMotionUpdate;
import vestige.event.impl.EventPostMotionUpdate;
import vestige.event.impl.EventSendPacket;
import vestige.event.impl.EventSlowdown;
import vestige.module.Category;
import vestige.module.Module;
import vestige.setting.impl.ModeSetting;
import vestige.setting.impl.NumberSetting;
import vestige.util.network.PacketUtils;

public class Noslow extends Module {
	
	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "NCP", "AAC4", "AAC5", "Hypixel", "NoGround");
	public NumberSetting forward = new NumberSetting("Forward", 1, 0.2, 1, 0.05, this);
	public NumberSetting strafe = new NumberSetting("Strafe", 1, 0.2, 1, 0.05, this);
	
	private boolean stoppedBlocking = true;
	
	public Noslow() {
		super("Noslow", Category.MOVEMENT);
		this.addSettings(mode, forward, strafe);
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
			
			if(player.isUsingItem()) {
				if(mode.is("NCP")) {
					if(player.isUsingSword()) {
						packet.releaseUseItem(false);
					}
				} else if(mode.is("AAC4")) {
					if(player.isUsingSword() && mc.thePlayer.ticksExisted % 2 == 0) {
						packet.releaseUseItem(false);
					}
				} else if(mode.is("AAC5")) {
					packet.sendBlocking(false, false);
				} else if(mode.is("NoGround")) {
					e.setOnGround(false);
				} else if(mode.is("Hypixel")) {
					mc.thePlayer.setSprinting(false);
					if(mc.thePlayer.ticksExisted % 2 == 0) {
						PacketUtils.sendBlocking(false, false);
					}
				}
			} else {
				if(mode.is("AAC5") && !stoppedBlocking) {
					packet.releaseUseItem(false);
					stoppedBlocking = true;
				}
			}
		} else if(event instanceof EventPostMotionUpdate) {
			if(mode.is("NCP")) {
				if(player.isUsingSword()) {
					packet.sendBlocking(false, false);
				}
			} else if(mode.is("AAC4")) {
				if(player.isUsingSword() && mc.thePlayer.ticksExisted % 2 == 0) {
					packet.sendBlocking(false, false);
				}
			}
		} else if(event instanceof EventSendPacket) {
			EventSendPacket e = (EventSendPacket) event;
			if(e.getPacket() instanceof C07PacketPlayerDigging) {
				C07PacketPlayerDigging packet = (C07PacketPlayerDigging) e.getPacket();
				if(packet.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
					if(mode.is("AAC5")) {
						//e.setCancelled(true);
					}
				}
			}
		} else if(event instanceof EventSlowdown) {
			EventSlowdown e = (EventSlowdown) event;
			if(mode.is("AAC4") && player.isUsingItem() && !player.isUsingSword()) {
				return;
			}
			if(forward.getValue() == 1 && strafe.getValue() == 1) {
				e.setCancelled(true);
			} else {
				e.setForward((float) forward.getValue());
				e.setStrafe((float) strafe.getValue());
			}
		}
	}

}
