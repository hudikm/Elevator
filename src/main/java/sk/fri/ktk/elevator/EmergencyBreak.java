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
import sk.fri.ktk.Singleton;
import sk.fri.ktk.elevator.Packets.EmergencyPaket;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

public class EmergencyBreak
extends Element {
    public boolean isEmergencyBreak() {
        return Singleton.getInstance().emergencyBreak;
    }

    public void setEmergencyBreak(boolean emergencyBreak) {
        Singleton.getInstance().emergencyBreak = emergencyBreak;
    }

    public EmergencyBreak(EventBus eventBus, Element.UI ui, Integer address) {
        super(eventBus, ui, address);
    }

    @Override
    public void newPacket(SerialCommPacket serialCommPacket) {
        if (serialCommPacket.getData() != null) {
            serialCommPacket.getData().rewind();
            switch (serialCommPacket.getData().get()) {
                case 1: {
                    this.setEmergencyBreak(true);
                    this.getUi().updateUI(this);
                    break;
                }
                case 0: {
                    this.setEmergencyBreak(false);
                    this.getUi().updateUI(this);
                }
            }
        }
    }

    @Override
    public void newBroadcast(SerialCommPacket serialCommPacket) {
    }

    @Override
    public String getTooltipText() {
        return "<h1><strong>Emergency break</strong></h1>\n<h2>Address: 0x" + Integer.toHexString(this.getAddress()) + "&nbsp;</h2>\n<h4>Description:</h4>\n<p>Emergency break for elevator can be activated manually throw command or it is activated automatically after watchdog timer is triggered  </p>\n<p>Commands:</p>\n<table>\n<tbody>\n<tr>\n<td style=\"text-align: center;\"><em><strong>Command&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Description&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Data In&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Data Out&nbsp;(response)</strong></em></td>\n</tr>\n<tr>\n<td style=\"text-align: center;\"> 0x00</td>\n<td style=\"text-align: center;\">Deactivate emergency break</td>\n<td style=\"text-align: center;\">Byte </td>\n</tr>\n<tr>\n<td style=\"text-align: center;\"> 0x01 </td>\n<td style=\"text-align: center;\">Activate emergency break </td>\n<td style=\"text-align: center;\">Byte </td>\n</tr>\n</tbody>\n</table>";
    }

    @Subscribe
    public void EmergencyBreak(EmergencyPaket emergencyPaket) {
        this.getUi().updateUI(this);
    }
}

