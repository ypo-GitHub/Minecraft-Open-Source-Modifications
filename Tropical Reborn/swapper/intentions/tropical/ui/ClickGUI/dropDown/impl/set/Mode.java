package swapper.intentions.tropical.ui.ClickGUI.dropDown.impl.set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import swapper.intentions.tropical.settings.settings.ModeSetting;
import swapper.intentions.tropical.ui.ClickGUI.dropDown.ClickGui;
import swapper.intentions.tropical.ui.ClickGUI.dropDown.impl.Button;
import swapper.intentions.tropical.utils.font.Fonts;
import swapper.intentions.tropical.utils.font.MCFontRenderer;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Mode extends SetComp {

	private boolean dragging = false;
	private double x;
	private double y;
	private static int height = 13;
	private boolean hovered;
	private ModeSetting set;
	private boolean isOpened = false;

	public Mode(ModeSetting s, Button b) {
		super(s, b, height);
		this.set = s;
	}

	@Override
	public double drawScreen(int mouseX, int mouseY, double x, double y) {
		this.hovered = this.isHovered(mouseX, mouseY);
		this.x = x;
		this.y = y;
		MCFontRenderer font = Fonts.apple18;
		String name = this.set.getName() + "";

		Gui.drawRect(x, y, x + this.parent.getWidth(), y + height + 1, ClickGui.getSecondaryColor().getRGB());
		Gui.drawRect(this.x + 1, this.y, this.x - 1 + this.parent.getWidth(), this.y + this.height, ClickGui.getSecondaryColor().brighter().getRGB());

		font.drawString(name, (int)(this.x + 3), (int)(y + (font.getHeight() / 2) + 0.0F), new Color(255,255,255).darker().getRGB());
		font.drawString(this.set.getCurrentValue(), (int)((this.x + 100) - font.getStringWidth(this.set.getCurrentValue()) - 3), (int)((y + (font.getHeight() / 2) + 0.0F)), -1);
		
		return this.height;
	}

	@Override
	public void mouseClicked(int x, int y, int button) {
		if ((button == 0 || button == 1) && this.hovered) {
				List<String> options = Arrays.asList(this.set.getValue());
				int index = options.indexOf(this.set.getCurrentValue());
				if (button == 0) {
					index++;
				} else if (button == 1) {
					index--;
				}
				if (index >= options.size()) {
					index = 0;
				} else if (index < 0) {
					index = options.size() - 1;
				}
				this.set.setCurrentValue(this.set.getValue()[index]);
			}

	}

	private boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + this.parent.getWidth() && mouseY >= y && mouseY <= y + height;
	}
}
