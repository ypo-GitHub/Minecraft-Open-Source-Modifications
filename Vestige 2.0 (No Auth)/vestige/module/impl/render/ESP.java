package vestige.module.impl.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.Config;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventRender;
import vestige.module.Category;
import vestige.module.Module;

public class ESP extends Module {

	public ESP() {
		super("ESP", Category.RENDER);
	}
	
	public void onEnable() {
		if(Config.isFastRender()) {
			Vestige.addChatMessage("You need to disable fast render in minecraft settings to use ESP.");
			this.setEnabled(false);
		}
	}
	
}