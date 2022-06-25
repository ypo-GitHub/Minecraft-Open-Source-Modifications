package swapper.intentions.tropical.module.movement.fly.impl.matrix;

import net.minecraft.util.MovementInput;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class Matrix117 extends FlyMode {
	public double ticks = 0;
	
	public Matrix117() {
		super("Matrix 1.17");
	}
	
	@Override
	public void onEnable() {
		ticks = 0;
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		
		MovementInput movementInput = mc.thePlayer.movementInput;
        if (mc.thePlayer.getHealth() == mc.thePlayer.getMaxHealth()) {
            mc.timer.timerSpeed = 5.1f;
        } else {
            mc.timer.timerSpeed = 1f;
        }
        if (mc.thePlayer.ticksExisted % 4 == 1) {
            mc.thePlayer.motionY = movementInput.jump ? 0.98 : movementInput.sneak ? -0.98 : .0784;
            MoveUtils.strafe(0.98);
        }
        if (mc.thePlayer.ticksExisted % 4 == 0) {
            mc.thePlayer.motionY = 0;
            MoveUtils.strafe(0);
            mc.thePlayer.jumpMovementFactor = 0;
        }
	}
	
	@Override
	public void onMove(MoveEvent e) {
	}

	@Override
	public void onPacket(PacketEvent e) {
		
	}

	@Override
	public void onBlockCollision(BlockCollisionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
