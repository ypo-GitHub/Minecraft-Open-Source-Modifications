package swapper.intentions.tropical.event.impl;

import swapper.intentions.tropical.event.Event;

public class MoveEvent extends Event {

    public boolean isSafewalk;
    private double motionX, motionY, motionZ;

    public MoveEvent(double motionX, double motionY, double motionZ) {
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    public double getMotionX() {
        return motionX;
    }

    public double getMotionY() {
        return motionY;
    }

    public double getMotionZ() {
        return motionZ;
    }

    public void setMotionX(double motionX) {
        this.motionX = motionX;
    }

    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    public void setMotionZ(double motionZ) {
        this.motionZ = motionZ;
    }

    public boolean isSafewalk() {
        return isSafewalk;
    }

    public void setSafewalk(boolean safewalk) {
        isSafewalk = safewalk;
    }
}