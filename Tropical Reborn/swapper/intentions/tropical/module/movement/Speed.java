package swapper.intentions.tropical.module.movement;

import java.util.ArrayList;
import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.StrafeEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.module.movement.speed.SpeedMode;
import swapper.intentions.tropical.module.movement.speed.impl.*;
import swapper.intentions.tropical.settings.settings.BooleanSetting;
import swapper.intentions.tropical.settings.settings.ModeSetting;
import swapper.intentions.tropical.settings.settings.NumberSetting;
import swapper.intentions.tropical.utils.ChatUtils;

public class Speed extends Module {
	public LinkedList<SpeedMode> speedModes = new LinkedList<>();
	public SpeedMode currentSpeedMode;
	//public NumberSetting matrixSpeed = new NumberSetting("Matrix Speed", 7.0, 0.1, 0.0, 10.0);
	public static NumberSetting speed = new NumberSetting("General Speed", 0.45, 0.05, 0.0, 5.0);
    public static BooleanSetting smoothStrafe = new BooleanSetting("Hypixel Smooth", false);
	public ModeSetting speedModeSetting;

    @Override
    public void onRenderAlways() {
        if(currentSpeedMode == null) {
            ChatUtils.clientMessage("Speed Mode has been set to BHop as it was invalid.", this.getName());
            currentSpeedMode = speedModes.peek();
        }
        String speedMode = speedModeSetting.getCurrentValue();
        if(getSpeedModeByName(speedMode) != null)
            currentSpeedMode = getSpeedModeByName(speedMode);
        if(currentSpeedMode != null) {
            smoothStrafe.setHidden(!currentSpeedMode.getName().equalsIgnoreCase("Hypixel"));
            speed.setHidden(currentSpeedMode.getName().equalsIgnoreCase("NCP BHop") || currentSpeedMode.getName().equalsIgnoreCase("Hypixel") || currentSpeedMode.getName().equalsIgnoreCase("Matrix Strafe") ||
                    currentSpeedMode.getName().equalsIgnoreCase("VerusY") || currentSpeedMode.getName().equalsIgnoreCase("Vulcan"));
        }
    }

    public Speed() {
        super("Speed", "Lets you speed around", Keyboard.KEY_V, Category.MOVEMENT);
        speedModes.add(new BHop());
        speedModes.add(new NCPBHop());
        speedModes.add(new MatrixStrafe());
		speedModes.add(new VerusYPort());
		speedModes.add(new DEV());
        speedModes.add(new BWP());
        speedModes.add(new Matrix117());
        speedModes.add(new Vulcan());
        speedModes.add(new Hypixel());
        currentSpeedMode = speedModes.peek();
        
        ArrayList<String> speedModesAsName = new ArrayList<>();
        speedModes.forEach((sm) -> speedModesAsName.add(sm.getName()));
        String[] hehe = new String[speedModes.size()];
        final int[] i = {0};
        speedModesAsName.forEach((sm) -> {hehe[i[0]] = sm; i[0]++;});
        speedModeSetting = new ModeSetting("Mode", hehe);
    	
        addSettings(speedModeSetting, speed, smoothStrafe);
    }

    @Override
    public void onEnable() {
        currentSpeedMode.onEnable();
    }
    
    @Subscribe
    public void onUpdate(UpdateEvent event) {
        setSuffix(currentSpeedMode.getName());
    	currentSpeedMode.onUpdate(event);
    }
    
    @Subscribe
    public void onMove(MoveEvent event) {
    	currentSpeedMode.onMove(event);
    }
    
    @Subscribe
    public void onPacket(PacketEvent event) {
    	currentSpeedMode.onPacket(event);
    }

    @Subscribe
    public void onStrafe(StrafeEvent event) {
        currentSpeedMode.onStrafeEvent(event);
    }

    @Override
    public void onDisable() { mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0; mc.timer.timerSpeed = 1.0F; mc.thePlayer.jumpMovementFactor = 0.02f; }
    
    public SpeedMode getSpeedModeByName(String name) {
    	for(SpeedMode fly: speedModes) {
        	if(fly.getName().equalsIgnoreCase(name)) {
        		return fly;
        	}
        }
		return null;
    }
}
