/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  javafx.application.Platform
 *  javafx.scene.Node
 *  javafx.scene.Parent
 *  javafx.scene.paint.Color
 *  javafx.scene.paint.Paint
 *  javafx.scene.shape.Arc
 */
package sk.fri.ktk.elevator.GUI;

import java.util.List;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import sk.fri.ktk.elevator.Element;
import sk.fri.ktk.elevator.GUI.UI;
import sk.fri.ktk.elevator.LimitSwitch;

public class LimitSwitchUi
extends UI
implements Element.UI {
    private Arc arc;
    private LimitSwitch limitSwitch;

    public LimitSwitchUi(Arc arc, LimitSwitch limitSwitch, List<Element> elementList) {
        super(limitSwitch, arc.getParent(), (Node)arc, elementList);
        this.arc = arc;
        this.limitSwitch = limitSwitch;
    }

    @Override
    public void updateUI(Element element) {
        LimitSwitch limitSwitch = (LimitSwitch)element;
        Platform.runLater(() -> {
            if (limitSwitch.state == 2) {
                this.arc.setFill((Paint)Color.RED);
            } else if (limitSwitch.state == 1) {
                this.arc.setFill((Paint)Color.ORANGE);
            } else {
                this.arc.setFill((Paint)Color.BLUE);
            }
        });
    }
}

