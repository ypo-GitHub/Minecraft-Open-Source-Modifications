package swapper.intentions.tropical.module.render;

import java.util.ArrayList;

import com.google.common.eventbus.Subscribe;

import swapper.intentions.tropical.event.impl.RenderOverlayEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.BooleanSetting;
import swapper.intentions.tropical.settings.settings.ModeSetting;
import swapper.intentions.tropical.settings.settings.NumberSetting;

public class Animations extends Module {
	public BooleanSetting fullBlock = new BooleanSetting("Full Auto Block", true);
	public NumberSetting itemScale = new NumberSetting("Item Scale", 1.0, 0.05, 0.25, 2.0);
	public NumberSetting extraY = new NumberSetting("Y Offset", 0.0, 0.05, -0.25, 1.0);
	public ModeSetting animation = new ModeSetting("Animation", "1.7", "Aladeen!!", "DEV", "Exhibition", "Dortware", "Smooth");
	public Animations() {
		super("Animations", "Animation settings for client", 0x0, Category.VISUALS);
		addSettings(animation, itemScale, extraY, fullBlock);
	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub
		
	}
	
	@Subscribe
	public void onRender(RenderOverlayEvent event) {
		setSuffix(animation.getCurrentValue());
	}

	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub
		
	}

}
