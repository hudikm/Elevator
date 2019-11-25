/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  javafx.application.Application
 *  javafx.scene.Parent
 *  javafx.scene.Scene
 *  javafx.scene.paint.Color
 *  javafx.scene.paint.Paint
 *  javafx.stage.Stage
 */
package sk.fri.ktk;

import java.util.List;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import sk.fri.ktk.elevator.Element;
import sk.fri.ktk.elevator.GUI.Browser;

public class HelpApp
extends Application {
    private Scene scene;
    List<Element> elementList;

    public static void main(String[] args) {
        HelpApp.launch((String[])args);
    }

    public HelpApp(List<Element> elementList) {
        this.elementList = elementList;
    }

    public void start(Stage stage) {
        stage.setTitle("Help");
        this.scene = new Scene((Parent)new Browser(this.elementList), 750.0, 500.0, (Paint)Color.web((String)"#666970"));
        stage.setScene(this.scene);
        stage.show();
    }
}

