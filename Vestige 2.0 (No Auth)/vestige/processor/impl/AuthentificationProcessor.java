package vestige.processor.impl;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import net.minecraft.client.Minecraft;
import vestige.Vestige;
import vestige.ui.menu.VestigeMainMenu;
import vestige.util.auth.Encryption;

public class AuthentificationProcessor implements Runnable {
	
	private boolean isAuthentificated;
	private boolean runnedOnce = false;
	
	public AuthentificationProcessor() {
		
	}
	
	
	@Override
	public void run() {
		
	}

	public boolean isAuthentificated() {
		return isAuthentificated;
	}
	
	public void setAuthentificated(boolean authentificated) {
		this.isAuthentificated = authentificated;
	}

	public boolean runnedOnce() {
		return runnedOnce;
	}
	
	public String getHWID() {
		String hwid = System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getProperty("os.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
		String encryptedHWID = Encryption.hashMD5(Encryption.encryptAES(hwid, hwid));
		
		return encryptedHWID;
	}
	
}