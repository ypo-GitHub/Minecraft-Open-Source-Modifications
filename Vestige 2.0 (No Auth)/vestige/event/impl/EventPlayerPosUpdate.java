package vestige.event.impl;

import net.minecraft.entity.player.EntityPlayer;
import vestige.event.Event;

public class EventPlayerPosUpdate extends Event {
	
	private final EntityPlayer player;

	public EventPlayerPosUpdate(EntityPlayer p) {
		super(false);
		this.player = p;
	}
	
	public EntityPlayer getPlayer() {
		return player;
	}

}