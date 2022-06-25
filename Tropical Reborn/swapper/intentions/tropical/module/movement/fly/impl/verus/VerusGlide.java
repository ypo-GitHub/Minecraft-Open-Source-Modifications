package swapper.intentions.tropical.module.movement.fly.impl.verus;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.Fly;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class VerusGlide extends FlyMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public VerusGlide() {
		super("Verus Glide");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		double speed = /*fly.speed.getValue()*/ 0.37485F;

		MoveUtils.strafe(speed);
	}

	@Override
	public void onMove(MoveEvent e) {
		Fly fly = ((Fly) Tropical.instance.moduleManager.getModuleByName("Flight"));
		mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
		if(mc.thePlayer.fallDistance > 0)
			mc.thePlayer.motionY = 0.0;
    	if(mc.thePlayer.onGround) {
    		mc.thePlayer.jump(); e.setMotionY(.42F);
    	}
	}

	@Override
	public void onPacket(PacketEvent e) {
		if(e.getPacket() instanceof S08PacketPlayerPosLook && mc.getNetHandler().doneLoadingTerrain) {
			S08PacketPlayerPosLook iq = e.getPacket();
			double x = iq.getX(),
					y = iq.getY(),
					z = iq.getZ();
					float yaw = iq.getYaw(),
					pitch = iq.getPitch();
			//mc.thePlayer.setPositionAndRotation(x, y, z, yaw, pitch);
			//mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(x,y,z,yaw,pitch,true));
			//e.setCancelled(true);
		}
	}

	@Override
	public void onEnable() {
		ticks = 0;
	}

	@Override
	public void onBlockCollision(BlockCollisionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
