package swapper.intentions.tropical.utils;

import net.minecraft.client.gui.Gui;

public class RenderUtils {
    public static void drawBorderedRect(float x, float y, float endX, float endY, float width, int rect, int border) {
        Gui.drawRect(x,y,endX,endY,rect);
        Gui.drawRect(x - width,y,x,endY,border);
        Gui.drawRect(endX,y,endX + width,endY,border);
        Gui.drawRect(x - width,y - width,endX + width,y,border);
        Gui.drawRect(x - width,endY,endX + width,endY + width,border);
    }
}
