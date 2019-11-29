/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 *  com.google.common.eventbus.Subscribe
 */
package sk.fri.ktk.elevator;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import sk.fri.ktk.Singleton;
import sk.fri.ktk.elevator.Packets.CabineMovePacket;
import sk.fri.ktk.elevator.Packets.MotorPacket;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

public class Cabin extends Element {
    private static final int maxHeight = 1000;
    private static final int minHeight = 0;
    private byte isDoorLocked = 0;
    private boolean doorClosed;
    private double position = 1000.0 - new Random().nextInt(1000);

    public Cabin(EventBus eventBus, Element.UI ui, Integer address) {
        super(eventBus, ui, address);
        this.setSuperClassName(this.getClass().getSimpleName());
    }

    public byte getIsDoorLocked() {
        return this.isDoorLocked;
    }

    public double getPositionRelative() {
        return 0.001 * (this.position);
    }

    public double getPosition() {
        return this.position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    @Override
    public void newPacket(SerialCommPacket serialCommPacket) {
        if (serialCommPacket.getData() != null) {
            serialCommPacket.getData().rewind();
            this.isDoorLocked = serialCommPacket.getData().get() == 0 ? (byte) 0 : 1;
            this.getUi().updateUI(this);
            if (this.isDoorLocked == 0)
                Singleton.logElevator.fine("Cabin door: Opening");
            else
                Singleton.logElevator.fine("Cabin door: Closing");
        } else {
            SerialCommPacket serialCommPacket2 = new SerialCommPacket();
            serialCommPacket2.setAddress(serialCommPacket.getSenderAddr());
            serialCommPacket2.setSenderAddr(this.address);
            serialCommPacket2.setData(ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)/*.put(this.isDoorLocked)*/.put((byte) (isDoorClosed() ? 1 : 0)));
            this.eventBus.post((Object) serialCommPacket2);

            Singleton.logElevator.fine("Cabin door: Is "+ (isDoorClosed()?"closed":"opened"));
        }
    }

    @Override
    public void newBroadcast(SerialCommPacket serialCommPacket) {
    }

    @Override
    public String getTooltipText() {
        return "<h1><strong>Elevator cabin</strong></h1>\n<h2>Address: 0x" + Integer.toHexString(this.getAddress()) + "&nbsp;</h2>\n<h4>Description:</h4>\n<p>Elevator has a door with lock that needs to be lock during ride.</p>\n<p>Commands:</p>\n<table>\n<tbody>\n<tr>\n<td style=\"text-align: center;\"><em><strong>Command&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Description&nbsp;</strong></em></td>\n</tr>\n<tr>\n<td style=\"text-align: center;\">0x00</td>\n<td style=\"text-align: center;\">&nbsp;Unlock the door</td>\n</tr>\n<tr>\n<td style=\"text-align: center;\">0x01</td>\n<td style=\"text-align: center;\">Lock the door</td>\n</tr>\n<tr>\n<td style=\"text-align: center;\">NULL(empty)</td>\n<td style=\"text-align: center;\">Send status(0=Unlock,1=Lock)</td>\n</tr>\n</tbody>\n</table>";
    }

    @Subscribe
    public void motorMovement(MotorPacket motorPacket) {
        ByteBuffer byteBuffer;
        this.position += motorPacket.getSpeed() * (double) Singleton.SIMULATION_RESOLUTION / 1000.0;

        if (this.position > 1000.0) {
            this.position = 1000.0;
            //Todo na co?
            byteBuffer = ByteBuffer.allocate(1).put((byte) 1);
            this.eventBus.post(new SerialCommPacket(motorPacket.getMotorAddress(), byteBuffer));
        } else if (this.position < 0.0) {
            this.position = 0.0;

            byteBuffer = ByteBuffer.allocate(1).put((byte) 1);
            this.eventBus.post(new SerialCommPacket(motorPacket.getMotorAddress(), byteBuffer));
        }
        this.getUi().updateUI(this);
        this.eventBus.post(new CabineMovePacket(this, doorClosed));
        if (!isDoorClosed()) {
            Singleton.logElevator.severe("Elevator is moving but doors are not closed!");
        }
    }

    public boolean isDoorClosed() {
        return doorClosed;
    }

    public void setDoorClosed(boolean doorClosed) {
        this.doorClosed = doorClosed;
    }
}

