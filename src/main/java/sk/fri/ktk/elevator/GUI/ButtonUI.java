/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  javafx.event.ActionEvent
 *  javafx.event.Event
 *  javafx.event.EventHandler
 *  javafx.scene.Node
 *  javafx.scene.Parent
 *  javafx.scene.control.Button
 */
package sk.fri.ktk.elevator.GUI;

import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import sk.fri.ktk.elevator.ButtonOnFloor;
import sk.fri.ktk.elevator.Element;
import sk.fri.ktk.elevator.GUI.UI;

public class ButtonUI
extends UI
implements Element.UI {
    private Button button;
    private ButtonOnFloor buttonOnFloor;

    public ButtonUI(Button button, ButtonOnFloor buttonOnFloor, List<Element> elementList) {
        super(buttonOnFloor, button.getParent(), (Node)button, elementList);
        this.button = button;
        this.buttonOnFloor = buttonOnFloor;
        button.setOnAction(event -> buttonOnFloor.onClick());
        buttonOnFloor.setUi(this);
    }

    @Override
    public void updateUI(Element element) {
    }
}

