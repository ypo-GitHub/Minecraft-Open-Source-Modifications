package vestige.util.misc;

import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import vestige.util.base.BaseUtil;
import vestige.util.network.PacketUtils;

public class HotbarUtil implements BaseUtil {
	
	public static int getAirSlot() {
		for(int i = 0; i < 9; i++) {
			if(mc.thePlayer.inventory.mainInventory[i] == null) {
				return i;
			}
		}
		return -1;
	}
	
}