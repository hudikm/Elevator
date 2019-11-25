/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  javafx.animation.KeyFrame
 *  javafx.animation.KeyValue
 *  javafx.animation.Timeline
 *  javafx.beans.property.DoubleProperty
 *  javafx.beans.value.WritableValue
 *  javafx.collections.ObservableList
 *  javafx.event.ActionEvent
 *  javafx.event.Event
 *  javafx.event.EventHandler
 *  javafx.scene.Node
 *  javafx.scene.Parent
 *  javafx.scene.Scene
 *  javafx.scene.layout.StackPane
 *  javafx.scene.paint.Color
 *  javafx.scene.paint.Paint
 *  javafx.scene.text.Font
 *  javafx.scene.text.Text
 *  javafx.stage.Stage
 *  javafx.stage.StageStyle
 *  javafx.stage.Window
 *  javafx.util.Duration
 */
package sk.fri.ktk.elevator.GUI;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

public final class Toast {
    public static void makeText(Stage ownerStage, String toastMsg, int toastDelay, int fadeInDelay, int fadeOutDelay) {
        Stage toastStage = new Stage();
        toastStage.initOwner((Window)ownerStage);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);
        Text text = new Text(toastMsg);
        text.setFont(Font.font((String)"Verdana", (double)40.0));
        text.setFill((Paint)Color.RED);
        StackPane root = new StackPane(new Node[]{text});
        root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.2); -fx-padding: 50px;");
        root.setOpacity(0.0);
        Scene scene = new Scene((Parent)root);
        scene.setFill((Paint)Color.TRANSPARENT);
        toastStage.setScene(scene);
        toastStage.show();
        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis((double)fadeInDelay), new KeyValue[]{new KeyValue((WritableValue)toastStage.getScene().getRoot().opacityProperty(), (Object)1)});
        fadeInTimeline.getKeyFrames().add(fadeInKey1);
        fadeInTimeline.setOnFinished(ae -> new Thread(() -> {
            try {
                Thread.sleep(toastDelay);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            Timeline fadeOutTimeline = new Timeline();
            KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis((double)fadeOutDelay), new KeyValue[]{new KeyValue((WritableValue)toastStage.getScene().getRoot().opacityProperty(), (Object)0)});
            fadeOutTimeline.getKeyFrames().add(fadeOutKey1);
            fadeOutTimeline.setOnFinished(aeb -> toastStage.close());
            fadeOutTimeline.play();
        }).start());
        fadeInTimeline.play();
    }
}

