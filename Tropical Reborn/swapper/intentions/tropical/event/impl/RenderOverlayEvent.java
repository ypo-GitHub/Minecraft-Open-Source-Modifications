package swapper.intentions.tropical.event.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import swapper.intentions.tropical.event.Event;

public class RenderOverlayEvent extends Event {

    private ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

    public ScaledResolution getSr() {
        return sr;
    }
}