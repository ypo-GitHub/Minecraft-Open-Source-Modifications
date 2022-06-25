package swapper.intentions.tropical.module.movement.fly.impl.verus;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.Fly;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class VerusPacket extends FlyMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public VerusPacket() {
		super("Verus Packet");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		if(mc.thePlayer.ticksExisted % 2 == 0) {
			e.setPosY(mc.thePlayer.posY-27.25);
		}
	}

	@Override
	public void onMove(MoveEvent e) {
		Fly fly = ((Fly) Tropical.instance.moduleManager.getModuleByName("Flight"));
		mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
		double speed = fly.speed.getValue();
		mc.thePlayer.motionY = (mc.gameSettings.keyBindJump.isKeyDown() ? speed :
				mc.gameSettings.keyBindSneak.isKeyDown() ? -speed :
						0.0);
		MoveUtils.strafe(speed);
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
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(x,y,z,yaw,pitch,true));
			e.setCancelled(true);
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,mc.thePlayer.rotationYaw,mc.thePlayer.rotationPitch,true));
		}
		if(e.getPacket() instanceof S12PacketEntityVelocity) {
			if(((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
				S12PacketEntityVelocity a = e.getPacket();
				double x = a.getMotionX()/8000.0;
				double z = a.getMotionZ()/8000.0;
			}
		}
	}

	@Override
	public void onEnable() {
		ticks = 0;
	}

	@Override
	public void onBlockCollision(BlockCollisionEvent e) {

	}
}
