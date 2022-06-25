package swapper.intentions.tropical.module.combat;

import com.google.common.eventbus.Subscribe;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.BooleanSetting;
import swapper.intentions.tropical.settings.settings.ModeSetting;
import swapper.intentions.tropical.settings.settings.NumberSetting;
import swapper.intentions.tropical.utils.PacketUtil;
import swapper.intentions.tropical.utils.TimerUtils;

import java.util.ArrayList;

public class KillAura extends Module {
	
	public static Entity target;
	public boolean blocking;
	public float yaw = 0;
	public float pitch = 0;
	public TimerUtils timer = new TimerUtils();
	public NumberSetting cps = new NumberSetting("CPS", 10, 0.5, 1, 20);
	public NumberSetting cpsRandom = new NumberSetting("CPS Spike", 2.5, 0.1, 0, 5);
	public NumberSetting range = new NumberSetting("Range", 4.5, 0.1, 1, 8);
	public NumberSetting findRange = new NumberSetting("Find Range", 6, 0.1, 1, 10);
	public BooleanSetting silentRots = new BooleanSetting("Silent Rots", true);
	public BooleanSetting smoothRots = new BooleanSetting("Smooth Rots", true);
	public BooleanSetting keepSprint = new BooleanSetting("Keep Sprint", true);
	public BooleanSetting autoBlock = new BooleanSetting("Auto Block", true);
	public BooleanSetting rots = new BooleanSetting("Rotations", true);
	public BooleanSetting is19 = new BooleanSetting("1.9", false);
	public ModeSetting autoBlockMode = new ModeSetting("Auto Block", "Fake", "Item Use", "Hypixel", "Vanilla", "Matrix");
	public NumberSetting hypixelTicks = new NumberSetting("Hypixel AB Ticks", 3, 1, 1, 10);
	public ModeSetting rotationMode = new ModeSetting("Rotation",  "Decent", "Simple", "Simple2", "Snap");
	public BooleanSetting hvh = new BooleanSetting("hvh", false);
	public NumberSetting packets = new NumberSetting("Packets", 100, 1, 1, 200);
	public BooleanSetting noDelay = new BooleanSetting("No Delay", false);
	
    public KillAura() {
        super("KillAura", "Automatically attacks players", Keyboard.KEY_R, Category.COMBAT);
        addSettings(cps, cpsRandom, hvh, packets, noDelay, range, findRange, keepSprint, rotationMode,rots, silentRots, smoothRots, autoBlock, autoBlockMode, hypixelTicks);
    }

	@Override
	public void onRenderAlways(){
		packets.setHidden(!hvh.getValue());
		noDelay.setHidden(!hvh.getValue());
	}

    @Override
    public void onEnable() { blocking = false; yaw = 0; pitch = 0;  }
    
