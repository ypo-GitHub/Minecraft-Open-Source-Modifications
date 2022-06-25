package swapper.intentions.tropical.module.player;

import java.util.Locale;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.ChatEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.utils.ChatUtils;

public class ClientCommands extends Module {

	public ClientCommands() {
		super("ClientCommands", "Let's you use client commands.", 0x0, Category.PLAYER);
		setDisplayName("Client Commands");
		this.toggle();
	}

	@Override
	protected void onEnable() {}
	
	@Subscribe
	public void onChat(ChatEvent e) {
		if(e.getMessage().startsWith(".")) {
			e.setCancelled(true);
			String[] splitMessage = e.getMessage().split(" ");
			switch(e.getMessage().split(" ")[0]) {
				case ".bind": {
					try {
                        Tropical.instance.moduleManager.getModuleByName(splitMessage[1]).setKey(Keyboard.getKeyIndex(splitMessage[2].toUpperCase(Locale.ROOT)));
						ChatUtils.clientMessage("Bound " + splitMessage[1] + " to " + splitMessage[2]);
					} catch(Exception exception) {
						ChatUtils.clientMessage("Incorrect syntax, usage: bind module key", "Client Commands");
					}
					break;
				}

				case ".hide": {
					try {
						Module mod = Tropical.instance.moduleManager.getModuleByName(splitMessage[1]);
						mod.setHidden(!mod.isHidden());
						ChatUtils.clientMessage("Made " + mod.getDisplayName() + " hidden property " + mod.isHidden());
					} catch(Exception exception) {
						ChatUtils.clientMessage("Incorrect syntax, usage: hide module", "Client Commands");
					}
					break;
				}

				case ".vclip": {
					try {
						if(mc.thePlayer != null)
							mc.thePlayer.setPosition(mc.thePlayer.posX,mc.thePlayer.posY + Integer.parseInt(splitMessage[1]), mc.thePlayer.posZ);
					} catch(Exception exception) {
						ChatUtils.clientMessage("Incorrect syntax, usage: vclip height", "Client Commands");
					}
					break;
				}

			}
		}
	}

	@Override
	protected void onDisable() {}

}
