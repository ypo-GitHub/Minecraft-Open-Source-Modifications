package vestige.module.impl.combat;

import java.util.ArrayList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.world.WorldSettings.GameType;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventReceivePacket;
import vestige.event.impl.EventUpdate;
import vestige.module.Category;
import vestige.module.ListeningType;
import vestige.module.Module;
import vestige.setting.impl.BooleanSetting;
import vestige.setting.impl.NumberSetting;

public class Antibot extends Module {
	
	private NumberSetting ticksExisted = new NumberSetting("Ticks Existed", 40, 0, 200, 5, this);
	private BooleanSetting invisibleOnSpawn = new BooleanSetting("Invisible on spawn", true, this);
	private BooleanSetting hycraft = new BooleanSetting("Hycraft", false, this);
	
	private ArrayList<EntityLivingBase> blacklistedEntities = new ArrayList<>();
	
	public Antibot() {
		super("Antibot", Category.COMBAT, ListeningType.ALWAYS);
		this.addSettings(ticksExisted, invisibleOnSpawn, hycraft);
	}
	
	public void onEvent(Event event) {
		if(mc.thePlayer.ticksExisted >= 5 && !this.isEnabled()) {
			return;
		}
		if(event instanceof EventUpdate) {
			if(mc.thePlayer.ticksExisted < 5) {
				blacklistedEntities.clear();
			}
		} else if(event instanceof EventReceivePacket) {
			EventReceivePacket e = (EventReceivePacket) event;
			if(mc.thePlayer.ticksExisted > 300 && mc.playerController.getCurrentGameType() == GameType.SURVIVAL) {
				if(hycraft.isEnabled()) {
					if(e.getPacket() instanceof S0CPacketSpawnPlayer) {
						S0CPacketSpawnPlayer packet = (S0CPacketSpawnPlayer) e.getPacket();
						blacklistedEntities.add(packet.getEntityPlayer());
						Vestige.addChatMessage("Detected a bot : hycraft antibot");
					}
				}
			}
		}
	}
	
	public boolean canAttack(EntityLivingBase e) {
		if(invisibleOnSpawn.isEnabled()) {
			if(e.ticksExisted < 10 && e.isInvisible()) {
				blacklistedEntities.add(e);
				Vestige.addChatMessage("Detected a bot : invisible on spawn antibot");
				return false;
			}
		}
		
		if(mc.thePlayer.ticksExisted < ticksExisted.getValue()) {
			return false;
		}
		
		if(blacklistedEntities.contains(e)) {
			return false;
		}
		
		return true;
	}

}