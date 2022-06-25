package vestige.anticheat;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import vestige.Vestige;
import vestige.anticheat.checks.movement.FlyA;

public class ChecksManager {
	
	private static ArrayList<User> users = new ArrayList<>();
	
	public static void addPlayer(EntityPlayer p) {
		User user = new User(p);
		users.add(user);
		addChecks(user);
	}
	
	public static void removePlayer(EntityPlayer p) {
		for(User user : users) {
			if(user.getPlayer().equals(p)) {
				users.remove(user);
			}
		}
	}
	
	public static ArrayList<User> getUsers() {
		return users;
	}
	
	private static void addChecks(User user) {
		user.getChecks().add(new FlyA(user));
	}
	
}