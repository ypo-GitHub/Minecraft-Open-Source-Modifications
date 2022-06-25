package swapper.intentions.tropical.module.movement.fly.impl.verus;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.utils.MoveUtils;
import swapper.intentions.tropical.utils.PacketUtil;

public class VerusLJ extends FlyMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public VerusLJ() {
		super("Verus DMG Glide");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		ticks++;
	}

	@Override
	public void onMove(MoveEvent e) {
		if(ticks < 3) {
			e.setCancelled(true);
		}
		if(ticks == 3) {
			BlockPos pos = mc.thePlayer.getPosition().add(0, -1.5, 0);
			PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(pos, 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, pos)), 0.0F, 0.5F+(float)Math.random()*0.44f, 0.0F));
			double x = mc.thePlayer.posX,
					y = mc.thePlayer.posY,
					z = mc.thePlayer.posZ;
			PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y+3+Math.random()*0.07, z, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
			PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
			PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
			mc.timer.timerSpeed = 0.25f;
		}
		if(mc.thePlayer.hurtTime > 2) {
			mc.thePlayer.motionY += 0.7f;
			e.setMotionY(mc.thePlayer.motionY);
			mc.timer.timerSpeed = 1.0f;
			MoveUtils.strafe(9.9f);
		}
		if(mc.thePlayer.hurtTime == 3)
			mc.thePlayer.motionY = 0.42;
		if(mc.thePlayer.hurtTime == 0) {
			MoveUtils.strafe(0.36F);
			if(mc.thePlayer.fallDistance > 0)
				mc.thePlayer.motionY = 0.0;
		}
	}

	@Override
	public void onPacket(PacketEvent e) {
		if(e.getPacket() instanceof C03PacketPlayer) {
			if(ticks < 3) {
				e.setCancelled(true);
			}
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
