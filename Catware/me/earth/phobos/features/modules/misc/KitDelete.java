package me.earth.phobos.features.modules.misc;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.TextUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Keyboard;

public class KitDelete extends Module {
  private final Setting<Bind> deleteKey = register(new Setting("Key", new Bind(-1)));
  
  private boolean keyDown;
  
  public KitDelete() {
    super("KitDelete", "Automates /deleteukit", Module.Category.MISC, false, true, false);
  }
  
  public void onTick() {
    if (((Bind)this.deleteKey.getValue()).getKey() != -1)
      if (mc.currentScreen instanceof GuiContainer && Keyboard.isKeyDown(((Bind)this.deleteKey.getValue()).getKey())) {
        Slot slot = ((GuiContainer)mc.currentScreen).getSlotUnderMouse();
        if (slot != null && !this.keyDown) {
          mc.player.sendChatMessage("/deleteukit " + TextUtil.stripColor(slot.getStack().getDisplayName()));
          this.keyDown = true;
        } 
      } else if (this.keyDown) {
        this.keyDown = false;
      }  
  }
}
