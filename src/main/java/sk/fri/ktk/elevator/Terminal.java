/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 */
package sk.fri.ktk.elevator;

import com.google.common.eventbus.EventBus;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Terminal
extends Element {
    private String newData;

    public String getNewData() {
        return this.newData;
    }

    public Terminal(EventBus eventBus, Element.UI ui, Integer address) {
        super(eventBus, ui, address);
    }

    public void sendPacket(String text) {
        SerialCommPacket serialCommPacketSend = new SerialCommPacket();
        serialCommPacketSend.setAddress(SerialCommPacket.SERIAL_LINK);
        serialCommPacketSend.setSenderAddr(this.address);
        serialCommPacketSend.setData(ByteBuffer.allocate(text.length()).order(ByteOrder.LITTLE_ENDIAN).put(text.getBytes()));
        this.eventBus.post((Object) serialCommPacketSend);
    }

    @Override
    public void newPacket(SerialCommPacket serialCommPacket) {
        if (serialCommPacket.getData() != null) {
            this.newData = new String(serialCommPacket.getData().array());
            this.ui.updateUI(this);
        }
    }

    @Override
    public void newBroadcast(SerialCommPacket serialCommPacket) {
    }

    @Override
    public String getTooltipText() {
        return "<h1><strong>Terminal</strong></h1>\n<h2>Address: 0x" + Integer.toHexString(this.getAddress()) + "&nbsp;</h2>\n<h4>Description:</h4>\n<p>You can receive and send text commands between PC and MCU. Data need's to be in ASCII encoding.</p>\n";
    }
}

