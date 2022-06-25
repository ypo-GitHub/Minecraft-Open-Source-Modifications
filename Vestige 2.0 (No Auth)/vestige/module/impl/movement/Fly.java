package vestige.module.impl.movement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventMotionUpdate;
import vestige.event.impl.EventPostMotionUpdate;
import vestige.event.impl.EventReceivePacket;
import vestige.event.impl.EventRender;
import vestige.event.impl.EventSendPacket;
import vestige.event.impl.EventUpdate;
import vestige.event.impl.MovementEvent;
import vestige.module.Category;
import vestige.module.ListeningType;
import vestige.module.Module;
import vestige.module.impl.combat.Killaura;
import vestige.module.impl.world.Scaffold;
import vestige.setting.impl.ModeSetting;
import vestige.setting.impl.NumberSetting;
import vestige.util.movement.MovementUtils;
import vestige.util.network.PacketUtils;
import vestige.util.network.ServerUtils;
import vestige.util.player.DamageUtil;
import vestige.util.player.ScaffoldUtils;

public class Fly extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Hypixel", "Hypixel dmg", "Redesky", "Verus", "Minemora", "MMC", "Hycraft");
	
	private ModeSetting vanillaMode = new ModeSetting("Vanilla Mode", "Motion", "Motion", "Creative");
	private NumberSetting vanillaSpeed = new NumberSetting("Vanilla Speed", 1, 0.2, 9, 0.2, this);
	private NumberSetting vanillaVerticalSpeed = new NumberSetting("Vanilla Vertical Speed", 1, 0.2, 9, 0.2, this);
	
	private NumberSetting timer = new NumberSetting("Timer", 1, 0.1, 4, 0.1, this);
	
	private NumberSetting hypixelPhaseMotion = new NumberSetting("Hypixel Phase Motion", -0.2, -0.5, -0.02, 0.02, this);
	
	private ModeSetting verusMode = new ModeSetting("Verus mode", "Latest", "Latest", "Latest dmg");
	private NumberSetting verusSpeed = new NumberSetting("Verus speed", 2, 0.5, 9, 0.5, this);
	
	private BlockPos originalPos;
	private double x, y, z;
	
	private ArrayList<BlockPos> blocks = new ArrayList<>();
	
	private double speed;
	private int counter, ticks;
	private boolean lagbacked;
	private boolean started;
	
	public Fly() {
		super("Fly", Category.MOVEMENT, ListeningType.ALWAYS, Keyboard.KEY_G);
		this.addSettings(mode, vanillaMode, vanillaSpeed, vanillaVerticalSpeed, verusMode, verusSpeed, timer, hypixelPhaseMotion);
	}
	
	public void onEnable() {
		originalPos = new BlockPos(x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ);
		ticks = counter = 0;
		lagbacked = false;
		
		started = false;
		
		if(mode.is("Hypixel dmg")) {
			DamageUtil.hypixelDamage();
			mc.thePlayer.jump();
			
			Vestige.getPacketsProcessor().setBlinking(true);
			speed = move.getBaseMoveSpeed() + 0.1;
			ticks = 1;
		} else if(mode.is("Minemora")) {
			Vestige.getPacketsProcessor().setBlinking(true);
			mc.thePlayer.jump();
			move.setSpeed(0);
		} else if(mode.is("Verus latest")) {
			if(verusMode.is("Verus")) {
				speed = move.getBaseMoveSpeed();
			} else if(verusMode.is("Latest dmg")) {
				speed = verusSpeed.getValue();
			}
		}
	}
	
	public void onDisable() {
		mc.thePlayer.capabilities.isFlying = false;
		mc.timer.timerSpeed = 1F;
		if(mode.is("Vanilla")) {
			mc.thePlayer.motionX *= 0.1;
			mc.thePlayer.motionY *= 0.1;
			mc.thePlayer.motionZ *= 0.1;
		}
		
		if(mode.is("Hypixel")) {
			PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
			Vestige.getPacketsProcessor().setBlinking(false);
		}
		
		if(mode.is("Hypixel dmg")) {
			//DamageUtil.Hypixel dmgDamage(x, y, z);
			Vestige.getPacketsProcessor().setBlinking(false);
			move.setSpeed(Math.min(speed, move.getBaseMoveSpeed()));
		}
		
		if(mode.is("Minemora")) {
			move.setSpeed(0);
			PacketUtils.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX , mc.thePlayer.posY, mc.thePlayer.posZ, true));
			DamageUtil.verusDamage();
			PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
			for(int i = 0; i < 6; i++) {
				Vestige.getPacketsProcessor().getBlinkingPackets().add(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
			}
			mc.thePlayer.setSprinting(false);
			Vestige.getPacketsProcessor().setBlinking(false);
		}
		
		if(mode.is("Blocksmc")) {
			Vestige.getPacketsProcessor().setBlinking(false);
			Vestige.getPacketsProcessor().setPingPacketsDelay(0);
		}
		
		if(mode.is("MMC")) {
			Vestige.getPacketsProcessor().setBlinking(false);
			mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;
		}
		
		if(mode.is("Hycraft")) {
			Vestige.getPacketsProcessor().setBlinking(false);
			move.setSpeed(0);
			mc.thePlayer.motionY = 0;
		}
		
		if(!blocks.isEmpty()) {
			for(BlockPos pos : blocks) {
				mc.theWorld.setBlockToAir(pos);	
			}
			blocks.clear();
		}
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRender) {
			vanillaMode.setShowed(mode.is("Vanilla"));
			vanillaSpeed.setShowed(mode.is("Vanilla") && vanillaMode.is("Motion"));
			vanillaVerticalSpeed.setShowed(mode.is("Vanilla") && vanillaMode.is("Motion"));
			timer.setShowed(mode.is("Hypixel"));
			hypixelPhaseMotion.setShowed(mode.is("Hypixel"));
			verusMode.setShowed(mode.is("Verus"));
			verusSpeed.setShowed(mode.is("Verus") && verusMode.is("Latest dmg"));
			
			this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
		}
		
		if(!isEnabled()) {
			return;
		}
		
		switch(mode.getMode()) {
		case "Vanilla":
			Vanilla(e);
			break;
		case "Hypixel":
			Hypixel(e);
			break;
		case "Hypixel dmg":
			HypixelDmg(e);
			break;
		case "Verus":
			Verus(e);
			break;
		case "Blocksmc":
			BlocksMC(e);
			break;
		case "Redesky":
			Redesky(e);
			break;
		case "Minemora":
			Minemora(e);
			break;
		case "MMC":
			MMC(e);
			break;
		case "Hycraft":
			Hycraft(e);
			break;
		}
	}

	private void Vanilla(Event e) {
		if(e instanceof MovementEvent) {
			mc.timer.timerSpeed = 1F;
			MovementEvent event = (MovementEvent) e;
			if(vanillaMode.is("Motion")) {
				move.setSpeed(event, vanillaSpeed.getValue());
				
				if(mc.gameSettings.keyBindJump.isKeyDown()) {
					event.setMotionY(vanillaVerticalSpeed.getValue());
				} else if(mc.gameSettings.keyBindSneak.isKeyDown()) {
					event.setMotionY(-vanillaVerticalSpeed.getValue());
				} else {
					event.setMotionY(0);
				}
				mc.thePlayer.motionY = 0;
			} else if(vanillaMode.is("Creative")) {
				mc.thePlayer.capabilities.isFlying = true;
			}
		}
	}
	
	private void Hypixel(Event event) {
		/*
		if(!lagbacked && mc.thePlayer.motionY < 0 && !mc.thePlayer.onGround) {
			//mc.thePlayer.noClip = true;
		} else {
			//mc.thePlayer.noClip = false;
		}
		
		if(event instanceof EventUpdate) {
			Vestige.getPacketsProcessor().setBlinking(false);
			if(lagbacked) {
				mc.thePlayer.onGround = true;
				if(ticks < 40) {
					mc.timer.timerSpeed = (float) timer.getValue();
				} else {
					mc.timer.timerSpeed = 0.6F;
				}
				
				//Vestige.getPacketsProcessor().setBlinking(ticks > 5 && ticks % 4 != 0);
			} else {
				mc.timer.timerSpeed = 1F;
			}
			
			ticks++;
		} else if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(lagbacked) {
				e.setMotionY(mc.thePlayer.motionY = 0);
				//move.setSpeed(move.getBaseMoveSpeed() * 0.6);
			} else {
				if(mc.thePlayer.onGround) {
					e.setMotionY(0.41999998688698);
					mc.thePlayer.motionY = 0.42;
				} else if(e.getMotionY() < -0 && e.getMotionY() > -0.1) {
					boolean foundSlot = false;
					int itemSpoofed = 0;
					
					for(int i = 0; i < 9; i++) {
						if (mc.thePlayer.inventory.getStackInSlot(i) == null)
							continue;
						if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !ScaffoldUtils.blockBlacklist.contains(((ItemBlock) mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {
							itemSpoofed = i;
							foundSlot = true;
							break;
						}
					}
					if(foundSlot) {
						BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ);
						PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(itemSpoofed));
						PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(pos, EnumFacing.UP.getIndex(), mc.thePlayer.inventory.getStackInSlot(itemSpoofed), 0, 1, 0));
						mc.thePlayer.swingItem();
						//e.setMotionY(hypixelPhaseMotion.getValue());
					} else {
						this.setEnabled(false);
						Vestige.addChatMessage("You need blocks in your hotbar to fly !");
						return;
					}
				}
				move.setSpeed(0);
			}
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			if(!lagbacked && mc.thePlayer.motionY >= -0.1) {
				e.setPitch(90);
			} else if(lagbacked) {
				//e.setPitch(90);
				e.setOnGround(true);
			}
		} else if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S08PacketPlayerPosLook) {
				lagbacked = true;
			} else if(e.getPacket() instanceof S21PacketChunkData || e.getPacket() instanceof S22PacketMultiBlockChange || e.getPacket() instanceof S23PacketBlockChange || e.getPacket() instanceof S24PacketBlockAction) {
				if(!lagbacked) {
					e.setCancelled(true);
				}
			}
		}
		*/
		if(event instanceof EventUpdate) {
			if(lagbacked) {
				//mc.thePlayer.onGround = true;
			}
			mc.timer.timerSpeed = 1F;
		} else if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(lagbacked) {
				e.setMotionY(mc.thePlayer.motionY = 0);
			} else {
				move.setSpeed(0);
				if(mc.thePlayer.onGround && ticks == 0) {
					e.setMotionY(mc.thePlayer.motionY = 0.0625);
					//mc.theWorld.setBlockToAir(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ));
				}
			}
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			if(ticks > 1 && ticks < 6) {
				e.setY(originalPos.getY() - 0.08);
			}
			ticks++;
		} else if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S08PacketPlayerPosLook) {
				lagbacked = true;
			}
		}
	}
	
	private void HypixelDmg(Event event) {
		if(event instanceof EventMotionUpdate) {
			mc.timer.timerSpeed = 0.6F;
			EventMotionUpdate e = (EventMotionUpdate) event;
			speed = Math.max(move.getBaseMoveSpeed(), speed);
			speed -= speed / 159;
			
			if(ticks % 3 != 0) {
				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1E-8, mc.thePlayer.posZ);
			} else {
				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1E-8, mc.thePlayer.posZ);
			}
			
			if(ticks > 9) {
				this.setEnabled(false);
				return;
			}

			mc.thePlayer.motionY = 0;
			move.setSpeed(speed);
			ticks++;
		}
	}
	
	private void Redesky(Event event) {
		if(event instanceof EventUpdate) { 
			mc.thePlayer.setSprinting(true);
			
            if(mc.thePlayer.onGround) {
            	mc.thePlayer.speedInAir = 0.02F;
            	move.jump(1);
            	speed = 0.135;
            	mc.timer.timerSpeed = 1F;
            } else {
            	if(!lagbacked) {
            		if(mc.thePlayer.motionY < 0.9) {
            			mc.thePlayer.jumpMovementFactor = 0.2F;
            		}
            	} else {
            		mc.thePlayer.jumpMovementFactor = (float) speed;
            	}
            }
            
            if(move.isWalking()) {
				mc.timer.timerSpeed = 1F;
        	} else {
        		mc.timer.timerSpeed = 0.1F;
        	}
		}
		if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
            if(mc.thePlayer.ticksExisted % 10 == 0) {
            	e.setOnGround(true);
            }
		} else if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
				
				if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
					e.setCancelled(true);
				}
			} else if(e.getPacket() instanceof S27PacketExplosion) {
				e.setCancelled(true);
			} else if(e.getPacket() instanceof S08PacketPlayerPosLook) {
				lagbacked = true;
			}
		}
	}
	
	private void Verus(Event event) {
		if(verusMode.is("Latest")) {
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
					
					ticks = 0;
				} else if(ticks == 1) {
					speed *= 0.8;
				} else {
					speed += 0.0003;
				}

				//speed = Math.min(speed, move.getBaseMoveSpeed() + 0.3527);
				speed = Math.max(speed, move.getBaseMoveSpeed());
				
				move.setSpeed(e, speed);
				ticks++;
			} else if(event instanceof EventUpdate) {
				if(mc.thePlayer.fallDistance > (mc.gameSettings.keyBindJump.isKeyDown() ? 0 : 0.6)) {
					BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
					if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
						if(!blocks.isEmpty()) {
							for(BlockPos pos1 : blocks) {
								mc.theWorld.setBlockToAir(pos1);
							}
							blocks.clear();
						}
						
						mc.theWorld.setBlockState(pos, Blocks.barrier.getDefaultState());
						blocks.add(pos);
					}
				}
			} else if(event instanceof EventMotionUpdate) {
				EventMotionUpdate e = (EventMotionUpdate) event;
			}
		} else if(verusMode.is("Latest dmg")) {
			if(event instanceof MovementEvent) {
				MovementEvent e = (MovementEvent) event;
				if(ticks <= 3) {
					e.setCancelled(true);
				} else if(ticks == 4) {
					
				} else if(ticks == 5) {
					DamageUtil.verusDamage();
					move.jump(0.42);
					e.setMotionY(0.41999998688698);
				} else {
					speed -= speed / 10;
					
					if(mc.thePlayer.onGround) {
						this.setEnabled(false);
						return;
					}
				}
				
				speed = Math.max(move.getBaseMoveSpeed(), speed);
				
				move.setSpeed(e, speed);
			} else if(event instanceof EventMotionUpdate) {
				ticks++;
			} else if(event instanceof EventSendPacket) {
				EventSendPacket e = (EventSendPacket) event;
				if(e.getPacket() instanceof C03PacketPlayer) {
					if(ticks <= 3) {
						e.setCancelled(true);
					}
				}
 			}
		}
	}
	
	private void BlocksMC(Event event) {
		if(event instanceof EventUpdate) {
			if(mc.thePlayer.fallDistance > (mc.gameSettings.keyBindJump.isKeyDown() ? 0 : 0.6)) {
				BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
				if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
					if(!blocks.isEmpty()) {
						for(BlockPos pos1 : blocks) {
							mc.theWorld.setBlockToAir(pos1);
						}
						blocks.clear();
					}
					
					mc.theWorld.setBlockState(pos, Blocks.barrier.getDefaultState());
					blocks.add(pos);
				}
			}
			
			if(mc.thePlayer.onGround) {
				move.jump();
			}
		} else if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S08PacketPlayerPosLook) {
				S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
				PacketUtils.sendPacketNoEvent(new C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false));
				e.setCancelled(true);
			}
		}
	}
	
	private void Minemora(Event e) {
		if(e instanceof EventMotionUpdate) {
			Vestige.getPacketsProcessor().setBlinking(true);
			mc.timer.timerSpeed = 0.8F;
			move.setSpeed(2);
			mc.thePlayer.motionY = 0;
			if(mc.gameSettings.keyBindJump.isKeyDown()) {
				//move.setSpeed(1);
				mc.thePlayer.motionY = 2;
			}
		}
	}
	
	private void MMC(Event event) {
		/*
		if(event instanceof EventUpdate) {
			mc.timer.timerSpeed = started ? 0.8F : 1.0F;
			if(started) {
				if(counter < 100) {
					counter++;
				} else {
					this.setEnabled(false);
					return;
				}
			}
		} else if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
			if(!started || counter < 10) {
				e.setCancelled(true);
				return;
			}
			//player.setSpeed(e, 9);
			move.setSpeed(e, 2.2);
			e.setMotionY(mc.thePlayer.motionY = 0);
			if(mc.gameSettings.keyBindJump.isKeyDown()) {
				e.setMotionY(1);
			}
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			if(!started) {
				mc.timer.timerSpeed = 1F;
				
				started = true;
				counter = 8;
				if(mc.thePlayer.onGround) {
					//e.setY(mc.thePlayer.posY - 4);
				}
				
				//mc.gameSettings.keyBindForward.pressed = false;
				if(counter > 5) {
					started = true;
				}
			} else {
				Vestige.getPacketsProcessor().setBlinking(true);
			}
		} else if(event instanceof EventSendPacket) {
			EventSendPacket e = (EventSendPacket) event;
			if(e.getPacket() instanceof C03PacketPlayer || e.getPacket() instanceof C02PacketUseEntity) {
				if(started && counter > 7) {
					e.setCancelled(counter % 4 != 0 || !move.isWalking());
				}
			}
		}
		*/
		if(event instanceof MovementEvent) {
			MovementEvent e = (MovementEvent) event;
		} else if(event instanceof EventUpdate) {
			if(mc.thePlayer.fallDistance > (mc.gameSettings.keyBindJump.isKeyDown() ? 0 : 0.6)) {
				BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
				if(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
					if(!blocks.isEmpty()) {
						for(BlockPos pos1 : blocks) {
							mc.theWorld.setBlockToAir(pos1);
						}
						blocks.clear();
					}
					
					mc.theWorld.setBlockState(pos, Blocks.barrier.getDefaultState());
					blocks.add(pos);
				}
			}
			
			if(mc.thePlayer.onGround) {
				move.jump();
			}
		} else if(event instanceof EventMotionUpdate) {
			EventMotionUpdate e = (EventMotionUpdate) event;
			Vestige.getPacketsProcessor().setBlinking(mc.thePlayer.motionY > 0.2 || mc.thePlayer.motionY < 0);
		}
	}
	
	private void Hycraft(Event event) {

		if(ticks == 3) {
			DamageUtil.legitDamage();
		} else {
			if(ticks > 0) {
				mc.thePlayer.motionY = 0;
				mc.thePlayer.onGround = true;
				MovementUtils.setSpeed(0.25);
			}
		}
		
		ticks++;
	}
	
}