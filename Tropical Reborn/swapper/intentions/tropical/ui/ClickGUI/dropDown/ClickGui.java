//K1LE HALLAHHH! (not recoding yet)

package swapper.intentions.tropical.ui.ClickGUI.dropDown;

import java.io.IOException;
import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiScreen;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.module.Module.Category;
import swapper.intentions.tropical.ui.ClickGUI.dropDown.impl.Panel;

public class ClickGui extends GuiScreen {
	
	public ArrayList<Panel> panels = new ArrayList<Panel>();
	
	public ClickGui() {
		Tropical.instance.moduleManager.sortModules();
		int count = 0;
		
		for(Category category : Category.values()) {
			panels.add(new Panel(5 + (count * 105),category,this));
			count++;
		}
	}

	public static Color getMainColor() {
		return new Color(21,21,21);
	}
	
	public static Color getSecondaryColor() {
		return new Color(25,25,25);
	}
	
	public static Color getThirdColor() {
		return new Color(31,31,31);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		for(Panel p : panels) {
			p.drawScreen(mouseX, mouseY, partialTicks);
		}
	}

	protected void keyTyped(char typedChat, int keyCode) {
		if(keyCode == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(null);
		} else {
			for(Panel p : panels) {
				p.keyTyped(typedChat, keyCode);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for(Panel p : panels) {
			p.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		for(Panel p : panels) {
			p.mouseReleased(mouseX, mouseY, state);
		}
	}
	
	@Override
	public void onGuiClosed() {
		Tropical.instance.moduleManager.getModuleByName("ClickGUI").toggle();
	}
	
}
