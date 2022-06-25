package swapper.intentions.tropical.module.movement;

import com.google.common.eventbus.Subscribe;

import swapper.intentions.tropical.event.impl.NoSlowEvent;
import swapper.intentions.tropical.module.Module;

public class NoSlow extends Module {
	public NoSlow() {
		super("NoSlow", "Removes slowdown from items", 0x0, Category.MOVEMENT);
		setDisplayName("No Slow");
	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub
		
	}
	
	@Subscribe
	public void onSlow(NoSlowEvent event) {
		event.setCancelled(true);
		setSuffix("Vanilla");
	}

	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub
		
	}
}
