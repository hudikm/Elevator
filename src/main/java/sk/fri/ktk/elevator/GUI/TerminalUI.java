/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  javafx.application.Platform
 *  javafx.collections.ObservableList
 *  javafx.event.ActionEvent
 *  javafx.event.Event
 *  javafx.event.EventHandler
 *  javafx.event.EventType
 *  javafx.scene.Node
 *  javafx.scene.Parent
 *  javafx.scene.control.Button
 *  javafx.scene.control.ComboBox
 *  javafx.scene.control.TextArea
 *  javafx.scene.control.TextField
 *  javafx.scene.input.KeyCode
 *  javafx.scene.input.KeyEvent
 */
package sk.fri.ktk.elevator.GUI;

import java.util.List;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sk.fri.ktk.elevator.Element;
import sk.fri.ktk.elevator.GUI.UI;
import sk.fri.ktk.elevator.Terminal;

public class TerminalUI
extends UI
implements Element.UI {
    private TextArea node;

    public TerminalUI(Element element, TextArea node, ComboBox comboBox, Button sendBtn, List<Element> elementList) {
        super(element, node.getParent(), (Node)node, elementList);
        this.node = node;
        element.setUi(this);
        comboBox.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String newValue = (String)comboBox.getValue();
                if (newValue != null) {
                    ((Terminal)element).sendPacket(newValue);
                }
                if (newValue != null && !comboBox.getItems().contains((Object)newValue)) {
                    comboBox.getItems().add((Object)newValue);
                }
            }
        });
        sendBtn.setOnAction(event -> {
            String newValue = (String)comboBox.getValue();
            if (newValue != null) {
                ((Terminal)element).sendPacket(newValue);
            }
            if (newValue != null && !comboBox.getItems().contains((Object)newValue)) {
                comboBox.getItems().add((Object)newValue);
            }
        });
    }

    @Override
    public void updateUI(Element element) {
        Terminal terminal = (Terminal)element;
        Platform.runLater(() -> this.node.appendText(terminal.getNewData()));
    }
}

