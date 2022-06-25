package swapper.intentions.tropical.module.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;

public class Fullbright extends Module {
	public Fullbright() {
		super("Fullbright", "see deep in dark", 0x0, Category.VISUALS);
	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub
		
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent event) {
		//exposed for shit code mr hackton bozo
		mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 42, 1337));
	}

	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub
		mc.thePlayer.removePotionEffect(Potion.nightVision.id);
	}
}
