package swapper.intentions.tropical.module.movement.fly.impl.other;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.Fly;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.utils.MoveUtils;
import swapper.intentions.tropical.utils.PacketUtil;

public class Mush extends FlyMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public Mush() {
		super("Mush");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		ticks++;
		if(mc.thePlayer.ticksExisted % 4 <= 2)
			e.setOnGround(true);
		if(mc.theWorld.isAirBlock(mc.thePlayer.getPosition().add(0,-1,0))) {
			if(ticks % 21 <= 4) {
				if(ticks % 21 % 2 == 0) {
					e.setPosY(mc.thePlayer.posY - 0.55 + Math.random() * 0.2);
					if (!MoveUtils.isMoving() && (!mc.thePlayer.movementInput.jump && !mc.thePlayer.movementInput.sneak))
						PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(e.getPosX(), e.getPosY(), e.getPosZ(), true));
				}
			}
		}
		if(mc.thePlayer.onGround)
			mc.thePlayer.jump();
	}

	@Override
	public void onMove(MoveEvent e) {
		if(ticks > 1) {
			mc.thePlayer.motionY = 0F;
			e.setMotionY(.0);
		}
		Fly fly = ((Fly) Tropical.instance.moduleManager.getModuleByName("Flight"));
		mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
		double speed = fly.speed.getValue();
		MoveUtils.strafe(speed + (mc.thePlayer.ticksExisted%5)*0.05);
		PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem+1 % 8));
		PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
		if(mc.gameSettings.keyBindJump.isKeyDown())
			e.setMotionY(speed - (mc.thePlayer.ticksExisted%5)*0.05);
		else if(mc.gameSettings.keyBindSneak.isKeyDown())
			e.setMotionY(-speed + (mc.thePlayer.ticksExisted%5)*0.05);
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
		AxisAlignedBB bb = e.getAxisAlignedBB();
		BlockPos pos = mc.thePlayer.getPosition();
		
		/*if(bb != null)
			e.setAxisAlignedBB(AxisAlignedBB.fromBounds(bb.minX-1,ticks-1,bb.minZ-1,bb.maxX+1,ticks,bb.maxZ+1));
		else
			e.setAxisAlignedBB(AxisAlignedBB.fromBounds(pos.getX()-1,ticks-1,pos.getZ()-1,pos.getX()+1,ticks,pos.getZ()+1));*/
	}
}
