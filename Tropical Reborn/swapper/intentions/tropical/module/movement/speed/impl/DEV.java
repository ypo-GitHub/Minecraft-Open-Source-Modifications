package swapper.intentions.tropical.module.movement.speed.impl;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.StrafeEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.Speed;
import swapper.intentions.tropical.module.movement.speed.SpeedMode;
import swapper.intentions.tropical.utils.MoveUtils;
import swapper.intentions.tropical.utils.PacketUtil;

public class DEV extends SpeedMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public DEV() {
		super("DEV");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
	}

	@Override
	public void onMove(MoveEvent e) {
	}

	@Override
	public void onPacket(PacketEvent e) {
	}

	@Override
	public void onStrafeEvent(StrafeEvent e) {
		e.setFriction(e.getFriction()*1.8F);
	}

	@Override
	public void onEnable() {
		ticks = 0;
	}
}
