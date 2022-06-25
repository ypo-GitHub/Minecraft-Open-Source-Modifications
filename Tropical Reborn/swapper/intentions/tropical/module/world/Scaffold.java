package swapper.intentions.tropical.module.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.BooleanSetting;
import swapper.intentions.tropical.settings.settings.ModeSetting;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import net.minecraft.item.ItemBlock;
import swapper.intentions.tropical.utils.MoveUtils;

public final class Scaffold extends Module {

	final BlockPos[] possiblePositions = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
	final EnumFacing[] possibleFacings = new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH};

	public static ModeSetting bypass = new ModeSetting("Bypass", "None", "Hypixel", "HypixelJump");;
	public BooleanSetting sprint = new BooleanSetting("Sprint",false);
	public BooleanSetting noBob = new BooleanSetting("NoBob",false);
	public BooleanSetting rotations = new BooleanSetting("Rotations",true);
	public BooleanSetting boost = new BooleanSetting("Boost",false);
	public BooleanSetting safewalk = new BooleanSetting("Safewalk",true);
	float prevYaw = 0, prevPitch = 0;
	int oldItemSlot;
	ItemStack oldItemStack;
	Set<Block> disallowedBlocks = new HashSet<>();
	double yPos = 0;
	public Scaffold() {
		super("Scaffold", "Automatically places blocks below you", Keyboard.KEY_G, Category.WORLD);
		addSettings(sprint, rotations, boost, bypass, safewalk);
		disallowedBlocks.addAll(Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence));
	}

	@Subscribe
	public void onMove(MoveEvent e) {
		if(safewalk.getValue() && mc.thePlayer.onGround)
			e.setSafewalk(true);
	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		if(noBob.getValue())
			mc.thePlayer.cameraYaw = 0.0f;
		if(getValidBlockSlotFromHotbar() != -1) {
			mc.thePlayer.inventory.currentItem = getValidBlockSlotFromHotbar();
		}
		if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
			mc.gameSettings.keyBindSprint.pressed = sprint.getValue();
			mc.thePlayer.setSprinting(sprint.getValue());
		}
		BlockPos underneath = new BlockPos(1,1,1);
		underneath = new BlockPos(Math.floor(mc.thePlayer.posX),Math.floor(bypass.getCurrentValue() == "HypixelJump" ? yPos : mc.thePlayer.posY),Math.floor(mc.thePlayer.posZ)).add(0, -1, 0);
		BlockData currentBlock = getBlockData(underneath);
		if (e.isPre()) {
			if(currentBlock != null) {
				//Vec3 vector = getVector(underneath, currentBlock.getFacing());
				if(rotations.getValue()) {
					e.setRotationYaw(getNCPRots(currentBlock.getPosition().add(-MoveUtils.getStrafe(Math.min(MoveUtils.getSpeed(),0.2F))[0], 0, -MoveUtils.getStrafe(Math.min(MoveUtils.getSpeed(),0.2F))[1]))[0]);
					e.setRotationPitch(getNCPRots(currentBlock.getPosition())[1]);
				}
				if((mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) || Math.sqrt(mc.thePlayer.motionX*mc.thePlayer.motionX + mc.thePlayer.motionZ*mc.thePlayer.motionZ) > 1.0E-4D) {
					if(bypass.getCurrentValue() == "HypixelJump") {
						if(mc.thePlayer.onGround)
							mc.thePlayer.jump();
						if(rotations.getValue())
							e.setRotationYaw(System.currentTimeMillis()%360);
					}else if(bypass.getCurrentValue() == "Hypixel") {
						while(MoveUtils.getSpeed() > 0.1/MoveUtils.getSpeedPotMultiplier(.025)) {
							mc.thePlayer.motionX *= 0.98;
							mc.thePlayer.motionZ *= 0.98;
						}
					}
					if(placeBlock(currentBlock)) {
						//prevYaw = getNCPRots(currentBlock.getPosition().add(-MoveUtils.getStrafe(Math.min(MoveUtils.getSpeed(),0.5F))[0], 0, -MoveUtils.getStrafe(Math.min(MoveUtils.getSpeed(),0.5F))[1]))[1]-((float)Math.random() * .8F);
						//prevPitch = getNCPRots(currentBlock.getPosition())[1]-((float)Math.random() * 8);
						if(boost.getValue() && mc.thePlayer.onGround) {
							mc.thePlayer.motionX *= 1.3;
							mc.thePlayer.motionZ *= 1.3;
						}
					}
				}
			}
		}
	}

	boolean placeBlock(final BlockData block){
		if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
			final BlockPos pos = block.getPosition();
			final EnumFacing face = block.getFacing();
			if(mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, face, getVector(pos,face))){
				mc.thePlayer.swingItem();
				return true;
			}
		}
		return false;
	}

	boolean isPosSolid(BlockPos pos) {
		return (!(mc.theWorld.getBlockState(pos) instanceof BlockLiquid)) && ((mc.theWorld.getBlockState(pos)).getBlock().getMaterial() != Material.air);
	}

	private Vec3 getVector(BlockPos pos, EnumFacing face) {
		Vec3i direction = face.getDirectionVec();
		double x = direction.getX() * .5, y = direction.getY() * .5, z = direction.getZ() * .5;
		return new Vec3(pos).addVector(.5, .5, .5).addVector(x, y, z);
	}
	
	float[] getNCPRots(BlockPos p) {
		if (p == null) {
			return null;
		} else {
			Vec3 positionEyes = mc.thePlayer.getPositionEyes(2.0F);
			Vec3 add = (new Vec3((double) p.getX() + 0.5D, (double) p.getY() + 0.5D, (double) p.getZ() + 0.5D));
			double n = add.xCoord - positionEyes.xCoord;
			double n2 = add.yCoord - positionEyes.yCoord;
			double n3 = add.zCoord - positionEyes.zCoord;
			return new float[]{(float) (Math.atan2(n3, n) * 180.0D / Math.PI - 90.0D), -((float) (Math.atan2(n2, (float) Math.hypot(n, n3)) * 180.0D / Math.PI))};
		}
	}

	/*
	* I Nullswap will support the quote of nquantum and will credit whoever has made the getBlockData function: I took it from my old client, I don't know who made this but credit to them / replace this please
	 */
	public BlockData getBlockData(BlockPos pos) {
		if (isPosSolid(pos.add(0, -1, 0))) {
			return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos.add(-1, 0, 0))) {
			return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos.add(1, 0, 0))) {
			return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos.add(0, 0, 1))) {
			return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos.add(0, 0, -1))) {
			return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos1 = pos.add(-1, 0, 0);

		if (isPosSolid(pos1.add(0, -1, 0))) {
			return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos1.add(-1, 0, 0))) {
			return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos1.add(1, 0, 0))) {
			return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos1.add(0, 0, 1))) {
			return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos1.add(0, 0, -1))) {
			return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos2 = pos.add(1, 0, 0);

		if (isPosSolid(pos2.add(0, -1, 0))) {
			return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos2.add(-1, 0, 0))) {
			return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos2.add(1, 0, 0))) {
			return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos2.add(0, 0, 1))) {
			return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos2.add(0, 0, -1))) {
			return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos3 = pos.add(0, 0, 1);

		if (isPosSolid(pos3.add(0, -1, 0))) {
			return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos3.add(-1, 0, 0))) {
			return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos3.add(1, 0, 0))) {
			return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos3.add(0, 0, 1))) {
			return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos3.add(0, 0, -1))) {
			return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos4 = pos.add(0, 0, -1);

		if (isPosSolid(pos4.add(0, -1, 0))) {
			return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos4.add(-1, 0, 0))) {
			return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos4.add(1, 0, 0))) {
			return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos4.add(0, 0, 1))) {
			return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos4.add(0, 0, -1))) {
			return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos19 = pos.add(-2, 0, 0);

		if (isPosSolid(pos1.add(0, -1, 0))) {
			return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos1.add(-1, 0, 0))) {
			return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos1.add(1, 0, 0))) {
			return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos1.add(0, 0, 1))) {
			return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos1.add(0, 0, -1))) {
			return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos29 = pos.add(2, 0, 0);

		if (isPosSolid(pos2.add(0, -1, 0))) {
			return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos2.add(-1, 0, 0))) {
			return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos2.add(1, 0, 0))) {
			return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos2.add(0, 0, 1))) {
			return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos2.add(0, 0, -1))) {
			return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos39 = pos.add(0, 0, 2);

		if (isPosSolid(pos3.add(0, -1, 0))) {
			return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos3.add(-1, 0, 0))) {
			return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos3.add(1, 0, 0))) {
			return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos3.add(0, 0, 1))) {
			return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos3.add(0, 0, -1))) {
			return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos49 = pos.add(0, 0, -2);

		if (isPosSolid(pos4.add(0, -1, 0))) {
			return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos4.add(-1, 0, 0))) {
			return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos4.add(1, 0, 0))) {
			return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos4.add(0, 0, 1))) {
			return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos4.add(0, 0, -1))) {
			return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos5 = pos.add(0, -1, 0);

		if (isPosSolid(pos5.add(0, -1, 0))) {
			return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos5.add(-1, 0, 0))) {
			return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos5.add(1, 0, 0))) {
			return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos5.add(0, 0, 1))) {
			return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos5.add(0, 0, -1))) {
			return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos6 = pos5.add(1, 0, 0);

		if (isPosSolid(pos6.add(0, -1, 0))) {
			return new BlockData(pos6.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos6.add(-1, 0, 0))) {
			return new BlockData(pos6.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos6.add(1, 0, 0))) {
			return new BlockData(pos6.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos6.add(0, 0, 1))) {
			return new BlockData(pos6.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos6.add(0, 0, -1))) {
			return new BlockData(pos6.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos7 = pos5.add(-1, 0, 0);

		if (isPosSolid(pos7.add(0, -1, 0))) {
			return new BlockData(pos7.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos7.add(-1, 0, 0))) {
			return new BlockData(pos7.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos7.add(1, 0, 0))) {
			return new BlockData(pos7.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos7.add(0, 0, 1))) {
			return new BlockData(pos7.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos7.add(0, 0, -1))) {
			return new BlockData(pos7.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos8 = pos5.add(0, 0, 1);

		if (isPosSolid(pos8.add(0, -1, 0))) {
			return new BlockData(pos8.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos8.add(-1, 0, 0))) {
			return new BlockData(pos8.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos8.add(1, 0, 0))) {
			return new BlockData(pos8.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos8.add(0, 0, 1))) {
			return new BlockData(pos8.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos8.add(0, 0, -1))) {
			return new BlockData(pos8.add(0, 0, -1), EnumFacing.SOUTH);
		}

		BlockPos pos9 = pos5.add(0, 0, -1);

		if (isPosSolid(pos9.add(0, -1, 0))) {
			return new BlockData(pos9.add(0, -1, 0), EnumFacing.UP);
		} else if (isPosSolid(pos9.add(-1, 0, 0))) {
			return new BlockData(pos9.add(-1, 0, 0), EnumFacing.EAST);
		} else if (isPosSolid(pos9.add(1, 0, 0))) {
			return new BlockData(pos9.add(1, 0, 0), EnumFacing.WEST);
		} else if (isPosSolid(pos9.add(0, 0, 1))) {
			return new BlockData(pos9.add(0, 0, 1), EnumFacing.NORTH);
		} else if (isPosSolid(pos9.add(0, 0, -1))) {
			return new BlockData(pos9.add(0, 0, -1), EnumFacing.SOUTH);
		}
		return null;
	}


	@Override
	public void onEnable() {
		oldItemSlot = mc.thePlayer.inventory.currentItem;
		yPos = mc.thePlayer.posY;
		prevYaw = mc.thePlayer.rotationYaw;
		prevPitch = mc.thePlayer.rotationPitch;
	}

	@Override
	public void onDisable() {
		mc.thePlayer.inventory.currentItem = oldItemSlot;
	}

	int getValidBlockSlotFromHotbar(){
		 for(int i = 36; i < 45; ++i) {
			 ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
		     if (stack != null && stack.getItem() instanceof ItemBlock && !disallowedBlocks.contains(((ItemBlock)(stack.getItem())).getBlock())) {
		    	 return i - 36;
		     }
		 }
		 return -1;
	}


	boolean isAir(BlockPos pos) {
		return mc.theWorld.isAirBlock(pos);
	}

	boolean doesItemQualify(Item item) {
		if (!(item instanceof ItemBlock)) {
			return false;
		} else {
			final ItemBlock itemBlock = (ItemBlock) item;
			final Block block = itemBlock.getBlock();

			return !disallowedBlocks.contains(block);
		}
	}


	private static final class BlockData{
		BlockPos position;
		EnumFacing facing;

		public BlockData(BlockPos position, EnumFacing facing) {
			this.position = position;
			this.facing = facing;
		}

		public BlockPos getPosition() {
			return position;
		}

		public void setPosition(BlockPos position) {
			this.position = position;
		}

		public EnumFacing getFacing() {
			return facing;
		}

		public void setFacing(EnumFacing facing) {
			this.facing = facing;
		}
	}
}