    @Subscribe
    public void onUpdate(UpdateEvent event) {
    	double roundedCps = Math.floor(cps.getValue()*100)/100;
    	if(target == null)
    		setSuffix(String.valueOf(roundedCps));
    	if(!event.isPre())
    		return;
    	for(Entity e: mc.theWorld.loadedEntityList) {
    		if(e instanceof EntityPlayer &&
    				((EntityPlayer) e).getHealth() > 0 &&
    					e.getDistanceToEntity(mc.thePlayer) < findRange.getValue() && e != mc.thePlayer && !e.isInvisible()) {
				if(Tropical.instance.moduleManager.getModuleByName("AntiBot").isToggled())
					if(!mc.getNetHandler().getPlayerInfoMap().stream().anyMatch(i -> i.getGameProfile().getName().equals(e.getName())))
						return;
				if(e != target) {
					yaw = mc.thePlayer.rotationYaw;
					pitch = mc.thePlayer.rotationPitch;
				}
    			target = e;
    			blocking = false;
    		}
    	}
    	
    	if(target != null) {

			if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && target != null && target.getDistanceToEntity(mc.thePlayer) < findRange.getValue() && !mc.thePlayer.isUsingItem() && autoBlock.getValue()) {
				switch(autoBlockMode.getCurrentValue()) {
					case "Item Use": {
						if(event.isPre())
							mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 2);
						break;
					}
					case "Hypixel": {
						int t = hypixelTicks.getValue().intValue();
						if(mc.thePlayer.ticksExisted % t == 0) {
							PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
							blocking = true;
						}else if(mc.thePlayer.ticksExisted % t == 1) {
							PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
							blocking = false;
						}
						break;
					}
					case "Vanilla": {
						if(event.isPre()) {
							if(!mc.thePlayer.isBlocking()) {
								PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
							}
							blocking = true;
						}
						break;
					}
					case "Matrix": {
						if(!mc.gameSettings.keyBindUseItem.isPressed() && target.getDistanceToEntity(mc.thePlayer) < range.getValue())
							mc.gameSettings.keyBindUseItem.pressed = true;
						break;
					}
				}
			}
    		if(target.getDistanceToEntity(mc.thePlayer) > findRange.getValue() || target.isDead || target.isInvisible()) {
    			target = null;
				if(autoBlockMode.getCurrentValue().equalsIgnoreCase("Matrix")) {
					mc.gameSettings.keyBindUseItem.pressed = false;
				}
				return;
    		}
			if(rots.getValue()) {
				if(!rotationMode.getCurrentValue().equals("Snap")) {
					float[] rotz = new float[2];
					switch (rotationMode.getCurrentValue()) {
						case "Decent": {
							rotz = MatrixRotations(target);
							break;
						}
						case "Simple": {
							rotz = SimpleRotations(target);
							break;
						}
						case "Simple2": {
							rotz = otherSimpleRotThingy(target);
							break;
						}
					}
					float speed = mc.gameSettings.mouseSensitivity - (ThreadLocalRandom.current().nextFloat() - 0.5F) * 0.3F;
					speed = Math.min(0.2F, speed);
					yaw += (rotz[0] - yaw) * speed;
					pitch += (rotz[1] - pitch) * speed;
					if (!smoothRots.getValue()) {
						yaw = rotz[0];
						pitch = rotz[1];
					}
					event.setRotationYaw(yaw);
					event.setRotationPitch(pitch);
				}
			}
    		double cpsRandomized = (cpsRandom.getValue() >= 0.05 ? (cps.getValue() + ThreadLocalRandom.current().nextDouble(-Math.abs(cpsRandom.getValue() - 1), cpsRandom.getValue())) : cps.getValue());
    		if(!silentRots.getValue()) {
				mc.thePlayer.rotationYaw = event.getRotationYaw();
				mc.thePlayer.rotationPitch = event.getRotationPitch();
			}
			double time = 1000/cpsRandomized;
			if(noDelay.getValue()) time = 0;
			if(timer.hasReached(time) && target.getDistanceToEntity(mc.thePlayer) < range.getValue()+1) {
    			//setSuffix(Math.floor(cpsRandomized * 2) / 2 + " / Distance: " + Math.floor(target.getDistanceToEntity(mc.thePlayer)*100)/100);
    			if(event.isPre()) {
					if(autoBlockMode.getCurrentValue().equalsIgnoreCase("Matrix")) {
						mc.gameSettings.keyBindUseItem.pressed = false;
					}
    				mc.thePlayer.stopUsingItem();
					mc.thePlayer.swingItem();
					if(blocking && autoBlockMode.getCurrentValue().equalsIgnoreCase("hypixel")) {
						PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
						blocking = false;
					}
					if(target.getDistanceToEntity(mc.thePlayer) <= range.getValue()) { //bcuz just swing at +0.5 range
						if(rotationMode.getCurrentValue().equals("Snap")) {
							event.setRotationYaw(otherSimpleRotThingy(target)[0]);
							event.setRotationPitch(otherSimpleRotThingy(target)[1]);
						}
						if(hvh.getValue()){
							for(int i = 0; i < packets.getValue(); i++) {
								mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, Action.ATTACK));
							}
						}else{
							if(keepSprint.getValue()) {
								mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, Action.ATTACK));
							}else {
								mc.playerController.attackEntity(mc.thePlayer, target);
							}
						}
					}
					if (timer.hasReached(time + (50 + ThreadLocalRandom.current().nextInt(50)))) {
						timer.reset();
					}
    			}
    		}
    	}
    }
    
    @Subscribe
    public void onMove(MoveEvent event) {

    }

    @Override
    public void onDisable() {
		target = null;
		if(blocking) mc.thePlayer.stopUsingItem(); blocking = false;
		if(autoBlockMode.getCurrentValue().equalsIgnoreCase("Matrix")) {
			mc.gameSettings.keyBindUseItem.pressed = false;
		}
	}
    
    //Lucaforever coded this for tw, please do not use this comment as top 10 expose.
    public static float[] MatrixRotations(Entity entity) {
		boolean random = ThreadLocalRandom.current().nextBoolean();
        double diffX = entity.posX - mc.thePlayer.posX, diffY;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.9
                    - (mc.thePlayer.posY
                    + mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0D
                    - (mc.thePlayer.posY
                    + mc.thePlayer.getEyeHeight());
        }
        double diffZ = entity.posZ - mc.thePlayer.posZ, dist = Math.hypot(diffX, diffZ);
        float sensitivity = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F,
                gcd = sensitivity * sensitivity * sensitivity * 1.2F,
				y = 6.0F*ThreadLocalRandom.current().nextFloat(),
				p = 6.0F*ThreadLocalRandom.current().nextFloat(),
                yawRand = random ? -RandomUtils.nextFloat(0.0F, y) : RandomUtils.nextFloat(0.0F, y),
                pitchRand = random ? -RandomUtils.nextFloat(0.0F, p) : RandomUtils.nextFloat(0.0F, p),
                yaw = ((float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F) + yawRand,
                pitch = MathHelper.clamp_float(((float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI)) + pitchRand + mc.thePlayer.getDistanceToEntity(entity) * 1.25F, -90.0F, 90.0F);
        if (mc.thePlayer.ticksExisted % 2 == 0) {
            pitch = MathHelper.clamp_float(pitch + (random ? RandomUtils.nextFloat(2.0F, 8.0F) : -RandomUtils.nextFloat(2.0F, 8.0F)), -90.0F, 90.0F);
        }
        pitch -= pitch % gcd;
        yaw -= yaw % gcd;
        return new float[]{
                mc.thePlayer.rotationYaw
                        + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
                mc.thePlayer.rotationPitch
                        + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)
        };
    }

	public static float[] SimpleRotations(Entity entity) {
		double diffX = entity.posX - mc.thePlayer.posX, diffY;
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
			diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.9
					- (mc.thePlayer.posY
					+ mc.thePlayer.getEyeHeight());
		} else {
			diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0D
					- (mc.thePlayer.posY
					+ mc.thePlayer.getEyeHeight());
		}
		double diffZ = entity.posZ - mc.thePlayer.posZ, dist = Math.hypot(diffX, diffZ);
		float sensitivity = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F,
				gcd = sensitivity * sensitivity * sensitivity * 1.2F,
				yaw = ((float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F),
				pitch = MathHelper.clamp_float(((float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI)) + mc.thePlayer.getDistanceToEntity(entity) * 1.25F, -90.0F, 90.0F);
		if (mc.thePlayer.ticksExisted % 2 == 0) {
			pitch = MathHelper.clamp_float(pitch, -90.0F, 90.0F);
		}
		pitch -= pitch % gcd;
		yaw -= yaw % gcd;
		return new float[]{
				mc.thePlayer.rotationYaw
						+ MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
				mc.thePlayer.rotationPitch
						+ MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)
		};
	}

	public static float[] otherSimpleRotThingy(Entity entity) {
		return new float[]{
				(float) Math.toDegrees(Math.atan2(entity.posZ - mc.thePlayer.posZ, entity.posX - mc.thePlayer.posX)) - 90,
				(float) Math.toDegrees(-Math.atan2(entity.posY - mc.thePlayer.posY, Math.sqrt(Math.pow(entity.posX - mc.thePlayer.posX, 2) + Math.pow(entity.posZ - mc.thePlayer.posZ, 2))))
		};
	}

}
