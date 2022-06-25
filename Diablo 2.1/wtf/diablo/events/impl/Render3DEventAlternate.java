package wtf.diablo.events.impl;

import lombok.Getter;
import lombok.Setter;
import wtf.diablo.events.Event;

@Getter@Setter
public class Render3DEventAlternate extends Event {
    private final float partialTicks;

    public Render3DEventAlternate(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}