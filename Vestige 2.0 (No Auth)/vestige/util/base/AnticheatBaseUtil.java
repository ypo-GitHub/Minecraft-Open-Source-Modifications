package vestige.util.base;

import net.minecraft.client.Minecraft;
import vestige.util.anticheat.AnticheatMoveUtil;

public interface AnticheatBaseUtil {
	
	static Minecraft mc = Minecraft.getMinecraft();
	static AnticheatMoveUtil move = new AnticheatMoveUtil();
	
}