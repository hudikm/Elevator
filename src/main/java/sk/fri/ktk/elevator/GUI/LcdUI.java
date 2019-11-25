/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  javafx.application.Platform
 *  javafx.collections.ObservableList
 *  javafx.scene.Node
 *  javafx.scene.Parent
 *  javafx.scene.control.Label
 */
package sk.fri.ktk.elevator.GUI;

import java.util.List;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import sk.fri.ktk.elevator.Element;
import sk.fri.ktk.elevator.GUI.UI;
import sk.fri.ktk.elevator.LCD;

public class LcdUI
extends UI
implements Element.UI {
    private final Label lcdLabel;

    public LcdUI(Element element, Label node, List<Element> elementList) {
        super(element, node.getParent(), (Node)node, elementList);
        this.lcdLabel = node;
        element.setUi(this);
        node.getStyleClass().remove((Object)"label");
    }

    @Override
    public void updateUI(Element element) {
        final LCD lcd = (LCD)element;
        Platform.runLater((Runnable)new Runnable(){

            @Override
            public void run() {
                if (lcd.getUpDown() == 1) {
                    LcdUI.this.lcdLabel.setText("\u2191");
                } else if (lcd.getUpDown() == 2) {
                    LcdUI.this.lcdLabel.setText("\u2193");
                } else {
                    LcdUI.this.lcdLabel.setText("");
                }
                LcdUI.this.lcdLabel.setText(LcdUI.this.lcdLabel.getText().concat(lcd.getLcdText()));
            }
        });
    }

}

