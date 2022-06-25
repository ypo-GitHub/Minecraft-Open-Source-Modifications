package swapper.intentions.tropical.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import swapper.intentions.tropical.event.impl.MoveEvent;

public class MoveUtils {
	static Minecraft mc = Minecraft.getMinecraft();
	
	public static float getSpeedPotMultiplier() {
        float speedPotMultiplier = 1F;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amp = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            speedPotMultiplier = 1.0F + 0.2F * (amp + 1);
        }
        return speedPotMultiplier;
    }
	
	public static boolean isMoving() {
		return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
	}
	
	public static float getSpeedPotMultiplier(double multi) {
        float speedPotMultiplier = 1F;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amp = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            speedPotMultiplier = 1.0F + (float)multi * (amp + 1);
        }
        return speedPotMultiplier;
    }

    public static float getSpeed() {
        return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }
	
	public static double getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;
        if (mc.thePlayer.moveForward < 0F) forward = -0.5F;
        else if (mc.thePlayer.moveForward > 0F) forward = 0.5F;

        if (mc.thePlayer.moveStrafing > 0F) rotationYaw -= 90F * forward;

        if (mc.thePlayer.moveStrafing < 0F) rotationYaw += 90F * forward;

        return rotationYaw;
    }

    public static void strafe(final double speed) {
        if (!mc.thePlayer.isMoving()) return;

        final double yaw = getDirection() / 180 * Math.PI;
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }
    
    public static double[] getStrafe(float speed) {
        final double yaw = getDirection() / 180 * Math.PI;
		
    	double[] values = {0.0,0.0};
    	values[0] = (-(Math.sin(yaw) * speed));
    	values[1] = (Math.cos(yaw) * speed);
    	
    	return values;
    }
    
    public static void strafe(MoveEvent event, final double speed) {
        if (!mc.thePlayer.isMoving()) return;

        final double yaw = getDirection() / 180 * Math.PI;
        event.setMotionX(-Math.sin(yaw) * speed);
        event.setMotionZ(Math.cos(yaw) * speed);
    }
}
