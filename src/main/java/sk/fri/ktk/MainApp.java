package sk.fri.ktk;

import com.athaydes.logfx.LogFX;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import sk.fri.ktk.elevator.*;
import sk.fri.ktk.elevator.GUI.*;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class MainApp extends Application {
    public static final String SYSLOG_TXT = "./sys.txt";
    public static final String ELEVATORLOG_TXT = "./elevator.txt";
    public static final String SERIALLOG_TXT = "./serial.txt";
    private Comunication comunication;
    private final String version = "0.3.5";
    private final String PORTREFRESH = "refresh ...";
    private Stage helpStage = null;
    private Stage sysLogStage = null;
    private Stage elevLogStage = null;
    private Stage serialLogStage = null;
    private MenuItem serial_speedCmd;
    private ObjectMapper mapper = new ObjectMapper(); // create once, reuse

    private static Logger sysLog = Singleton.logSystem;

    @Override
    public void stop() throws Exception {
        super.stop();
        this.comunication.closePort();
        Platform.exit();

    }

    public static void main(String[] args) throws Exception {

//        Platform.runLater(new Runnable() {
//            public void run() {
//                LogFX logFX = LogFX.getInstance();
//                Stage anotherStage = new Stage();
//                logFX.start(anotherStage);
//
//            }
//        });

//        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] [%1$tL] %5$s %n");
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT:%1$tL] [%4$-7s] %5$s %n");
        FileHandler fileHandlerSys = new FileHandler(SYSLOG_TXT, false);
        FileHandler fileHandlerElev = new FileHandler(ELEVATORLOG_TXT, false);
        FileHandler serialHandlerSerial = new FileHandler(SERIALLOG_TXT, false);

        SimpleFormatter formatter = new SimpleFormatter();

        fileHandlerElev.setFormatter(formatter);
        fileHandlerElev.setLevel(Level.FINE);
        fileHandlerSys.setFormatter(formatter);
        fileHandlerSys.setLevel(Level.FINE);
        serialHandlerSerial.setFormatter(formatter);

        Singleton.logSystem.addHandler(fileHandlerSys);
        Singleton.logElevator.addHandler(fileHandlerElev);
        Singleton.logElevator.setLevel(Level.FINE);


        Singleton.serialLog.setUseParentHandlers(false);
        Singleton.serialLog.addHandler(serialHandlerSerial);
        MainApp.launch((String[]) args);

    }
     private void loadSettings(){
         try {
             Settings settings = mapper.readValue(new File("settings.json"), Settings.class);
             Singleton.getInstance().setSettings(settings);
         } catch (IOException e) {
         }
     }

    public void start(Stage stage) throws Exception {
        loadSettings();
        EventBus eventBus = Singleton.getEventBus();
        eventBus.register((Object) this);
        ArrayList<Element> elementList = new ArrayList<Element>();
        this.comunication = new Comunication(eventBus);

        sysLog.info("Starting Elevator JavaFX application");
        final String fxmlFile = "fxml/lift.fxml";

        sysLog.info(MessageFormat.format("Loading FXML for main view from: {0}", fxmlFile));
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = (Parent) loader.load(this.getClass().getResourceAsStream(fxmlFile));
        sysLog.info("Showing JFX scene");
        Scene scene = new Scene(rootNode);
        String css = this.getClass().getResource("styles/styles.css").toExternalForm();
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("images/elevator.png")));

        scene.getStylesheets().add(css);
        stage.setTitle("Elevator ver: " + this.version);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> {
            Singleton.logSystem.info("Closing the program!");

            try {
                //Save Settings data to file
                mapper.writeValue(new File("settings.json"), Singleton.getInstance().getSettings());
            } catch (IOException e) {
                e.printStackTrace();
            }
            comunication.closePort();
            System.exit(0);
        });

        Platform.setImplicitExit(true);

        MenuBar menuBar = (MenuBar) rootNode.lookup("#MenuBar"); //new MenuBar();
        menuBar.getMenus().clear();
        Menu mainMenu = new Menu("Help");
        MenuItem helpCmd = new MenuItem("Show help");
        helpCmd.setAccelerator((KeyCombination) new KeyCodeCombination(KeyCode.F1, new KeyCombination.Modifier[0]));
        helpCmd.setOnAction(event -> {
            if (this.helpStage == null) {
                this.helpStage = new Stage();
            } else {
                this.helpStage.close();
            }
            new HelpApp(elementList).start(this.helpStage);
        });
        mainMenu.getItems().addAll(new MenuItem[]{helpCmd});

        Menu logMenu = new Menu("Logs");
        MenuItem sysLogsCmd = new MenuItem("Show System logs");
        MenuItem elevlogsCmd = new MenuItem("Show Elevator logs");
        MenuItem seriallogsCmd = new MenuItem("Show Serial Logs");
        sysLogsCmd.setOnAction(event -> {
            if (this.sysLogStage == null) {
                this.sysLogStage = new Stage();
            } else {
                this.sysLogStage.close();
            }
            final LogFX logFX = LogFX.getInstance(new File(SYSLOG_TXT));
            final Stage fsysLogStage = sysLogStage;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    logFX.start(fsysLogStage);
                }
            });

        });

        elevlogsCmd.setOnAction(event -> {
            if (this.elevLogStage == null) {
                this.elevLogStage = new Stage();
            } else {
                this.elevLogStage.close();
            }
            final LogFX logFX = LogFX.getInstance(new File(ELEVATORLOG_TXT));
            final Stage felevLogStage = elevLogStage;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    logFX.start(felevLogStage);
                }
            });

        });

        seriallogsCmd.setOnAction(event -> {
            if (this.serialLogStage == null) {
                this.serialLogStage = new Stage();
            } else {
                this.serialLogStage.close();
            }
            final LogFX logFX = LogFX.getInstance(new File(SERIALLOG_TXT));
            final Stage fSerialLogStage = serialLogStage;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    logFX.start(fSerialLogStage);
                }
            });

        });
        logMenu.getItems().addAll(List.of(sysLogsCmd, elevlogsCmd, seriallogsCmd));
        Menu serialMenu = new Menu("Serial");

        serial_speedCmd = new MenuItem("Serial Speed");
        serial_speedCmd.setOnAction(actionEvent -> {
            ChoiceDialog<Integer> dialog = new ChoiceDialog<>(Singleton.getInstance().settings.NEW_BAUD_RATE, Singleton.getSerialSpeedList());
            dialog.setTitle("Serial speed");
            dialog.setHeaderText("Set new serial speed");
            dialog.setContentText("Bit rate\n" +
                    "(Baud rate)");
            Optional<Integer> result = dialog.showAndWait();
            result.ifPresent(newSpeed -> Singleton.getInstance().settings.NEW_BAUD_RATE = newSpeed);
        });
        serialMenu.getItems().add(serial_speedCmd);
        menuBar.getMenus().addAll(new Menu[]{mainMenu, logMenu, serialMenu});

