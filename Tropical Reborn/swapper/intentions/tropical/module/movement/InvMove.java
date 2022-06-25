package swapper.intentions.tropical.module.movement;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;

public class InvMove extends Module {
	public InvMove() {
		super("InventoryMove", "Lets you move around in a inventory", 0x0, Category.MOVEMENT);
		setDisplayName("Inventory Move");
	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub
		
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent event) {
		//exposed for shit code mr hackton bozo
		if(mc.currentScreen != null && !mc.ingameGUI.getChatGUI().getChatOpen()) {
			mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
			mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
			mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
			mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
			mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
			mc.gameSettings.keyBindSprint.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode());
		}
	}

	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub
		
	}
}
