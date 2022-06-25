package vestige.module.impl.ghost;

import vestige.event.Event;
import vestige.event.impl.EventReach;
import vestige.module.Category;
import vestige.module.Module;
import vestige.setting.impl.NumberSetting;

public class Reach extends Module {
	
	public NumberSetting reach = new NumberSetting("Reach", 4, 3, 6, 0.05, this);
	
	public Reach() {
		super("Reach", Category.GHOST);
		this.addSettings(reach);
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventReach) {
			EventReach e = (EventReach) event;
			e.setReach(reach.getValue());
		}
	}

}
