/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 *  com.google.common.eventbus.Subscribe
 *  javafx.scene.Node
 *  javafx.scene.web.WebEngine
 *  javafx.scene.web.WebView
 */
package sk.fri.ktk.elevator;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import sk.fri.ktk.Singleton;
import sk.fri.ktk.elevator.Packets.SerialCommPacket;

public abstract class Element {
    protected EventBus eventBus;
    protected String superClassName;
    protected Integer address;
    protected UI ui;
    private Node tooltipWeb;

    public Element(EventBus eventBus, UI ui, Integer address) {
        this.eventBus = eventBus;
        this.ui = ui;
        this.address = address;
        eventBus.register((Object) this);
    }

    public String getSuperClassName() {
        return this.superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public Integer getAddress() {
        return this.address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public UI getUi() {
        return this.ui;
    }

    public void setUi(UI ui) {
        this.ui = ui;
    }

    @Subscribe
    public void PacketArrived(SerialCommPacket serialCommPacket) {
        if (Singleton.getInstance().isDEBUG_MODE()) {
            Singleton.logElevator.info(String.format("Packet arrived to class %s with address: 0x%x Data: %s \n", this.superClassName, serialCommPacket.getAddress(), serialCommPacket.getData() == null ? "NULL" : serialCommPacket.getData().toString()));
        }
        if (serialCommPacket.getAddress().equals(this.address)) {
            this.newPacket(serialCommPacket);
        } else if (serialCommPacket.getAddress().equals(SerialCommPacket.NET_MASK)) {
            this.newBroadcast(serialCommPacket);
        }
    }

    public abstract void newPacket(SerialCommPacket var1);

    public abstract void newBroadcast(SerialCommPacket var1);

    public abstract String getTooltipText();

    public Node getTooltipWeb() {
        WebView web = new WebView();
        WebEngine webEngine = web.getEngine();
        webEngine.loadContent(this.getTooltipText());
        web.setMinSize(100.0, 100.0);
        return web;
    }

    public interface UI {
        void updateUI(Element var1);
    }

}

