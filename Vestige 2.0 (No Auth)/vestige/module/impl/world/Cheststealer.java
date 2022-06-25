package vestige.module.impl.world;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.util.BlockPos;
import vestige.event.Event;
import vestige.event.impl.EventUpdate;
import vestige.module.Category;
import vestige.module.Module;
import vestige.setting.impl.BooleanSetting;
import vestige.setting.impl.NumberSetting;
import vestige.util.misc.TimerUtil;

public class Cheststealer extends Module {
	
	TimerUtil timer = new TimerUtil();
	
	public NumberSetting delay = new NumberSetting("Delay", 25, 0, 300, 10, this);
	public BooleanSetting autoClose = new BooleanSetting("Auto close", true, this);
	public BooleanSetting GuiDetect = new BooleanSetting("Gui Detect", true, this);

	public Cheststealer() {
		super("ChestStealer", Category.WORLD, Keyboard.KEY_N);
		this.addSettings(delay, autoClose, GuiDetect);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if((mc.thePlayer.openContainer != null) && (mc.thePlayer.openContainer instanceof ContainerChest)) {
				if(GuiDetect.isEnabled() && isGui()) {
					return;
				}
				
				ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;

				for(int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
					if((chest.getLowerChestInventory().getStackInSlot(i) != null)) {
						if(timer.hasReached((long) delay.getValue())) {
							mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
							timer.reset();
						}

					}

				}

				if(isChestEmpty(chest) && autoClose.isEnabled() && timer.hasReached((long) delay.getValue())) {
					mc.thePlayer.closeScreen();
					timer.reset();
				}

			}
		}
	}
	
	public boolean isChestEmpty(final Container container) {
        boolean isEmpty = true;
        int maxSlot = (container.inventorySlots.size() == 90) ? 54 : 27;
        for (int i = 0; i < maxSlot; ++i) {
            if (container.getSlot(i).getHasStack()) {
                isEmpty = false;
            }
        }
        return isEmpty;
    }
	
	public boolean isGui() {
		int range = 5;
		for(int i = -range; i < range; ++i) {
			for(int j = range; j > -range; --j) {
				for(int k = -range; k < range; ++k) {
					int n2 = (int)mc.thePlayer.posX + i;
					int n3 = (int)mc.thePlayer.posY + j;
					int n4 = (int)mc.thePlayer.posZ + k;
					BlockPos blockPos = new BlockPos(n2, n3, n4);
					Block block = mc.theWorld.getBlockState(blockPos).getBlock();
					if(block instanceof BlockChest || block instanceof BlockEnderChest) {
						return false;
					}
				}
			}
		}
		return true;
	}

}
