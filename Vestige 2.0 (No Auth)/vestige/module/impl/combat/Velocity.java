package vestige.module.impl.combat;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventReceivePacket;
import vestige.event.impl.EventRender;
import vestige.event.impl.EventUpdate;
import vestige.module.Category;
import vestige.module.ListeningType;
import vestige.module.Module;
import vestige.setting.impl.ModeSetting;
import vestige.setting.impl.NumberSetting;
import vestige.util.network.PacketUtils;

public class Velocity extends Module {
	
	public NumberSetting horizontal = new NumberSetting("Horizontal", 0, 0, 1, 0.01, this);
	public NumberSetting vertical = new NumberSetting("Vertical", 0, 0, 1, 0.01, this);
	
	public ModeSetting mode = new ModeSetting("Mode", "Cancel", "Cancel", "Edit", "AAC4", "AAC4 full", "Redesky", "Reduce");
	
	public int counter;
	public boolean receivedVelocity;
	
	private boolean stoppedBlinking;
	
	public Velocity() {
		super("Velocity", Category.COMBAT, ListeningType.ALWAYS);
		this.addSettings(mode, horizontal, vertical);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		Vestige.getPacketsProcessor().setBlinking(false);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRender) {
			horizontal.setShowed(mode.is("Edit") || mode.is("Reduce"));
			vertical.setShowed(mode.is("Edit"));
		}
	
		if(!stoppedBlinking && !mode.is("AAC4") && !mode.is("AAC4 full")) {
			Vestige.getPacketsProcessor().setBlinking(false);
			stoppedBlinking = true;
		}
		
		if(!isEnabled()) {
			return;
		}
		
		this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
		
		switch(mode.getMode()) {
		case "Cancel":
			Cancel(e);
			break;
		case "Edit":
			Edit(e);
			break;
		case "AAC4":
			AAC4(e);
			break;
		case "AAC4 full":
			AAC4Full(e);
			break;
		case "Redesky":
			Redesky(e);
			break;
		case "Reduce":
			Reduce(e);
			break;
		}
		
	}
	
	private void Cancel(Event event) {
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
					e.setCancelled(true);
				}
			} else if(e.getPacket() instanceof S27PacketExplosion) {
				S27PacketExplosion packet = (S27PacketExplosion) e.getPacket();
				e.setCancelled(true);
			}
		}
	}
	
	private void Edit(Event event) {
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
					packet.setMotionX((int) (packet.getMotionX() * horizontal.getValue()));
					packet.setMotionZ((int) (packet.getMotionX() * horizontal.getValue()));
					
					packet.setMotionY((int) (packet.getMotionY() * vertical.getValue()));
				}
			} else if(e.getPacket() instanceof S27PacketExplosion) {
				S27PacketExplosion packet = (S27PacketExplosion) e.getPacket();
				packet.setMotionX((float) (packet.getMotionX() * horizontal.getValue()));
				packet.setMotionZ((float) (packet.getMotionX() * horizontal.getValue()));
				
				packet.setMotionY((float) (packet.getMotionY() * vertical.getValue()));
			}
		}
	}
	
	private void AAC4(Event event) {
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
					receivedVelocity = true;
					stoppedBlinking = false;
					counter = 0;
				}
			} else if(e.getPacket() instanceof S27PacketExplosion) {
				S27PacketExplosion packet = (S27PacketExplosion) e.getPacket();
				receivedVelocity = true;
				stoppedBlinking = false;
				counter = 0;
			}
		} else if(event instanceof EventUpdate) {
			if(counter < 3) {
				Vestige.getPacketsProcessor().setBlinking(true);
			} else {
				Vestige.getPacketsProcessor().setBlinking(false);
			}
			
			if(counter < 3) {
				counter++;
			}
			
			if(mc.thePlayer.hurtTime == 8) {
				move.motionMult(0.6);
			}
			
			if(mc.thePlayer.hurtTime == 0) {
				receivedVelocity = false;
			}
		}
	}
	
	private void AAC4Full(Event event) {
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
					e.setCancelled(true);
					counter = 0;
					Vestige.getPacketsProcessor().setBlinking(true);
					stoppedBlinking = false;
				}
			}
		} else if(event instanceof EventUpdate) {
			if(counter < 3) {
				Vestige.getPacketsProcessor().setBlinking(true);
			} else {
				Vestige.getPacketsProcessor().setBlinking(false);
			}
			
			if(counter < 3) {
				counter++;
			}
		}
	}
	
	private void Redesky(Event event) {
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId() && !Vestige.getModuleManager().getModuleByName("Fly").isEnabled()) {
					receivedVelocity = true;
					e.setCancelled(true);
					mc.thePlayer.motionY = packet.getMotionY() / 8000.0D;
					move.motionMult(0.3);
					for(int i = 0; i < 5; i++) {
						PacketUtils.attackFakePlayer();
					}
				}
			}
		} else if(event instanceof EventUpdate) {
			if(mc.thePlayer.hurtTime == 0) {
				receivedVelocity = false;
			}
		}
	}
	
	private void Reduce(Event event) {
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
					receivedVelocity = true;
				}
			} else if(e.getPacket() instanceof S27PacketExplosion) {
				receivedVelocity = true;
			}
		} else if(event instanceof EventUpdate) {
			if(mc.thePlayer.hurtTime == 9) {
				move.motionMult(horizontal.getValue());
			}
			
			if(mc.thePlayer.hurtTime == 0) {
				receivedVelocity = false;
			}
		}
	}

}
