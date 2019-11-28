/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  javafx.collections.ObservableList
 *  javafx.geometry.HPos
 *  javafx.geometry.VPos
 *  javafx.scene.Node
 *  javafx.scene.layout.HBox
 *  javafx.scene.layout.Priority
 *  javafx.scene.layout.Region
 *  javafx.scene.web.WebEngine
 *  javafx.scene.web.WebView
 */
package sk.fri.ktk.elevator.GUI;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.sun.webkit.WebPage;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import sk.fri.ktk.Singleton;
import sk.fri.ktk.elevator.Element;

public class Browser
extends Region {
    final WebView browser = new WebView();
    final WebEngine webEngine = this.browser.getEngine();
    WebPage page;

    public void find(String text){
        page.find(text, true, true, false);
    }
    public Browser(List<Element> elementList) {
        try {
            Field pageField = webEngine.getClass().getDeclaredField("page");
            pageField.setAccessible(true);

            page = (WebPage) pageField.get(webEngine);

        } catch(Exception e) {
            Singleton.logSystem.severe(e.getMessage());
        }


        this.getStyleClass().add("browser");
        String helpContent = new String();
        elementList.sort((o1, o2) -> o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName()));
        for (Element element : elementList) {
            helpContent = helpContent.concat(element.getTooltipText() + "<hr>");
        }

        this.webEngine.loadContent(helpContent);
        this.getChildren().add(this.browser);
    }

    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow((Node)spacer, (Priority)Priority.ALWAYS);
        return spacer;
    }

    protected void layoutChildren() {
        double w = this.getWidth();
        double h = this.getHeight();
        this.layoutInArea((Node)this.browser, 0.0, 0.0, w, h, 0.0, HPos.CENTER, VPos.CENTER);
    }

    protected double computePrefWidth(double height) {
        return 750.0;
    }

    protected double computePrefHeight(double width) {
        return 500.0;
    }
}

