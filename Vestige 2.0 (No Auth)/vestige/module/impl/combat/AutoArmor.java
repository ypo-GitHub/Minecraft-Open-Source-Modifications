package vestige.module.impl.combat;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;
import vestige.event.Event;
import vestige.event.impl.EventUpdate;
import vestige.module.Category;
import vestige.module.Module;
import vestige.setting.impl.BooleanSetting;
import vestige.setting.impl.NumberSetting;
import vestige.util.misc.ArmorUtils;
import vestige.util.misc.TimerUtil;

public class AutoArmor extends Module {
	
	private int[] chestplate, leggings, boots, helmet;
    private int delay;
    private boolean best;
    
    private NumberSetting delayValue = new NumberSetting("Delay", 50, 0, 300, 10, this);
    private BooleanSetting openInv = new BooleanSetting("Open Inv", false, this);
    
    private TimerUtil timer = new TimerUtil();
	
	public AutoArmor() {
		super("AutoArmor", Category.COMBAT);
		this.addSettings(delayValue, openInv);
		
		chestplate = new int[] {311, 307, 315, 303, 299};
        leggings = new int[] {312, 308, 316, 304, 300};
        boots = new int[] {313, 309, 317, 305, 301};
        helmet = new int[] {310, 306, 314, 302, 298};
        delay = 0;
        best = true;
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventUpdate) {
			if(mc.currentScreen instanceof GuiInventory || !openInv.isEnabled()) {
				autoArmor();
		        betterArmor();
			}
		}
	}

    public void autoArmor() {
        if(best)
            return;
        int item = -1;
        //delay += 1;
        if(timer.hasReached((long) delayValue.getValue())) {
            if(mc.thePlayer.inventory.armorInventory[0] == null) {
                int[] boots;
                int length = (boots = this.boots).length;
                for(int i =0; i < length; i++) {
                    int id = boots[i];
                    if(ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if(mc.thePlayer.inventory.armorInventory[1] == null) {
                int[] leggings;
                int length = (leggings = this.leggings).length;
                for(int i = 0; i < length; i++) {
                    int id = leggings[i];
                    if(ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if(mc.thePlayer.inventory.armorInventory[2] == null) {
                int[] chestplate;
                int length = (chestplate = this.chestplate).length;
                for(int i = 0; i < length; i++) {
                    int id = chestplate[i];
                    if(ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if(mc.thePlayer.inventory.armorInventory[3] == null) {
                int[] helmet;
                int length = (helmet = this.helmet).length;
                for(int i = 0; i < length; i++) {
                    int id = helmet[i];
                    if(ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if(item != -1) {
                mc.playerController.windowClick(0, item, 0, 1, mc.thePlayer);
               	timer.reset();
            }
        }
    }

    public void betterArmor() {
        if(!best)
            return;
        //delay += 1;
        if(timer.hasReached((long) delayValue.getValue()) && (mc.thePlayer.openContainer == null || mc.thePlayer.openContainer.windowId == 0)) {
            boolean switchArmor = false;
            int item = -1;
            int[] array;
            int i;
            if(mc.thePlayer.inventory.armorInventory[0] == null) {
                int j = (array = this.boots).length;
                for(i = 0; i < j; i++) {
                    int id = array[i];
                    if(ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if (ArmorUtils.isBetterArmor(0, this.boots)) {
                item = 8;
                switchArmor = true;
            }
            if(mc.thePlayer.inventory.armorInventory[3] == null) {
                int j = (array = this.helmet).length;
                for(i = 0; i < j; i++) {
                    int id = array[i];
                    if(ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if (ArmorUtils.isBetterArmor(3, this.helmet)) {
                item = 5;
                switchArmor = true;
            }
            if(mc.thePlayer.inventory.armorInventory[1] == null) {
                int j = (array = this.leggings).length;
                for(i = 0; i < j; i++) {
                    int id = array[i];
                    if(ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if (ArmorUtils.isBetterArmor(1, this.leggings)) {
                item = 7;
                switchArmor = true;
            }
            if(mc.thePlayer.inventory.armorInventory[2] == null) {
                int j = (array = this.chestplate).length;
                for(i = 0; i < j; i++) {
                    int id = array[i];
                    if(ArmorUtils.getItem(id) != -1) {
                        item = ArmorUtils.getItem(id);
                        break;
                    }
                }
            }
            if (ArmorUtils.isBetterArmor(2, this.chestplate)) {
                item = 6;
                switchArmor = true;
            }
            boolean b = false;
            ItemStack[] stackArray;
            int k = (stackArray = mc.thePlayer.inventory.mainInventory).length;
            for(int j = 0; j < k; j++) {
                ItemStack stack = stackArray[j];
                if(stack == null) {
                    b = true;
                    break;
                }
            }
            switchArmor = switchArmor && !b;
            if(item != -1) {
                mc.playerController.windowClick(0, item, 0, switchArmor ? 4 : 1, mc.thePlayer);
                timer.reset();
            }
        }
    }

}
