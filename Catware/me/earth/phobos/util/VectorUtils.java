package me.earth.phobos.util;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class VectorUtils implements Util {
  static Matrix4f modelMatrix = new Matrix4f();
  
  static Matrix4f projectionMatrix = new Matrix4f();
  
  private static void VecTransformCoordinate(Vector4f vec, Matrix4f matrix) {
    float x = vec.x;
    float y = vec.y;
    float z = vec.z;
    vec.x = x * matrix.m00 + y * matrix.m10 + z * matrix.m20 + matrix.m30;
    vec.y = x * matrix.m01 + y * matrix.m11 + z * matrix.m21 + matrix.m31;
    vec.z = x * matrix.m02 + y * matrix.m12 + z * matrix.m22 + matrix.m32;
    vec.w = x * matrix.m03 + y * matrix.m13 + z * matrix.m23 + matrix.m33;
  }
  
  public static Plane toScreen(double x, double y, double z) {
    Entity view = mc.getRenderViewEntity();
    if (view == null)
      return new Plane(0.0D, 0.0D, false); 
    Vec3d camPos = ActiveRenderInfo.position;
    Vec3d eyePos = ActiveRenderInfo.projectViewFromEntity(view, mc.getRenderPartialTicks());
    float vecX = (float)(camPos.x + eyePos.x - (float)x);
    float vecY = (float)(camPos.y + eyePos.y - (float)y);
    float vecZ = (float)(camPos.z + eyePos.z - (float)z);
    Vector4f pos = new Vector4f(vecX, vecY, vecZ, 1.0F);
    modelMatrix.load(ActiveRenderInfo.MODELVIEW
        
        .asReadOnlyBuffer());
    projectionMatrix.load(ActiveRenderInfo.PROJECTION
        
        .asReadOnlyBuffer());
    VecTransformCoordinate(pos, modelMatrix);
    VecTransformCoordinate(pos, projectionMatrix);
    if (pos.w > 0.0F) {
      pos.x *= -100000.0F;
      pos.y *= -100000.0F;
    } else {
      float invert = 1.0F / pos.w;
      pos.x *= invert;
      pos.y *= invert;
    } 
    ScaledResolution res = new ScaledResolution(mc);
    float halfWidth = res.getScaledWidth() / 2.0F;
    float halfHeight = res.getScaledHeight() / 2.0F;
    pos.x = halfWidth + 0.5F * pos.x * res.getScaledWidth() + 0.5F;
    pos.y = halfHeight - 0.5F * pos.y * res.getScaledHeight() + 0.5F;
    boolean bVisible = true;
    if (pos.x < 0.0F || pos.y < 0.0F || pos.x > res.getScaledWidth() || pos.y > res.getScaledHeight())
      bVisible = false; 
    return new Plane(pos.x, pos.y, bVisible);
  }
  
  public static Plane toScreen(Vec3d vec) {
    return toScreen(vec.x, vec.y, vec.z);
  }
  
  @Deprecated
  public static ScreenPos _toScreen(double x, double y, double z) {
    Plane plane = toScreen(x, y, z);
    return new ScreenPos(plane.getX(), plane.getY(), plane.isVisible());
  }
  
  @Deprecated
  public static ScreenPos _toScreen(Vec3d vec3d) {
    return _toScreen(vec3d.x, vec3d.y, vec3d.z);
  }
  
  @Deprecated
  public static Object vectorAngle(Vec3d vec3d) {
    return null;
  }
  
  public static Vec3d multiplyBy(Vec3d vec1, Vec3d vec2) {
    return new Vec3d(vec1.x * vec2.x, vec1.y * vec2.y, vec1.z * vec2.z);
  }
  
  public static Vec3d copy(Vec3d toCopy) {
    return new Vec3d(toCopy.x, toCopy.y, toCopy.z);
  }
  
  public static double getCrosshairDistance(Vec3d eyes, Vec3d directionVec, Vec3d pos) {
    return pos.subtract(eyes).normalize().subtract(directionVec).lengthSquared();
  }
  
  @Deprecated
  public static class ScreenPos {
    public final int x;
    
    public final int y;
    
    public final boolean isVisible;
    
    public final double xD;
    
    public final double yD;
    
    public ScreenPos(double x, double y, boolean isVisible) {
      this.x = (int)x;
      this.y = (int)y;
      this.xD = x;
      this.yD = y;
      this.isVisible = isVisible;
    }
  }
}
