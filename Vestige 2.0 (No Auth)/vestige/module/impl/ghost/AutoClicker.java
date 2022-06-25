package vestige.module.impl.ghost;

import vestige.event.Event;
import vestige.event.impl.EventRender;
import vestige.event.impl.EventUpdate;
import vestige.module.Category;
import vestige.module.Module;
import vestige.setting.impl.NumberSetting;
import vestige.util.misc.TimerUtil;

public class AutoClicker extends Module {
	
	private NumberSetting cps = new NumberSetting("CPS", 10, 1, 20, 0.5, this);
	
	private TimerUtil timer = new TimerUtil();
	
	public AutoClicker() {
		super("AutoClicker", Category.GHOST);
		this.addSettings(cps);
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventUpdate) {
			if(mc.gameSettings.keyBindAttack.isKeyDown() && !mc.thePlayer.isUsingItem()) {
				if(timer.hasReached((long) (1000 / cps.getValue())) && mc.thePlayer.ticksExisted % 5 != 0 && mc.thePlayer.ticksExisted % 17 != 0) {
					mc.leftClickCounter = 0;
		            mc.clickMouse();
					timer.reset();
				}
			} else {
				timer.reset();
			}
		}
	}
}
