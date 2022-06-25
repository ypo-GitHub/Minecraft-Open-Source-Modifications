package vestige.module;

import java.util.ArrayList;

import vestige.module.impl.combat.*;
import vestige.module.impl.exploit.*;
import vestige.module.impl.ghost.*;
import vestige.module.impl.misc.*;
import vestige.module.impl.movement.*;
import vestige.module.impl.player.*;
import vestige.module.impl.render.*;
import vestige.module.impl.world.*;

public class ModuleManager {
	
	private final ArrayList<Module> modules = new ArrayList<>();
	
	public ModuleManager() {
		//Combat
		modules.add(new Killaura());
		modules.add(new AutoArmor());
		modules.add(new Velocity());
		modules.add(new Criticals());
		modules.add(new Antibot());
		
		//Movement
		modules.add(new Sprint());
		modules.add(new Speed());
		modules.add(new Fly());
		modules.add(new Longjump());
		modules.add(new InvMove());
		modules.add(new Noslow());
		modules.add(new BoatFly());
		
		//Player
		modules.add(new Nofall());
		
		//World
		modules.add(new Cheststealer());
		modules.add(new Scaffold());
		modules.add(new Breaker());
		
		//Render
		modules.add(new ClickGuiModule());
		modules.add(new Fullbright());
		modules.add(new HUD());
		modules.add(new ESP());
		modules.add(new Chams());
		
		//Exploit
		modules.add(new Disabler());
		
		//Ghost
		modules.add(new AutoClicker());
		modules.add(new Reach());
		modules.add(new AimAssist());
		modules.add(new AutoBridge());
		
		//Misc
		modules.add(new Anticheat());
		modules.add(new NoRotate());
	}

	public ArrayList<Module> getModules() {
		return modules;
	}
	
	public Module getModuleByName(String name) {
		for(Module m : modules) {
			if(m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}
	
}