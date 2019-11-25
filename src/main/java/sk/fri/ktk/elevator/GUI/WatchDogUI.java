/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  javafx.application.Platform
 *  javafx.event.ActionEvent
 *  javafx.event.Event
 *  javafx.event.EventHandler
 *  javafx.scene.Node
 *  javafx.scene.Parent
 *  javafx.scene.control.CheckBox
 *  javafx.scene.control.Label
 */
package sk.fri.ktk.elevator.GUI;

import java.util.List;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import sk.fri.ktk.elevator.Element;
import sk.fri.ktk.elevator.GUI.UI;
import sk.fri.ktk.elevator.WatchDog;

public class WatchDogUI
extends UI
implements Element.UI {
    private Label node;

    public WatchDogUI(WatchDog element, Label node, CheckBox enabled, List<Element> elementList) {
        super(element, node.getParent(), (Node)node, elementList);
        this.node = node;
        element.setUi(this);
        element.setEnabled(enabled.isSelected());
        enabled.setOnAction(event -> element.setEnabled(enabled.isSelected()));
    }

    @Override
    public void updateUI(Element element) {
        WatchDog emergencyBreak = (WatchDog)element;
        Platform.runLater(() -> {
            if (emergencyBreak.isFired()) {
                this.node.setStyle("-fx-background-color: red;");
            } else {
                this.node.setStyle("-fx-background-color: green;");
            }
        });
    }
}

