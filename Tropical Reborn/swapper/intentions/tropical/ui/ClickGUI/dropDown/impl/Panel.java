package swapper.intentions.tropical.ui.ClickGUI.dropDown.impl;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import swapper.intentions.tropical.module.Module.Category;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.ui.ClickGUI.dropDown.ClickGui;
import swapper.intentions.tropical.utils.font.Fonts;

public class Panel {

	public ClickGui clickGUI;
	public Category category;
	public ArrayList<Button> buttons = new ArrayList<>();
	
	public double x;
	public double y = 5;
	
	public double width = 100;
	public double height = 14.5;
	private double offsetX = 0;
	private double offsetY = 0;
	
	public boolean dragging;
	public boolean open = true;
	
	public Panel(int x,Category category, ClickGui clickGUI) {
		this.x = x;
		this.clickGUI = clickGUI;
		this.category = category;
		int count = 0;
		for(Module mod : Tropical.instance.moduleManager.modules) {
			if(mod.getCategory() == category) {
				buttons.add(new Button(height + (count * 15), mod,this));
				count++;
			}
		}
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if(dragging) {
			x = mouseX - offsetX;
			y = mouseY - offsetY;
		}
		Minecraft mc = Minecraft.getMinecraft();
		Gui.drawRect(x, y, x + width, y + height, ClickGui.getMainColor().getRGB());
		Fonts.apple18.drawString(category.name, (float)(x + 5), (float)(y + ((height - Fonts.apple18.getHeight()) / 2)), -1);
		if(open) {
			int count = 0;
			double lButHeight = 0;
			for(Button b : buttons) {
				if(count > 0)
					lButHeight += buttons.get(count - 1).setHeight;
				Gui.drawRect(x, b.y + y + lButHeight, x + width, b.y + y + b.height + 1 + lButHeight, ClickGui.getMainColor().getRGB());
				b.drawScreen(mouseX, mouseY, partialTicks, lButHeight);
				count++;
			}
		}
	}

	public void keyTyped(char typedChat, int keyCode) {
		if(open)
			for(Button b : buttons) {
				b.keyTyped(typedChat, keyCode);
			}
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(isHovered(mouseX,mouseY)) {
			switch(mouseButton) {
				case 0:
					dragging = true;
					offsetX = mouseX - x;
					offsetY = mouseY - y;
					break;
				case 1:
					open = !open;
					break;
			}
		}
		if(open)
			for(Button b : buttons) {
				b.mouseClicked(mouseX, mouseY, mouseButton);
			}
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		if(open)
			for(Button b : buttons) {
				b.mouseReleased(mouseX, mouseY, state);
			}
		dragging = false;
	}
	
	public boolean isHovered(int mouseX, int mouseY) {
		return (mouseX > x && mouseX < x + width)
				&& (mouseY > y && mouseY < y + height);
	}
}
