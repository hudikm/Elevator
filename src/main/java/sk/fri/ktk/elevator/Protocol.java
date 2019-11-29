/*
 * Decompiled with CFR 0.145.
 */
package sk.fri.ktk.elevator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.MessageFormat;
import java.util.Date;

import sk.fri.ktk.Singleton;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

public class Protocol {

    public static final long RECEIVE_BYTE_TIMEOUT = 2L; //bolo 25
    byte crc = 0;
    private Date startTime;
    private static final byte startByte = -96;
    private SerialCommPacket serialCommPacket;
    private static final int timeout = 25;
    private STATE state = STATE.START;
    private ByteBuffer byteBuffer;

    public SerialCommPacket getSerialCommPacket() {
        return this.serialCommPacket;
    }

    public class ReturnValue {
        boolean commCompleted;
        STATE lastSTATE;

        public ReturnValue(boolean commCompleted, STATE lastSTATE) {
            this.commCompleted = commCompleted;
            this.lastSTATE = lastSTATE;
        }
    }

    public static ByteBuffer getSerialData(SerialCommPacket serialCommPacket) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(5 + (serialCommPacket.getData() == null ? 0 : serialCommPacket.getData().capacity()));
        if (serialCommPacket.isAck()) {
            byteBuffer.put((byte) 0xa1);
        } else {
            byteBuffer.put((byte) 0xa0);
        }
        byteBuffer.put((byte) serialCommPacket.getAddress().intValue());
        byteBuffer.put((byte) serialCommPacket.getSenderAddr().intValue());
        if (serialCommPacket.getData() != null) {
            byteBuffer.put((byte) serialCommPacket.getData().capacity());
            serialCommPacket.getData().rewind();
            byteBuffer.put(serialCommPacket.getData());
        } else {
            byteBuffer.put((byte) 0);
        }
        byteBuffer.put(serialCommPacket.getCrc());
        return byteBuffer;
    }

    public ReturnValue newChar(byte chr) {
        long diff;
        Date currentDate = new Date();
        if (this.startTime != null && (diff = currentDate.getTime() - this.startTime.getTime()) >= getReceiveByteTimeout() && this.state != STATE.START) {
            this.state = STATE.START;
            Singleton.logElevator.warning(MessageFormat.format("Protocol restart: Timout {0} ms -> The board did not send another character in time!", diff));
        }
        switch (this.state) {
            case START: {
                this.crc = 0;
                this.startTime = new Date();
                if (chr == (byte) 0xa0 /*-96*/) {
                    this.serialCommPacket = new SerialCommPacket();
                    this.serialCommPacket.setAck(false);
                    this.state = STATE.ADDRESS;
                } else if (chr == (byte) 0xa1 /*-95*/) {
                    this.serialCommPacket = new SerialCommPacket();
                    this.serialCommPacket.setAck(true);
                    this.state = STATE.ADDRESS;
                }
                return new ReturnValue(false, STATE.START);
            }
            case ADDRESS: {

                this.serialCommPacket.setAddress(chr & 255);
                this.state = STATE.ADDRESS_SEND;
                return new ReturnValue(false, STATE.ADDRESS);
            }
            case ADDRESS_SEND: {
                this.serialCommPacket.setSenderAddr(chr & 255);
                this.state = STATE.LENGTH;
                return new ReturnValue(false, STATE.ADDRESS_SEND);
            }
            case LENGTH: {
                if (chr == 0) {
                    this.state = STATE.CRC;
                    return new ReturnValue(false, STATE.LENGTH);

                }
                this.byteBuffer = ByteBuffer.allocate(chr & 255);
                this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                this.serialCommPacket.setData(this.byteBuffer);
                this.state = STATE.DATA;
                return new ReturnValue(false, STATE.LENGTH);

            }
            case DATA: {
                this.byteBuffer.put(chr);
                if (!this.byteBuffer.hasRemaining()) {
                    this.state = STATE.CRC;
                }
                return new ReturnValue(false, STATE.DATA);
            }
            case CRC: {
                this.state = STATE.START;
                this.crc = SerialCommPacket.crcCalc(this.serialCommPacket);
                if (this.crc != chr) {
                    Singleton.logElevator.warning(String.format("Protocol: CRC error -> " + Integer.toHexString(this.crc & 255) + " != " + Integer.toHexString(chr & 255)));
                    return new ReturnValue(false, STATE.CRC);
                }
                return new ReturnValue(true, STATE.CRC);
            }
        }
        return new ReturnValue(true, STATE.START);
    }

    private long getReceiveByteTimeout() {
        return  Singleton.getInstance().getRxTimeOut();
    }

    static enum STATE {
        START,
        ADDRESS,
        ADDRESS_SEND,
        LENGTH,
        DATA,
        CRC;

    }

}

