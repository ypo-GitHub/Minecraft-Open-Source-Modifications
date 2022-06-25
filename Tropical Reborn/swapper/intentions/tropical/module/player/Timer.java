package swapper.intentions.tropical.module.player;

import com.google.common.eventbus.Subscribe;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.NumberSetting;

public class Timer extends Module {
	public NumberSetting speed = new NumberSetting("Timer Speed", 1.0, 0.01, 0.01, 10);

	public Timer() {
		super("Timer", "drip speed", 0x0, Category.PLAYER);
		addSettings(speed);
	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		setSuffix(String.valueOf(Math.floor((speed.getValue()+0.005) * 100) / 100));
		if(!e.isPre())
			return;
		mc.timer.timerSpeed = speed.getValue().floatValue();
	}
	
	@Override
	protected void onDisable() {
		mc.timer.timerSpeed = 1.0f;
	}
}
