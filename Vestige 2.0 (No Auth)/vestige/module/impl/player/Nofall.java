package vestige.module.impl.player;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.network.play.client.C03PacketPlayer;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventMotionUpdate;
import vestige.module.Category;
import vestige.module.Module;
import vestige.setting.impl.ModeSetting;
import vestige.util.network.PacketUtils;

public class Nofall extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "Edit", "Edit", "Hypixel", "Redesky", "Packet");
	
	private double currentFallDistance;
	private double ticksSpoofedGround;
	private boolean blinking;
	
	public Nofall() {
		super("Nofall", Category.PLAYER);
		this.addSettings(mode);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		if(blinking) {
			Vestige.getPacketsProcessor().setBlinking(false);
			blinking = false;
		}
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventMotionUpdate) {
			this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
			
			EventMotionUpdate e = (EventMotionUpdate) event;
			
			if(mc.thePlayer.onGround) {
				currentFallDistance = 0;
				ticksSpoofedGround = 0;
			} else {
				if(mc.thePlayer.motionY < 0) {
					currentFallDistance += -mc.thePlayer.motionY;
				}
			}
			
			switch(mode.getMode()) {
			case "Edit":
				if(currentFallDistance >= 3) {
					e.setOnGround(true);
					currentFallDistance = 0;
				}
				if(blinking) {
					Vestige.getPacketsProcessor().setBlinking(false);
					blinking = false;
				}
				break;
			case "Redesky":
				if(currentFallDistance >= 2 && ticksSpoofedGround < 6) {
					e.setOnGround(true);
					currentFallDistance = 0;
					ticksSpoofedGround++;
				}
				if(blinking) {
					Vestige.getPacketsProcessor().setBlinking(false);
					blinking = false;
				}
				break;
			case "Packet":
				if(currentFallDistance >= 2) {
					PacketUtils.sendPacket(new C03PacketPlayer(true));
					currentFallDistance = 0;
				}
				if(blinking) {
					Vestige.getPacketsProcessor().setBlinking(false);
					blinking = false;
				}
				break;
			case "NCP":
				if(mc.thePlayer.onGround) {
					if(blinking) {
						Vestige.getPacketsProcessor().setBlinking(false);
						blinking = false;
					}
				} else if(currentFallDistance > 0) {
					e.setOnGround(true);
					Vestige.getPacketsProcessor().setBlinking(true);
					blinking = true;
				}
				break;
			case "Hypixel":
				if(currentFallDistance > 3) {
					e.setOnGround(true);
					currentFallDistance = 0;
				}
				
				if(currentFallDistance > 1 && currentFallDistance < 2) {
					if(blinking) {
						Vestige.getPacketsProcessor().setBlinking(false);
						blinking = false;
					}
				} else {
					Vestige.getPacketsProcessor().setBlinking(true);
					blinking = true;
				}
				break;
			}
		}
	}

}