// 
// Decompiled by Procyon v0.5.36
// 

package vip.Resolute.command.impl;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.client.Minecraft;
import vip.Resolute.command.Command;

public class HClip extends Command
{
    public Minecraft mc;
    
    public HClip() {
        super("HClip", "HClips through blocks", ".hclip <value>", new String[] { "hclip" });
        this.mc = Minecraft.getMinecraft();
    }
    
    @Override
    public void onCommand(final String[] args, final String command) {
        if (args.length > 0) {
            final float distance = Float.parseFloat(args[0]);
            final double yaw = Math.toRadians(this.mc.thePlayer.rotationYaw);
            final double x = -Math.sin(yaw) * distance;
            final double z = Math.cos(yaw) * distance;
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX + x, this.mc.thePlayer.posY, this.mc.thePlayer.posZ + z, true));
            this.mc.thePlayer.setPosition(this.mc.thePlayer.posX + x, this.mc.thePlayer.posY, this.mc.thePlayer.posZ + z);
        }
    }
}
