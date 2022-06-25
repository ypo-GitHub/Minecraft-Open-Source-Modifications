package vestige.module.impl.movement;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventMotionUpdate;
import vestige.event.impl.EventPostMotionUpdate;
import vestige.event.impl.EventReceivePacket;
import vestige.event.impl.EventRender;
import vestige.event.impl.EventUpdate;
import vestige.event.impl.MovementEvent;
import vestige.module.Category;
import vestige.module.ListeningType;
import vestige.module.Module;
import vestige.setting.impl.BooleanSetting;
import vestige.setting.impl.ModeSetting;
import vestige.setting.impl.NumberSetting;
import vestige.util.misc.TimerUtil;
import vestige.util.movement.FrictionUtil;
import vestige.util.movement.MovementUtils;
import vestige.util.network.ServerUtils;

public class Speed extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "NCP", "AAC", "Redesky", "Hypixel", "Blocksmc", "Verus", "MMC", "Hycraft");
	
	private BooleanSetting autoJump = new BooleanSetting("AutoJump", true, this);
	private NumberSetting vanillaSpeed = new NumberSetting("Vanilla Speed", 1, 0.1, 5, 0.1, this);
	
	private NumberSetting timer = new NumberSetting("Timer", 1, 0.1, 4, 0.1, this);
	
	private ModeSetting aacMode = new ModeSetting("AAC Mode", "AAC3", "AAC3", "AAC4", "AAC5");
	
	private ModeSetting hypixelMode = new ModeSetting("Hypixel Mode", "Hop", "Hop", "FastFall", "LowHop");
	
	private ModeSetting verusMode = new ModeSetting("Verus Mode", "Hop", "Hop", "LowHop");
	
	private FrictionUtil frictionUtil = new FrictionUtil();
	
	private TimerUtil timerUtil = new TimerUtil();
	
	private double speed, lastSpeed;
	private boolean prevOnGround;
	
	private double lastDirection;
	
	private int jumpsAmount;
	
	public Speed() {
		super("Speed", Category.MOVEMENT, ListeningType.ALWAYS, Keyboard.KEY_B);
		this.addSettings(mode, aacMode, autoJump, vanillaSpeed, hypixelMode, verusMode, timer);
	}
	
	public void onEnable() {
		jumpsAmount = 0;
		
		if(mode.is("Hypixel")) {
			speed = move.getCurrentMotion();
			lastDirection = Math.toRadians(mc.thePlayer.rotationYaw);
		} else if(mode.is("NCP")) {
			speed = move.getBaseMoveSpeed() + 0.05;
		} else if(mode.is("Blocksmc")) {
			speed = move.getBaseMoveSpeed();
		}
		
		lastSpeed = move.getCurrentMotion();
	}
	
	public void onDisable() {
		mc.timer.timerSpeed = 1F;
		mc.thePlayer.speedInAir = 0.02F;
		Vestige.getPacketsProcessor().setBlinking(false);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRender) {
			autoJump.setShowed(mode.is("Vanilla"));
			vanillaSpeed.setShowed(mode.is("Vanilla"));
			
			aacMode.setShowed(mode.is("AAC"));
			hypixelMode.setShowed(mode.is("Hypixel"));
			verusMode.setShowed(mode.is("Verus"));
			
			timer.setShowed(mode.is("Hypixel"));
		}
		
		if(!isEnabled()) {
			return;
		}
		
		if(e instanceof EventUpdate) {
			this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
		}
		
		switch(mode.getMode()) {
		case "Vanilla":
			Vanilla(e);
			break;
		case "NCP":
			NCP(e);
			break;
		case "AAC":
			AAC(e);
			break;
		case "Redesky":
			Redesky(e);
			break;
		case "Hypixel":
			Hypixel(e);
			break;
		case "Verus":
			Verus(e);
			break;
		case "Blocksmc":
			BlocksMC(e);
			break;
		case "MMC":
			MMC(e);
			break;
		case "Hycraft":
			Hycraft(e);
			break;
		case "SpoofGround Strafe":
			SpoofGroundStrafe(e);
			break;
		}
	}
	
	private void Vanilla(Event e) {
		if(e instanceof MovementEvent) {
			MovementEvent event = (MovementEvent) e;
			if(autoJump.isEnabled()) {
				if(mc.thePlayer.onGround && move.isWalking()) {
					event.setMotionY(0.42);
				}
			}
			mc.thePlayer.motionY = event.getMotionY();
			move.setSpeed(event, vanillaSpeed.getValue());
		}
	}
	
	private void NCP(Event event) {
		/*
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(move.isWalking()) {
				if(mc.thePlayer.onGround) {
					prevOnGround = true;
					move.jump();
					e.setMotionY(mc.thePlayer.motionY = 0.0625);
					speed += 0.105;
				} else {
					if(prevOnGround) {
						speed *= 0.7;
						prevOnGround = false;
					} else {
						speed = Math.max(move.getCurrentMotion(), speed - speed / 159);
					}
				}
				if(speed < move.getBaseMoveSpeed() * 0.6) {
					speed = move.getBaseMoveSpeed() * 0.6;
				}
			}
			
			speed = Math.min(speed, move.getBaseMoveSpeed() + 0.2);
			move.setSpeed(e, speed);
		} else if(event instanceof EventUpdate) {
			mc.timer.timerSpeed = 1F;
			if(move.isWalking()) {
				if(mc.thePlayer.onGround) {
					mc.timer.timerSpeed = 1.1F;
				} else {
					mc.timer.timerSpeed = 1.08F;
				}
			}
		}
		*/
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(move.isWalking()) {
				if(mc.thePlayer.onGround) {
					prevOnGround = true;
					move.jump();
					e.setMotionY(mc.thePlayer.motionY);
					speed += 0.2035;
				} else {
					if(prevOnGround) {
						speed *= 0.665;
						prevOnGround = false;
					} else {
						speed = Math.max(move.getCurrentMotion(), speed - speed / 159);
					}
				}
				if(speed < move.getBaseMoveSpeed() * 0.6) {
					speed = move.getBaseMoveSpeed() * 0.6;
				}
			}
			move.setSpeed(e, speed);
			
			speed = Math.min(speed, move.getBaseMoveSpeed() + 0.4);
		} else if(event instanceof EventUpdate) {
			mc.timer.timerSpeed = 1F;
			if(move.isWalking()) {
				if(mc.thePlayer.onGround) {
					mc.timer.timerSpeed = 1.12F;
				} else {
					mc.timer.timerSpeed = 1.08F;
				}
			}
		}
		/*
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(move.isWalking()) {
				if(mc.thePlayer.onGround) {
					prevOnGround = true;
					move.jump();
					e.setMotionY(mc.thePlayer.motionY);
					speed = move.getBaseMoveSpeed() + 0.1127;
				} else {
					if(prevOnGround) {
						speed *= 0.96;
						prevOnGround = false;
					} else {
						speed = Math.max(move.getCurrentMotion(), speed - speed / 159);
					}
				}
				if(speed < move.getBaseMoveSpeed() * 0.6) {
					speed = move.getBaseMoveSpeed() * 0.6;
				}
			}
			move.setSpeed(e, speed);
			
			speed = Math.min(speed, move.getBaseMoveSpeed() + 0.3527);
		} else if(event instanceof EventUpdate) {
			mc.timer.timerSpeed = 1F;
			if(move.isWalking()) {
				if(mc.thePlayer.onGround) {
					mc.timer.timerSpeed = 1.12F;
				} else {
					mc.timer.timerSpeed = 1.08F;
				}
			}
		}
		*/
	}
	
	private void AAC(Event event) {
		if(event instanceof EventUpdate) {
			if(aacMode.is("AAC4")) {
				mc.timer.timerSpeed = 1F;
				mc.thePlayer.speedInAir = 0.02F;
				if(move.isWalking()) {
					if(mc.thePlayer.onGround) {
						prevOnGround = true;
						if(!mc.gameSettings.keyBindJump.isKeyDown()) {
							mc.thePlayer.jump();
							mc.thePlayer.motionY -= 0.03;
						}
					} else {
						if(prevOnGround) {
							mc.thePlayer.speedInAir = 0.14F;
							prevOnGround = false;
						}
					}
				} else {
					mc.timer.timerSpeed = 1;
				}
				
				Vestige.getPacketsProcessor().setBlinking(mc.thePlayer.ticksExisted % 6 != 0);
			}
		} else if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(aacMode.is("AAC3")) {
				/*
				mc.thePlayer.speedInAir = 0.02F;
				mc.timer.timerSpeed = 1F;
				if(mc.thePlayer.onGround) {
					prevOnGround = true;
					if(move.isWalking()) {
						move.jump();
						e.setMotionY(mc.thePlayer.motionY);
						mc.timer.timerSpeed = 1.08F;
						move.setSpeed(e, Math.max(move.getCurrentMotion(), move.getBaseMoveSpeed() + 0.2));
						move.setSpeed(e, Math.min(move.getCurrentMotion(), move.getBaseMoveSpeed() + 0.3227));
					}
				} else {
					if(prevOnGround) {
						speed = move.getCurrentMotion() + 0.003;
					} else {
						speed = Math.max(speed - speed / 35, move.getCurrentMotion());
						speed *= 1.015;
					}
					
					if(mc.thePlayer.motionY > 0) {
						mc.timer.timerSpeed = 1.08F;
					}
					
					if(move.isWalking()) {
						move.setSpeed(e, speed);
					} else {
						move.setSpeed(e, 0);
					}
				}
				*/
				if(move.isWalking()) {
					if(mc.thePlayer.onGround) {
						e.setMotionY(0.41999998688698);
						mc.thePlayer.motionY = 0.42;
						speed += 0.2;
						speed = Math.max(move.getBaseMoveSpeed() + 0.31, speed);
						
						if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
							speed += 0.02 + mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.02;
						}
						prevOnGround = true;
					} else {
						if(prevOnGround) {
							speed *= 0.59;
							prevOnGround = false;
						} else {
							speed -= speed / 159;
						}
					}
				}
				
				speed = Math.max(speed, move.getBaseMoveSpeed());
				
				move.setSpeed(e, speed);
			} else if(aacMode.is("AAC5")) {
				mc.timer.timerSpeed = 1F;
				if(move.isWalking()) {
					if(mc.thePlayer.onGround) {
						mc.timer.timerSpeed = 1F;
						move.jump();
						e.setMotionX(mc.thePlayer.motionX);
						e.setMotionY(mc.thePlayer.motionY);
						e.setMotionZ(mc.thePlayer.motionZ);
						Vestige.getPacketsProcessor().setBlinking(true);
						prevOnGround = true;
					} else {
						double speed = 0.001;
						
						if(prevOnGround) {
							mc.timer.timerSpeed = 1.5F;
							Vestige.getPacketsProcessor().setBlinking(true);
							prevOnGround = false;
						} else {
							mc.timer.timerSpeed = 1.2F;
							Vestige.getPacketsProcessor().setBlinking(mc.thePlayer.ticksExisted % 3 != 0);
						}
						
						/*
						if(mc.thePlayer.motionX > 0.04) {
							e.setMotionX(mc.thePlayer.motionX + speed);
						} else if(mc.thePlayer.motionX < -0.04) {
							e.setMotionX(mc.thePlayer.motionX - speed);
						}
						
						if(mc.thePlayer.motionZ > 0.04) {
							e.setMotionZ(mc.thePlayer.motionZ + speed);
						} else if(mc.thePlayer.motionZ < -0.04) {
							e.setMotionZ(mc.thePlayer.motionZ - speed);
						}
						*/
					}
				} else {
					Vestige.getPacketsProcessor().setBlinking(false);
				}
			}
		}
	}
	
	private void Hypixel(Event event) {
		if(hypixelMode.is("Hop")) {
			if(event instanceof MovementEvent) {
				MovementEvent e = (MovementEvent) event;
				
				if(move.isWalking()) {
					if(mc.thePlayer.onGround) {
						e.setMotionY(0.41999998688698);
						mc.thePlayer.motionY = 0.42;
						speed += 0.2;
						speed = Math.max(move.getBaseMoveSpeed() + 0.2, speed);
						
						if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
							speed += 0.02 + mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.02;
						}
						prevOnGround = true;
					} else {
						if(prevOnGround) {
							speed *= 0.66 - Math.random() * 0.00001;
							prevOnGround = false;
						} else {
							speed -= speed / 159;
						}
					}
				}
				
				speed = Math.max(speed, move.getBaseMoveSpeed());
				
				setSpeedHypixel(e, speed);
			} else if(event instanceof EventUpdate) {
				mc.timer.timerSpeed = (float) timer.getValue();
				this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + "Hypixel Hop");
			}
		} else if(hypixelMode.is("FastFall")) {
			if(event instanceof MovementEvent) {
				MovementEvent e = (MovementEvent) event;
				
				if(move.isWalking()) {
					if(mc.thePlayer.onGround) {
						e.setMotionY(0.399);
						mc.thePlayer.motionY = 0.4;
						speed += 0.2;
						speed = Math.max(move.getBaseMoveSpeed() + 0.25, speed);
						
						if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
							speed += 0.02 + mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.02;
						}
						prevOnGround = true;
					} else {

							if(mc.thePlayer.motionY < 0.1D && mc.thePlayer.motionY > -0.23D) mc.thePlayer.motionY -= 0.08D + (Math.random() * .03);
						
						
						if(prevOnGround) {
							speed *= 0.64;
							prevOnGround = false;
						} else {
							speed -= speed / 159;
						}
					}
				}
				
				speed = Math.max(speed, move.getBaseMoveSpeed());
				
				setSpeedHypixel(e, speed);
			} else if(event instanceof EventUpdate) {
				mc.timer.timerSpeed = (float) timer.getValue();
				this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + "Hypixel FastFall");
			}
		} else if(hypixelMode.is("LowHop")) {
			if(event instanceof MovementEvent) {
				MovementEvent e = (MovementEvent) event;
				
				if(move.isWalking()) {
					if(mc.thePlayer.onGround) {
						if(mc.gameSettings.keyBindJump.isKeyDown()) {
							e.setMotionY(0.41999998688698);
							mc.thePlayer.motionY = 0.42;
							speed += 0.14;
							speed = Math.max(move.getBaseMoveSpeed() + 0.14, speed);
							
							if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
								speed += 0.02 + mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.02;
							}
						} else {
							e.setMotionY(mc.thePlayer.motionY = 0.0625);
							speed += 0.1;
							speed = Math.max(move.getBaseMoveSpeed() + 0.1, speed);
							
							if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
								speed += 0.02 + mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.02;
							}
						}
						prevOnGround = true;
					} else {
						if(prevOnGround) {
							speed *= 0.74;
							prevOnGround = false;
						} else {
							speed -= speed / 159;
						}
					}
				}
				
				speed = Math.max(speed, move.getBaseMoveSpeed());
				
				setSpeedHypixel(e, speed);
			} else if(event instanceof EventUpdate) {
				mc.timer.timerSpeed = (float) timer.getValue();
				this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + "Hypixel LowHop");
			}
		}
	}
	
	private void Verus(Event event) {
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(mc.thePlayer.onGround) {
				move.jump(0.42);
				e.setMotionY(0.41999998688698);
				
				if(mc.thePlayer.isSprinting() && mc.thePlayer.moveForward > 0) {
					speed = move.getBaseMoveSpeed() + 0.2127;
				} else {
					speed = move.getBaseMoveSpeed() + 0.1127;
				}
				
				prevOnGround = true;
			} else if(prevOnGround) {
				speed *= 0.8;
				prevOnGround = false;
			}
			
			//speed = Math.min(speed, move.getBaseMoveSpeed() + 0.3527);
			speed = Math.max(speed, move.getBaseMoveSpeed());
			
			move.setSpeed(e, speed);
		}
	}
	
	private void BlocksMC(Event event) {
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			
			if(move.isWalking()) {
				if(mc.thePlayer.onGround) {
					e.setMotionY(0.41999998688698);
					mc.thePlayer.motionY = 0.42;
					
					speed = move.getBaseMoveSpeed() + 0.2927;
					
					//speed += 0.2;
					//speed = Math.max(move.getBaseMoveSpeed() + 0.2327, speed);
					
					if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
						speed += 0.04 + mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() * 0.04;
					}
					prevOnGround = true;
					//mc.thePlayer.speedInAir = 0.021F;
				} else {
					if(prevOnGround) {
						speed *= 0.6 - Math.random() * 0.00001;
						prevOnGround = false;
					} else {
						speed -= speed / 159;
					}
				}
			}
			
			speed = Math.max(speed, move.getBaseMoveSpeed());
			
			move.setSpeed(e, speed);
		} else if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
					e.setCancelled(true);
					speed = move.getBaseMoveSpeed() + 0.5;
				}
			} else if(e.getPacket() instanceof S27PacketExplosion) {
				e.setCancelled(true);
				speed = move.getBaseMoveSpeed() + 0.5;
			}
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			//e.setYaw((float) move.getDirection());
		}
		
		if(event instanceof EventUpdate || event instanceof MovementEvent) {
			mc.thePlayer.setSprinting(true);
		}
	}
	
	private void SpoofGroundStrafe(Event event) {
		mc.gameSettings.keyBindJump.pressed = false;
		
		if(event instanceof EventUpdate) {
			if(move.isWalking()) {
				if(mc.thePlayer.posY % 0.5 == 0) {
					move.jump(0.42);
					move.setSpeed(move.getCurrentMotion());
				} else {
					if(mc.thePlayer.motionY < 0.34) {
						//mc.thePlayer.motionX *= 1.03;
						//mc.thePlayer.motionZ *= 1.03;
					} else {
						mc.thePlayer.motionX *= 0.9;
						mc.thePlayer.motionZ *= 0.9;
					}
				}
			}
			
			mc.thePlayer.onGround = true;
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			e.setOnGround(mc.thePlayer.posY % 0.5 == 0);
		}
	}
	
	private void Redesky(Event event) {
		if(event instanceof EventUpdate) {
			mc.timer.timerSpeed = 1F;
			if(move.isWalking()) {
				if(mc.thePlayer.onGround) {
					mc.timer.timerSpeed = 1F;
					move.jump();
					Vestige.getPacketsProcessor().setBlinking(true);
					prevOnGround = true;
					jumpsAmount++;
				} else {
					if(prevOnGround) {
						Vestige.getPacketsProcessor().setBlinking(true);
						prevOnGround = false;
					} else {
						Vestige.getPacketsProcessor().setBlinking(mc.thePlayer.ticksExisted % 3 != 0);
					}
					
					if(jumpsAmount % 2 == 0) {
						if(mc.thePlayer.ticksExisted % 2 == 0) {
							mc.timer.timerSpeed = 1.6F;
						} else {
							mc.timer.timerSpeed = 1.2F;
						}
					} else {
						mc.timer.timerSpeed = 0.9F;
					}
				}
			} else {
				Vestige.getPacketsProcessor().setBlinking(false);
			}
		} else if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			
			/*
			double speed = 0.001;
			
			if(mc.thePlayer.motionX > 0.04) {
				e.setMotionX(mc.thePlayer.motionX + speed);
			} else if(mc.thePlayer.motionX < -0.04) {
				e.setMotionX(mc.thePlayer.motionX - speed);
			}
			
			if(mc.thePlayer.motionZ > 0.04) {
				e.setMotionZ(mc.thePlayer.motionZ + speed);
			} else if(mc.thePlayer.motionZ < -0.04) {
				e.setMotionZ(mc.thePlayer.motionZ - speed);
			}
			*/
		}
	}
	
	private void MMC(Event event) {
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			double friction = lastSpeed * 0.91;
			double attributeSpeed = 0.026;
			
			double speed = friction + attributeSpeed;
			
			Vestige.getPacketsProcessor().setBlinking(mc.thePlayer.ticksExisted % 3 != 0);
			
			if(mc.thePlayer.onGround) {
				e.setMotionY(0.41999998688698);
				mc.thePlayer.motionY = 0.42;
				speed += 0.2;
				prevOnGround = true;
			} else if(prevOnGround) {
				speed *= 0.75;
				prevOnGround = false;
			}
			
			if(mc.thePlayer.hurtTime == 9) {
				speed += 0.2;
			}
			
			speed = Math.max(speed, move.getBaseMoveSpeed());
			
			this.speed = speed;
			move.setSpeed(e, this.speed);
			
			mc.thePlayer.setSprinting(move.isWalking());
			
			this.lastSpeed = move.getCurrentMotion();
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			//e.setYaw((float) move.getYawDirection());
		}
	}
	
	private void Hycraft(Event event) {
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			double friction = speed * 0.91;
			double attributeSpeed = 0.026;
			
			speed = friction + attributeSpeed;
			
			Vestige.getPacketsProcessor().setBlinking(mc.thePlayer.ticksExisted % 3 != 0);
			
			if(mc.thePlayer.onGround) {
				e.setMotionY(0.41999998688698);
				mc.thePlayer.motionY = 0.42;
				speed += 0.2;
				prevOnGround = true;
			} else if(prevOnGround) {
				speed *= 0.7;
				prevOnGround = false;
			}
			
			if(mc.thePlayer.hurtTime == 9) {
				//speed += 0.2;
			}
			
			speed = Math.max(speed, move.getBaseMoveSpeed());
			
			setSpeedHypixel(e, speed);
			
			mc.thePlayer.setSprinting(true);
			
			//this.lastSpeed = move.getCurrentMotion();
			
			mc.timer.timerSpeed = 1.3F;
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			//e.setYaw((float) move.getYawDirection());
			Vestige.getPacketsProcessor().setBlinking(mc.thePlayer.ticksExisted % 10 != 0);
		}
	}
	
	private void setSpeedHypixel(MovementEvent event, double speed) {
		if(MovementUtils.isWalking()) {
			event.setMotionX(mc.thePlayer.motionX = -Math.sin(getHypixelDirection()) * speed);
			event.setMotionZ(mc.thePlayer.motionZ = Math.cos(getHypixelDirection()) * speed);
		} else {
			event.setMotionX(mc.thePlayer.motionX = 0);
			event.setMotionZ(mc.thePlayer.motionZ = 0);
		}
	}
	
	private double getHypixelDirection() {
		boolean movingForward = mc.thePlayer.moveForward > 0.0F;
		boolean movingBackward = mc.thePlayer.moveForward < 0.0F;
		boolean movingRight = mc.thePlayer.moveStrafing > 0.0F;
		boolean movingLeft = mc.thePlayer.moveStrafing < 0.0F;
		
		boolean isMovingSideways = movingLeft || movingRight;
		boolean isMovingStraight = movingForward || movingBackward;
		
		double direction = MathHelper.wrapAngleTo180_double(mc.thePlayer.rotationYaw);
		
		if(movingForward && !isMovingSideways) {
			
		} else if(movingBackward && !isMovingSideways) {
			direction += 180;
		} else if(movingForward && movingLeft) {
			direction += 45;
		} else if(movingForward) {
			direction -= 45;
		} else if(!isMovingStraight && movingLeft) {
			direction += 90;
		} else if(!isMovingStraight && movingRight) {
			direction -= 90;
		} else if(movingBackward && movingRight) {
			direction -= 135;
		} else if(movingBackward) {
			direction += 135;
		}
		
		
		if(lastDirection == Double.NaN) {
			lastDirection = direction;
		}
		
		double change = 45;
		
		if(Math.abs(direction - lastDirection) > 45 && Math.abs(direction - lastDirection) < 315) {
			//direction = lastDirection;
			if(direction > lastDirection) {
				direction += change;
			} else {
				direction -= change;
			}
		}
		
		lastDirection = direction;
		
		return Math.toRadians(direction);
	}
	
}