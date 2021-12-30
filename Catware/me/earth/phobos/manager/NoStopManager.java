package me.earth.phobos.manager;

import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.client.Managers;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.util.math.BlockPos;

public class NoStopManager extends Feature {
  private final Timer timer = new Timer();
  
  private String prefix;
  
  private boolean running;
  
  private boolean sentMessage;
  
  private BlockPos pos;
  
  private BlockPos lastPos;
  
  private boolean stopped;
  
  public void onUpdateWalkingPlayer() {
    if (fullNullCheck()) {
      stop();
      return;
    } 
    if (this.running && this.pos != null) {
      BlockPos currentPos = mc.player.getPosition();
      if (currentPos.equals(this.pos)) {
        BlockUtil.debugPos("<Baritone> Arrived at Position: ", this.pos);
        this.running = false;
        return;
      } 
      if (currentPos.equals(this.lastPos)) {
        if (this.stopped && this.timer.passedS(((Integer)(Managers.getInstance()).baritoneTimeOut.getValue()).intValue())) {
          sendMessage();
          this.stopped = false;
          return;
        } 
        if (!this.stopped) {
          this.stopped = true;
          this.timer.reset();
        } 
      } else {
        this.lastPos = currentPos;
        this.stopped = false;
      } 
      if (!this.sentMessage) {
        sendMessage();
        this.sentMessage = true;
      } 
    } 
  }
  
  public void sendMessage() {
    mc.player.sendChatMessage(this.prefix + "goto " + this.pos.getX() + " " + this.pos.getY() + " " + this.pos.getZ());
  }
  
  public void start(int x, int y, int z) {
    this.pos = new BlockPos(x, y, z);
    this.sentMessage = false;
    this.running = true;
  }
  
  public void stop() {
    if (this.running) {
      if (mc.player != null)
        mc.player.sendChatMessage(this.prefix + "stop"); 
      this.running = false;
    } 
  }
  
  public void setPrefix(String prefixIn) {
    this.prefix = prefixIn;
  }
}
