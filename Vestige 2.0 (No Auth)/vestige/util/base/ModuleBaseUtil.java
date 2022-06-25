package vestige.util.base;

import net.minecraft.client.Minecraft;
import vestige.util.movement.MovementUtils;
import vestige.util.network.PacketUtils;
import vestige.util.player.PlayerUtils;
import vestige.util.world.WorldUtils;

public interface ModuleBaseUtil {
	
	static Minecraft mc = Minecraft.getMinecraft();
	static final MovementUtils move = new MovementUtils();
	static final PlayerUtils player = new PlayerUtils();
	static final WorldUtils world = new WorldUtils();
	static final PacketUtils packet = new PacketUtils();
	
}