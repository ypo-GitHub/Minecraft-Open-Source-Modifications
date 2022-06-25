package swapper.intentions.tropical.module.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import org.lwjgl.input.Keyboard;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.ModeSetting;
import swapper.intentions.tropical.utils.TimerUtils;

public class Criticals extends Module {

	public static boolean isCrit;
	public static int step;
	public static TimerUtils timer = new TimerUtils();
	public static ModeSetting mode = new ModeSetting("Mode", "Mini", "MiniServer");

    public Criticals() {
        super("Criticals", "Automatically crits players.", Keyboard.KEY_NONE, Category.COMBAT);
        addSettings(mode);
    }

    @Override
    public void onEnable() {}

	@Subscribe
	public void onUpdate(UpdateEvent event) {
		if(isCrit && mc.thePlayer.onGround) {
			if(!timer.hasReached(440L)) {
				isCrit = false;
				return;
			}
			switch(mode.getCurrentValue()) {
				case "Mini":
					if(mc.thePlayer.motionY < -0.07 && mc.thePlayer.motionY > -0.08)
						mc.thePlayer.motionY = 0.14 + Math.random()*0.08;
					isCrit = false;
					timer.reset();
					break;
				case "MiniServer":
					if(step == 0) {
						event.setPosY(mc.thePlayer.posY + 0.1002);
						event.setOnGround(false);
					}
					if(step == 1) {
						event.setPosY(mc.thePlayer.posY + 0.0216);
						event.setOnGround(false);
						isCrit = false;
						timer.reset();
					}
					step++;
					break;
			}
		}
	}

    @Subscribe
    public void onPacket(PacketEvent event) {
    	setSuffix(mode.getCurrentValue());
    	if(event.getPacket() instanceof C02PacketUseEntity && mc.thePlayer.onGround && !isCrit) {
    		isCrit = true;
    	}
    }

    @Override
    public void onDisable() {

	}
}
