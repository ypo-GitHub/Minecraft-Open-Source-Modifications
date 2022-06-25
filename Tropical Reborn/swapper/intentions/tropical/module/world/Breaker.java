package swapper.intentions.tropical.module.world;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import org.lwjgl.input.Keyboard;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.ModeSetting;
import swapper.intentions.tropical.settings.settings.NumberSetting;

import java.util.LinkedList;

public class Breaker extends Module {
    public NumberSetting range = new NumberSetting("Range", 3, 0.1, 0, 6);
    public ModeSetting mode = new ModeSetting("Mode", "Normal", "Through walls");

    Block[] invalidblocks = new Block[]{Blocks.air, Blocks.water, Blocks.lava, Blocks.fire};

    public Breaker() {
        super("Breaker", "Breakes things that are meant to be broken", Keyboard.KEY_Z, Category.WORLD);
        this.addSettings(range);
    }

    LinkedList<Block> blocks = new LinkedList<>();

    @Subscribe
    public void onUpdate(UpdateEvent e) {
        for (double x = -range.getValue(); x <= range.getValue(); x++) {
            for (double y = -range.getValue(); y <= range.getValue(); y++) {
                for (double z = -range.getValue(); z <= range.getValue(); z++) {
                    Block block = mc.theWorld.getBlockState(mc.thePlayer.getPosition().add(x, y, z)).getBlock();
                    blocks.add(block);
                }
            }
        }
        Block bed = null;
        for (Block block : blocks) {
            if (block.getMaterial().equals(Blocks.bed)) {
                bed = block;
            }
        }

        if (bed != null) {
            blocks.remove(bed);
            return;
        }

        if(!getPlayerInRange(bed)) return;

        if (bed != null) {
            e.setRotationYaw(getRotation(bed)[0]);
            e.setRotationPitch(getRotation(bed)[0]);
        }
    }

    public float[] getRotation(Block block) {
        return new float[]{
                (float) Math.toDegrees(Math.atan2(block.getBlockBoundsMinZ() - mc.thePlayer.posZ, block.getBlockBoundsMinX() - mc.thePlayer.posX)) - 90,
                (float) Math.toDegrees(-Math.atan2(block.getBlockBoundsMinY() - mc.thePlayer.posY, Math.sqrt(Math.pow(block.getBlockBoundsMinX() - mc.thePlayer.posX, 2) + Math.pow(block.getBlockBoundsMinZ() - mc.thePlayer.posZ, 2))))
        };
    }

    public Boolean getPlayerInRange(Block block){
        return block.getBlockBoundsMinX() - mc.thePlayer.posX <= range.getValue() && block.getBlockBoundsMinX() - mc.thePlayer.posX >= -range.getValue();
    }
}
