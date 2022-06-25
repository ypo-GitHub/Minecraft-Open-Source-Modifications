package swapper.intentions.tropical.module.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.BooleanSetting;
import swapper.intentions.tropical.settings.settings.ModeSetting;
import swapper.intentions.tropical.settings.settings.NumberSetting;
import swapper.intentions.tropical.utils.MoveUtils;
import swapper.intentions.tropical.utils.PacketUtil;

import java.util.ArrayList;

public class LongJump extends Module {
	public ModeSetting mode = new ModeSetting("Mode", "Vulcan", "Hypixel");
	public NumberSetting speed = new NumberSetting("Speed", 2, 0.1, 0.1, 8);
	public NumberSetting hurt = new NumberSetting("Hurt Time", 2, 1, 0, 9);
	public BooleanSetting high = new BooleanSetting("High Jump", true);
	public double step;
	public double ticks;

	public LongJump() {
		super("LongJump", "jumps idk", Keyboard.KEY_Y, Category.MOVEMENT);
		addSettings(mode, speed, hurt, high);
	}

	@Override
	protected void onEnable() {
		step = 0;
		ticks = 0;
	}

	@Override
	public void onRenderAlways() {
		hurt.setHidden(!mode.getCurrentValue().equals("Vulcan"));
		high.setHidden(!mode.getCurrentValue().equals("Vulcan"));
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent event) {
		switch(mode.getCurrentValue()) {
			case "Vulcan": {
				if(!event.isPre())
					return;
				if(mc.thePlayer.onGround) {
					if(step == 5)
						this.toggle();
					if(step <= 3) {
						event.setOnGround(false);
						mc.thePlayer.jump();
						step++;
					}
				}
				if(mc.thePlayer.hurtTime != 0) {
					step = 5;
					if(mc.thePlayer.hurtTime == 9 && high.getValue()) {
						mc.thePlayer.motionY = 0.6;
					}
					if(mc.thePlayer.hurtTime > hurt.getValue().intValue()) {
						if(mc.thePlayer.motionY < 0.44) {
							mc.thePlayer.jump();

							float f = mc.thePlayer.rotationYaw * 0.017453292F;
							mc.thePlayer.motionX -= MathHelper.sin(f) * -0.007F;
							mc.thePlayer.motionZ += MathHelper.cos(f) * -0.007F;
							while (MoveUtils.getSpeed() > 0.4F) {
								mc.thePlayer.motionX *= 0.958F;
								mc.thePlayer.motionZ *= 0.958F;
							}
						}
					}
				}
				if(step == 5 && !mc.thePlayer.onGround) {
					if(mc.thePlayer.fallDistance > 0.02) {
						if(mc.thePlayer.ticksExisted % 2 == 0)
							mc.thePlayer.motionY = -0.1;
						else
							mc.thePlayer.motionY = -0.165;
					}
				}
				break;
			}
			case "Hypixel":
				ticks++;
				if(ticks == 9) {
					double x = mc.thePlayer.posX,
							y = mc.thePlayer.posY,
							z = mc.thePlayer.posZ;
					for (int i = 0; i < 66; i++) {
						mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0624399212, z, false));
						mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
					}
					mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
					MoveUtils.strafe(0.4875F);
					mc.thePlayer.motionY = 0.7;
					mc.timer.timerSpeed = 0.9f;
				}else if(ticks > 9) {
					if(ticks == 10) {
						MoveUtils.strafe(0.4725F);
					}
					if((ticks > 26 && ticks < 32) || ticks == 44)
						mc.thePlayer.motionY += 0.021F;
					MoveUtils.strafe(MoveUtils.getSpeed());
					if(mc.thePlayer.onGround && ticks > 6) {
						this.toggle();
					}
				}
				break;
		}
	}

	@Subscribe
	public void onMove(MoveEvent event) {
		switch(mode.getCurrentValue()) {
			case "Vulcan": {
				if(step <= 4) {
					MoveUtils.strafe(event, 0.0);
				}
				break;
			}
			case "Hypixel":
				if(ticks < 9)
					event.setCancelled(true);
				break;
		}
	}

	@Subscribe
	public void onPacket(PacketEvent event) {
		if(event.getPacket() instanceof S12PacketEntityVelocity) {
			if(((S12PacketEntityVelocity) event.getPacket()).getEntityID() == mc.thePlayer.getEntityId())
				event.setCancelled(true);
		}
		if(event.getPacket() instanceof C03PacketPlayer && ticks < 8 && mode.getCurrentValue() == "Hypixel")
			event.setCancelled(true);
	}

	@Override
	protected void onDisable() {
		mc.timer.timerSpeed = 1.0f;
	}
}
