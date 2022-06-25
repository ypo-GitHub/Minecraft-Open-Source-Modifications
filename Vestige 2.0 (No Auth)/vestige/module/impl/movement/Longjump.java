package vestige.module.impl.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventReceivePacket;
import vestige.event.impl.EventRender;
import vestige.event.impl.EventSendPacket;
import vestige.event.impl.EventUpdate;
import vestige.event.impl.MovementEvent;
import vestige.module.Category;
import vestige.module.ListeningType;
import vestige.module.Module;
import vestige.module.impl.combat.Killaura;
import vestige.setting.impl.ModeSetting;
import vestige.util.movement.FrictionUtil;
import vestige.util.network.PacketUtils;
import vestige.util.player.DamageUtil;

public class Longjump extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "NCP", "NCP", "Hypixel", "AAC", "Hycraft");
	private ModeSetting aacMode = new ModeSetting("AAC Mode", "Minemora", "Minemora", "AAC4", "AAC4 Infinite", "skywars.com");
	
	private FrictionUtil frictionUtil = new FrictionUtil();
	
	private int counter, ticks;
	private boolean doneBoosting;
	private double speed;
	
	public Longjump() {
		super("Longjump", Category.MOVEMENT, ListeningType.ALWAYS, Keyboard.KEY_C);
		this.addSettings(mode, aacMode);
	}
	
	public void onEnable() {
		counter = 0;
		ticks = 0;
		if(mode.is("Hypixel")) {
			//DamageUtil.legitDamage();
			//Vestige.getPacketsProcessor().setBlinking(false);
		}
	}
	
	public void onDisable() {
		mc.timer.timerSpeed = 1F;
		mc.thePlayer.speedInAir = 0.02F;
		Vestige.getPacketsProcessor().setBlinking(false);
		
		if(mode.is("Hycraft")) {
			move.setSpeed(0);
		}
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRender) {
			aacMode.setShowed(mode.is("AAC"));
		}
		
		if(!isEnabled()) {
			return;
		}
		
		switch(mode.getMode()) {
		case "NCP":
			NCP(e);
			break;
		case "Hypixel":
			Hypixel(e);
			break;
		case "AAC":
			AAC(e);
			break;
		case "Hycraft":
			Hycraft(e);
			break;
		}
	}

	private void NCP(Event event) {
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(mc.thePlayer.onGround) {
				counter++;
			}
			if(counter > 1) {
				this.setEnabled(false);
				move.setSpeed(e, 0.1);
				return;
			}
			
			double friction = e.getMotionY() > 0 ? 159 : 60;
			
			BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockIce || mc.theWorld.getBlockState(pos).getBlock() instanceof BlockPackedIce) {
				frictionUtil.updateNCPFriction(e, 0.4219, 0.7127, 159, 1.6);
			} else {
				frictionUtil.updateNCPFriction(e, 0.4219, 0.2327, friction, 1.4);
			}
			
			if(!mc.thePlayer.onGround) {
				if(e.getMotionY() >= 0.2 && e.getMotionY() < 0.4) {
					e.setMotionY(mc.thePlayer.motionY = e.getMotionY() + 0.01);
				} else if(e.getMotionY() >= 0) {
					//e.setMotionY(mc.thePlayer.motionY = e.getMotionY() + 0.005);
				} else if(e.getMotionY() >= -0.4) {
					e.setMotionY(mc.thePlayer.motionY = e.getMotionY() + 0.0455);
				} else {
					e.setMotionY(mc.thePlayer.motionY = e.getMotionY() + 0.016);
				}
			}
			
			/*
			if(e.getMotionY() >= 0.08) {
				e.setMotionY(e.getMotionY() + 0.005);
			} else if(e.getMotionY() < 0.08 && e.getMotionY() > -0.01) {
				e.setMotionY(e.getMotionY() + 0.05);
			} else if(e.getMotionY() < 0 && e.getMotionY() > -0.15) {
				e.setMotionY(e.getMotionY() + 0.0972);
			} else {
				e.setMotionY(e.getMotionY() + 0.11);
			}
			*/
		}
	}
	
	private void Hypixel(Event event) {
		/*
		if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(mc.thePlayer.getEntityId() == packet.getEntityID()) {
					e.setCancelled(true);
				}
			}
		}
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(mc.thePlayer.onGround) {
				counter++;
			}
			
			if(counter > 2) {
				move.setSpeed(e, 0);
				e.setMotionY(0);
				this.setEnabled(false);
				return;
			}
			
			double speed = 0.2827 * (mc.thePlayer.moveForward + 0.02);
			
			if(counter < 2) {
				return;
			}
			
			BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
			if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockIce || mc.theWorld.getBlockState(pos).getBlock() instanceof BlockPackedIce) {
				frictionUtil.updateNCPFriction(e, 0.5, 0.7127, mc.thePlayer.motionY > 0 ? 120 : 60, 0.68);
			} else {
				frictionUtil.updateNCPFriction(e, 0.5, mc.thePlayer.isPotionActive(Potion.moveSpeed) ? speed + 0.05 : speed, e.getMotionY() > 0 ? 140 : 60, mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.69 : 0.64);
			}
			
			if(e.getMotionY() == 0.54) {
				ticks = 0;
			}
			
			if(ticks <= 11 && ticks > 0) {
				e.setMotionY(e.getMotionY() + 0.08);
			} else if(e.getMotionY() >= 0.08) {
				e.setMotionY(e.getMotionY() + 0.03);
			} else if(e.getMotionY() < 0.08 && e.getMotionY() > -0.01) {
				e.setMotionY(e.getMotionY() + 0.045);
			} else if(e.getMotionY() < 0 && e.getMotionY() > -0.15) {
				e.setMotionY(e.getMotionY() + 0.07);
			} else {
				e.setMotionY(e.getMotionY() + 0.1);
			}
			
			ticks++;
		}
		*/
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(ticks < 14) {
				if(ticks < 13) {
					move.setSpeed(e, 0);
				}
				e.setMotionY(0);
				if(ticks == 13) {
					DamageUtil.legitDamage();
				}
			} else if(ticks == 14) {
				e.setMotionY(0.649);
				mc.thePlayer.motionY = 0.65;
				move.setSpeed(e, speed = move.getBaseMoveSpeed() + 0.2527);
				doneBoosting = false;
			} else {
				Vestige.getPacketsProcessor().setBlinking(false);
				if(mc.thePlayer.onGround) {
					this.setEnabled(false);
					return;
				}
				
				if(!doneBoosting) {
					speed *= 0.69;
					doneBoosting = true;
				} else {
					mc.thePlayer.motionY += 0.03;
					e.setMotionY(e.getMotionY() + 0.03);
					
					if(e.getMotionY() > 0) {
						speed -= speed / 100;
					} else {
						speed -= speed / 50;
					}
				}
				
				move.setSpeed(e, speed);
			}
		} else if(event instanceof EventSendPacket) {
			EventSendPacket e = (EventSendPacket) event;
			if(e.getPacket() instanceof C03PacketPlayer) {
				e.setCancelled(ticks < 10);
				ticks++;
			}
		} else if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	private void AAC(Event event) {
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(aacMode.is("Minemora")) {
				if(mc.thePlayer.onGround) {
					e.setMotionY(mc.thePlayer.motionY = 0.42);
					move.jump();
				} else {
					if(mc.thePlayer.motionY <= -0.08) {
						e.setMotionY(mc.thePlayer.motionY = 0);
					}
				}
				move.setSpeed(e, Math.max(move.getCurrentMotion(), move.getBaseMoveSpeed()));
			}
		} else if(event instanceof EventUpdate) {
			if(aacMode.is("AAC4 Infinite")) {
				if(mc.thePlayer.onGround) {
					mc.timer.timerSpeed = 2F;
					if(!mc.gameSettings.keyBindJump.isKeyDown()) {
						mc.thePlayer.jump();
					}
					mc.thePlayer.motionY += 0.03F;
					mc.thePlayer.motionX *= 1.04;
					mc.thePlayer.motionZ *= 1.04;
				} else {
					AAC4Longjump();
				}
			} else if(aacMode.is("AAC4")) {
				if(counter > 0 && mc.thePlayer.onGround) {
					this.setEnabled(false);
					return;
				}
				
				mc.timer.timerSpeed = 1F;
				if(mc.thePlayer.onGround) {
					doneBoosting = false;
					move.jump();
					if(mc.thePlayer.isSprinting()) {
						move.speedBoost(0.2F);
					}
					mc.thePlayer.motionY += 0.2;
					Vestige.getPacketsProcessor().setBlinking(true);
					counter++;
				} else {
					if(!doneBoosting) {
						mc.thePlayer.motionY += 0.08;
						if(mc.thePlayer.isSprinting()) {
							move.speedBoost(0.2F);
						}
						Vestige.getPacketsProcessor().setBlinking(true);
						doneBoosting = true;
					} else {
						Vestige.getPacketsProcessor().setBlinking(mc.thePlayer.ticksExisted % 3 != 0);
					}
					
					AAC4Longjump();
				}
			} else if(aacMode.is("skywars.com")) {
				if(counter > 0 && mc.thePlayer.onGround) {
					this.setEnabled(false);
					return;
				}
				
				mc.timer.timerSpeed = 1F;
				if(mc.thePlayer.onGround) {
					doneBoosting = false;
					move.jump();
					if(mc.thePlayer.isSprinting()) {
						move.speedBoost(0.1F);
					}
					mc.thePlayer.motionY += 0.16;
					Vestige.getPacketsProcessor().setBlinking(true);
					counter++;
				} else {
					if(!doneBoosting) {
						mc.thePlayer.motionY += 0.08;
						Vestige.getPacketsProcessor().setBlinking(true);
						doneBoosting = true;
						mc.thePlayer.speedInAir = 0.1F;
					} else {
						Vestige.getPacketsProcessor().setBlinking(mc.thePlayer.ticksExisted % 3 != 0);
						
						mc.timer.timerSpeed = 0.5F;
						if(mc.thePlayer.motionY > 0.1) {
							mc.thePlayer.motionY += 0.055;
							mc.thePlayer.speedInAir = 0.05F;
						} else {
							mc.thePlayer.speedInAir = 0.02F;
						}
					}
				}
			}
		}
	}
	
	private void Hycraft(Event event) {
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(ticks < 22) {
				move.setSpeed(e, 0);
				e.setMotionY(0);
				if(ticks == 18) {
					DamageUtil.legitDamage();
				}
			} else if(ticks == 22) {
				e.setMotionY(0.41999998688698);
				mc.thePlayer.motionY = 0.42;
				move.setSpeed(e, speed = 1);
				PacketUtils.verusRightClick();
			} else {
				Vestige.getPacketsProcessor().setBlinking(true);
				if(mc.thePlayer.onGround) {
					this.setEnabled(false);
					return;
				}
				speed *= 0.91;
				speed += 0.03;
				move.setSpeed(e, speed);
			}
		} else if(event instanceof EventSendPacket) {
			EventSendPacket e = (EventSendPacket) event;
			if(e.getPacket() instanceof C03PacketPlayer) {
				e.setCancelled(ticks < 18);
				ticks++;
			}
		} else if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	private void AAC4Longjump() {
		if(mc.thePlayer.motionY > 0) {
			mc.timer.timerSpeed = 0.85F;
			mc.thePlayer.speedInAir = 0.025F;
			if(mc.thePlayer.motionY > 0.2) {
				mc.thePlayer.motionX *= 1.017F;
				mc.thePlayer.motionZ *= 1.017F;
			} else {
				mc.thePlayer.motionX *= 1.0112F;
				mc.thePlayer.motionZ *= 1.0112F;
			}
			mc.thePlayer.motionY += 0.03F;
		} else {
			mc.timer.timerSpeed = 0.35F;
			mc.thePlayer.motionY += 0.02F;
			mc.thePlayer.speedInAir = 0.025F;
			if(mc.thePlayer.motionY < -0.6F) {
				if(mc.thePlayer.motionX > 0.05) {
					mc.thePlayer.motionX += 0.027F;
				} else if(mc.thePlayer.motionX < -0.05) {
					mc.thePlayer.motionX -= 0.027F;
				}
				if(mc.thePlayer.motionZ > 0.05) {
					mc.thePlayer.motionZ += 0.027F;
				} else if(mc.thePlayer.motionZ < -0.05) {
					mc.thePlayer.motionZ -= 0.027F;
				}
			} else {
				if(mc.thePlayer.motionX > 0.05) {
					mc.thePlayer.motionX += 0.017F;
				} else if(mc.thePlayer.motionX < -0.05) {
					mc.thePlayer.motionX -= 0.017F;
				}
				if(mc.thePlayer.motionZ > 0.05) {
					mc.thePlayer.motionZ += 0.017F;
				} else if(mc.thePlayer.motionZ < -0.05) {
					mc.thePlayer.motionZ -= 0.017F;
				}
			}
		}
	}

}
