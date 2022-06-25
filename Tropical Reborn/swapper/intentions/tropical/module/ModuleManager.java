package swapper.intentions.tropical.module;

import java.util.ArrayList;
import java.util.Comparator;

import swapper.intentions.tropical.module.Module.Category;
import swapper.intentions.tropical.module.combat.*;
import swapper.intentions.tropical.module.movement.*;
import swapper.intentions.tropical.module.player.*;
import swapper.intentions.tropical.module.exploit.Disabler;
import swapper.intentions.tropical.module.exploit.FastBow;
import swapper.intentions.tropical.module.render.*;
import swapper.intentions.tropical.module.world.Breaker;
import swapper.intentions.tropical.module.world.Scaffold;

public class ModuleManager {
	
	public ArrayList<Module> modules = new ArrayList<>();

    public void setup(){
        System.out.println("\n[Tropical: ModuleManager] Setup");

        register(new Fly());
        register(new KillAura());
        register(new AimAssist());
        register(new AutoClicker());
        register(new LongJump());
        register(new ClientCommands());
        register(new AntiBot());
        register(new Timer());
        register(new Velocity());
        register(new ChestStealer());
        register(new InvMove());
        register(new Sprint());
        register(new Fullbright());
        register(new Disabler());
        register(new FastBow());
        register(new NoFall());
        register(new ESP());
        register(new Criticals());
        register(new TargetHUD());
        register(new Blink());
        register(new InventoryManager());
        register(new Speed());
        register(new NoSlow());
        register(new Scaffold());
        register(new Animations());
        register(new HUD());
        register(new Breaker());
        register(new ClickGUI());

        System.out.println("[Tropical: ModuleManager] Loaded " + modules.size() + " mods");
    }

    public void register(Module m) {
        modules.add(m);
        System.out.println("[Tropical: ModuleManager] Loaded " + m.getName() + (!m.getDisplayName().equals(m.getName()) ? "/" + m.getDisplayName() : ""));
    }

    public Module getModuleByName(String name) {
        for(Module m: modules){
            if(m.getName().equalsIgnoreCase(name)){
                return m;
            }
        }
        return null;
    }
    
    public ArrayList<Module> getModulesInCategory(Category c) {
    	ArrayList<Module> modules = new ArrayList<>();
    	
        for(Module m: this.modules){
            if(m.getCategory() == c){
                modules.add(m);
            }
        }
        
        return modules;
    }

    public void onKey(int key) {
        for(Module m: modules){
            if(m.getKey() == key){
                m.toggle();
            }
        }
    }

	public void sortModules() {
        modules.sort(new ModuleStringLengthComparator());
	}
	
	static class ModuleStringLengthComparator implements Comparator<Module>{

	    @Override
	    public int compare(Module m1, Module m2) {
	        return m1.getName().length() - m2.getName().length();// compare length of Strings
	    }
	 }
}
