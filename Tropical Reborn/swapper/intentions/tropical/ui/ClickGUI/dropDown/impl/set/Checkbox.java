package swapper.intentions.tropical.ui.ClickGUI.dropDown.impl.set;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import swapper.intentions.tropical.module.render.HUD;
import swapper.intentions.tropical.settings.settings.BooleanSetting;
import swapper.intentions.tropical.ui.ClickGUI.dropDown.ClickGui;
import swapper.intentions.tropical.ui.ClickGUI.dropDown.impl.Button;
import swapper.intentions.tropical.utils.ColorUtils;
import swapper.intentions.tropical.utils.font.Fonts;
import swapper.intentions.tropical.utils.font.MCFontRenderer;

public class Checkbox extends SetComp {

	private boolean dragging = false;
	private double x;
	private double y;
	private static double height = 13;
	private boolean hovered;
	private BooleanSetting set;
	
	public Checkbox(BooleanSetting s, Button b) {
		super(s, b,height);
		this.set = s;
	}
	
	@Override
	public double drawScreen(int mouseX, int mouseY, double x, double y) {
		this.hovered = this.isHovered(mouseX, mouseY);
		this.x = x;
		this.y = y;
		MCFontRenderer font = Fonts.apple18;
		
		Gui.drawRect(x, y, x + this.parent.getWidth(), y + height + 1, ClickGui.getSecondaryColor().getRGB());
		Gui.drawRect(this.x + 1, this.y, this.x + this.parent.getWidth() - 1, this.y + this.height, ClickGui.getSecondaryColor().brighter().getRGB());
		String name = this.set.getName();
		font.drawString(name, (float)(this.x + 3), (float)(y + (font.getHeight() / 2) + 0.0F), -1);
		Gui.drawRect(this.x + this.parent.getWidth() - 12, this.y + 4, this.x + this.parent.getWidth() - 3, this.y + 12, this.set.getValue() ? ColorUtils.getColorInt((int) (this.y/1.875)) : new Color(17, 17, 17).getRGB());
		GlStateManager.color(1, 1, 1);
		float x1 = this.set.getValue() ? 5 : 1.5F;
		float x2 = this.set.getValue() ? 3 : -0.5F;
		
		float x1Diff = x1 - this.lastX1;
		float x2Diff = x2 - this.lastX2;
		this.lastX1 += x1Diff / 4;
		this.lastX2 += x2Diff / 4;
		//RenderUtils.drawRoundedRect(this.x + lastX1 + 2, this.y + 5, this.x + 10 + lastX2, this.y + 11, new Color(0, 0, 0, 0).getRGB(), new Color(255, 255, 255).getRGB());
		
		return this.height;
	}
	
	private float lastX1 = 1.5F;
	private float lastX2 = -0.5F;
	
	private float red = 0.70588235294F;
	private float green = 0.70588235294F;
	private float blue = 0.70588235294F;
	
	@Override
	public void mouseClicked(int x, int y, int button) {
		if (button == 0 && this.hovered)  {
			this.set.setValue(!this.set.getValue());
		}
	}
	
	private boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + this.parent.getWidth() && mouseY > y && mouseY < y + height;
	}
}
