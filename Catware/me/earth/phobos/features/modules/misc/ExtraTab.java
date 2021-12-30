package me.earth.phobos.features.modules.misc;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;

public class ExtraTab extends Module {
  private static ExtraTab INSTANCE = new ExtraTab();
  
  public Setting<Integer> size = register(new Setting("Size", Integer.valueOf(250), Integer.valueOf(1), Integer.valueOf(1000)));
  
  public ExtraTab() {
    super("ExtraTab", "Extends Tab.", Module.Category.MISC, false, false, false);
    setInstance();
  }
  
  public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
    String name = (networkPlayerInfoIn.getDisplayName() != null) ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName((Team)networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName()), string = name;
    if (Phobos.friendManager.isFriend(name))
      return "§b" + name; 
    return name;
  }
  
  public static ExtraTab getINSTANCE() {
    if (INSTANCE == null)
      INSTANCE = new ExtraTab(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
}
