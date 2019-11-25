/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  javafx.application.Platform
 *  javafx.scene.Node
 *  javafx.scene.Parent
 *  javafx.scene.paint.Color
 *  javafx.scene.paint.Paint
 *  javafx.scene.shape.Circle
 */
package sk.fri.ktk.elevator.GUI;

import java.util.List;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import sk.fri.ktk.elevator.Element;
import sk.fri.ktk.elevator.GUI.UI;
import sk.fri.ktk.elevator.Led;

public class LedUi
extends UI
implements Element.UI {
    private Circle circle;
    private Led led;

    public LedUi(Led led, Circle node, List<Element> elementList) {
        super(led, node.getParent(), (Node)node, elementList);
        this.circle = node;
        this.led = led;
        led.setUi(this);
    }

    @Override
    public void updateUI(Element element) {
        Platform.runLater(() -> {
            if (((Led)element).getValue().booleanValue()) {
                this.circle.setFill((Paint)Color.RED);
            } else {
                this.circle.setFill((Paint)Color.WHITE);
            }
        });
    }
}

