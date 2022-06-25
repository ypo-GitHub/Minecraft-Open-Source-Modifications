package vestige.event.impl;

import net.minecraft.entity.Entity;
import vestige.event.Event;

public class EventPostRenderPlayer extends Event {
	
	private Entity entity;
    private float partialTicks;

    public EventPostRenderPlayer(Entity entity, float partialTicks) {
    	super(false);
        this.entity = entity;
        this.partialTicks = partialTicks;
    }

    public Entity getEntity() {
        return entity;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
	
}