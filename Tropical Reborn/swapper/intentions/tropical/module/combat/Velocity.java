package swapper.intentions.tropical.module.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import org.lwjgl.input.Keyboard;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.ModeSetting;

public class Velocity extends Module {

	public static ModeSetting mode = new ModeSetting("Mode", "Cancel", "Jartex", "Matrix");
	
    public Velocity() {
        super("Velocity", "Reduces knockback taken.", Keyboard.KEY_NONE, Category.COMBAT);
        addSettings(mode);
    }

    @Override
    public void onEnable() {}
    
    @Subscribe
    public void onPacket(PacketEvent event) {
    	setSuffix(mode.getCurrentValue());
    	if(event.getPacket() instanceof S12PacketEntityVelocity) {
    		S12PacketEntityVelocity packet = event.getPacket();
    		if(mc.thePlayer == null || packet.getEntityID() != mc.thePlayer.getEntityId())
    			return;
    		switch(mode.getCurrentValue()) {
	    		case "Cancel":
				case "Jartex": {
	    			event.setCancelled(true);
	    			break;
	    		}
				case "Matrix": {
	    			event.setPacket(new S12PacketEntityVelocity(mc.thePlayer.getEntityId(),packet.getMotionY()/20000.0,packet.getMotionY()/8000.0,packet.getMotionY()/20000.0));
	    			mc.thePlayer.motionX *= 0.6;
	    			mc.thePlayer.motionZ *= 0.6;
	    			break;
	    		}
    		}
    	} 
    	if(event.getPacket() instanceof S27PacketExplosion)
    		event.setCancelled(true);
    }

    @Override
    public void onDisable() { }
}
