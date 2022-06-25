package swapper.intentions.tropical.module.combat;

import com.google.common.eventbus.Subscribe;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.NumberSetting;
import swapper.intentions.tropical.utils.TimerUtils;

public class AutoClicker extends Module {

	public TimerUtils timer = new TimerUtils();
	public NumberSetting cps = new NumberSetting("CPS", 10, 0.5, 1, 20);
	public NumberSetting cpsRandom = new NumberSetting("CPS Spike", 2.5, 0.1, 0, 5);

    public AutoClicker() {
        super("AutoClicker", "Automatically attacks players", Keyboard.KEY_NONE, Category.COMBAT);
        addSettings(cps, cpsRandom);
		setDisplayName("Auto Clicker");
    }

    @Override
    public void onEnable() {  }
    
    @Subscribe
    public void onUpdate(UpdateEvent event) {
    	if(!event.isPre())
    		return;
		double cpsRandomized = (cpsRandom.getValue() >= 0.05 ? (cps.getValue() + ThreadLocalRandom.current().nextDouble(-cpsRandom.getValue(), cpsRandom.getValue())) : cps.getValue());
		double time = 1000/cpsRandomized;
		if(Mouse.isButtonDown(0)) {
			if (timer.hasReached(time)) {
				setSuffix(String.valueOf(Math.floor(cpsRandomized * 100) / 100));
				if (event.isPre()) {
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
					KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
					if (timer.hasReached(time + (50 + ThreadLocalRandom.current().nextInt(50)))) {
						if(mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
							KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
						timer.reset();
					}
				}
			}
		}
    }
    
    @Subscribe
    public void onMove(MoveEvent event) {

    }

    @Override
    public void onDisable() {  }

}
