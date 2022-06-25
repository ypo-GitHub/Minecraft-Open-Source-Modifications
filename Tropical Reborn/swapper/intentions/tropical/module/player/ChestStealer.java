package swapper.intentions.tropical.module.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.BooleanSetting;
import swapper.intentions.tropical.settings.settings.NumberSetting;
import swapper.intentions.tropical.utils.TimerUtils;

public class ChestStealer extends Module {

	public TimerUtils timer = new TimerUtils();
	public NumberSetting delay = new NumberSetting("Delay", 50, 5, 5, 500);
	public BooleanSetting checkTitle = new BooleanSetting("Check Chest Title", false);
	ArrayList<String> badItems = new ArrayList<>(Arrays.asList("stick", "string", "flint", "bucket", "feather", "snow", "piston", "web", "slime", "shears", "exp", "enchant", "seeds", "torch", "boat", "wheat"));
	
	public ChestStealer() {
		super("ChestStealer", "Automatically steals chests.", Keyboard.KEY_B, Category.PLAYER);
		setDisplayName("Chest Stealer");
		addSettings(delay, checkTitle);
	}
	
	@Override
	public void onEnable() {
		
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent event) {
		setSuffix(String.valueOf(delay.getValue().intValue()));
		if(mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest) {
			if(mc.thePlayer == null)
				return;
			ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
			if((chest.getLowerChestInventory().getName().contains("Kit") || chest.getLowerChestInventory().getName().contains("like")) && checkTitle.getValue())
				return;
			int items = 0;
			for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
				if(chest.getLowerChestInventory().getStackInSlot(i) != null && !isBad(chest.getLowerChestInventory().getStackInSlot(i).getItem())) {
					items++;
				}
			}
			for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
				if(chest.getLowerChestInventory().getStackInSlot(i) != null && !isBad(chest.getLowerChestInventory().getStackInSlot(i).getItem()) && timer.hasReached(delay.getValue())) {
					mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
					timer.reset();
				}
			}
			if(items == 0) {
				mc.thePlayer.closeScreen();
			}
		}
	}

	@Override
	protected void onDisable() {
		
	}
	
	private boolean isBad(Item i) {
		return badItems.contains(i.getUnlocalizedName());
	}

}
