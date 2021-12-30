package me.earth.phobos.mixin.mixins;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Biome.BiomeProperties.class})
public class MixinBiomeProperties {}
