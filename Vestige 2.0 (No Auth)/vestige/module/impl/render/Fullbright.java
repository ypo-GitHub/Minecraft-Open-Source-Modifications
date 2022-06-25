package vestige.module.impl.render;

import vestige.module.Category;
import vestige.module.ListeningType;
import vestige.module.Module;

public class Fullbright extends Module {

	public Fullbright() {
		super("Fullbright", Category.RENDER, ListeningType.NEVER);
	}
	
	public void onEnable() {
		mc.gameSettings.gammaSetting = 100;
	}
	
	public void onDisable() {
		mc.gameSettings.gammaSetting = 1;
	}

}
