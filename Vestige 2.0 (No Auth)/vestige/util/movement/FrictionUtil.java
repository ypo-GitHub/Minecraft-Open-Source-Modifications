package vestige.util.movement;

import net.minecraft.client.Minecraft;
import vestige.Vestige;
import vestige.event.impl.MovementEvent;
import vestige.util.base.BaseUtil;

public class FrictionUtil implements BaseUtil {

    private double speed;
    private boolean prevOnGround;
    private boolean strafing;
    
    private float lastDirection;
    
    private double lastMotionX, lastMotionZ;
    
    public void updateNCPFriction(MovementEvent e, double jumpMotionY, double jumpSpeed, double friction, double lastGroundMult) {
        if(mc.thePlayer.onGround) {
        	if(MovementUtils.isWalking()) {
        		e.setMotionY(mc.thePlayer.motionY = jumpMotionY);
        		speed = MovementUtils.getBaseMoveSpeed() + jumpSpeed;
                prevOnGround = true;
        	}
        } else {
            if (prevOnGround) {
                speed *= lastGroundMult;
                prevOnGround = false;
            }
            else {
            	if(speed > MovementUtils.getBaseMoveSpeed() / 2) {
            		speed -= speed / friction;
            	}
            }
        }
        MovementUtils.setSpeed(e, speed);
    }
    
}
