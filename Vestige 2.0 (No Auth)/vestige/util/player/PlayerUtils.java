package vestige.util.player;

import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import vestige.util.base.BaseUtil;

public class PlayerUtils implements BaseUtil {
	
	public static boolean isUsingItem() {
		return mc.thePlayer.isUsingItem();
	}
	
	public static boolean isUsingSword() {
		return mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
	}
	
}