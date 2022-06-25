package swapper.intentions.tropical.module.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.module.Module;

public class AntiBot extends Module {
	public AntiBot() {
		super("AntiBot", "Removes anticheat bots", 0x0, Category.COMBAT);
	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub
		
	}
	
	@Subscribe
	public void onPacket(PacketEvent packetEvent) {
		if (packetEvent.getPacket() instanceof S38PacketPlayerListItem && mc.getNetHandler() != null && (mc.thePlayer.ticksExisted > 300 || Tropical.instance.moduleManager.getModuleByName("KillAura").isToggled())) {
            final S38PacketPlayerListItem packetPlayerListItem = packetEvent.getPacket();
            for (final Object playerData : packetPlayerListItem.func_179767_a()) {
                S38PacketPlayerListItem.AddPlayerData addPlayerData = (S38PacketPlayerListItem.AddPlayerData) playerData;
                if (packetPlayerListItem.func_179768_b() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                    final NetworkPlayerInfo networkplayerinfo = new NetworkPlayerInfo(addPlayerData);
                    if (mc.getNetHandler().getPlayerInfoMap().stream().anyMatch(i -> i.getGameProfile().getName().equals(networkplayerinfo.getGameProfile().getName()))) {
                        packetEvent.setCancelled(true);
                    }
                }
            }
        }
	}

	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub
		
	}
}
