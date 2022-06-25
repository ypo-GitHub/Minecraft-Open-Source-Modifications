package swapper.intentions.tropical.module.render;

import org.lwjgl.input.Keyboard;

import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.ui.ClickGUI.dropDown.ClickGui;

public class ClickGUI extends Module {

	public ClickGui clickGUI = new ClickGui();
	
	public ClickGUI() {
		super("ClickGUI", "Lets you turn on modules.", Keyboard.KEY_RSHIFT, Category.VISUALS);
	}
	
	@Override
	public void onEnable() {
		mc.displayGuiScreen(clickGUI);
	}

	@Override
	protected void onDisable() {
		
	}

}
