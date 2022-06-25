package vestige.module.impl.movement;

import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventUpdate;
import vestige.module.Category;
import vestige.module.ListeningType;
import vestige.module.Module;
import vestige.setting.impl.BooleanSetting;
import vestige.setting.impl.NumberSetting;
import vestige.util.network.PacketUtils;

public class Sprint extends Module {
	
	private BooleanSetting omnisprint = new BooleanSetting("Omnisprint", false, this);
	
	public Sprint() {
		super("Sprint", Category.MOVEMENT);
		this.setEnabledSilently(true);
		this.addSettings(omnisprint);
	}
	
	public void onDisable() {
		mc.gameSettings.keyBindSprint.pressed = false;
		if(omnisprint.isEnabled()) {
			mc.thePlayer.setSprinting(false);
		}
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if(omnisprint.isEnabled()) {
				mc.thePlayer.setSprinting(true);
			} else {
				mc.thePlayer.setSprinting(mc.thePlayer.moveForward > 0 && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally && mc.thePlayer.getFoodStats().getFoodLevel() > 6);
				if(mc.thePlayer.isUsingItem() && !Vestige.getModuleManager().getModuleByName("Noslow").isEnabled()) {
					mc.thePlayer.setSprinting(false);
				}
			}
		}
	}

}