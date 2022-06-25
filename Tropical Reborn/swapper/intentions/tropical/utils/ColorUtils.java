package swapper.intentions.tropical.utils;

import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.module.ModuleManager;
import swapper.intentions.tropical.module.render.HUD;

import java.awt.*;

public class ColorUtils {
    public static float getRainbow(float seconds, int count) {
        return ((System.currentTimeMillis() + count) % (int)(seconds * 1000)) / (float)(seconds * 1000);
    }

    public static int fade(float seconds, int count) {
        Color c = new Color(HUD.color.getColor());
        float hsbVals[] = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float b = ColorUtils.getRainbow(seconds, count*10)%0.5F;
        b = (b > 0.25F ? 0.5F - b : b);
        return Color.getHSBColor( hsbVals[0], hsbVals[1], 0.5f + b * (1.0f + hsbVals[2])).getRGB();
    }

    public static int getColorInt(int count) {
        if(HUD.colorMode.getCurrentValue().equals("Rainbow")) {
            return Color.HSBtoRGB(ColorUtils.getRainbow(HUD.speed.getValue().floatValue(), count*8),0.6f,1f);
        }else if(HUD.colorMode.getCurrentValue().equals("Fade")) {
            return fade(HUD.speed.getValue().floatValue(), (int) (count * HUD.speed.getValue()*.5));
        }else if(HUD.colorMode.getCurrentValue().equals("Astolfo")) {
            return astolfo(HUD.speed.getValue().floatValue(), count*10);
        }
        return HUD.color.getColor();
    }

    public static int astolfo(float seconds, int offset) {
        float speed = seconds*1000;
        float hue = (System.currentTimeMillis() % (int)speed) + (offset);
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5F - (hue - 0.5f);
        }
        hue += 0.5F;
        return Color.getHSBColor(hue, 0.6F, 1F).getRGB();
    }
}
