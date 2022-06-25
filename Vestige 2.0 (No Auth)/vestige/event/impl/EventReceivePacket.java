package vestige.event.impl;

import net.minecraft.network.Packet;
import vestige.event.Event;

public class EventReceivePacket extends Event {
	
	private Packet packet;

	public EventReceivePacket(Packet packet) {
		super(false);
		this.packet = packet;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

}