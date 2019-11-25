/*
 * Decompiled with CFR 0.145.
 */
package sk.fri.ktk.elevator.Packets;

import sk.fri.ktk.elevator.Cabin;

public class CabineMovePacket {
    private Cabin cabin;
    private boolean doorClosed;

    public CabineMovePacket(Cabin cabin, boolean doorClosed) {
        this.cabin = cabin;
        this.doorClosed = doorClosed;
    }

    public Cabin getCabin() {
        return this.cabin;
    }

    public double getPosition() {
        return cabin.getPosition();
    }

    public boolean isDoorClosed() {
        return doorClosed;
    }
}

