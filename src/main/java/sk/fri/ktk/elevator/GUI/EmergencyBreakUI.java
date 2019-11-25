/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  javafx.application.Platform
 *  javafx.scene.Node
 *  javafx.scene.Parent
 *  javafx.scene.control.Label
 */
package sk.fri.ktk.elevator.GUI;

import java.util.List;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import sk.fri.ktk.elevator.Element;
import sk.fri.ktk.elevator.EmergencyBreak;
import sk.fri.ktk.elevator.GUI.UI;

public class EmergencyBreakUI
extends UI
implements Element.UI {
    private Label node;

    public EmergencyBreakUI(Element element, Label node, List<Element> elementList) {
        super(element, node.getParent(), (Node)node, elementList);
        this.node = node;
        element.setUi(this);
    }

    @Override
    public void updateUI(Element element) {
        EmergencyBreak emergencyBreak = (EmergencyBreak)element;
        Platform.runLater(() -> {
            if (emergencyBreak.isEmergencyBreak()) {
                this.node.setStyle("-fx-background-color: red;");
            } else {
                this.node.setStyle("-fx-background-color: green;");
            }
        });
    }
}

