/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  javafx.event.Event
 *  javafx.event.EventHandler
 *  javafx.scene.Node
 *  javafx.scene.Parent
 *  javafx.scene.control.ContentDisplay
 *  javafx.scene.control.Label
 *  javafx.scene.control.Tooltip
 *  javafx.scene.input.MouseButton
 *  javafx.scene.input.MouseEvent
 */
package sk.fri.ktk.elevator.GUI;

import java.util.List;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import sk.fri.ktk.elevator.Element;

public class UI {
    private final Element element;
    private final Label label;

    public UI(Element element, Parent parent, Node node, List<Element> elementList) {
        Tooltip tp = new Tooltip();
        this.element = element;
        elementList.add(element);
        this.label = (Label)parent.lookup("#toolTip");
        tp.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        node.setOnMouseEntered(event -> this.label.setText("Addr: 0x" + Integer.toHexString(element.getAddress())));
        node.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.MIDDLE || event.getButton() == MouseButton.SECONDARY) {
                tp.setGraphic(element.getTooltipWeb());
                tp.getWidth();
                tp.getHeight();
                tp.show(node, event.getScreenX(), event.getScreenY());
                tp.setAutoHide(true);
            }
        });
        node.setOnMouseExited(event -> this.label.setText(""));
    }

    public Element getElement() {
        return this.element;
    }
}

