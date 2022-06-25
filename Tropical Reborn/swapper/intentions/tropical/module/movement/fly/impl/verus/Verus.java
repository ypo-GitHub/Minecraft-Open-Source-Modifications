package swapper.intentions.tropical.module.movement.fly.impl.verus;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
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

public class Verus extends FlyMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public Verus() {
		super("Verus DMG");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		ticks++;

		if(ticks == 3) {
			BlockPos pos = mc.thePlayer.getPosition().add(0, -1.5, 0);
			PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(pos, 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, pos)), 0.0F, 0.5F+(float)Math.random()*0.44f, 0.0F));
			double x = mc.thePlayer.posX,
					y = mc.thePlayer.posY,
					z = mc.thePlayer.posZ;
			PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY+3.25,mc.thePlayer.posZ,false));
			PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,false));
			PacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
			mc.timer.timerSpeed = 0.4f;
			mc.thePlayer.jump();
		}else {
			if(ticks == 4)
				mc.thePlayer.motionY += 0.5;
		}
	}

	@Override
	public void onMove(MoveEvent e) {
		Fly fly = ((Fly) Tropical.instance.moduleManager.getModuleByName("Flight"));
		double speed = fly.speed.getValue();
		if(ticks < 3) {
			e.setCancelled(true);
		}
		if(ticks > 4)
			mc.thePlayer.motionY = 0.0f;
		if(ticks <= 25) {
			mc.timer.timerSpeed = 0.8f;
			mc.thePlayer.motionX = 0.0;
			mc.thePlayer.motionZ = 0.0;
			MoveUtils.strafe(speed);
		}else {
			MoveUtils.strafe(0.29);
		}
	}

	@Override
	public void onPacket(PacketEvent e) {
		if(e.getPacket() instanceof C03PacketPlayer) {
			if(ticks < 3) {
				e.setCancelled(true);
			}
			C03PacketPlayer c03 = e.getPacket();
			if(!c03.isMoving())
				e.setCancelled(true);
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
