package swapper.intentions.tropical.event.impl;

import swapper.intentions.tropical.event.Event;

public class Render3DEvent extends Event {

    private float pTicks;

    public Render3DEvent(float partialTicks) {
        pTicks = partialTicks;
    }

    public float getPartialTicks() {
        return pTicks;
    }
}