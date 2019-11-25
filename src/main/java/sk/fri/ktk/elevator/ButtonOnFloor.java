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

public class ButtonOnFloor
extends Element {
    public void onClick() {
        SerialCommPacket serialCommPacket = new SerialCommPacket();
        serialCommPacket.setAddress(SerialCommPacket.SERIAL_LINK);
        serialCommPacket.setSenderAddr(this.address);
        this.eventBus.post((Object) serialCommPacket);
    }

    public ButtonOnFloor(EventBus eventBus, Element.UI ui, Integer address) {
        super(eventBus, ui, address);
        this.setSuperClassName(this.getClass().getSimpleName());
    }

    @Override
    public void newPacket(SerialCommPacket serialCommPacket) {
    }

    @Override
    public void newBroadcast(SerialCommPacket serialCommPacket) {
    }

    @Override
    public String getTooltipText() {
        return "<h1><strong>Elevator button</strong></h1>\n<h2>Address: 0x" + Integer.toHexString(this.getAddress()) + "&nbsp;</h2>\n<h4>Description:</h4>\n<p>On click the button send packet with address byte 0x" + Integer.toHexString(this.getAddress()) + " and NULL(empty) data </p>\n";
    }
}

