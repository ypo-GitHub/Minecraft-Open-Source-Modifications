package vestige.module.impl.movement;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventReceivePacket;
import vestige.event.impl.EventUpdate;
import vestige.module.Category;
import vestige.module.Module;
import vestige.processor.impl.PacketsProcessor;
import vestige.setting.impl.ModeSetting;
import vestige.setting.impl.NumberSetting;

public class BoatFly extends Module {
	
	private boolean wasRidingEntity = false;
	private boolean highjumping = false;
	private int ticksHighJumping = 0;
	
	private ModeSetting mode = new ModeSetting("Mode", "Redesky", "Redesky" , "Redesky longjump");
	private NumberSetting speed = new NumberSetting("Speed", 0.4, 0.1, 0.6, 0.05, this);
	
	public BoatFly() {
		super("BoatFly", Category.MOVEMENT, Keyboard.KEY_NONE);
		this.addSettings(mode, speed);
	}
	
	public void onEnable() {
		wasRidingEntity = false;
		highjumping = false;
		ticksHighJumping = 0;
	}
	
	public void onDisable() {
		mc.thePlayer.speedInAir = 0.02F;
		mc.timer.timerSpeed = 1F;
		Vestige.getPacketsProcessor().setBlinking(false);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventReceivePacket) {
			EventReceivePacket event = (EventReceivePacket) e;
			
			if(event.getPacket() instanceof S08PacketPlayerPosLook) {
				if(mode.is("Redesky") && ticksHighJumping > 20) {
					highjumping = false;
					mc.timer.timerSpeed = 1F;
					Vestige.getPacketsProcessor().setBlinking(false);
					mc.thePlayer.speedInAir = 0.02F;
				}
			}
		} else if(e instanceof EventUpdate) {
			this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
			if(mode.is("Redesky longjump")) {
				/*
				if(mc.thePlayer.isRiding()) {
					wasRidingEntity = true;
				} else {
					if(wasRidingEntity) {
						ticksHighJumping = 0;
						highjumping = true;
						wasRidingEntity = false;
					}
				}
				
				if(highjumping) {
					//mc.timer.timerSpeed = 2.5F;
					if(ticksHighJumping == 0) {
						mc.thePlayer.motionY = 0.55;
						mc.thePlayer.speedInAir = 0.07F;
						move.setSpeed(move.getBaseMoveSpeed() + 0.06);
					} else if(mc.thePlayer.motionY > 0.3) {
						mc.thePlayer.motionY += ticksHighJumping < 8 ? 0.08 : 0.035;
						mc.thePlayer.speedInAir = ticksHighJumping < 8 ? 0.15F : 0.04F;
					} else {
						mc.thePlayer.speedInAir = 0.02F;
						highjumping = false;
					}
					if(!highjumping) {
						mc.timer.timerSpeed = 1F;
					}
				}
				
				if(ticksHighJumping < 100) {
					ticksHighJumping++;
				}
				*/
				if(mc.thePlayer.isRiding()) {
					wasRidingEntity = true;
				} else {
					if(wasRidingEntity) {
						ticksHighJumping = 0;
						highjumping = true;
						wasRidingEntity = false;
					}
				}
				
				if(highjumping) {
					//mc.timer.timerSpeed = 2.5F;
					if(ticksHighJumping == 0) {
						Vestige.getPacketsProcessor().setBlinking(true);
						mc.thePlayer.motionY = 0.5;
						move.setSpeed(move.getBaseMoveSpeed() + 0.06);
					} else if(mc.thePlayer.motionY > 0.3) {
						mc.thePlayer.motionY += ticksHighJumping < 8 ? 0.14 : 0.06;
						mc.thePlayer.speedInAir = (float) Math.max(speed.getValue() - (ticksHighJumping * 0.03), 0.03);
					} else {
						mc.thePlayer.speedInAir = 0.02F;
						highjumping = false;
						//Vestige.getPacketsProcessor().setBlinking(false);
					}
					if(!highjumping) {
						mc.timer.timerSpeed = 1F;
					}
				}
				
				Vestige.getPacketsProcessor().setBlinking(ticksHighJumping < 25);
				
				if(ticksHighJumping < 100) {
					ticksHighJumping++;
				}
			} else if(mode.is("Redesky")) {
				if(mc.thePlayer.isRiding()) {
					wasRidingEntity = true;
				} else {
					if(wasRidingEntity) {
						ticksHighJumping = 0;
						highjumping = true;
						wasRidingEntity = false;
					}
				}
				
				if(highjumping) {
					if(mc.thePlayer.onGround) {
						move.jump(0.5);
					} else {
						if(ticksHighJumping > 6) {
							mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 0.24 : 0;
							mc.timer.timerSpeed = 0.6F;
							if(ticksHighJumping < 20) {
								mc.thePlayer.speedInAir = (float) speed.getValue();
							} else {
								mc.thePlayer.speedInAir = 0.02F;
								mc.thePlayer.motionX *= 0.9;
								mc.thePlayer.motionZ *= 0.9;
							}
							
							if(!move.isWalking() && ticksHighJumping > 10) {
								move.setSpeed(0);
							}
						}
					}
					
					double limitSpeed = 0.3127;
					
					//mc.thePlayer.motionX = Math.min(mc.thePlayer.motionX, move.getBaseMoveSpeed() + limitSpeed);
					//mc.thePlayer.motionX = Math.max(mc.thePlayer.motionX, -move.getBaseMoveSpeed() + limitSpeed);
					
					//mc.thePlayer.motionZ = Math.min(mc.thePlayer.motionZ, move.getBaseMoveSpeed() + limitSpeed);
					//mc.thePlayer.motionZ = Math.max(mc.thePlayer.motionZ, -move.getBaseMoveSpeed() + limitSpeed);
					
					Vestige.getPacketsProcessor().setBlinking(ticksHighJumping < 35);
					
					if(ticksHighJumping > 35) {
						mc.timer.timerSpeed = 1F;
					}
					
					if(ticksHighJumping > 45 || (mc.thePlayer.onGround && ticksHighJumping > 20) || (mc.gameSettings.keyBindSneak.isKeyDown() && ticksHighJumping > 15)) {
						highjumping = false;
						mc.timer.timerSpeed = 1F;
						Vestige.getPacketsProcessor().setBlinking(false);
						mc.thePlayer.speedInAir = 0.02F;
					}
					
					if(mc.gameSettings.keyBindSneak.isKeyDown() && ticksHighJumping > 15) {
						mc.gameSettings.keyBindSneak.pressed = false;
					}
				}
				
				if(ticksHighJumping < 100) {
					ticksHighJumping++;
				}
			}
		}
	}
	
}
