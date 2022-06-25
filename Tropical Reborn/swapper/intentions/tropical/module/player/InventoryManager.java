package swapper.intentions.tropical.module.player;

import net.minecraft.item.*;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.BooleanSetting;
import swapper.intentions.tropical.settings.settings.NumberSetting;
import swapper.intentions.tropical.utils.TimerUtils;

public class InventoryManager extends Module {

	public NumberSetting delay = new NumberSetting("Delay", 50, 5, 5, 500);
	public BooleanSetting invOpen = new BooleanSetting("Inv Open", false);
	public TimerUtils timer = new TimerUtils();

	public InventoryManager() {
		super("InventoryManager", "Manages your inventory for you", Keyboard.KEY_K, Category.PLAYER);
		addSettings(delay, invOpen);
		setDisplayName("Inventory Manager");
	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub

	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		setDisplayName("Inventory Manager");
		setSuffix(String.valueOf(delay.getValue().intValue()));
		if (invOpen.getValue())
			if (!(mc.currentScreen instanceof GuiInventory))
				return;

		for (int i = 9; i < 45; ++i) {
			if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
				continue;

			ItemStack stackInSlot = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			Item iteminSlot = stackInSlot.getItem();
			Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
			if(timer.hasReached(delay.getValue())){
				if (isBad(iteminSlot) || iteminSlot instanceof ItemPotion && isBadPotion(stackInSlot)) {
					dropItem(i);
					timer.reset();
				} else {
					if (iteminSlot.getUnlocalizedName().contains("sword") && i != 36) {
						if(mc.thePlayer.inventoryContainer.getSlot(36).getHasStack()) {
							if(iteminSlot.isItemTool(stackInSlot)) {
								if(getItemDamage(stackInSlot) < getItemDamage(mc.thePlayer.inventoryContainer.getSlot(36).getStack())) {
									dropItem(i);
								}else {
									dropItem(36);
									mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
									mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 36, 0, 0, mc.thePlayer);
								}
							}else {
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 36, 0, 0, mc.thePlayer);
							}
						}
						timer.reset();
						continue;
					}
					if (iteminSlot instanceof ItemArmor) {
						if (iteminSlot.getUnlocalizedName().contains("chestplate")) {
							if(mc.thePlayer.inventoryContainer.getSlot(6).getHasStack()) {
								if(((ItemArmor)iteminSlot).damageReduceAmount < ((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(6).getStack().getItem()).damageReduceAmount) {
									dropItem(i);
								}else {
									dropItem(6);
									mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
									mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 6, 0, 0, mc.thePlayer);
								}
							}else {
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 6, 0, 0, mc.thePlayer);
							}
							timer.reset();
							continue;
						}
						if (iteminSlot.getUnlocalizedName().contains("leggings")) {
							if(mc.thePlayer.inventoryContainer.getSlot(7).getHasStack()) {
								if(((ItemArmor)iteminSlot).damageReduceAmount < ((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(7).getStack().getItem()).damageReduceAmount) {
									dropItem(i);
								}else {
									dropItem(7);
									mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
									mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 7, 0, 0, mc.thePlayer);
								}
							}else {
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 7, 0, 0, mc.thePlayer);
							}
							timer.reset();
							continue;
						}
						if (iteminSlot.getUnlocalizedName().contains("boots")) {
							if(mc.thePlayer.inventoryContainer.getSlot(8).getHasStack()) {
								if(((ItemArmor)iteminSlot).damageReduceAmount < ((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(8).getStack().getItem()).damageReduceAmount) {
									dropItem(i);
								}else {
									dropItem(8);
									mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
									mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 8, 0, 0, mc.thePlayer);
								}
							}else {
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 8, 0, 0, mc.thePlayer);
							}
							timer.reset();
							continue;
						}

						if (iteminSlot.getUnlocalizedName().contains("helmet") && !(iteminSlot instanceof ItemSkull)) {
							if(mc.thePlayer.inventoryContainer.getSlot(5).getHasStack()) {
								if(((ItemArmor)iteminSlot).damageReduceAmount < ((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(5).getStack().getItem()).damageReduceAmount) {
									dropItem(i);
								}else {
									dropItem(5);
									mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
									mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 5, 0, 0, mc.thePlayer);
								}
							}else {
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 0, mc.thePlayer);
								mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 5, 0, 0, mc.thePlayer);
							}
							timer.reset();
							continue;
						}
					}
				}
			}
		}
	}


	private float getItemDamage(ItemStack stack) {
		if (stack.getItem() instanceof ItemSword) {
			ItemSword sword = (ItemSword) stack.getItem();
			float sharpness = (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F;
			float fireAspect = (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 1.5F;
			return sword.getDamageVsEntity() + sharpness + fireAspect;
		} else {
			return 0.0F;
		}
	}

	private boolean isBad(Item i) {
		return i.getUnlocalizedName().contains("stick") ||
				i.getUnlocalizedName().contains("string") ||
				i.getUnlocalizedName().contains("flint") ||
				i.getUnlocalizedName().contains("bucket") ||
				i.getUnlocalizedName().contains("feather") ||
				i.getUnlocalizedName().contains("snow") ||
				i.getUnlocalizedName().contains("piston") ||
				i instanceof ItemGlassBottle ||
				i.getUnlocalizedName().contains("web") ||
				i.getUnlocalizedName().contains("slime") ||
				i.getUnlocalizedName().contains("trip") ||
				i.getUnlocalizedName().contains("wire") ||
				i.getUnlocalizedName().contains("sugar") ||
				i.getUnlocalizedName().contains("note") ||
				i.getUnlocalizedName().contains("record") ||
				i.getUnlocalizedName().contains("flower") ||
				i.getUnlocalizedName().contains("wheat") ||
				i.getUnlocalizedName().contains("fishing") ||
				i.getUnlocalizedName().contains("boat") ||
				i.getUnlocalizedName().contains("leather") ||
				i.getUnlocalizedName().contains("seeds") ||
				i.getUnlocalizedName().contains("skull") ||
				i.getUnlocalizedName().contains("torch") ||
				i.getUnlocalizedName().contains("anvil") ||
				i.getUnlocalizedName().contains("enchant") ||
				i.getUnlocalizedName().contains("exp") ||
				i.getUnlocalizedName().contains("shears");
	}

	private void dropItem(int slot) {
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 0, mc.thePlayer);
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, -999, 0, 0, mc.thePlayer);
	}

	private boolean isBadPotion(ItemStack stack) {
		if (stack != null && stack.getItem() instanceof ItemPotion) {
			ItemPotion potion = (ItemPotion) stack.getItem();
			if (ItemPotion.isSplash(stack.getItemDamage())) {

				for (Object o : potion.getEffects(stack)) {
					PotionEffect effect = (PotionEffect) o;
					if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
						return true;
					}
				}
			}
		}

		return false;
	}


	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub

	}
}
