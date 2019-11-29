/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 */
package sk.fri.ktk.elevator;

import com.google.common.eventbus.EventBus;
import sk.fri.ktk.Singleton;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

public class LCD
extends Element {
    private String lcdText;
    private byte upDown = 0;

    public LCD(EventBus eventBus, Element.UI ui, Integer address) {
        super(eventBus, ui, address);
        this.setSuperClassName(this.getClass().getSimpleName());
    }

    public byte getUpDown() {
        return this.upDown;
    }

    public String getLcdText() {
        return this.lcdText;
    }

    @Override
    public void newPacket(SerialCommPacket serialCommPacket) {
        if (serialCommPacket.getData() != null) {
            serialCommPacket.getData().rewind();
            this.upDown = serialCommPacket.getData().get();
            this.lcdText = new String(serialCommPacket.getData().array());
            this.lcdText = this.lcdText.substring(1, this.lcdText.length());
            this.getUi().updateUI(this);
            Singleton.logElevator.fine("LCD: "+ this.lcdText);
        }else{
            Singleton.logElevator.warning("LCD: Empty Data");
        }
    }

    @Override
    public void newBroadcast(SerialCommPacket serialCommPacket) {
    }

    @Override
    public String getTooltipText() {
        return "<h1><strong>Information display</strong></h1>\n<h2>Address: 0x" + Integer.toHexString(this.getAddress()) + "&nbsp;</h2>\n<h4>Description:</h4>\n<p>Display show the current level and the direction of movement(UP\u2191, DOWN\u2193, none).  </p>\n<p>Commands:</p>\n<p>Packet - data array:</p>\n<table>\n<tbody>\n<tr>\n<td style=\"text-align: center;\">&nbsp;1B</td>\n<td style=\"text-align: center;\">max 254B&nbsp;</td>\n</tr>\n<tr>\n<td style=\"text-align: center;\">&nbsp;Move direction(byte)</td>\n<td style=\"text-align: center;\">&nbsp;Text to show(string)</td>\n</tr>\n</tbody>\n</table><table>\n<tbody>\n<tr>\n<td style=\"text-align: center;\"><em><strong>Move direction byte&nbsp;</strong></em></td>\n<td style=\"text-align: center;\"><em><strong>Description&nbsp;</strong></em></td>\n</tr>\n<tr>\n<td style=\"text-align: center;\"> 0x01 </td>\n<td style=\"text-align: center;\">UP\u2191</td>\n</tr>\n<tr>\n<td style=\"text-align: center;\"> 0x02 </td>\n<td style=\"text-align: center;\"> DOWN\u2193</td>\n</tr>\n<tr>\n<td style=\"text-align: center;\"> else </td>\n<td style=\"text-align: center;\">none</td>\n</tr>\n</tbody>\n</table>";
    }
}

