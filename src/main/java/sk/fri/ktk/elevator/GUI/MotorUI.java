/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  javafx.scene.Node
 *  javafx.scene.Parent
 *  javafx.scene.control.Label
 *  javafx.scene.image.ImageView
 */
package sk.fri.ktk.elevator.GUI;

import java.util.List;

import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import sk.fri.ktk.elevator.Element;
import sk.fri.ktk.elevator.GUI.UI;
import sk.fri.ktk.elevator.Motor;

public class MotorUI
        extends UI
        implements Element.UI {
    private Motor motor;
    private ImageView motorIcon;
    private Label speedLbl;
    private RotateTransition rotateTransition;

    public MotorUI(Motor motor, ImageView motorIcon, Label speedLbl, List<Element> elementList) {
        super(motor, motorIcon.getParent(), (Node) speedLbl, elementList);
        this.motor = motor;
        this.motorIcon = motorIcon;
        this.speedLbl = speedLbl;
        rotateTransition = animation(motorIcon, 400, 360);
        this.updateUI(null);
    }

    private RotateTransition animation(Node node, int speed, int angle) {
        //Creating a rotate transition
        RotateTransition rotateTransition = new RotateTransition();

        //Setting the duration for the transition
        rotateTransition.setDuration(Duration.millis(speed));

        //Setting the node for the transition
        rotateTransition.setNode(node);

        //Setting the angle of the rotation
        rotateTransition.setByAngle(angle);

        //Setting the cycle count for the transition
        rotateTransition.setCycleCount(Timeline.INDEFINITE);

        //Setting auto reverse value to false
        rotateTransition.setAutoReverse(false);

        return rotateTransition;

    }

    @Override
    public void updateUI(Element element) {
        this.speedLbl.setText(String.valueOf(this.motor.getCurrentState().getCurrentSpeed()));
        if (this.motor.getCurrentState().getCurrentSpeed() == 0.0) {
            this.motorIcon.setVisible(false);
            rotateTransition.stop();
        } else {
            this.motorIcon.setVisible(true);
            rotateTransition.stop();
            rotateTransition.setDuration(Duration.millis((150 - Math.abs(motor.getCurrentState().getCurrentSpeed())) * 4));


            if (this.motor.getCurrentState().getCurrentSpeed() > 0.0) {
                this.motorIcon.setScaleX(-1.0);
                rotateTransition.setByAngle(360);

            } else {
                this.motorIcon.setScaleX(1.0);
                rotateTransition.setByAngle(-360);

            }
            rotateTransition.play();
        }
    }
}