//        BorderPane borderPane = (BorderPane) rootNode.lookup("#BorderPane");
//        borderPane.setTop((Node) menuBar);


        Motor motor = new Motor(eventBus, null, 241);
        Group liftGrp = (Group) rootNode.lookup("#liftGrp");
        Cabin cabin = new Cabin(eventBus, null, 240);
        CabineUI cabineUI = new CabineUI(liftGrp, cabin, elementList);
        cabin.setUi(cabineUI);
        CheckBox watchDogChck = (CheckBox) rootNode.lookup("#watchDogChck");
        watchDogChck.setSelected(Singleton.getInstance().getSettings().watchDog);
        watchDogChck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Singleton.getInstance().getSettings().watchDog=newValue;
        });

        Label watchDogLbl = (Label) rootNode.lookup("#watchDogLbl");
        new WatchDogUI(new WatchDog(eventBus, null, 254), watchDogLbl, watchDogChck, elementList);
        final ComboBox<String> portName = (ComboBox) rootNode.lookup("#portCmb");
        Button openPort = (Button) rootNode.lookup("#openPortBtn");
        Button closePort = (Button) rootNode.lookup("#closePortBtn");

        portName.setOnAction(event -> {
            if (portName.getValue() != null && portName.getValue().equals(PORTREFRESH)) {
                List<String> ports = comunication.getPorts();

                if (!portName.getItems().isEmpty()) portName.getItems().clear();
                if (!ports.isEmpty())
                    portName.getItems().addAll(ports);
                portName.setValue(ports.stream().findFirst().orElse(""));
                portName.getItems().add(PORTREFRESH);
            }
        });
        portName.setValue(PORTREFRESH);

        openPort.setOnAction(event -> {
            if (this.comunication.openConnection((String) portName.getValue())) {
                serial_speedCmd.setDisable(true);
                Toast.makeText(stage, "Port " + (String) portName.getValue() + " is opened", 1000, 500, 500);
                openPort.setDisable(true);
                closePort.setDisable(false);
                portName.setDisable(true);
            } else {
                Toast.makeText(stage, "Port" + (String) portName.getValue() + " cannot be opened!", 1000, 500, 500);
            }
        });
        closePort.setOnAction(event -> {
            if (this.comunication.closePort()) {
                Toast.makeText(stage, "Port " + (String) portName.getValue() + " is closed", 1000, 500, 500);
                serial_speedCmd.setDisable(false);
                openPort.setDisable(false);
                closePort.setDisable(true);
                portName.setDisable(false);
            } else {
                Toast.makeText(stage, "Port" + portName.getPromptText() + " cannot be closed!", 1000, 500, 500);
            }
        });
        Button LiftBP = (Button) rootNode.lookup("#LiftBP");
        Button LiftB1 = (Button) rootNode.lookup("#LiftB1");
        Button LiftB2 = (Button) rootNode.lookup("#LiftB2");
        Button LiftB3 = (Button) rootNode.lookup("#LiftB3");
        Button LiftB4 = (Button) rootNode.lookup("#LiftB4");
        new ButtonUI(LiftBP, new ButtonOnFloor(eventBus, null, 176), elementList);
        new ButtonUI(LiftB1, new ButtonOnFloor(eventBus, null, 177), elementList);
        new ButtonUI(LiftB2, new ButtonOnFloor(eventBus, null, 178), elementList);
        new ButtonUI(LiftB3, new ButtonOnFloor(eventBus, null, 179), elementList);
        new ButtonUI(LiftB4, new ButtonOnFloor(eventBus, null, 180), elementList);
        Label speedLbl = (Label) rootNode.lookup("#speedLbl");
        ImageView motorIcon = (ImageView) rootNode.lookup("#motorIcon");
        MotorUI motorUI = new MotorUI(motor, motorIcon, speedLbl, elementList);
        motor.setUi(motorUI);
        Label emBreak = (Label) rootNode.lookup("#emergencyBreak");
        new EmergencyBreakUI(new EmergencyBreak(eventBus, null, 15), emBreak, elementList);
        Arc swp = (Arc) rootNode.lookup("#swp");
        Arc sw1 = (Arc) rootNode.lookup("#sw1");
        Arc sw2 = (Arc) rootNode.lookup("#sw2");
        Arc sw3 = (Arc) rootNode.lookup("#sw3");
        Arc sw4 = (Arc) rootNode.lookup("#sw4");
        Arc swBottom = (Arc) rootNode.lookup("#swBottom");
        Arc swTop = (Arc) rootNode.lookup("#swTop");
        Button BP = (Button) rootNode.lookup("#BP");
        Button B1 = (Button) rootNode.lookup("#B1");
        Button B2 = (Button) rootNode.lookup("#B2");
        Button B3 = (Button) rootNode.lookup("#B3");
        Button B4 = (Button) rootNode.lookup("#B4");
        new ButtonUI(BP, new ButtonOnFloor(eventBus, null, 192), elementList);
        new ButtonUI(B1, new ButtonOnFloor(eventBus, null, 193), elementList);
        new ButtonUI(B2, new ButtonOnFloor(eventBus, null, 194), elementList);
        new ButtonUI(B3, new ButtonOnFloor(eventBus, null, 195), elementList);
        new ButtonUI(B4, new ButtonOnFloor(eventBus, null, 196), elementList);
        Circle LP = (Circle) rootNode.lookup("#LP");
        Circle L1 = (Circle) rootNode.lookup("#L1");
        Circle L2 = (Circle) rootNode.lookup("#L2");
        Circle L3 = (Circle) rootNode.lookup("#L3");
        Circle L4 = (Circle) rootNode.lookup("#L4");
        new LedUi(new Led(eventBus, null, 16), LP, elementList);
        new LedUi(new Led(eventBus, null, 17), L1, elementList);
        new LedUi(new Led(eventBus, null, 18), L2, elementList);
        new LedUi(new Led(eventBus, null, 19), L3, elementList);
        new LedUi(new Led(eventBus, null, 20), L4, elementList);
        Circle LiftLP = (Circle) rootNode.lookup("#LiftLP");
        Circle LiftL1 = (Circle) rootNode.lookup("#LiftL1");
        Circle LiftL2 = (Circle) rootNode.lookup("#LiftL2");
        Circle LiftL3 = (Circle) rootNode.lookup("#LiftL3");
        Circle LiftL4 = (Circle) rootNode.lookup("#LiftL4");
        new LedUi(new Led(eventBus, null, 32), LiftLP, elementList);
        new LedUi(new Led(eventBus, null, 33), LiftL1, elementList);
        new LedUi(new Led(eventBus, null, 34), LiftL2, elementList);
        new LedUi(new Led(eventBus, null, 35), LiftL3, elementList);
        new LedUi(new Led(eventBus, null, 36), LiftL4, elementList);
        Label lcdLabel = (Label) rootNode.lookup("#LCD");
        new LcdUI(new LCD(eventBus, null, 48), lcdLabel, elementList);
        LimitSwitch limitSwitchP = new LimitSwitch(eventBus, null, 224, 0.050, false);
        LimitSwitch limitSwitch1 = new LimitSwitch(eventBus, null, 225, 0.275, false);
        LimitSwitch limitSwitch2 = new LimitSwitch(eventBus, null, 226, 0.500, false);
        LimitSwitch limitSwitch3 = new LimitSwitch(eventBus, null, 227, 0.725, false);
        LimitSwitch limitSwitch4 = new LimitSwitch(eventBus, null, 228, 0.95, false);
        LimitSwitch limitSwitchBottom = new LimitSwitch(eventBus, null, 223, 0, true);
        LimitSwitch limitSwitchTop = new LimitSwitch(eventBus, null, 229, 1, true);
        LimitSwitchUi limitSwitchUiP = new LimitSwitchUi(swp, limitSwitchP, elementList);
        LimitSwitchUi limitSwitchUi1 = new LimitSwitchUi(sw1, limitSwitch1, elementList);
        LimitSwitchUi limitSwitchUi2 = new LimitSwitchUi(sw2, limitSwitch2, elementList);
        LimitSwitchUi limitSwitchUi3 = new LimitSwitchUi(sw3, limitSwitch3, elementList);
        LimitSwitchUi limitSwitchUi4 = new LimitSwitchUi(sw4, limitSwitch4, elementList);
        LimitSwitchUi limitSwitchUiBottom = new LimitSwitchUi(swBottom, limitSwitchBottom, elementList);
        LimitSwitchUi limitSwitchUiTop = new LimitSwitchUi(swTop, limitSwitchTop, elementList);
        limitSwitchP.setUi(limitSwitchUiP);
        limitSwitch1.setUi(limitSwitchUi1);
        limitSwitch2.setUi(limitSwitchUi2);
        limitSwitch3.setUi(limitSwitchUi3);
        limitSwitch4.setUi(limitSwitchUi4);
        limitSwitchBottom.setUi(limitSwitchUiBottom);
        limitSwitchTop.setUi(limitSwitchUiTop);

        Slider sliderProp = (Slider) rootNode.lookup("#propSlider");
        CheckBox noiseCheckBox = (CheckBox) rootNode.lookup("#noiseChckBox");
        TextField textArea = (TextField) rootNode.lookup("#intFiled");
        TextArea terminal = (TextArea) rootNode.lookup("#terminal");
        ComboBox comboBox = (ComboBox) rootNode.lookup("#sendText");
        Button sendBtn = (Button) rootNode.lookup("#sendBtn");
        new TerminalUI(new Terminal(eventBus, null, 208), terminal, comboBox, sendBtn, elementList);
        textArea.setText(String.valueOf(Singleton.getInstance().settings.propabilityRate));
        sliderProp.setValue(Singleton.getInstance().settings.propabilityRate);
        noiseCheckBox.setSelected(Singleton.getInstance().settings.isLINK_NOISE);
        sliderProp.valueProperty().addListener((observable, oldValue, newValue) -> {
            Singleton.getInstance().settings.propabilityRate = (Double) newValue;
            textArea.setText(newValue.toString());
        });
        noiseCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Singleton.getInstance().settings.isLINK_NOISE = newValue;
        });
        // Only updates UI
        motor.getCurrentState().setMaxSpeed(0);
    }
}

