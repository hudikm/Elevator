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
import sk.fri.ktk.Singleton;
import sk.fri.ktk.elevator.Packets.CabineMovePacket;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

public class LimitSwitch
extends Element {
    public static final int FIRST_SENSOR = 1;
    public static final int SECOND_SENSOR = 2;
    private double position;
    public int state = 0;
    private int lastState = 0;
    private boolean bottomOrTopSwitch;

    public LimitSwitch(EventBus eventBus, UI ui, Integer address, double position, boolean bottomOrTopSwitch) {
        super(eventBus, ui, address);
        this.position = position;
        this.bottomOrTopSwitch = bottomOrTopSwitch;
        this.setSuperClassName(this.getClass().getSimpleName());
    }

    @Override
    public void newPacket(SerialCommPacket serialCommPacket) {
        SerialCommPacket serialCommPacket2 = new SerialCommPacket();
        serialCommPacket2.setAddress(SerialCommPacket.SERIAL_LINK);
        serialCommPacket2.setSenderAddr(this.address);
        serialCommPacket2.setData(ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).put((byte)this.state));
        this.eventBus.post((Object) serialCommPacket2);
    }

    @Override
    public void newBroadcast(SerialCommPacket serialCommPacket) {
    }

    @Override
    public String getTooltipText() {
        return "<h1><strong>Limit switch</strong></h1>\n<h2>Address: 0x" + Integer.toHexString(this.getAddress()) + "&nbsp;</h2>\n<h4>Description:</h4>\n<p>Switch indicates if the cabin is on the correct level. The switch has three states: off, the cabin is in wide proximity to switch and cabin is in narrow proximity to switch. <br><b>Note:</b> The information about state change is send asynchronously(without request). Also, the state information can be obtain upon a request.  </p>\n<table>\n<tbody>\n<tr>\n<td style=\"text-align: center;\"><em><strong>Command&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Description&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Data In&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Data Out&nbsp;(response)</strong></em></td>\n</tr>\n<tr>\n<td style=\"text-align: center;\">0x00</td>\n<td style=\"text-align: center;\">Switch low </td>\n<td style=\"text-align: center;\">null </td>\n<td style=\"text-align: center;\">Type: byte </td>\n</tr>\n<tr>\n<td style=\"text-align: center;\">0x01</td>\n<td style=\"text-align: center;\">The cabin is in wide proximity</td>\n<td style=\"text-align: center;\">null </td>\n<td style=\"text-align: center;\">Type: byte </td>\n</tr>\n<tr>\n<td style=\"text-align: center;\">0x02</td>\n<td style=\"text-align: center;\">The cabin is in narrow proximity </td>\n<td style=\"text-align: center;\">null </td>\n<td style=\"text-align: center;\">Type: byte </td>\n</tr>\n</tbody>\n</table>";
    }

    @Subscribe
    public void cabineMove(CabineMovePacket cabineMovePacket) {
        double delta = Math.abs(cabineMovePacket.getCabin().getPositionRelative() - this.position);

        if (delta < 0.055 && delta > 0.005) {
            this.state = 1;
            Singleton.getInstance().settings.sensorSwitch = false;
        } else if (delta < 0.005) {
            this.state = 2;
            Singleton.getInstance().settings.sensorSwitch = true;
        } else {
            this.state = 0;
            Singleton.getInstance().settings.sensorSwitch = false;
        }

        //Send info about switch change
        if (this.state != this.lastState) {
            this.lastState = this.state;
            if(!bottomOrTopSwitch || state == 2) {
                SerialCommPacket serialCommPacket = new SerialCommPacket();
                serialCommPacket.setAddress(SerialCommPacket.SERIAL_LINK);
                serialCommPacket.setSenderAddr(this.address);
                serialCommPacket.setData(ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).put((byte) this.state));
                this.eventBus.post(serialCommPacket);
            }
        }
        this.getUi().updateUI(this);
    }
}

