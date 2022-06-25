	package swapper.intentions.tropical.module.movement.speed.impl;

import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.speed.SpeedMode;
import swapper.intentions.tropical.utils.MoveUtils;

    public class BWP extends SpeedMode {
        public double ticks; //double bcuz might be testing more stuff and too lazy to restart
        public BWP() {
            super("BWP");
        }

        @Override
        public void onUpdate(UpdateEvent e) {
            ticks++;
            if(ticks == 3 && !mc.thePlayer.onGround && MoveUtils.getSpeed() < 0.4)
                MoveUtils.strafe(MoveUtils.getSpeed()/0.9);
            //if(ticks == 2 || ticks == 3)
                //mc.thePlayer.motionY -= 0.3;
            if(!e.isPre())
                return;
            MoveUtils.strafe(Math.min(MoveUtils.getSpeed(), 0.4));
            if(MoveUtils.isMoving()) {
                if (mc.thePlayer.onGround) {
                    if (MoveUtils.getSpeed() < 0.29) {
                        mc.thePlayer.motionX /= 0.96;
                        mc.thePlayer.motionZ /= 0.96;
                    }
                    if(ticks > 2) {
                        mc.thePlayer.jump();
                        ticks = 0;
                    }
                }
            }else {
                mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
            }
        }

        @Override
        public void onMove(MoveEvent e) {

        }

        @Override
        public void onPacket(PacketEvent e) {
        }

        @Override
        public void onEnable() {
            ticks = 0;
        }
    }
