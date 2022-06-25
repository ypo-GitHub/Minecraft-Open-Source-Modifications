package swapper.intentions.tropical.module.movement.fly.impl.mmc;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class MMC extends FlyMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public MMC() {
		super("MMC");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
	}

	@Override
	public void onMove(MoveEvent e) {
		mc.timer.timerSpeed = 0.333f;
		mc.thePlayer.motionY = 0.0;
		mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer());
		mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY-3.2,mc.thePlayer.posZ,true));
		mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(null));
		MoveUtils.strafe(e,5);
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
			/*
			mc.thePlayer.setPosition(x, y, z);
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(x,y,z,yaw,pitch,true));
			e.setCancelled(true);
			mc.thePlayer.posX = mc.thePlayer.lastTickPosX;
			mc.thePlayer.posY = mc.thePlayer.lastTickPosY;
			mc.thePlayer.posZ = mc.thePlayer.lastTickPosZ;*/
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
