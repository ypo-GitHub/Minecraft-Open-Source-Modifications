package vestige.module.impl.render;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventRender;
import vestige.event.impl.EventUpdate;
import vestige.module.Category;
import vestige.module.ListeningType;
import vestige.module.Module;
import vestige.processor.impl.PacketsProcessor;
import vestige.setting.impl.ModeSetting;
import vestige.ui.click.vestige.VestigeClickGUI;
import vestige.ui.click.vestige.VestigeRainbowClickGUI;
import vestige.util.misc.TimerUtil;

public class ClickGuiModule extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "Vestige", "Vestige", "Vestige Rainbow");
	
	private TimerUtil timer = new TimerUtil();
	
	public ClickGuiModule() {
		super("ClickGUI", Category.RENDER, Keyboard.KEY_RSHIFT);
		this.addSettings(mode);
	}
	
	public void onEnable() {
		switch(mode.getMode()) {
		case "Vestige":
			mc.displayGuiScreen(new VestigeClickGUI());
			break;
		case "Vestige Rainbow":
			mc.displayGuiScreen(new VestigeRainbowClickGUI());
			break;
		}
	}
	
	public void onEvent(Event event) {
		keyset(mc.gameSettings.keyBindForward);
		keyset(mc.gameSettings.keyBindLeft);
		keyset(mc.gameSettings.keyBindRight);
		keyset(mc.gameSettings.keyBindBack);
		keyset(mc.gameSettings.keyBindJump);
		
		if(event instanceof EventRender) {
			handleRotations();
		}
	}
	
	private void keyset(KeyBinding key){
        key.pressed = GameSettings.isKeyDown(key);
    }
	
	private void handleRotations() {
		final double speed = 0.15F;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) mc.thePlayer.rotationYaw += speed * timer.getTimeElapsed();
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) mc.thePlayer.rotationYaw -= speed * timer.getTimeElapsed();
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) mc.thePlayer.rotationPitch -= speed * timer.getTimeElapsed();
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) mc.thePlayer.rotationPitch += speed * timer.getTimeElapsed();
		
		if(mc.thePlayer.rotationPitch > 90) {
			mc.thePlayer.rotationPitch = 90;
		} else if(mc.thePlayer.rotationPitch < -90) {
			mc.thePlayer.rotationPitch = -90;
		}
		
		timer.reset();
	}

}
