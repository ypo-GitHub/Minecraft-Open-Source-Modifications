package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class ViewModel extends Module {
  public Setting<Float> sizeX;
  
  public Setting<Float> sizeY;
  
  public Setting<Float> sizeZ;
  
  public Setting<Float> rotationX;
  
  public Setting<Float> rotationY;
  
  public Setting<Float> rotationZ;
  
  public Setting<Float> positionX;
  
  public Setting<Float> positionY;
  
  public Setting<Float> positionZ;
  
  public Setting<Float> itemFOV;
  
  public ViewModel() {
    super("Viewmodel", "Changes to the viewmodel.", Module.Category.RENDER, false, false, false);
    this.sizeX = register(new Setting("Size-X", Float.valueOf(1.0F), Float.valueOf(0.0F), Float.valueOf(2.0F)));
    this.sizeY = register(new Setting("Size-Y", Float.valueOf(1.0F), Float.valueOf(0.0F), Float.valueOf(2.0F)));
    this.sizeZ = register(new Setting("Size-X", Float.valueOf(1.0F), Float.valueOf(0.0F), Float.valueOf(2.0F)));
    this.rotationX = register(new Setting("Rotation-X", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(1.0F)));
    this.rotationY = register(new Setting("Rotation-Y", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(1.0F)));
    this.rotationZ = register(new Setting("Rotation-Z", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(1.0F)));
    this.positionX = register(new Setting("Position-X", Float.valueOf(0.0F), Float.valueOf(-2.0F), Float.valueOf(2.0F)));
    this.positionY = register(new Setting("Position-Y", Float.valueOf(0.0F), Float.valueOf(-2.0F), Float.valueOf(2.0F)));
    this.positionZ = register(new Setting("Position-Z", Float.valueOf(0.0F), Float.valueOf(-2.0F), Float.valueOf(2.0F)));
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static ViewModel getINSTANCE() {
    if (INSTANCE == null)
      INSTANCE = new ViewModel(); 
    return INSTANCE;
  }
  
  private static ViewModel INSTANCE = new ViewModel();
}
