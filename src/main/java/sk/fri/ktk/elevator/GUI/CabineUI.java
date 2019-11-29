/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  javafx.application.Platform
 *  javafx.scene.Group
 *  javafx.scene.Node
 *  javafx.scene.Parent
 *  javafx.scene.image.Image
 *  javafx.scene.image.ImageView
 */
package sk.fri.ktk.elevator.GUI;

import java.util.List;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import sk.fri.ktk.Singleton;
import sk.fri.ktk.elevator.Cabin;
import sk.fri.ktk.elevator.Element;

public class CabineUI extends UI implements Element.UI {
    private static final int max = 0;
    private static final int min = 380;
    private final ImageView lock;
    private final ImageView doorL;
    private final ImageView doorR;
    private final Timeline timelineCloseDoorL;
    private final Timeline timelineOpenDoorL;
    private final Timeline timelineCloseDoorR;
    private final Timeline timelineOpenDoorR;

    private Group group;
    private Cabin cabin;
    private Label positionLbl;
    Image lockImg = new Image(this.getClass().getResourceAsStream("/sk/fri/ktk/images/lock.png"));
    Image unlockImg = new Image(this.getClass().getResourceAsStream("/sk/fri/ktk/images/unlock.png"));

    public CabineUI(Group group, Cabin cabin, List<Element> elementList) {
        super(cabin, group.getParent(), (Node) group, elementList);
        this.group = group;
        this.cabin = cabin;
        this.lock = (ImageView) group.lookup("#liftLock");
        doorL = (ImageView) group.lookup("#liftDoorL");
        doorR = (ImageView) group.lookup("#liftDoorR");

        timelineCloseDoorL = new Timeline();
        timelineCloseDoorL.setCycleCount(1);
        timelineCloseDoorL.setAutoReverse(false);
        final KeyValue kv = new KeyValue(doorL.xProperty(), 0);
        final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        timelineCloseDoorL.getKeyFrames().add(kf);

        timelineOpenDoorL = new Timeline();
        timelineOpenDoorL.setCycleCount(1);
        timelineOpenDoorL.setAutoReverse(false);
        final KeyValue kv2 = new KeyValue(doorL.xProperty(), -20);
        final KeyFrame kf2 = new KeyFrame(Duration.millis(500), kv2);
        timelineOpenDoorL.getKeyFrames().add(kf2);

        timelineCloseDoorR = new Timeline();
        timelineCloseDoorR.setCycleCount(1);
        timelineCloseDoorR.setAutoReverse(false);
        final KeyValue kv3 = new KeyValue(doorR.xProperty(), 0);
        final KeyFrame kf3 = new KeyFrame(Duration.millis(500), kv3);
        timelineCloseDoorR.getKeyFrames().add(kf3);


        timelineOpenDoorR = new Timeline();
        timelineOpenDoorR.setCycleCount(1);
        timelineOpenDoorR.setAutoReverse(false);
        final KeyValue kv4 = new KeyValue(doorR.xProperty(), 20);
        final KeyFrame kf4 = new KeyFrame(Duration.millis(500), kv4);
        timelineOpenDoorR.getKeyFrames().add(kf4);
        this.positionLbl = (Label) group.getParent().lookup("#cabinPosLbl");
        this.updateUI(null);
    }

    @Override
    public void updateUI(Element element) {

        Platform.runLater(() -> {


            if (cabin.getIsDoorLocked() == 1) {
                this.lock.setImage(this.lockImg);

                timelineCloseDoorL.play();
                timelineCloseDoorR.play();
                timelineCloseDoorL.setOnFinished(event -> {
                    cabin.setDoorClosed(true);
                    Singleton.logElevator.fine("Cabin door closed!");
                });
            } else {
                this.lock.setImage(this.unlockImg);

                timelineOpenDoorL.play();
                timelineOpenDoorR.play();
                timelineOpenDoorL.setOnFinished(event -> {
                    cabin.setDoorClosed(false);
                    Singleton.logElevator.fine("Cabin door opened!");
                });
            }

            this.group.setLayoutY(380.0 + this.cabin.getPositionRelative() * -380.0);
            positionLbl.setText("Cabine position: " + String.valueOf(this.cabin.getPosition()));
        });
    }
}

