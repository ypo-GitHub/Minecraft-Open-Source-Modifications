package swapper.intentions.tropical.module.movement;

import java.util.ArrayList;
import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.module.movement.fly.impl.matrix.Matrix;
import swapper.intentions.tropical.module.movement.fly.impl.matrix.Matrix117;
import swapper.intentions.tropical.module.movement.fly.impl.mmc.Hurttime;
import swapper.intentions.tropical.module.movement.fly.impl.mmc.MMC;
import swapper.intentions.tropical.module.movement.fly.impl.other.*;
import swapper.intentions.tropical.module.movement.fly.impl.verus.*;
import swapper.intentions.tropical.settings.settings.BooleanSetting;
import swapper.intentions.tropical.settings.settings.ModeSetting;
import swapper.intentions.tropical.settings.settings.NumberSetting;
import swapper.intentions.tropical.utils.ChatUtils;

public class Fly extends Module {
	public LinkedList<FlyMode> flyModes = new LinkedList<>();
	public FlyMode currentFlyMode;
	public NumberSetting speed = new NumberSetting("General Speed", 2.0, 0.1, 0.0, 10.0);
	public NumberSetting cTicks = new NumberSetting("Ticks", 1.0, 1, 1.0, 20.0);
	public BooleanSetting cAlways = new BooleanSetting("Always Speed", false);
	public BooleanSetting cSet = new BooleanSetting("Set Speed", true);
	public NumberSetting cY = new NumberSetting("Y Motion", 0.0, 0.01, -10.0, 10.0);
	public NumberSetting cTimer = new NumberSetting("Timer", 1.0, 0.01, 0.02, 20.0);
	public BooleanSetting cPost = new BooleanSetting("On Post", false);
	public NumberSetting htDivisor = new NumberSetting("HurtTime Divisor", 1.0, 0.1, 0.5, 4.0);
	public ModeSetting flyModeSetting;

	@Override
	public void onRenderAlways() {
		String flyMode = flyModeSetting.getCurrentValue();
		if (getFlyModeByName(flyMode) != null)
			currentFlyMode = getFlyModeByName(flyMode);
		if (currentFlyMode == null) {
			ChatUtils.clientMessage("Fly Mode has been set to Vanilla as it was invalid.", this.getName());
			currentFlyMode = flyModes.peek();
		}
		speed.setHidden(currentFlyMode.getName().equals("MMC") || currentFlyMode.getName().equals("Matrix") || currentFlyMode.getName().equals("Matrix 1.17") || currentFlyMode.getName().equals("Verus Glide"));
		htDivisor.setHidden(!currentFlyMode.getName().equals("HurtTime"));
		cTicks.setHidden(!currentFlyMode.getName().equals("Custom"));
		cAlways.setHidden(!currentFlyMode.getName().equals("Custom"));
		cY.setHidden(!currentFlyMode.getName().equals("Custom"));
		cTimer.setHidden(!currentFlyMode.getName().equals("Custom"));
		cPost.setHidden(!currentFlyMode.getName().equals("Custom"));
		cSet.setHidden(!currentFlyMode.getName().equals("Custom"));
	}

	public Fly() {
		super("Flight", "Lets you fly in survival mode", Keyboard.KEY_F, Category.MOVEMENT);
		flyModes.add(new Vanilla());
		flyModes.add(new Vulcan());
		flyModes.add(new Custom());
		flyModes.add(new Matrix());
		flyModes.add(new VerusLJ());
		flyModes.add(new Verus());
		flyModes.add(new VerusGlide());
		flyModes.add(new VerusGradual());
		flyModes.add(new VerusPacket());
		flyModes.add(new Hurttime());
		flyModes.add(new MMC());
		flyModes.add(new DEV());
		flyModes.add(new Collide());
		flyModes.add(new Zonecraft());
		flyModes.add(new BWP());
		flyModes.add(new Mush());
		flyModes.add(new Matrix117());
		currentFlyMode = flyModes.peek();

		ArrayList<String> flyModesAsName = new ArrayList<>();
		flyModes.forEach((fm) -> flyModesAsName.add(fm.getName()));
		String[] hehe = new String[flyModes.size()];
		final int[] i = {0};
		flyModesAsName.forEach((sm) -> {hehe[i[0]] = sm; i[0]++;});
		flyModeSetting = new ModeSetting("Mode", hehe);

		addSettings(flyModeSetting, speed, htDivisor, cTicks, cY, cAlways, cPost, cSet);
	}

	@Override
	public void onEnable() {
		currentFlyMode.onEnable();
	}

	@Subscribe
	public void onUpdate(UpdateEvent event) {
		String suffix = currentFlyMode.getName();
		setSuffix(suffix);
		currentFlyMode.onUpdate(event);
	}

	@Subscribe
	public void onMove(MoveEvent event) {
		currentFlyMode.onMove(event);
	}
	
	@Subscribe
	public void onBlockCollision(BlockCollisionEvent event) {
		currentFlyMode.onBlockCollision(event);
	}

	@Subscribe
	public void onPacket(PacketEvent event) {
		currentFlyMode.onPacket(event);
	}

	@Override
	public void onDisable() {
		mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0.0;
		mc.timer.timerSpeed = 1.0F;
		mc.thePlayer.capabilities.isFlying = false;
	}

	public FlyMode getFlyModeByName(String name) {
		for (FlyMode fly : flyModes) {
			if (fly.getName().equalsIgnoreCase(name)) {
				return fly;
			}
		}
		return null;
	}
}
