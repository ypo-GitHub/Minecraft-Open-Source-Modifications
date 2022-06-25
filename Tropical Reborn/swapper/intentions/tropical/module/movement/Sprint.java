package swapper.intentions.tropical.module.movement;

import com.google.common.eventbus.Subscribe;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.utils.MoveUtils;

public class Sprint extends Module {
	public Sprint() {
		super("Sprint", "always do the sprinting sir", 0x0, Category.MOVEMENT);
	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub
		
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent event) {
		//exposed for shit code mr hackton bozo
		if(!Tropical.instance.moduleManager.getModuleByName("Scaffold").isToggled())
			mc.gameSettings.keyBindSprint.pressed = true;
	}

	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub
		
	}
}
