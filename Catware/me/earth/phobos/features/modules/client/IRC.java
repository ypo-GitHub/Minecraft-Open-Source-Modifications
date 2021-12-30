package me.earth.phobos.features.modules.client;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.WaypointManager;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import me.earth.phobos.util.Util;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;

public class IRC extends Module {
  public static final Random avRandomizer = new Random();
  
  private static final ResourceLocation SHULKER_GUI_TEXTURE = null;
  
  public static IRC INSTANCE;
  
  public static IRCHandler handler;
  
  public static List<String> phobosUsers;
  
  public Setting<String> ip = register(new Setting("IP", "206.189.218.150"));
  
  public Setting<Boolean> waypoints = register(new Setting("Waypoints", Boolean.valueOf(false)));
  
  public Setting<Boolean> ding = register(new Setting("Ding", Boolean.valueOf(false), v -> ((Boolean)this.waypoints.getValue()).booleanValue()));
  
  public Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.waypoints.getValue()).booleanValue()));
  
  public Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.waypoints.getValue()).booleanValue()));
  
  public Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.waypoints.getValue()).booleanValue()));
  
  public Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.waypoints.getValue()).booleanValue()));
  
  public Setting<Boolean> inventories = register(new Setting("Inventories", Boolean.valueOf(false)));
  
  public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(true), v -> ((Boolean)this.inventories.getValue()).booleanValue()));
  
  public Setting<Boolean> own = register(new Setting("OwnShulker", Boolean.valueOf(true), v -> ((Boolean)this.inventories.getValue()).booleanValue()));
  
  public Setting<Integer> cooldown = register(new Setting("ShowForS", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(5), v -> ((Boolean)this.inventories.getValue()).booleanValue()));
  
  public Setting<Boolean> offsets = register(new Setting("Offsets", Boolean.valueOf(false)));
  
  private final Setting<Integer> yPerPlayer = register(new Setting("Y/Player", Integer.valueOf(18), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
  
  private final Setting<Integer> xOffset = register(new Setting("XOffset", Integer.valueOf(4), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
  
  private final Setting<Integer> yOffset = register(new Setting("YOffset", Integer.valueOf(2), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
  
  private final Setting<Integer> trOffset = register(new Setting("TROffset", Integer.valueOf(2), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
  
  public Setting<Integer> invH = register(new Setting("InvH", Integer.valueOf(3), v -> ((Boolean)this.inventories.getValue()).booleanValue()));
  
  public Setting<Bind> pingBind = register(new Setting("Ping", new Bind(-1)));
  
  public boolean status = false;
  
  public Timer updateTimer = new Timer();
  
  public Timer downTimer = new Timer();
  
  public BlockPos waypointTarget;
  
  private int textRadarY = 0;
  
  private boolean down = false;
  
  private boolean pressed = false;
  
  public IRC() {
    super("just don't", "Phobos chat server", Module.Category.CLIENT, true, true, true);
    INSTANCE = this;
  }
  
  public static void updateInventory() throws IOException {
    handler.outputStream.writeUTF("updateinventory");
    handler.outputStream.writeUTF(mc.player.getName());
    writeByteArray(serializeInventory(), handler.outputStream);
  }
  
  public static void updateInventories() {
    for (String player : phobosUsers) {
      try {
        send("inventory", player);
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
  }
  
  public static void updateWaypoint(BlockPos pos, String server, String dimension, Color color) throws IOException {
    send("waypoint", server + ":" + dimension + ":" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ(), color.getRed() + ":" + color.getGreen() + ":" + color.getBlue() + ":" + color.getAlpha());
  }
  
  public static void removeWaypoint() throws IOException {
    handler.outputStream.writeUTF("removewaypoint");
    handler.outputStream.writeUTF(mc.player.getName());
    handler.outputStream.flush();
  }
  
  public static void send(String command, String data, String data1) throws IOException {
    handler.outputStream.writeUTF(command);
    handler.outputStream.writeUTF(mc.player.getName());
    handler.outputStream.writeUTF(data);
    handler.outputStream.writeUTF(data1);
    handler.outputStream.flush();
  }
  
  public static void send(String command, String data) throws IOException {
    handler.outputStream.writeUTF(command);
    handler.outputStream.writeUTF(mc.player.getName());
    handler.outputStream.writeUTF(data);
    handler.outputStream.flush();
  }
  
  private static byte[] readByteArrayLWithLength(DataInputStream reader) throws IOException {
    int length = reader.readInt();
    if (length > 0) {
      byte[] cifrato = new byte[length];
      reader.readFully(cifrato, 0, cifrato.length);
      return cifrato;
    } 
    return null;
  }
  
  public static void writeByteArray(byte[] data, DataOutputStream writer) throws IOException {
    writer.writeInt(data.length);
    writer.write(data);
    writer.flush();
  }
  
  public static List<ItemStack> deserializeInventory(byte[] inventory) throws IOException, ClassNotFoundException {
    ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(inventory));
    ArrayList<ItemStack> inventoryList = (ArrayList<ItemStack>)stream.readObject();
    return inventoryList;
  }
  
  public static byte[] serializeInventory() throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(new ArrayList((Collection<?>)mc.player.inventory.mainInventory));
    return bos.toByteArray();
  }
  
  public static void say(String message) throws IOException {
    handler.outputStream.writeUTF("message");
    handler.outputStream.writeUTF(mc.player.getName());
    handler.outputStream.writeUTF(message);
    handler.outputStream.flush();
  }
  
  public static void cockt(int id) throws IOException {
    handler.outputStream.writeUTF("cockt");
    handler.outputStream.writeInt(id);
    handler.outputStream.flush();
  }
  
  public static String getDimension(int dim) {
    switch (dim) {
      case 0:
        return "Overworld";
      case -1:
        return "Nether";
      case 1:
        return "End";
    } 
    return "";
  }
  
  public void onUpdate() {
    if (handler != null && handler.isAlive() && !handler.isInterrupted()) {
      this.status = !handler.socket.isClosed();
    } else {
      this.status = false;
    } 
    if (this.updateTimer.passedMs(5000L) && handler != null && handler.isAlive() && !handler.socket.isClosed()) {
      try {
        handler.outputStream.writeUTF("update");
        handler.outputStream.writeUTF(mc.player.getName());
        handler.outputStream.flush();
      } catch (Exception e) {
        e.printStackTrace();
      } 
      this.updateTimer.reset();
    } 
    if (!mc.isSingleplayer() && !(mc.currentScreen instanceof me.earth.phobos.features.gui.PhobosGui) && handler != null && !handler.socket.isClosed() && this.status) {
      if (this.down) {
        if (this.downTimer.passedMs(2000L)) {
          try {
            removeWaypoint();
          } catch (IOException e2) {
            e2.printStackTrace();
          } 
          this.down = false;
          this.downTimer.reset();
        } 
        if (!Keyboard.isKeyDown(((Bind)this.pingBind.getValue()).getKey()))
          try {
            updateWaypoint(this.waypointTarget, mc.currentServerData.serverIP, String.valueOf(mc.player.dimension), new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()));
          } catch (IOException e2) {
            e2.printStackTrace();
          }  
      } 
      if (Keyboard.isKeyDown(((Bind)this.pingBind.getValue()).getKey())) {
        if (!this.pressed) {
          this.down = true;
          this.pressed = true;
        } 
      } else {
        this.down = false;
        this.pressed = false;
        this.downTimer.reset();
      } 
    } 
  }
  
  public void onRender3D(Render3DEvent event) {
    if (Feature.fullNullCheck() || mc.isSingleplayer())
      return; 
    RayTraceResult result = mc.player.rayTrace(2000.0D, event.getPartialTicks());
    if (result != null)
      this.waypointTarget = new BlockPos(result.hitVec); 
    if (((Boolean)this.waypoints.getValue()).booleanValue())
      for (WaypointManager.Waypoint waypoint : Phobos.waypointManager.waypoints.values()) {
        if (mc.player.dimension != waypoint.dimension || 
          !mc.currentServerData.serverIP.equals(waypoint.server))
          continue; 
        waypoint.renderBox();
        waypoint.render();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
      }  
  }
  
  public void onRender2D(Render2DEvent event) {
    if (fullNullCheck())
      return; 
    if (((Boolean)this.inventories.getValue()).booleanValue()) {
      int x = -4 + ((Integer)this.xOffset.getValue()).intValue();
      int y = 10 + ((Integer)this.yOffset.getValue()).intValue();
      this.textRadarY = 0;
      for (String player : phobosUsers) {
        if (Phobos.inventoryManager.inventories.get(player) != null)
          continue; 
        List<ItemStack> stacks = (List<ItemStack>)Phobos.inventoryManager.inventories.get(player);
        renderShulkerToolTip(stacks, x, y, player);
        y += ((Integer)this.yPerPlayer.getValue()).intValue() + 60;
        this.textRadarY = y - 10 - ((Integer)this.yOffset.getValue()).intValue() + ((Integer)this.trOffset.getValue()).intValue();
      } 
    } 
  }
  
  public void connect() throws IOException {
    if (!INSTANCE.status) {
      Socket socket = new Socket((String)this.ip.getValue(), 1488);
      (handler = new IRCHandler(socket)).start();
      handler.outputStream.writeUTF("update");
      handler.outputStream.writeUTF(mc.player.getName());
      handler.outputStream.flush();
      INSTANCE.status = true;
      Command.sendMessage("Â§aIRC connected successfully!");
    } else {
      Command.sendMessage("Â§cIRC is already connected!");
    } 
  }
  
  public void disconnect() throws IOException {
    if (INSTANCE.status) {
      handler.socket.close();
      if (!handler.isInterrupted())
        handler.interrupt(); 
    } else {
      Command.sendMessage("Â§cIRC is not connected!");
    } 
  }
  
  public void friendAll() throws IOException {
    handler.outputStream.writeUTF("friendall");
    handler.outputStream.flush();
  }
  
  public void list() throws IOException {
    handler.outputStream.writeUTF("list");
    handler.outputStream.flush();
  }
  
  public void renderShulkerToolTip(List<ItemStack> stacks, int x, int y, String name) {
    GlStateManager.enableTexture2D();
    GlStateManager.disableLighting();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    mc.getTextureManager().bindTexture(SHULKER_GUI_TEXTURE);
    RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
    RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54 + ((Integer)this.invH.getValue()).intValue(), 500);
    RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
    GlStateManager.disableDepth();
    Color color = new Color(0, 0, 0, 255);
    this.renderer.drawStringWithShadow(name, (x + 8), (y + 6), ColorUtil.toRGBA(color));
    GlStateManager.enableDepth();
    RenderHelper.enableGUIStandardItemLighting();
    GlStateManager.enableRescaleNormal();
    GlStateManager.enableColorMaterial();
    GlStateManager.enableLighting();
    for (int i = 0; i < stacks.size(); i++) {
      int iX = x + i % 9 * 18 + 8;
      int iY = y + i / 9 * 18 + 18;
      ItemStack itemStack = stacks.get(i);
      (mc.getRenderItem()).zLevel = 501.0F;
      RenderUtil.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
      RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, iX, iY, null);
      (mc.getRenderItem()).zLevel = 0.0F;
    } 
    GlStateManager.disableLighting();
    GlStateManager.disableBlend();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
  }
  
  private static class IRCHandler extends Thread {
    public Socket socket;
    
    public DataInputStream inputStream;
    
    public DataOutputStream outputStream;
    
    public IRCHandler(Socket socket) {
      super(Util.mc.player.getName());
      this.socket = socket;
      try {
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
      } catch (IOException e) {
        e.printStackTrace();
      } 
    }
    
    public void run() {
      Command.sendMessage("Â§aSocket thread starting!");
      while (true) {
        try {
          String input = this.inputStream.readUTF();
          if (input.equalsIgnoreCase("message")) {
            String name = this.inputStream.readUTF();
            String message = this.inputStream.readUTF();
            Command.sendMessage("Â§c[IRC] Â§r<" + name + ">: " + message);
          } 
          if (input.equalsIgnoreCase("list")) {
            String f = this.inputStream.readUTF();
            String[] split = f.split("%%%"), friends = split;
            for (String friend : split)
              Command.sendMessage("Â§b" + friend.replace("_&_", " ID: ")); 
          } else if (input.equalsIgnoreCase("friendall")) {
            String f = this.inputStream.readUTF();
            String[] split2 = f.split("%%%"), friends = split2;
            for (String friend : split2) {
              if (!friend.equals(Util.mc.player.getName())) {
                Phobos.friendManager.addFriend(friend);
                Command.sendMessage("Â§b" + friend + " has been friended");
              } 
            } 
          } else if (input.equalsIgnoreCase("waypoint")) {
            String name = this.inputStream.readUTF();
            String[] inputs = this.inputStream.readUTF().split(":");
            String[] colors = this.inputStream.readUTF().split(":");
            String server = inputs[0];
            String dimension = inputs[1];
            Color color = new Color(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]), Integer.parseInt(colors[3]));
            Phobos.waypointManager.waypoints.put(name, new WaypointManager.Waypoint(name, server, Integer.parseInt(dimension), Integer.parseInt(inputs[2]), Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), color));
            Command.sendMessage("Â§c[IRC] Â§r" + name + " has set a waypoint at Â§c(" + Integer.parseInt(inputs[2]) + "," + Integer.parseInt(inputs[3]) + "," + Integer.parseInt(inputs[4]) + ")Â§r on the server Â§c" + server + "Â§r in the dimension Â§c" + IRC.getDimension(Integer.parseInt(dimension)));
            if (((Boolean)IRC.INSTANCE.ding.getValue()).booleanValue())
              Util.mc.world.playSound(Util.mc.player.posX, Util.mc.player.posY, Util.mc.player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 0.7F, false); 
          } else if (input.equalsIgnoreCase("removewaypoint")) {
            String name = this.inputStream.readUTF();
            Phobos.waypointManager.waypoints.remove(name);
            Command.sendMessage("Â§c[IRC] Â§r" + name + " has removed their waypoint");
            if (((Boolean)IRC.INSTANCE.ding.getValue()).booleanValue())
              Util.mc.world.playSound(Util.mc.player.posX, Util.mc.player.posY, Util.mc.player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, -0.7F, false); 
          } else if (input.equalsIgnoreCase("inventory")) {
            String name = this.inputStream.readUTF();
            byte[] inventory = IRC.readByteArrayLWithLength(this.inputStream);
            for (String player : IRC.phobosUsers) {
              if (player.equalsIgnoreCase(name))
                Phobos.inventoryManager.inventories.put(player, IRC.deserializeInventory(inventory)); 
            } 
          } else if (input.equalsIgnoreCase("users")) {
            byte[] inputBytes = IRC.readByteArrayLWithLength(this.inputStream);
            ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(inputBytes));
            List<String> players = (List<String>)stream.readObject();
            Command.sendMessage("Â§c[IRC]Â§r Active Users:");
            for (String name2 : players) {
              Command.sendMessage(name2);
              if (!IRC.phobosUsers.contains(name2))
                IRC.phobosUsers.add(name2); 
            } 
          } 
          IRC.INSTANCE.status = !this.socket.isClosed();
        } catch (IOException|ClassNotFoundException e) {
          e.printStackTrace();
        } 
      } 
    }
  }
}
