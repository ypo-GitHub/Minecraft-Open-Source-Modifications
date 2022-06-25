package swapper.intentions.tropical.module.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import swapper.intentions.tropical.event.impl.RenderOverlayEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.module.combat.KillAura;
import swapper.intentions.tropical.utils.ColorUtils;
import swapper.intentions.tropical.utils.RenderUtils;

import java.awt.*;

public class TargetHUD extends Module {

    public TargetHUD() {
        super("TargetHUD", "hud of target.", 0x0, Category.VISUALS);
        setDisplayName("Target HUD");
    }

    @Subscribe
    public void onRenderOverlay(RenderOverlayEvent event) {
        int x = 260;
        int y = 240;
        int width = 120;
        int height = 30;

        EntityPlayer target = (EntityPlayer) KillAura.target;
        if(target == null)
            return;

        RenderUtils.drawBorderedRect(x, y, x + width, y + height, 1, new Color(0, 0, 0, 144).getRGB(), ColorUtils.getColorInt(4));
        mc.fontRendererObj.drawCenteredString(target.getName(), x + width/2, y + 4, -1);
        mc.fontRendererObj.drawCenteredString(Math.floor(target.getHealth() * 10) / 10 + " HP", x + width/2, y + height - 4 - mc.fontRendererObj.FONT_HEIGHT, ColorUtils.getColorInt(4));
    }
}
