package me.earth.phobos.features.modules.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.DeathEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.FileManager;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoGG extends Module {
  private static final String path = "catware/autogg.txt";
  
  private final Setting<Boolean> onOwnDeath = register(new Setting("OwnDeath", Boolean.valueOf(false)));
  
  private final Setting<Boolean> greentext = register(new Setting("Greentext", Boolean.valueOf(false)));
  
  private final Setting<Boolean> loadFiles = register(new Setting("LoadFiles", Boolean.valueOf(false)));
  
  private final Setting<Integer> targetResetTimer = register(new Setting("Reset", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(90)));
  
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(30)));
  
  private final Setting<Boolean> test = register(new Setting("Test", Boolean.valueOf(false)));
  
  public Map<EntityPlayer, Integer> targets = new ConcurrentHashMap<>();
  
  public List<String> messages = new ArrayList<>();
  
  public EntityPlayer cauraTarget;
  
  private final Timer timer = new Timer();
  
  private final Timer cooldownTimer = new Timer();
  
  private boolean cooldown;
  
  public AutoGG() {
    super("AutoGG", "Automatically GGs", Module.Category.MISC, true, false, false);
    File file = new File("catware/autogg.txt");
    if (!file.exists())
      try {
        file.createNewFile();
      } catch (Exception e) {
        e.printStackTrace();
      }  
  }
  
  public void onEnable() {
    loadMessages();
    this.timer.reset();
    this.cooldownTimer.reset();
  }
  
  public void onTick() {
    if (((Boolean)this.loadFiles.getValue()).booleanValue()) {
      loadMessages();
      Command.sendMessage("<AutoGG> Loaded messages.");
      this.loadFiles.setValue(Boolean.valueOf(false));
    } 
    if (AutoCrystal.target != null && this.cauraTarget != AutoCrystal.target)
      this.cauraTarget = AutoCrystal.target; 
    if (((Boolean)this.test.getValue()).booleanValue()) {
      announceDeath((EntityPlayer)mc.player);
      this.test.setValue(Boolean.valueOf(false));
    } 
    if (!this.cooldown)
      this.cooldownTimer.reset(); 
    if (this.cooldownTimer.passedS(((Integer)this.delay.getValue()).intValue()) && this.cooldown) {
      this.cooldown = false;
      this.cooldownTimer.reset();
    } 
    if (AutoCrystal.target != null)
      this.targets.put(AutoCrystal.target, Integer.valueOf((int)(this.timer.getPassedTimeMs() / 1000L))); 
    this.targets.replaceAll((p, v) -> Integer.valueOf((int)(this.timer.getPassedTimeMs() / 1000L)));
    for (EntityPlayer player : this.targets.keySet()) {
      if (((Integer)this.targets.get(player)).intValue() <= ((Integer)this.targetResetTimer.getValue()).intValue())
        continue; 
      this.targets.remove(player);
      this.timer.reset();
    } 
  }
  
  @SubscribeEvent
  public void onEntityDeath(DeathEvent event) {
    if (this.targets.containsKey(event.player) && !this.cooldown) {
      announceDeath(event.player);
      this.cooldown = true;
      this.targets.remove(event.player);
    } 
    if (event.player == this.cauraTarget && !this.cooldown) {
      announceDeath(event.player);
      this.cooldown = true;
    } 
    if (event.player == mc.player && ((Boolean)this.onOwnDeath.getValue()).booleanValue()) {
      announceDeath(event.player);
      this.cooldown = true;
    } 
  }
  
  @SubscribeEvent
  public void onAttackEntity(AttackEntityEvent event) {
    if (event.getTarget() instanceof EntityPlayer && !Phobos.friendManager.isFriend(event.getEntityPlayer()))
      this.targets.put((EntityPlayer)event.getTarget(), Integer.valueOf(0)); 
  }
  
  @SubscribeEvent
  public void onSendAttackPacket(PacketEvent.Send event) {
    CPacketUseEntity packet;
    if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)mc.world) instanceof EntityPlayer && !Phobos.friendManager.isFriend((EntityPlayer)packet.getEntityFromWorld((World)mc.world)))
      this.targets.put((EntityPlayer)packet.getEntityFromWorld((World)mc.world), Integer.valueOf(0)); 
  }
  
  public void loadMessages() {
    this.messages = FileManager.readTextFileAllLines("catware/autogg.txt");
  }
  
  public String getRandomMessage() {
    loadMessages();
    Random rand = new Random();
    if (this.messages.size() == 0)
      return "<player> was so ez! Catware on top!"; 
    if (this.messages.size() == 1)
      return this.messages.get(0); 
    return this.messages.get(MathUtil.clamp(rand.nextInt(this.messages.size()), 0, this.messages.size() - 1));
  }
  
  public void announceDeath(EntityPlayer target) {
    mc.player.connection.sendPacket((Packet)new CPacketChatMessage((((Boolean)this.greentext.getValue()).booleanValue() ? ">" : "") + getRandomMessage().replaceAll("<player>", target.getDisplayNameString())));
  }
}
