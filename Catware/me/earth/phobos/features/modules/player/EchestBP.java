package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;

public class EchestBP extends Module {
  private GuiScreen echestScreen = null;
  
  public EchestBP() {
    super("EchestBP", "Allows to open your echest later.", Module.Category.PLAYER, false, true, false);
  }
  
  public void onUpdate() {
    InventoryBasic basic;
    Container container;
    if (mc.currentScreen instanceof GuiContainer && container = ((GuiContainer)mc.currentScreen).inventorySlots instanceof ContainerChest && ((ContainerChest)container).getLowerChestInventory() instanceof InventoryBasic && (basic = (InventoryBasic)((ContainerChest)container).getLowerChestInventory()).getName().equalsIgnoreCase("Ender Chest")) {
      this.echestScreen = mc.currentScreen;
      mc.currentScreen = null;
    } 
  }
  
  public void onDisable() {
    if (!fullNullCheck() && this.echestScreen != null)
      mc.displayGuiScreen(this.echestScreen); 
    this.echestScreen = null;
  }
}
