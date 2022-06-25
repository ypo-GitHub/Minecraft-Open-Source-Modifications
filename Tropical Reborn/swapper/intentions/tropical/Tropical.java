package swapper.intentions.tropical;

import org.lwjgl.opengl.Display;

import com.google.common.eventbus.EventBus;

import swapper.intentions.tropical.event.TropicalEventBus;
import swapper.intentions.tropical.module.ModuleManager;
import viamcp.ViaMCP;
//Please Notice This Client Is Being Started Quick With My SRT.
public class Tropical {
	public static Tropical instance = new Tropical();
	
	public String name = "Tropical", buildName = "2.2.5";
	public ModuleManager moduleManager = new ModuleManager();
	public EventBus eventBus = new EventBus(new TropicalEventBus());
	
	public void onPostStartup() {
		System.out.print("[Tropical: Base] Post Startup");
		Display.setTitle(name + " " + buildName);
		eventBus.register(this);
		moduleManager.setup();
		/*via-mcp*/
		try
		{
		  ViaMCP.getInstance().start();
		  ViaMCP.getInstance().initAsyncSlider(); // For top left aligned slider
		}
		catch (Exception e)
		{
		  e.printStackTrace();
		}
	}

	public void onShutdown() {

	}
}
