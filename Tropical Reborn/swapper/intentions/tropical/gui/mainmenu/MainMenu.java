package swapper.intentions.tropical.gui.mainmenu;

import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.gui.altlogin.GuiAltLogin;
import swapper.intentions.tropical.utils.ColorUtils;
import swapper.intentions.tropical.utils.font.Fonts;

import java.awt.*;
import java.io.IOException;

public class MainMenu extends GuiScreen {

    public MainMenu() {

    }

    public void initGui() {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 - 40, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 2 - 40 + 22, I18n.format("menu.multiplayer")));
        this.buttonList.add(new GuiButton(1337, this.width / 2 - 100, this.height / 2 - 40 + 44, I18n.format("Alt Login")));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 - 40 + 66, 98, 20, I18n.format("menu.options")));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, this.height / 2 - 40 + 66, 98, 20, I18n.format("menu.quit")));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        int sc = mc.gameSettings.guiScale;

        if(sc == 0)
            sc = 2;

        drawRect(0, 0, mc.displayWidth, mc.displayHeight, new Color(31,31,31).getRGB());
        drawGradientRect(0, (int)(mc.displayHeight*0.6) / sc, mc.displayWidth, mc.displayHeight / sc,new Color(31, 31, 31).getRGB(), ColorUtils.getColorInt(4));
        String s = Tropical.instance.name + " " + Tropical.instance.buildName;
        int x = (int)((mc.displayWidth - Fonts.axi45.getStringWidth(s) - 144) / 2 / sc);

        for(int i = 0; i < s.length(); i++) {
            Fonts.axi45.drawStringWithShadow(String.valueOf(s.charAt(i)), x, 28, ColorUtils.getColorInt(4 + i*6));
            x += Fonts.axi45.getStringWidth(String.valueOf(s.charAt(i)));
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if (button.id == 1)
        {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }

        if (button.id == 2)
        {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }

        if (button.id == 1337)
        {
            this.mc.displayGuiScreen(new GuiAltLogin(this));
        }

        if (button.id == 4)
        {
            this.mc.shutdown();
        }
    }
}
