package vestige.module.impl.combat;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import vestige.Vestige;
import vestige.event.Event;
import vestige.event.impl.EventMotionUpdate;
import vestige.event.impl.EventSendPacket;
import vestige.module.Category;
import vestige.module.Module;
import vestige.setting.impl.ModeSetting;
import vestige.util.network.PacketUtils;
import vestige.util.network.ServerUtils;

public class Criticals extends Module {
	
	public ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "Edit", "Hover", "NoGround");
	
	private int counter;
	private double y;
	
	public Criticals() {
		super("Criticals", Category.COMBAT);
		this.addSettings(mode);
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventMotionUpdate) {
			this.setDisplayName(this.getName() + " " + ChatFormatting.GRAY + mode.getMode());
			EventMotionUpdate e = (EventMotionUpdate) event;
			if(mode.is("NoGround") && Vestige.getModuleManager().getModuleByName("Killaura").isEnabled() && ((Killaura) Vestige.getModuleManager().getModuleByName("Killaura")).target != null && !Vestige.getModuleManager().getModuleByName("Fly").isEnabled()) {
				e.setOnGround(!mc.thePlayer.onGround && mc.thePlayer.motionY >= 0 && mc.thePlayer.motionY <= 0.16);
			}
			
			if(mode.is("Hover")) {
				mc.thePlayer.lastReportedPosY = 0;
				double ypos = mc.thePlayer.posY;
	    		if(mc.thePlayer.onGround) {
	    			e.setOnGround(false);
	    			if(counter == 0){
	    				y = ypos + 1E-8;
	    				e.setOnGround(true);
	    			} else if(counter == 1) {
	    				y -= 5E-15;
	    			} else {
	    				y -= 4E-15;
	    			}
	    			
	    			if(y <= mc.thePlayer.posY){
	    				counter = 0;
	    				y = mc.thePlayer.posY;
	    				e.setOnGround(true);
	    			}
	    			e.setY(y);
	    			counter++;
	    		} else {
	    			counter = 0;
	    		}
			}
			
			if(mode.is("Edit")) {
				if(Vestige.getModuleManager().getModuleByName("Killaura").isEnabled() && ((Killaura) Vestige.getModuleManager().getModuleByName("Killaura")).target != null) {
					Entity entity = ((Killaura) Vestige.getModuleManager().getModuleByName("Killaura")).target;
					if(entity instanceof EntityPlayer) {
						EntityPlayer target = (EntityPlayer) entity;
						
						if(ServerUtils.isOnHypixel()) {
							hypixelEditCrits(e, target);
						} else {
							EditCrits(e, target);
						}
					}
				}
			}
		} else if(event instanceof EventSendPacket) {
			EventSendPacket e = (EventSendPacket) event;
			if(e.getPacket() instanceof C02PacketUseEntity) {
				C02PacketUseEntity packet = (C02PacketUseEntity) e.getPacket();
				if(packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
					if(mode.is("Packet") && mc.thePlayer.onGround) {
						PacketUtils.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1E-4, mc.thePlayer.posZ, false));
						PacketUtils.sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1E-6, mc.thePlayer.posZ, false));
					}
				}
			}
		}
	}
	
	private void hypixelEditCrits(EventMotionUpdate e, EntityPlayer target) {
		if(mc.thePlayer.onGround) {
			//0.41999998688698, 0.7531999805212, 1.00133597911215, 1.166109260938214, 1.24918707874468, 1.170787077218804, 1.015555072702206, 0.78502770378924, 0.4807108763317, 0.10408037809304
			if(target.hurtTime == 2) {
				e.setY(e.getY() + 0.0625E-4);
				e.setOnGround(false);
			} else if(target.hurtTime == 0) {
				e.setY(0);
				e.setOnGround(false);
			}
		}
	}
	
	private void EditCrits(EventMotionUpdate e, EntityPlayer target) {
		if(target.hurtTime < 5) {
			e.setOnGround(true);
		} else if(target.hurtTime < 3) {
			e.setY(e.getY() + 0.01);
			e.setOnGround(false);
		} else {
			e.setY(e.getY() + 1E-5);
			e.setOnGround(false);
		}
	}
	
}