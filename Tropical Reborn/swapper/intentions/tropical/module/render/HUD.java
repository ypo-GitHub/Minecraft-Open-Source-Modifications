package swapper.intentions.tropical.module.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.RenderOverlayEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.BooleanSetting;
import swapper.intentions.tropical.settings.settings.ColorSetting;
import swapper.intentions.tropical.settings.settings.ModeSetting;
import swapper.intentions.tropical.settings.settings.NumberSetting;
import swapper.intentions.tropical.utils.ColorUtils;
import swapper.intentions.tropical.utils.font.Fonts;
import swapper.intentions.tropical.utils.font.MCFontRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class HUD extends Module {
	public BooleanSetting raw = new BooleanSetting("Raw", false);
	public static BooleanSetting mcFont = new BooleanSetting("MC Font", false);
	public static ModeSetting colorMode = new ModeSetting("Color", "Astolfo", "Static", "Rainbow", "Fade");
	public static ModeSetting rectMode = new ModeSetting("Rect", "Right", "Left", "Top", "Border", "None");
	public ModeSetting watermarkMode = new ModeSetting("Watermark", "Text", "None");
	public static ColorSetting color = new ColorSetting("First Color", new Color(160,0,255));
	public static NumberSetting speed = new NumberSetting("Color Speed", 5, .1, 1, 10);
	public static NumberSetting transparency = new NumberSetting("BG Alpha", 180, 1, 0, 255);
	public static NumberSetting extraHeight = new NumberSetting("Extra Height", 2, 1, 0, 3);
	public static NumberSetting extraX = new NumberSetting("Extra X", 5, 1, 0, 10);
	public static NumberSetting textY = new NumberSetting("Text Y", 1.5, 0.5, 0, 3);
	public static NumberSetting offset = new NumberSetting("Offset", 0.2, 1, 0, 10);
	
	public HUD() {
		super("HUD", "Display's the client overlay ingame", 0x0, Category.VISUALS);
		this.toggle();
		addSettings(raw, mcFont, textY, extraHeight, extraX, rectMode, transparency, color, colorMode, speed, watermarkMode, offset);
	}

	@Override
	protected void onEnable() {
		
	}

	public static class sortDefaultFont implements Comparator<Module> {
		@Override
		public int compare(Module arg0, Module arg1) {
			if (Minecraft.getMinecraft().fontRendererObj.getStringWidth(arg0.getFormattedName()) > Minecraft.getMinecraft().fontRendererObj.getStringWidth(arg1.getFormattedName())) {
				return -1;
			} else
				return 1;
		}
	}

	public static class sortCFont implements Comparator<Module> {
		@Override
		public int compare(Module arg0, Module arg1) {
			if (Fonts.apple18.getStringWidth(arg0.getFormattedName()) > Fonts.apple18.getStringWidth(arg1.getFormattedName())) {
				return -1;
			} else
				return 1;
		}
	}

	public static void drawString(String s, float x, float y, int color) {
		if(!mcFont.getValue())
			Fonts.apple18.drawString(s, x, y, color);
		else
			mc.fontRendererObj.drawStringWithShadow(s,x,y,color);
	}
	
	@Subscribe
	public void onRenderOverlay(RenderOverlayEvent event) {
		MCFontRenderer fr = Fonts.apple18;
		int count = 0;
		if(watermarkMode.getCurrentValue().equals("Text")) {
			String lText = Tropical.instance.name + " " + Tropical.instance.buildName;
			int x = 4;
			for(int i = 0; i < lText.length(); i++) {
				int w;
				if(!mcFont.getValue()) {
					w = Fonts.apple24.getStringWidth(String.valueOf(lText.charAt(i)));
					Fonts.apple24.drawString(String.valueOf(lText.charAt(i)), x, 4, ColorUtils.getColorInt(4 + i * 8), true);
				}else {
					w = mc.fontRendererObj.getStringWidth(String.valueOf(lText.charAt(i)));
					mc.fontRendererObj.drawString(String.valueOf(lText.charAt(i)), x, 4, ColorUtils.getColorInt(4 + i * 8), true);
				}
				x += w;
			}
		}
		double bpt = mc.thePlayer.getDistance(mc.thePlayer.lastTickPosX, mc.thePlayer.posY, mc.thePlayer.lastTickPosZ) * (Minecraft.getMinecraft().timer.ticksPerSecond * Minecraft.getMinecraft().timer.timerSpeed);
		bpt = Math.floor(bpt * 100) / 100;
		if(mc.thePlayer != null) {
			drawString(bpt + " BPS", 4, mc.displayHeight/mc.gameSettings.guiScale - fr.getHeight(), ColorUtils.getColorInt(50));
		}
		ArrayList<Module> mods = new ArrayList<>();
		Tropical.instance.moduleManager.modules.forEach((m) -> { if(m.isToggled() && !m.isHidden()) mods.add(m); });
		if(mcFont.getValue())
			mods.sort(new sortDefaultFont());
		else
			mods.sort(new sortCFont());
		int height = (int) ((mcFont.getValue() ? mc.fontRendererObj.FONT_HEIGHT : fr.getHeight())+extraHeight.getValue());
		for(Module m: mods) {
			String text = m.getFormattedName();
			if(raw.getValue()) {
				int y = offset.getValue().intValue() + (height*count);
				int textX = event.getSr().getScaledWidth() - offset.getValue().intValue() - (mcFont.getValue() ? mc.fontRendererObj.getStringWidth(text) : fr.getStringWidth(text));
				drawString(text, textX, y, ColorUtils.getColorInt(y));
			}else {
				int startX = event.getSr().getScaledWidth() - offset.getValue().intValue();
				int endX = event.getSr().getScaledWidth() - offset.getValue().intValue();
				int x = startX - extraX.getValue().intValue() - (mcFont.getValue() ? mc.fontRendererObj.getStringWidth(text) : fr.getStringWidth(text));
				int y = offset.getValue().intValue() + (height*count);
				int endY = offset.getValue().intValue() + (height*(count+1));
				Gui.drawRect(x, y, endX, endY, new Color(0, 0, 0, transparency.getValue().intValue()).getRGB());
				switch(rectMode.getCurrentValue()) {
					case "Border": {
						if(count+1 < mods.size()) {
							Module mod = mods.get(count+1);
							String otherText = mod.getFormattedName();
							endX = startX - extraX.getValue().intValue() - (mcFont.getValue() ? mc.fontRendererObj.getStringWidth(otherText) : fr.getStringWidth(otherText));
						}
						Gui.drawGradientRect(x, y, x + 1, endY, ColorUtils.getColorInt(y), ColorUtils.getColorInt(endY));
						Gui.drawGradientRect(x, endY, endX, endY + 1, ColorUtils.getColorInt(y), ColorUtils.getColorInt(endY));
						break;
					}

					case "Right": {
						Gui.drawGradientRect(startX - 1, y, startX, endY, ColorUtils.getColorInt(y), ColorUtils.getColorInt(endY));
						break;
					}

					case "Left": {
						Gui.drawGradientRect(x - 1, y, x, endY, ColorUtils.getColorInt(y), ColorUtils.getColorInt(endY));
						break;
					}

					case "Top": {
						if(count == 0) {
							Gui.drawGradientRect(x, y - 1, endX, y, ColorUtils.getColorInt(y), ColorUtils.getColorInt(endY));
						}
					}
				}
				drawString(text, x + extraX.getValue().intValue()-3, offset.getValue().intValue() + (textY.getValue().floatValue() + (height * count)), ColorUtils.getColorInt(y));
			}
			count++;
		}
	}

	@Override
	protected void onDisable() {
		
	}
}
