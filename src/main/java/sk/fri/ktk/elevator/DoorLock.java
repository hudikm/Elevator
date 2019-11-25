/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 */
package sk.fri.ktk.elevator;

import com.google.common.eventbus.EventBus;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

public class DoorLock
extends Element {
    public DoorLock(EventBus eventBus, Element.UI ui, Integer address) {
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
        return null;
    }
}

