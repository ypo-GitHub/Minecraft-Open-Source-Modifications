package vestige.ui.menu;

import java.awt.Color;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import vestige.Vestige;
import vestige.altlogin.GuiAltLogin;
import vestige.authentification.LoginScreen;
import vestige.util.misc.TimerUtil;
import vestige.util.render.ColorUtil;

public class VestigeMainMenu extends GuiScreen {
	
	private TimerUtil timer = new TimerUtil();
	public static TimerUtil loginTimer = new TimerUtil();
	private static boolean firstTime = true;
	private double posYName, posYMenu;
	
	public VestigeMainMenu() {
		posYName = -60;
		posYMenu = -4000;
		timer.reset();
	}
	
	public void initGui() {
		if(firstTime) {
			loginTimer.reset();
			firstTime = false;
		}

	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		FontRenderer fr = mc.fontRendererObj;
		ScaledResolution sr = new ScaledResolution(mc);
	
		
		if(timer.getTimeElapsed() > 0) {
			if(posYName < sr.getScaledHeight() / 15) {
				posYName += 0.18 * timer.getTimeElapsed() - (posYName / 6);
			}
			
			
			if(posYMenu == -4000) {
				posYMenu = sr.getScaledHeight() / 1.6F;
			}
			
			if(posYMenu > sr.getScaledHeight() / 2 - 50) {
				double diff = posYMenu - sr.getScaledHeight() / 2 - 50;
				posYMenu -= 0.25 * timer.getTimeElapsed() + (diff / 20);
			}
			
			if(posYMenu < sr.getScaledHeight() / 2 - 50) {
				posYMenu = sr.getScaledHeight() / 2 - 50;
			}
			
		}
		
		if(loginTimer.getTimeElapsed() < 50) {
			Vestige.getAuthProcessor().setAuthentificated(true);
			mc.displayGuiScreen(new LoginScreen());
			loginTimer.reset();
		}
		
		if(posYName > sr.getScaledHeight() / 15) {
			posYName = sr.getScaledHeight() / 15;
		}
		
		timer.reset();
		
		//posYMenu = sr.getScaledHeight() / 2 - 50;
		
		mc.getTextureManager().bindTexture(new ResourceLocation("vestige/background.jpg"));
		Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), ColorUtil.vestigeColors(4.5F, 20));
		this.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, this.width, this.height, this.width, this.height);
		this.drawGradientRect(0, height - 100, width, height, 0x00000000, 0x99000000);
		this.drawGradientRect(0, 100, width, 0, 0x00000000, 0x80000000);
		
		String[] buttons = {"Singleplayer", "Multiplayer", "Alt Manager", "Credits", "Settings", "Quit"};
		GlStateManager.translate(4, 4, 0);
		GlStateManager.scale(2, 2, 1);
		GlStateManager.translate(-4, -4, 0);
		//fr.drawStringWithShadow(Vestige.name, sr.getScaledWidth() / 4 - (fr.getStringWidth(Vestige.name) / 2), sr.getScaledHeight() / 15, -1);
		fr.drawStringWithShadow(Vestige.getName(), sr.getScaledWidth() / 4 - (fr.getStringWidth(Vestige.getName()) / 2), (float) posYName, -1);
		GlStateManager.translate(2, 2, 0);
		GlStateManager.scale(0.5, 0.5, 1);
		GlStateManager.translate(-3, -3, 0);
		
		//int offsetY = sr.getScaledHeight() / 2 - 50;
		float offsetY = (float) posYMenu;
		for(String button : buttons) {
			boolean moduleHovered = mouseX >= sr.getScaledWidth() / 2 - 40 && mouseY >= offsetY - 6 && mouseX < sr.getScaledWidth() / 2 + 40 && mouseY < offsetY + 11;
			if(moduleHovered) {
				Gui.drawRect(sr.getScaledWidth() / 2 - 40, offsetY - 4, sr.getScaledWidth() / 2 + 40, offsetY + 13, 0x95000000);
				
				//Gui.drawRect(sr.getScaledWidth() / 2 - 40, offsetY - 4, sr.getScaledWidth() / 2 - 38, offsetY + 13, ColorUtil.vestigeNewColors(4.5F, 20));
				//Gui.drawRect(sr.getScaledWidth() / 2 + 38, offsetY - 4, sr.getScaledWidth() / 2 + 40, offsetY + 13, ColorUtil.vestigeNewColors(4.5F, 20));
				
				//Gui.drawRect(sr.getScaledWidth() / 2 - 40, offsetY - 4, sr.getScaledWidth() / 2 + 40, offsetY - 2, ColorUtil.vestigeNewColors(4.5F, 20));
				//Gui.drawRect(sr.getScaledWidth() / 2 - 40, offsetY + 11, sr.getScaledWidth() / 2 + 40, offsetY + 13, ColorUtil.vestigeNewColors(4.5F, 20));
			} else {
				Gui.drawRect(sr.getScaledWidth() / 2 - 40, offsetY - 4, sr.getScaledWidth() / 2 + 40, offsetY + 13, 0x95000000);
			}
			fr.drawStringWithShadow(button, sr.getScaledWidth() / 2 - (fr.getStringWidth(button) / 2), offsetY, -1);
			offsetY += 17;
		}
	}
	
	public void mouseClicked(int mouseX, int mouseY, int button) {
		FontRenderer fr = mc.fontRendererObj;
		ScaledResolution sr = new ScaledResolution(mc);
		SoundHandler soundHandlerIn = this.mc.getSoundHandler();
		
		String[] buttons = {"Singleplayer", "Multiplayer", "Alt Manager", "Credits", "Settings", "Quit"};
		
		//int offsetY = sr.getScaledHeight() / 2 - 50;
		float offsetY = (float) posYMenu;
		
		for(String buttonClicked : buttons) {
			boolean moduleHovered = mouseX >= sr.getScaledWidth() / 2 - 40 && mouseY >= offsetY - 6 && mouseX < sr.getScaledWidth() / 2 + 40 && mouseY < offsetY + 11;
			if(moduleHovered) {
				switch(buttonClicked) {
					case "Singleplayer":
						soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
						mc.displayGuiScreen(new GuiSelectWorld(this));
						break;
					case "Multiplayer":
						soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
						mc.displayGuiScreen(new GuiMultiplayer(this));
						break;
					case "Alt Manager":
						soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
						mc.displayGuiScreen(new GuiAltLogin(mc.currentScreen));
						break;
					case "Credits":
						soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
						//mc.displayGuiScreen(new CreditsMenu());
						break;
					case "Settings":
						soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
						mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
						break;
					case "Quit":
						mc.shutdown();
						break;
				}
			}
			offsetY += 17;
		}
	}
	
	public void onGuiClosed() {
		
	}
	
}
