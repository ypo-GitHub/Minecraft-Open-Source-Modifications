package vestige.module.impl.movement;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventRender;
import vestige.event.impl.EventSendPacket;
import vestige.event.impl.EventUpdate;
import vestige.module.Category;
import vestige.module.Module;
import vestige.setting.impl.BooleanSetting;
import vestige.util.network.PacketUtils;
import vestige.util.misc.TimerUtil;

public class InvMove extends Module {
	
	public BooleanSetting noSprint = new BooleanSetting("No sprint", false, this);
	public BooleanSetting blink = new BooleanSetting("Blink", false, this);
	
	private boolean isBlinking;
	
	private TimerUtil timer = new TimerUtil();
	
	public InvMove() {
		super("InvMove", Category.MOVEMENT, Keyboard.KEY_NONE);
		this.addSettings(noSprint, blink);
	}
	
	public void onEnable() {
		isBlinking = false;
		timer.reset();
	}
	
	public void onDisable() {
		if(isBlinking) {
			Vestige.getPacketsProcessor().setBlinking(false);
			isBlinking = false;
		}
		timer.reset();
	}

	public void onEvent(Event event) {
		if(mc.currentScreen instanceof GuiChat || mc.currentScreen == null || Vestige.getModuleManager().getModuleByName("ClickGUI").isEnabled()) {
			if(isBlinking) {
				Vestige.getPacketsProcessor().setBlinking(false);
				isBlinking = false;
			}
            return;
		}
		
		if(noSprint.isEnabled()) {
			mc.thePlayer.setSprinting(false);
		}
		
		 keyset(mc.gameSettings.keyBindForward);
         keyset(mc.gameSettings.keyBindLeft);
         keyset(mc.gameSettings.keyBindRight);
         keyset(mc.gameSettings.keyBindBack);
         keyset(mc.gameSettings.keyBindJump);
		
		if(event instanceof EventRender) {
			handleRotations();
		} else if(event instanceof EventUpdate) {
			if(!isBlinking && blink.isEnabled()) {
				Vestige.getPacketsProcessor().setBlinking(true);
				isBlinking = true;
			}
			
			if(isBlinking && !blink.isEnabled()) {
				Vestige.getPacketsProcessor().setBlinking(false);
				isBlinking = false;
			}
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
