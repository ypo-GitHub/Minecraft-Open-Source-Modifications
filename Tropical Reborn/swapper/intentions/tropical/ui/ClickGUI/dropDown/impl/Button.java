package swapper.intentions.tropical.ui.ClickGUI.dropDown.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.awt.*;

import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.module.render.HUD;
import swapper.intentions.tropical.settings.Setting;
import swapper.intentions.tropical.settings.settings.BooleanSetting;
import swapper.intentions.tropical.settings.settings.ColorSetting;
import swapper.intentions.tropical.settings.settings.ModeSetting;
import swapper.intentions.tropical.settings.settings.NumberSetting;
import swapper.intentions.tropical.ui.ClickGUI.dropDown.ClickGui;
import swapper.intentions.tropical.ui.ClickGUI.dropDown.impl.set.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import swapper.intentions.tropical.ui.ClickGUI.dropDown.impl.set.Checkbox;
import swapper.intentions.tropical.utils.ColorUtils;
import swapper.intentions.tropical.utils.font.Fonts;

public class Button {
	public swapper.intentions.tropical.ui.ClickGUI.dropDown.impl.Panel panel;
	public Module mod;
	
	public ArrayList<SetComp> settings = new ArrayList<>();
	
	public double y;
	public boolean extended = false;
	public double height = 13;
	public double setHeight = 0;
	private double sussy = 0;
	
	public Button(double y,Module mod, Panel panel) {
		this.y = y;
		this.panel = panel;
		this.mod = mod;
		for (Setting s : mod.getSettings()) {
			if (s instanceof NumberSetting) {
				this.settings.add(new Slider((NumberSetting) s, this));
			}
			if (s instanceof BooleanSetting) {
				this.settings.add(new Checkbox((BooleanSetting) s, this));
			}
			if (s instanceof ModeSetting) {
				this.settings.add(new Mode((ModeSetting) s, this));
			}
			if (s instanceof ColorSetting) {
				this.settings.add(new ColorPicker((ColorSetting) s, this));
			}
		}
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks, double plusplus) {
		this.height = 14;
		Minecraft mc = Minecraft.getMinecraft();
		
		Gui.drawRect(panel.x + 1,panel.y + y + plusplus, panel.x + panel.width - 1, panel.y + y + height + plusplus, mod.isToggled() ? !isHovered(mouseX, mouseY) ? ColorUtils.getColorInt((int) (y/1.875)) : new Color(ColorUtils.getColorInt((int) (y))).darker().getRGB() : isHovered(mouseX, mouseY) ?
				ClickGui.getSecondaryColor().getRGB() : ClickGui.getThirdColor().getRGB());
		Fonts.apple18.drawString(mod.getDisplayName(), (float)(panel.x + 5), (float)(panel.y + y + ((height - Fonts.apple18.getHeight()) / 2) + plusplus), -1);

		setHeight = 0;
		sussy = plusplus;
		if(extended) {
			int count = 0;
			for(SetComp sc : settings) {
				if(!sc.getSetting().isHidden()) {
					setHeight += sc.drawScreen(mouseX, mouseY, panel.x, panel.y + y + height + plusplus + count*sc.getHeight());
					count++;
				}
			}
		}
	}

	public void keyTyped(char typedChat, int keyCode) {
		
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(isHovered(mouseX,mouseY)) {
			switch(mouseButton) {
				case 0:
					mod.toggle();
					break;
				case 1:
					extended = !extended;
					break;
			}
		}
		for(SetComp sc : settings) {

			if(!sc.getSetting().isHidden())
				sc.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		for(SetComp sc : settings) {

			if(!sc.getSetting().isHidden())
				sc.mouseReleased(mouseX, mouseY, state);
		}
	}
	
	public boolean isHovered(int mouseX, int mouseY) {
		return (mouseX > panel.x && mouseX < panel.x + panel.width)
				&& (mouseY > panel.y + y + sussy && mouseY < panel.y + y + height + sussy);
	}
	
	public double getWidth() {
		return panel.width;
	}
}
