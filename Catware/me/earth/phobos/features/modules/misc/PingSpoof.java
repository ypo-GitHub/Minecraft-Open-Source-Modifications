package me.earth.phobos.features.modules.misc;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PingSpoof extends Module {
  private final Setting<Boolean> seconds = register(new Setting("Seconds", Boolean.valueOf(false)));
  
  private final Setting<Integer> delay = register(new Setting("DelayMS", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(1000), v -> !((Boolean)this.seconds.getValue()).booleanValue()));
  
  private final Setting<Integer> secondDelay = register(new Setting("DelayS", Integer.valueOf(5), Integer.valueOf(0), Integer.valueOf(30), v -> ((Boolean)this.seconds.getValue()).booleanValue()));
  
  private final Setting<Boolean> offOnLogout = register(new Setting("Logout", Boolean.valueOf(false)));
  
  private final Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
  
  private final Timer timer = new Timer();
  
  private boolean receive = true;
  
  public PingSpoof() {
    super("PingSpoof", "Spoofs your ping!", Module.Category.MISC, true, false, false);
  }
  
  public void onLoad() {
    if (((Boolean)this.offOnLogout.getValue()).booleanValue())
      disable(); 
  }
  
  public void onLogout() {
    if (((Boolean)this.offOnLogout.getValue()).booleanValue())
      disable(); 
  }
  
  public void onUpdate() {
    clearQueue();
  }
  
  public void onDisable() {
    clearQueue();
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (this.receive && mc.player != null && !mc.isSingleplayer() && mc.player.isEntityAlive() && event.getStage() == 0 && event.getPacket() instanceof net.minecraft.network.play.client.CPacketKeepAlive) {
      this.packets.add(event.getPacket());
      event.setCanceled(true);
    } 
  }
  
  public void clearQueue() {
    if (mc.player != null && !mc.isSingleplayer() && mc.player.isEntityAlive() && ((!((Boolean)this.seconds.getValue()).booleanValue() && this.timer.passedMs(((Integer)this.delay.getValue()).intValue())) || (((Boolean)this.seconds.getValue()).booleanValue() && this.timer.passedS(((Integer)this.secondDelay.getValue()).intValue())))) {
      double limit = MathUtil.getIncremental(Math.random() * 10.0D, 1.0D);
      this.receive = false;
      int i = 0;
      while (i < limit) {
        Packet<?> packet = this.packets.poll();
        if (packet != null)
          mc.player.connection.sendPacket(packet); 
        i++;
      } 
      this.timer.reset();
      this.receive = true;
    } 
  }
}
