/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 */
package sk.fri.ktk.elevator;

import com.google.common.eventbus.EventBus;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

public class StopButton
extends Element {
    public StopButton(EventBus eventBus, Element.UI ui, Integer address) {
        super(eventBus, ui, address);
        this.setSuperClassName(this.getClass().getSimpleName());
    }

    @Override
    public void newPacket(SerialCommPacket serialCommPacket) {
        System.out.printf("Packet arrived", new Object[0]);
    }

    @Override
    public void newBroadcast(SerialCommPacket serialCommPacket) {
    }

    @Override
    public String getTooltipText() {
        return null;
    }
}

