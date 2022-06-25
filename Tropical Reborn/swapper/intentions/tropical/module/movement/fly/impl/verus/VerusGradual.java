package swapper.intentions.tropical.module.movement.fly.impl.verus;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.combat.KillAura;
import swapper.intentions.tropical.module.movement.Fly;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.utils.MoveUtils;
import swapper.intentions.tropical.utils.PacketUtil;

public class VerusGradual extends FlyMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public VerusGradual() {
		super("Verus Gradual");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		if(mc.thePlayer.fallDistance > 2.93) {
			e.setOnGround(true);
			mc.thePlayer.fallDistance = 0;
		}
		if(mc.thePlayer.onGround) {
			PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY+3.25,mc.thePlayer.posZ,false));
			PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,false));
			PacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
			mc.thePlayer.jump();
		}
	}

	@Override
	public void onMove(MoveEvent e) {
		ticks++;
		Fly fly = ((Fly) Tropical.instance.moduleManager.getModuleByName("Flight"));
		double speed = fly.speed.getValue();
		if(speed - ticks/75.0 > 0.25 && ticks < 38)
			MoveUtils.strafe(speed - ticks/75.0);
		else
			MoveUtils.strafe(MoveUtils.getSpeed());
		BlockPos pos = mc.thePlayer.getPosition().add(0, -1.5, 0);
		KillAura ka = ((KillAura) Tropical.instance.moduleManager.getModuleByName("KillAura"));
		if(ka.target == null) {
			if(!Tropical.instance.moduleManager.getModuleByName("Disabler").isToggled())
				PacketUtil.sendPacketNoEvent(3+((int)Math.random()*4), new C08PacketPlayerBlockPlacement(pos, 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, pos)), 0.0F, 0.5F+(float)Math.random()*0.44f, 0.0F));
			else
				PacketUtil.sendPacketNoEvent((int)Math.random()*2, new C08PacketPlayerBlockPlacement(pos, 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, pos)), 0.0F, 0.5F+(float)Math.random()*0.44f, 0.0F));
		}
		if(mc.thePlayer.onGround) {
			MoveUtils.strafe(e, .625f);
		}

		mc.thePlayer.motionY = 0.0784F;
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
		Fly fly = ((Fly) Tropical.instance.moduleManager.getModuleByName("Flight"));
		double speed = fly.speed.getValue();
		KillAura ka = ((KillAura) Tropical.instance.moduleManager.getModuleByName("KillAura"));
		if(e.getPacket() instanceof C03PacketPlayer && ticks > 1 && ticks % 4 != 0 && speed - ticks/75.0 > 0.25 && ticks < 38 && ka.target == null) {
			e.setCancelled(true);
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
