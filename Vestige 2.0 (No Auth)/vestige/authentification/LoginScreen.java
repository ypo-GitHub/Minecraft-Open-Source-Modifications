package vestige.authentification;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import net.minecraft.util.ResourceLocation;
import vestige.Vestige;
import vestige.util.misc.TimerUtil;
import vestige.util.render.ColorUtil;

import org.lwjgl.input.Keyboard;

public final class LoginScreen extends GuiScreen {
    
	private TimerUtil timer;
	private GuiButton loginButton;
	
	private Thread currentThread;
	
    public LoginScreen() {
    	timer = new TimerUtil();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
        	case 0: {
        		currentThread = new Thread(Vestige.getAuthProcessor());
        		currentThread.start();
        		timer.reset();
        		
        		button.enabled = false;
        		break;
        	}
        	case 1: {
        		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        		clipboard.setContents(new StringSelection(Vestige.getAuthProcessor().getHWID()), null);
        		break;
        	}
        }
    }

    @Override
    public void drawScreen(int x2, int y2, float z2) {
    	FontRenderer fr = mc.fontRendererObj;
        
    	ScaledResolution sr = new ScaledResolution(mc);
    	
    	loginButton.enabled = (currentThread == null || !currentThread.isAlive()) && timer.getTimeElapsed() > 1000;
    	
        mc.getTextureManager().bindTexture(new ResourceLocation("vestige/background.jpg"));
		Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), ColorUtil.vestigeColors(4.5F, 20));
		this.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, this.width, this.height, this.width, this.height);
		this.drawGradientRect(0, height - 100, width, height, 0x00000000, 0x99000000);
		this.drawGradientRect(0, 100, width, 0, 0x00000000, 0x80000000);
        
        super.drawScreen(x2, y2, z2);
        
        String aaa = currentThread != null && currentThread.isAlive() ? "Login in..." : Vestige.getAuthProcessor().runnedOnce() ? "Login failed !" : "Welcome back !";
        fr.drawStringWithShadow(aaa, width / 2 - fr.getStringWidth(aaa) / 2, this.height / 2 - 45, -1);
    }

    //he didnt
    //im better
    // i didnt deobf, its the full src
    
    @Override
    public void initGui() {
    	FontRenderer fr = mc.fontRendererObj;
        int var3 = height / 4 + 24;
        //this.username = new GuiTextField(var3, this.mc.fontRendererObj, width / 2 - 75, this.height / 2 - 20, 150, 20);
        this.buttonList.add(loginButton = new GuiButton(0, width / 2 - 75, this.height / 2 + 15, 150, 20, "Login"));
        this.buttonList.add(new GuiButton(1, width / 2 - 75, this.height / 2 + 40, 150, 20, "Get HWID"));
        //this.username.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}

