/*
 * Decompiled with CFR 0.145.
 */
package sk.fri.ktk.elevator.Packets;

import java.nio.ByteBuffer;

public class SerialCommPacket {
    public static final Integer NET_MASK = 255;
    public static final Integer SERIAL_LINK = 0;
    protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private ByteBuffer data;
    private Integer address;
    private Integer senderAddr;
    private boolean ack = false;
    private static final int[] crcTable = new int[]{0, 94, 188, 226, 97, 63, 221, 131, 194, 156, 126, 32, 163, 253, 31, 65, 157, 195, 33, 127, 252, 162, 64, 30, 95, 1, 227, 189, 62, 96, 130, 220, 35, 125, 159, 193, 66, 28, 254, 160, 225, 191, 93, 3, 128, 222, 60, 98, 190, 224, 2, 92, 223, 129, 99, 61, 124, 34, 192, 158, 29, 67, 161, 255, 70, 24, 250, 164, 39, 121, 155, 197, 132, 218, 56, 102, 229, 187, 89, 7, 219, 133, 103, 57, 186, 228, 6, 88, 25, 71, 165, 251, 120, 38, 196, 154, 101, 59, 217, 135, 4, 90, 184, 230, 167, 249, 27, 69, 198, 152, 122, 36, 248, 166, 68, 26, 153, 199, 37, 123, 58, 100, 134, 216, 91, 5, 231, 185, 140, 210, 48, 110, 237, 179, 81, 15, 78, 16, 242, 172, 47, 113, 147, 205, 17, 79, 173, 243, 112, 46, 204, 146, 211, 141, 111, 49, 178, 236, 14, 80, 175, 241, 19, 77, 206, 144, 114, 44, 109, 51, 209, 143, 12, 82, 176, 238, 50, 108, 142, 208, 83, 13, 239, 177, 240, 174, 76, 18, 145, 207, 45, 115, 202, 148, 118, 40, 171, 245, 23, 73, 8, 86, 180, 234, 105, 55, 213, 139, 87, 9, 235, 181, 54, 104, 138, 212, 149, 203, 41, 119, 244, 170, 72, 22, 233, 183, 85, 11, 136, 214, 52, 106, 43, 117, 151, 201, 74, 20, 246, 168, 116, 42, 200, 150, 21, 75, 169, 247, 182, 232, 10, 84, 215, 137, 107, 53};

    public SerialCommPacket(Integer address, ByteBuffer data) {
        this.data = data;
        this.address = address;
    }

    public SerialCommPacket() {
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
//        for (int j = 0; j < bytes.length; ++j) {
//            int v = bytes[j] & 255;
//            hexChars[j * 2] = hexArray[v >>> 4];
//            hexChars[j * 2 + 1] = hexArray[v & 15];
//        }
        String ret = "";
        for (Byte c : bytes) {
            ret = String.format("%s %02x", ret, c);
        }
        return ret;
    }

    public Integer getSenderAddr() {
        return this.senderAddr;
    }

    public void setSenderAddr(Integer senderAddr) {
        this.senderAddr = senderAddr;
    }

    public ByteBuffer getData() {
        return this.data;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }

    public Integer getAddress() {
        return this.address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public static byte crcCalc(SerialCommPacket crcSerialCommPacket) {
        byte crc = (byte) crcTable[crcSerialCommPacket.getAddress()];
        crc = (byte) crcTable[(crc ^ crcSerialCommPacket.getSenderAddr()) & 255];
        if (crcSerialCommPacket.getData() != null) {
            crcSerialCommPacket.getData().rewind();
            while (crcSerialCommPacket.getData().hasRemaining()) {
                crc = (byte) crcTable[(crc ^ crcSerialCommPacket.getData().get()) & 255];
            }
        }
        return crc;
    }

    public byte getCrc() {
        return SerialCommPacket.crcCalc(this);
    }

    public String toString() {
        return String.format("Dest. addr: 0x%02x Sender addr: 0x%02x Data(hex): %s", this.address, this.senderAddr, (this.data == null ? "NULL" : SerialCommPacket.bytesToHex(this.data.array())));
//        return "Destination addr: " + Integer.toHexString(this.address) + " Sender address: " + Integer.toHexString(this.senderAddr) + " Data(HEX):" )

    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }
}

