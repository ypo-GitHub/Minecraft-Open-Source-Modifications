package swapper.intentions.tropical.event.impl;

import net.minecraft.network.Packet;
import swapper.intentions.tropical.event.Event;
import swapper.intentions.tropical.event.PacketDirection;

public class PacketEvent extends Event {

    private final PacketDirection packetDirection;
    private Packet packet;

    public PacketEvent(PacketDirection packetDirection, Packet packet) {
        this.packetDirection = packetDirection;
        this.packet = packet;
    }

    public PacketDirection getPacketDirection() {
        return packetDirection;
    }

    public <T extends Packet> T getPacket() {
        return (T) packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public Class getPacketClass() {
        return packet.getClass();
    }

    public boolean isSending() {
        return this.packetDirection == PacketDirection.INBOUND;
    }
}