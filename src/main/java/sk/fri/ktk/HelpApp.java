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
import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
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
        HelpApp.launch((String[]) args);
    }

    private static String lastSearchText = "";

    public HelpApp(List<Element> elementList) {
        this.elementList = elementList;
    }

    public void start(Stage stage) {
        stage.setTitle("Help");
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("images/elevator.png")));
        Browser browser = new Browser(this.elementList);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(browser);

        MenuBar menuBar = new MenuBar();
        Menu mainMenu = new Menu("Find");
        MenuItem findCmd = new MenuItem("Find ...");
        MenuItem findNext = new MenuItem("Find next");
        findNext.setAccelerator((KeyCombination) new KeyCodeCombination(KeyCode.F3));

        findCmd.setAccelerator((KeyCombination) new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));

        findCmd.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(lastSearchText);
            dialog.setTitle("Search");
            //dialog.setHeaderText("Look, a Text Input Dialog");
            dialog.setContentText("Search text for:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> {
                lastSearchText = s;
                browser.find(s);
            });
        });

        findNext.setOnAction(event -> {
            if (!lastSearchText.isEmpty())
                browser.find(lastSearchText);
        });
        mainMenu.getItems().addAll(new MenuItem[]{findCmd, findNext});
        menuBar.getMenus().add(mainMenu);
        borderPane.setTop(menuBar);

        this.scene = new Scene(borderPane, 750.0, 500.0, (Paint) Color.web((String) "#666970"));

//        final KeyCombination keyCombinationSearch = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
//
//        scene.setOnKeyPressed(keyEvent -> {
//            System.out.println("KeyPressed");
//            if (keyCombinationSearch.match(keyEvent)) {
//                TextInputDialog dialog = new TextInputDialog(lastSearchText);
//                dialog.setTitle("Search");
//                //dialog.setHeaderText("Look, a Text Input Dialog");
//                dialog.setContentText("Search text for:");
//                Optional<String> result = dialog.showAndWait();
//                result.ifPresent(s -> {
//                    lastSearchText = s;
//                    browser.find(s);
//                });
//            } else if (keyEvent.getCode() == KeyCode.F3) {
//                if (!lastSearchText.isEmpty())
//                    browser.find(lastSearchText);
//            }
//        });
        stage.setScene(this.scene);
        stage.show();
    }
}

