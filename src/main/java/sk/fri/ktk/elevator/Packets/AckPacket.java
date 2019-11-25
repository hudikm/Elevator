package sk.fri.ktk.elevator.Packets;

public class AckPacket extends SerialCommPacket {
    public AckPacket(Integer senderAddress) {
        this.setAddress(0);
        this.setSenderAddr(senderAddress);
        this.setAck(true);
    }

    @Override
    public String toString() {
        return "ACK Packet";
    }
}
