package swapper.intentions.tropical.module.player;

import java.util.ArrayList;

import com.google.common.eventbus.Subscribe;

import net.minecraft.network.play.client.C03PacketPlayer;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.ModeSetting;
import swapper.intentions.tropical.utils.PacketUtil;

public class NoFall extends Module {
	public ModeSetting mode = new ModeSetting("Mode", "Edit", "Mush", "Reset");;
	
	public NoFall() {
		super("NoFall", "drip boots", 0x0, Category.PLAYER);
		addSettings(mode);
	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param e
	 */
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		setSuffix(mode.getCurrentValue());
		if(!e.isPre())
			return;
		
		switch(mode.getCurrentValue()) {
			case "Edit": {
				if(mc.thePlayer.fallDistance > 3) {
					e.setOnGround(true);
					mc.thePlayer.fallDistance = 0;
				}
				break;
			}
			case "Mush": {
				if(mc.thePlayer.fallDistance > 2.2 && mc.thePlayer.fallDistance <= 3.5) {
					e.setOnGround(true);
					mc.thePlayer.motionY *= 0.91;
					if(mc.thePlayer.fallDistance > 2.4) {
						PacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
						mc.thePlayer.fallDistance = 0;
					}
				}
				break;
			}
			case "Reset": {
				if(mc.thePlayer.fallDistance > 3) {
					mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0.0;
					e.setOnGround(true);
					mc.thePlayer.fallDistance = 0;
				}
				break;
			}
		}
	}
	
	@Subscribe
	public void onPacket(PacketEvent e) {

	}
	
	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub
		
	}
}
