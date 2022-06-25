package swapper.intentions.tropical.module.combat;

import com.google.common.eventbus.Subscribe;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.NumberSetting;
import swapper.intentions.tropical.utils.TimerUtils;

public class AimAssist extends Module {

	public Entity target;
	public TimerUtils timer = new TimerUtils();
	public NumberSetting findRange = new NumberSetting("Find Range", 6, 0.1, 1, 10);

    public AimAssist() {
        super("AimAssist", "Assists you with aiming.", Keyboard.KEY_NONE, Category.COMBAT);
        addSettings(findRange);
		setDisplayName("Aim Assist");
    }

    @Override
    public void onEnable() {  }
    
    @Subscribe
    public void onUpdate(UpdateEvent event) {
    	if(!event.isPre())
    		return;
    	for(Entity e: mc.theWorld.loadedEntityList) {
    		if(e instanceof EntityPlayer &&
    				((EntityPlayer) e).getHealth() > 0 &&
    					e.getDistanceToEntity(mc.thePlayer) < findRange.getValue() && e != mc.thePlayer && !e.isInvisible() && mc.getNetHandler().getPlayerInfoMap().stream().anyMatch(i -> i.getGameProfile().getName().equals(e.getName()))) {
    			target = e;
    		}
    	}
    	
    	if(target != null) {
    		if(target.getDistanceToEntity(mc.thePlayer) > findRange.getValue() || target.isDead || target.isInvisible()) {
    			target = null; return;
    		}
    		//double cpsRandomized = (cpsRandom.getValue() >= 0.05 ? (cps.getValue() + ThreadLocalRandom.current().nextDouble((-cpsRandom.getValue()) - 1, cpsRandom.getValue())) : cps.getValue());
			if(target.getDistanceToEntity(mc.thePlayer) < findRange.getValue()) {
    			//setSuffix(Math.floor(cpsRandomized * 2) / 2 + " / Distance: " + Math.floor(target.getDistanceToEntity(mc.thePlayer)*100)/100);
    			if(event.isPre()) {
					mc.thePlayer.rotationYaw += (MatrixRotations(target)[0] - mc.thePlayer.rotationYaw)*mc.gameSettings.mouseSensitivity;
					mc.thePlayer.rotationPitch += (MatrixRotations(target)[1] - mc.thePlayer.rotationPitch)*mc.gameSettings.mouseSensitivity;
					/*if(timer.hasReached(1000/cpsRandomized) && target.getDistanceToEntity(mc.thePlayer) < range.getValue()+0.5) { //bcuz just swing at +0.5 range
						mc.thePlayer.swingItem();
						if(target.getDistanceToEntity(mc.thePlayer) < range.getValue()) {
							if (keepSprint.getValue()) {
								mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, Action.ATTACK));
							} else {
								mc.playerController.attackEntity(mc.thePlayer, target);
							}
						}
						timer.reset();
					}*/
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
                yawRand = random ? -RandomUtils.nextFloat(0.0F, 3.0F) : RandomUtils.nextFloat(0.0F, 3.0F),
                pitchRand = random ? -RandomUtils.nextFloat(0.0F, 3.0F) : RandomUtils.nextFloat(0.0F, 3.0F),
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
}
