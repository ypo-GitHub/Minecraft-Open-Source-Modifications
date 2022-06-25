package swapper.intentions.tropical.module.movement.fly.impl.mmc;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.Fly;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.module.player.Blink;
import swapper.intentions.tropical.utils.MoveUtils;

//might move to speed when not lazy
public class Hurttime extends FlyMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public Hurttime() {
		super("HurtTime");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		Fly fly = ((Fly) Tropical.instance.moduleManager.getModuleByName("Flight"));
		Blink blink = (Blink) Tropical.instance.moduleManager.getModuleByName("Blink");
		if(!blink.isToggled()) {
			if (ticks > fly.speed.getValue()) {
				MoveUtils.strafe(ticks / fly.htDivisor.getValue());
				ticks = 0;
			}
			if (mc.thePlayer.hurtTime == 1) {
				if (MoveUtils.getSpeed() > 0.4) {
					mc.thePlayer.motionX *= 0.6;
					mc.thePlayer.motionZ *= 0.6;
				}
			}
		}
	}

	@Override
	public void onMove(MoveEvent e) {

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
		if(e.getPacket() instanceof S12PacketEntityVelocity) {
			if(((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
				S12PacketEntityVelocity a = e.getPacket();
				double x = a.getMotionX()/8000.0;
				double z = a.getMotionZ()/8000.0;
				ticks += Math.sqrt(Math.sqrt(x * x + z * z));
			}
		}
	}

	@Override
	public void onEnable() {
		ticks = 0;
	}

	@Override
	public void onBlockCollision(BlockCollisionEvent e) {
		/*
		AxisAlignedBB bb = e.getAxisAlignedBB();
		BlockPos pos = mc.thePlayer.getPosition();
		
		if(bb != null)
			e.setAxisAlignedBB(AxisAlignedBB.fromBounds(bb.minX-1,ticks-1,bb.minZ-1,bb.maxX+1,ticks,bb.maxZ+1));
		else
			e.setAxisAlignedBB(AxisAlignedBB.fromBounds(pos.getX()-1,ticks-1,pos.getZ()-1,pos.getX()+1,ticks,pos.getZ()+1));*/
	}
}
