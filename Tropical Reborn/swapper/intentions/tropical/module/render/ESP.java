package swapper.intentions.tropical.module.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import swapper.intentions.tropical.event.impl.Render3DEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.BooleanSetting;

public class ESP extends Module {
    public BooleanSetting invis = new BooleanSetting("Invisible", false);

    public ESP() {
        super("ESP", "Lets you see players through walls", 0x0, Category.VISUALS);
        addSettings(invis);
    }

    @Subscribe
    public void onRender3D(Render3DEvent event) {
        for(Entity entity : mc.theWorld.loadedEntityList) {
            if(entity instanceof EntityPlayer) {
                if(entity.isInvisible() && !invis.getValue())
                    continue;
                if(entity != mc.thePlayer)
                    drawEspBox(entity);
            }
        }
    }

    private void drawEspBox(Entity entity)
    {
        GlStateManager.pushMatrix();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableBlend();   
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth ();
        GlStateManager.depthMask(false);
        GlStateManager.color(1, 1, 1, 0.5F);
        RenderGlobal.renderAlligned(
                new AxisAlignedBB(
                        entity.boundingBox.minX
                                - 0.05
                                - entity.posX
                                + (entity.posX - mc.getRenderManager().renderPosX),
                        entity.boundingBox.minY
                                - entity.posY
                                + (entity.posY - mc.getRenderManager().renderPosY),
                        entity.boundingBox.minZ
                                - 0.05
                                - entity.posZ
                                + (entity.posZ - mc.getRenderManager().renderPosZ),
                        entity.boundingBox.maxX
                                + 0.05
                                - entity.posX
                                + (entity.posX - mc.getRenderManager().renderPosX),
                        entity.boundingBox.maxY
                                + 0.1
                                - entity.posY
                                + (entity.posY - mc.getRenderManager().renderPosY),
                        entity.boundingBox.maxZ
                                + 0.05
                                - entity.posZ
                                + (entity.posZ - mc.getRenderManager().renderPosZ)
                ));
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
