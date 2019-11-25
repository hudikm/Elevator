/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 */
package sk.fri.ktk.elevator;

import com.google.common.eventbus.EventBus;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

public class Led
extends Element {
    private Boolean value = false;

    public Boolean getValue() {
        return this.value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public Led(EventBus eventBus, Element.UI ui, Integer address) {
        super(eventBus, ui, address);
        this.setSuperClassName(this.getClass().getSimpleName());
    }

    @Override
    public void newPacket(SerialCommPacket serialCommPacket) {
        if (serialCommPacket.getData() != null) {
            serialCommPacket.getData().rewind();
            this.value = serialCommPacket.getData().get() == 0 ? Boolean.valueOf(false) : Boolean.valueOf(true);
            this.getUi().updateUI(this);
        }
    }

    @Override
    public void newBroadcast(SerialCommPacket serialCommPacket) {
    }

    @Override
    public String getTooltipText() {
        return "<h1><strong>LED</strong></h1>\n<h2>Address: 0x" + Integer.toHexString(this.getAddress()) + "&nbsp;</h2>\n<h4>Description:</h4>\n<p>Led indicator(Possible states on/off)</p>\n<p>Commands:</p>\n<table>\n<tbody>\n<tr>\n<td style=\"text-align: center;\"><em><strong>Command&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Description&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Data In&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Data Out&nbsp;(response)</strong></em></td>\n</tr>\n<tr>\n<td style=\"text-align: center;\"> 0x00</td>\n<td style=\"text-align: center;\">LED off</td>\n<td style=\"text-align: center;\">Byte </td>\n</tr>\n<tr>\n<td style=\"text-align: center;\"> 0x01</td>\n<td style=\"text-align: center;\">LED on</td>\n<td style=\"text-align: center;\">Byte </td>\n</tr>\n</tbody>\n</table>";
    }
}

