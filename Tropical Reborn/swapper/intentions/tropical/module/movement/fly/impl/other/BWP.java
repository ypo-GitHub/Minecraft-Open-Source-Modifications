package swapper.intentions.tropical.module.movement.fly.impl.other;

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
import swapper.intentions.tropical.utils.PacketUtil;

public class BWP extends FlyMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public BWP() {
		super("BWP");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		if(mc.thePlayer.onGround && ticks == 0) {
			mc.thePlayer.setPosition(mc.thePlayer.posX,mc.thePlayer.posY-1.8E-14,mc.thePlayer.posZ);
		}
	}

	@Override
	public void onMove(MoveEvent e) {
		Fly fly = ((Fly) Tropical.instance.moduleManager.getModuleByName("Flight"));
		if(ticks > 0) {
			ticks++;
			if (ticks < 19) {
				if(ticks == 4 && mc.thePlayer.ticksExisted < 240) {
					PacketUtil.sendPacketNoEventDelayed(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY+3.1,mc.thePlayer.posZ,false),70L);
					PacketUtil.sendPacketNoEventDelayed(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,false),75L);
					PacketUtil.sendPacketNoEventDelayed(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,true),100L);
				}
				if(ticks < 5)
					e.setCancelled(true);
				mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
				double speed = fly.speed.getValue();
				mc.thePlayer.motionY = (mc.gameSettings.keyBindJump.isKeyDown() ? speed :
						mc.gameSettings.keyBindSneak.isKeyDown() ? -speed :
								0.0);
				MoveUtils.strafe(speed);
			}
			if (ticks == 19) {
				mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0.0;
				ticks = 0;
			}
		}
	}

	@Override
	public void onPacket(PacketEvent e) {
		if(e.getPacket() instanceof S12PacketEntityVelocity) {
			if(((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
				S12PacketEntityVelocity a = e.getPacket();
				double x = a.getMotionX()/8000.0;
				double z = a.getMotionZ()/8000.0;
				//ticks += Math.sqrt(x * x + z * z);
			}
		}
		if(e.getPacket() instanceof S08PacketPlayerPosLook){
			ticks = 1;
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
