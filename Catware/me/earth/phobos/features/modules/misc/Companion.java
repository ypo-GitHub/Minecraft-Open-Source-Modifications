package me.earth.phobos.features.modules.misc;

import com.mojang.text2speech.Narrator;
import me.earth.phobos.event.events.DeathEvent;
import me.earth.phobos.event.events.TotemPopEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Companion extends Module {
  public Setting<String> totemPopMessage = register(new Setting("PopMessage", "<player> watch out you're popping!"));
  
  public Setting<String> deathMessages = register(new Setting("DeathMessage", "<player> you retard you just fucking died!"));
  
  private final Narrator narrator = Narrator.getNarrator();
  
  public Companion() {
    super("Companion", "The best module", Module.Category.MISC, true, false, false);
  }
  
  public void onEnable() {
    this.narrator.say("Hello and welcome to Catware");
  }
  
  public void onDisable() {
    this.narrator.clear();
  }
  
  @SubscribeEvent
  public void onTotemPop(TotemPopEvent event) {
    if (event.getEntity() == mc.player)
      this.narrator.say(((String)this.totemPopMessage.getValue()).replaceAll("<player>", mc.player.getName())); 
  }
  
  @SubscribeEvent
  public void onDeath(DeathEvent event) {
    if (event.player == mc.player)
      this.narrator.say(((String)this.deathMessages.getValue()).replaceAll("<player>", mc.player.getName())); 
  }
}
