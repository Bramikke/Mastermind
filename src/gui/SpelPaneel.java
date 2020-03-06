package gui;

import domein.DomeinController;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SpelPaneel extends StackPane {

    private final Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE, Color.BLACK, Color.LIGHTGRAY};
    private final DomeinController controller;
    private final HoofdPaneel hoofdPaneel;
    private final ResourceBundle r;
    private final GridPane gp = new GridPane();
    private final VBox vbxSpace = new VBox();
    private final Region rg = new Region();
    private final FadeTransition fadeTransition = new FadeTransition();
    private final Button btnCheck = new Button(), btnOpnieuw = new Button();
    private final Image cross = new Image(getClass().getResource("/gui/images/Cross.png").toExternalForm());
    private final Stage stage2 = new Stage();
    private boolean endGame, isLoadedGame = false;
    private int idSelector, level, UitdagerID;
    private String titel;
    private Object vorigeCircle;
    private final HBox headerWon = new HBox(), hbxButtons = new HBox();
    private final Label lblHeader = new Label(), lblStars = new Label(), lblWinnerText = new Label(), lblMoreWins = new Label();
    private final GridPane gpWon = new GridPane();
    private final BorderPane bpWon = new BorderPane();
    private final Region rgButtons = new Region();
    private boolean isLoadedBefore = false, isLoadedBeforeExit = false, isChallenge = false, isLoadedBeforeInfo = false, isFireWorkMade = false;
    private final Alert alertExit = new Alert(AlertType.CONFIRMATION);
    private ButtonType btnSave, btnMenu, btnCancel;
    private List<String[]> namen;
    private TextInputDialog dialog;
    private StackPane stack;
    private final Label lblLevel = new Label(), lblText = new Label(), lblPoging = new Label(), lblEvaluatie = new Label(), lblCode = new Label();
    private final VBox vbxExample = new VBox();
    private final HBox[] hbxExamples = new HBox[2], hbxCircles = new HBox[3];
    private Timeline[] timeline = new Timeline[9];

    public SpelPaneel(DomeinController controller, HoofdPaneel hoofdPaneel) {
        this.controller = controller;
        this.hoofdPaneel = hoofdPaneel;
        this.r = controller.getLanguage();
        this.level = 0;
    }

    public void laadSpel(List<List<Integer>> zetten, List<List<Integer>> correcties) {
        isLoadedGame = true;
        Circle pogingCircle;
        int i = 1;
        int row = 0;
        if (zetten != null && correcties != null) {
            for (List<Integer> zet : zetten) {
                row++;
                for (int getal : zet) {
                    pogingCircle = (Circle) gp.lookup("#" + i);
                    if(getal<8){
                    pogingCircle.setFill(colors[getal]);
                    }
                    if (row > 1) {
                        pogingCircle.setOpacity(1.01);
                    }
                    i++;
                }
            }
        }
        ActionEvent event = new ActionEvent();
        for (int j = 0; j < row; j++) {
            ButtonClicked(event);
        }
    }

    public void setIsChallenge(boolean isChallenge) {
        this.isChallenge = isChallenge;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public void setUitdagerID(int UitdagerID) {
        this.UitdagerID = UitdagerID;
    }

    private void configureerGrid() {
        gp.getColumnConstraints().clear();
        gp.getRowConstraints().clear();
        gp.setPadding(new Insets(20));
        for (int i = 0; i <= (level < 3 ? 10 : 11); i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.SOMETIMES);
            gp.getColumnConstraints().add(col);
        }
        for (int i = 0; i < 12; i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.SOMETIMES);
            row.setMinHeight(10);
            gp.getRowConstraints().add(row);
        }
    }

    public void voegComponentenToe() {
        getChildren().clear();
        gp.getChildren().clear();

        VBox vbx1 = new VBox();
        setPadding(new Insets(10));
        level = controller.geefLevel();
        //INFO
        Circle circleInfo = new Circle();
        circleInfo.setPickOnBounds(true);
        circleInfo.setFill(Color.BLUE);
        circleInfo.setRadius(10);
        Text text = new Text("?");
        text.setId("question");
        text.setFill(Color.WHITE);
        text.setBoundsType(TextBoundsType.VISUAL);
        stack = new StackPane();
        stack.getChildren().addAll(circleInfo, text);
        Tooltip t = new Tooltip(r.getString("ToolTipInfo"));
        Tooltip.install(stack, t);
        stack.setOnMouseClicked(e -> {
            Color[] allColors;
            switch (level) {
                case 1:
                    lblLevel.setText(r.getString("Easy") + ":");
                    lblText.setText(r.getString("InfoEasy"));
                    allColors = new Color[]{Color.GREEN, Color.YELLOW, Color.BLUE, Color.ORANGE, Color.BLACK, Color.WHITE, Color.BLACK, Color.GRAY, Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW};
                    break;
                case 2:
                    lblLevel.setText(r.getString("Normal") + ":");
                    lblText.setText(r.getString("InfoNormal"));
                    allColors = new Color[]{Color.GREEN, Color.GREEN, Color.BLUE, Color.ORANGE, Color.BLACK, Color.BLACK, Color.WHITE, Color.GRAY, Color.GREEN, Color.RED, Color.BLUE, Color.GREEN};
                    break;
                default:
                    lblLevel.setText(r.getString("Difficult") + ":");
                    lblText.setText(r.getString("InfoHard"));
                    allColors = new Color[]{Color.GREEN, Color.YELLOW, Color.BLUE, Color.TRANSPARENT, Color.ORANGE, Color.BLACK, Color.WHITE, Color.WHITE, Color.WHITE, Color.GRAY, Color.YELLOW, Color.RED, Color.TRANSPARENT, Color.BLUE, Color.ORANGE};
                    break;
            }
            if (!isLoadedBeforeInfo) {
                lblLevel.setStyle("-fx-font-size: 1.5em; -fx-font-weight: bold;");
                lblText.setWrapText(true);
                lblText.setTextAlignment(TextAlignment.JUSTIFY);
                lblPoging.setStyle("-fx-background-image: url(\"/gui/images/ExamplesBackground.png\"); -fx-background-size: stretch;");
                lblEvaluatie.setStyle("-fx-background-image: url(\"/gui/images/ExamplesBackground.png\"); -fx-background-size: stretch;");
                lblCode.setStyle("-fx-background-image: url(\"/gui/images/ExamplesBackground.png\"); -fx-background-size: stretch;");
                lblPoging.setAlignment(Pos.CENTER);
                lblEvaluatie.setAlignment(Pos.CENTER);
                lblCode.setAlignment(Pos.CENTER);
                lblPoging.setPrefSize(75, 30);
                lblEvaluatie.setPrefSize(75, 30);
                lblCode.setPrefSize(75, 30);
                lblPoging.setText(r.getString("Attempt"));
                lblEvaluatie.setText(r.getString("Evaluation"));
                lblCode.setText("Code");
                MakeExample(allColors);
                isLoadedBeforeInfo = true;
            }
            Stage stageInfo = new Stage();
            VBox vbxInfo = new VBox();
            vbxInfo.setPadding(new Insets(10));
            vbxInfo.setSpacing(10);
            vbxInfo.getChildren().addAll(lblLevel, lblText, vbxExample);
            Scene sceneInfo = new Scene(vbxInfo, 350, USE_COMPUTED_SIZE);
            stageInfo.setScene(sceneInfo);
            stageInfo.initModality(Modality.APPLICATION_MODAL);
            stageInfo.initOwner(this.getScene().getWindow());
            stageInfo.showAndWait();
        });
        hoofdPaneel.AddInfo(stack);
        //LEFT SIDE
        configureerGrid(); //grid
        idSelector = 1;
        // <editor-fold desc="make all circles (12 rows, 4 columns">
        for (int i = 0; i <= 11; i++) {
            for (int j = 1; j <= (level < 3 ? 4 : 5); j++) {
                Circle circle = new Circle();
                circle.setPickOnBounds(true);
                circle.setRadius(10);
                circle.setFill(Color.TRANSPARENT);
                circle.setStroke(Color.BLACK);
                circle.setId("" + idSelector++);
                if (i > 0) {
                    circle.setOpacity(0.5);
                } else {
                    circle.setStyle("-fx-cursor: hand");
                }
                //fill circle with selected color
                circle.setOnMouseClicked(e -> {
                    if (((Circle) e.getSource()).getOpacity() == 1) {
                        if (vorigeCircle != null) {
                            ((Circle) e.getSource()).setFill(((Circle) vorigeCircle).getFill());
                        } else {
                            ((Circle) e.getSource()).setFill(Color.TRANSPARENT);
                        }
                    }
                });
                gp.add(circle, j, i);
            }
        }
        idSelector = 1;
        // </editor-fold>
        //add button to screen
        btnCheck.setVisible(true);
        btnCheck.setText(r.getString("Check"));
        btnCheck.setDefaultButton(true);
        btnCheck.setMaxWidth(Double.MAX_VALUE);
        btnCheck.setId("btn0");
        //button CLICK
        btnCheck.setOnAction(this::ButtonClicked);
        gp.add(btnCheck, 7, 0, (level < 3 ? 4 : 5), 1);
        //RIGHT SIDE
        fadeTransition.setDuration(Duration.seconds(0.2));
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        for (int i = 0; i < 8; i++) {
            Circle circle = new Circle();
            circle.setPickOnBounds(true);
            circle.setFill(colors[i]);
            circle.setRadius(10);
            circle.setStroke(Color.BLACK);
            circle.setId("Circle" + (i + 1));
            circle.setStyle("-fx-cursor: hand");
            // <editor-fold desc="flicker color select button">
            circle.setOnMouseClicked(e -> {
                if (vorigeCircle != e.getSource()) {
                    if (vorigeCircle != null) {
                        fadeTransition.stop();
                        ((Circle) vorigeCircle).setOpacity(1);
                    }
                    vorigeCircle = e.getSource();
                    fadeTransition.setNode((Circle) vorigeCircle);
                    fadeTransition.play();
                } else {
                    fadeTransition.stop();
                    ((Circle) vorigeCircle).setOpacity(1);
                    vorigeCircle = null;
                }
            });
            // </editor-fold>
            vbx1.getChildren().add(circle);
        }

        //ALL
        rg.setStyle("-fx-border-color: black;"
                + "-fx-border-width: 0.5;"
                + "-fx-border-insets: 10,0, 10, 0;");
        rg.setMinWidth(1);
        StackPane SPAll = new StackPane();
        HBox hbxAll = new HBox();
        hbxAll.setSpacing(10);
        hbxAll.getChildren().addAll(gp, rg, vbx1);
        getChildren().addAll(hbxAll);
        vbx1.setMinWidth(100);
        vbx1.setMaxWidth(100);
        vbx1.setSpacing(20);
        vbx1.setAlignment(Pos.CENTER);
        hbxAll.setHgrow(vbx1, Priority.NEVER);
        hbxAll.setHgrow(gp, Priority.ALWAYS);

        addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.TAB && !event.isShiftDown()) {
                    if (vorigeCircle != null) {
                        fadeTransition.stop();
                        ((Circle) vorigeCircle).setOpacity(1);
                        int newId = Integer.parseInt(((Circle) vorigeCircle).getId().substring(6)) + 1;
                        if (newId > 8) {
                            vorigeCircle = vbx1.lookup("#Circle1");
                        } else {
                            vorigeCircle = vbx1.lookup("#Circle" + newId);
                        }
                    } else {
                        vorigeCircle = vbx1.lookup("#Circle1");
                    }
                    fadeTransition.setNode((Circle) vorigeCircle);
                    fadeTransition.play();
                } else if (event.getCode() == KeyCode.TAB && event.isShiftDown()) {
                    if (vorigeCircle != null) {
                        fadeTransition.stop();
                        ((Circle) vorigeCircle).setOpacity(1);
                        int newId = Integer.parseInt(((Circle) vorigeCircle).getId().substring(6)) - 1;
                        if (newId < 1) {
                            vorigeCircle = vbx1.lookup("#Circle8");
                        } else {
                            vorigeCircle = vbx1.lookup("#Circle" + newId);
                        }
                    } else {
                        vorigeCircle = vbx1.lookup("#Circle8");
                    }
                    fadeTransition.setNode((Circle) vorigeCircle);
                    fadeTransition.play();
                }
            }
        });
        (getScene().getWindow()).setOnCloseRequest(e -> {
            if ("gui.SpelPaneel".equals(((StackPane) hoofdPaneel.getCenter()).getChildren().get(0).getClass().getCanonicalName())) {
                btnMenu = new ButtonType(r.getString("MainMenu"));
                btnCancel = new ButtonType(r.getString("Cancel"), ButtonData.CANCEL_CLOSE);
                alertExit.setHeaderText(null);
                if (!isLoadedGame && !isChallenge) {
                    alertExit.setTitle(r.getString("MainMenu") + " " + r.getString("Or") + " " + r.getString("Save"));
                    alertExit.setContentText(r.getString("MenuSave"));
                    btnSave = new ButtonType(r.getString("Save"));
                    alertExit.getButtonTypes().setAll(btnSave, btnMenu, btnCancel);
                } else {
                    alertExit.setTitle(r.getString("MainMenu"));
                    alertExit.setContentText(r.getString("MenuAutoSave"));
                    alertExit.getButtonTypes().setAll(btnMenu, btnCancel);
                }
                if (alertExit.getOwner() == null) {
                    alertExit.initOwner(this.getScene().getWindow());
                }
                Optional<ButtonType> resultExit = alertExit.showAndWait();
                if (resultExit.get() == btnSave) {
                    namen = controller.geefOpgeslagenSpellenNamen();
                    dialog = new TextInputDialog();
                    dialog.setTitle(r.getString("NewTitle"));
                    dialog.setHeaderText(null);
                    dialog.setContentText(r.getString("EnterName"));
                    Optional<String> resultSave = dialog.showAndWait();
                    resultSave.ifPresent(this::SaveGame);
                    e.consume();
                } else if (resultExit.get() == btnMenu) {
                    hoofdPaneel.RemoveInfo(stack);
                    hoofdPaneel.spelerIsAangemeld();
                    if (isFireWorkMade) {
                        for (int i = 0; i < timeline.length; i++) {
                            timeline[i].stop();
                        }
                    }
                    e.consume();
                } else {
                    e.consume();
                }
            }
        });
    }

    private void MakeExample(Color allColors[]) {
        int teller = 0;
        for (int i = 0; i < 3; i++) {
            hbxCircles[i] = new HBox();
            hbxCircles[i].setSpacing(5);
            hbxCircles[i].setAlignment(Pos.CENTER);
            for (int j = 0; j < (level == 3 ? 5 : 4); j++) {
                Circle circle = new Circle();
                circle.setRadius(5);
                if (allColors[teller] == Color.GRAY) {
                    circle.setFill(new ImagePattern(cross));
                } else {
                    circle.setFill(allColors[teller]);
                }
                circle.setStroke(Color.BLACK);
                hbxCircles[i].getChildren().add(circle);
                teller++;
            }
        }
        hbxExamples[0] = new HBox();
        hbxExamples[0].setSpacing(5);
        hbxExamples[0].getChildren().addAll(lblPoging, hbxCircles[0], lblEvaluatie, hbxCircles[1]);
        hbxExamples[1] = new HBox();
        hbxExamples[1].setSpacing(5);
        hbxExamples[1].getChildren().addAll(lblCode, hbxCircles[2]);
        vbxExample.getChildren().addAll(hbxExamples[0], hbxExamples[1]);
    }

    private void SaveGame(String name) {
        boolean titelBestaat = false;
        for (String[] naam : namen) {
            if (naam[1].equals(name)) {
                titelBestaat = true;
            }
        }
        if (titelBestaat) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(r.getString("ErrTitleExists"));
            alert.setHeaderText(null);
            alert.setContentText(r.getString("ErrSaveGameExists"));
            alert.showAndWait();
            Optional<String> resultSave = dialog.showAndWait();
            resultSave.ifPresent(this::SaveGame);

        } else {
            controller.spelOpslaan(name);
            hoofdPaneel.spelerIsAangemeld();
            hoofdPaneel.RemoveInfo(stack);
        }
    }

    private void ButtonClicked(ActionEvent event) {
        int row = Integer.parseInt(btnCheck.getId().substring(3)) + 1;
        int[] poging = new int[(level < 3 ? 4 : 5)];
        int idSelectorBegin = idSelector;
        int idSelectorEnd = idSelector + (level < 3 ? 4 : 5);
        for (int i = idSelectorBegin; i < idSelectorEnd; i++) {
            Circle pogingCircle = (Circle) gp.lookup("#" + i);
            switch (pogingCircle.getFill().toString()) {
                case "0xff0000ff":
                    poging[i - idSelectorBegin] = 0;
                    break;
                case "0xffa500ff":
                    poging[i - idSelectorBegin] = 1;
                    break;
                case "0xffff00ff":
                    poging[i - idSelectorBegin] = 2;
                    break;
                case "0x008000ff":
                    poging[i - idSelectorBegin] = 3;
                    break;
                case "0x0000ffff":
                    poging[i - idSelectorBegin] = 4;
                    break;
                case "0x800080ff":
                    poging[i - idSelectorBegin] = 5;
                    break;
                case "0x000000ff":
                    poging[i - idSelectorBegin] = 6;
                    break;
                case "0xd3d3d3ff":
                    poging[i - idSelectorBegin] = 7;
                    break;
                case "0x00000000":
                    if (level < 3) {
                        return;
                    }
                    poging[i - idSelectorBegin] = 8;
                    break;
            }
        }

        gp.getChildren().remove(btnCheck);
        if (row < 12) {
            btnCheck.setId("btn" + row);
            gp.add(btnCheck, 7, row, (level < 3 ? 4 : 5), 1);
        }

        int[] ControleArray = controller.controleerPoging(poging, row);
        endGame = true;
        for (int i = 7; i <= (level < 3 ? 10 : 11); i++) {
            ((Circle) gp.lookup("#" + (idSelector))).setOpacity(1.01);
            ((Circle) gp.lookup("#" + (idSelector))).setStyle("-fx-cursor:default;");
            if (row < 12) {
                ((Circle) gp.lookup("#" + (idSelector + (level < 3 ? 4 : 5)))).setOpacity(1);
                ((Circle) gp.lookup("#" + (idSelector + (level < 3 ? 4 : 5)))).setStyle("-fx-cursor:hand;");
                idSelector++;
            }
            Circle controle = new Circle();
            switch (ControleArray[i - 7]) {
                case 1:
                    controle.setFill(Color.BLACK);
                    break;
                case 2:
                    controle.setFill(Color.WHITE);
                    break;
                case 3:
                    controle.setFill(new ImagePattern(cross));
                    break;
            }
            controle.setRadius(10);
            controle.setStroke(Color.BLACK);
            gp.add(controle, i, row - 1);
            if (ControleArray[i - 7] != 1) {
                endGame = false;
            }
        }
        new Thread(() -> {
            if (isLoadedGame || (isLoadedGame && isChallenge)) {
                controller.overschrijfSpel();
            } else if (isChallenge && !isLoadedGame) {
                controller.overschrijfChallenge(titel);
            }
            if (endGame) {
                if (isLoadedGame && !isChallenge) {
                    controller.verwijderOpgeslagenSpel();
                } else if (isChallenge) {
                    controller.eindeChallenge(UitdagerID, titel, level, 1);
                }
            } else if (row >= 12) {
                if (isLoadedGame && !isChallenge) {
                    controller.verwijderOpgeslagenSpel();
                } else if (isChallenge) {
                    controller.eindeChallenge(UitdagerID, titel, level, 0);
                }
            }
        }).start();
        if (endGame || row >= 12) {
            EndGame();
        }
    }

    private void EndGame() {
        //static
        if (!isLoadedBefore) {
            lblHeader.setFont(new Font(48));
            headerWon.getChildren().add(lblHeader);
            headerWon.setAlignment(Pos.CENTER);
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setHalignment(HPos.CENTER);
            col1.setFillWidth(true);
            col1.setHgrow(Priority.ALWAYS);
            gpWon.getColumnConstraints().add(col1);
            for (int i = 1; i <= 5; i++) {
                RowConstraints row1 = new RowConstraints();
                row1.setValignment(VPos.CENTER);
                gpWon.getRowConstraints().add(row1);
            }
            lblStars.setFont(new Font(27));
            lblStars.setTextFill(new Color(1, 0.87, 0, 1));
            lblStars.setAlignment(Pos.CENTER);
            lblWinnerText.setAlignment(Pos.CENTER);
            lblMoreWins.setAlignment(Pos.CENTER);
            HBox.setHgrow(rgButtons, Priority.ALWAYS);
            btnOpnieuw.setText(r.getString("MainMenu"));
            btnOpnieuw.setOnAction(e -> {
                hoofdPaneel.spelerIsAangemeld();
                hoofdPaneel.RemoveInfo(stack);
                stage2.close();
                if (isFireWorkMade) {
                    for (int i = 0; i < timeline.length; i++) {
                        timeline[i].stop();
                    }
                }
            });
            btnOpnieuw.setDefaultButton(true);
            hbxButtons.getChildren().addAll(rgButtons, btnOpnieuw);
            gpWon.setAlignment(Pos.CENTER);
            gpWon.setPadding(new Insets(10));
            gpWon.add(lblStars, 0, 0);
            gpWon.add(lblWinnerText, 0, 1);
            gpWon.add(lblMoreWins, 0, 2);
            gpWon.add(vbxSpace, 0, 3);
            gpWon.add(hbxButtons, 0, 4);
            GridPane.setVgrow(vbxSpace, Priority.ALWAYS);
            bpWon.setTop(headerWon);
            bpWon.setCenter(gpWon);
            Scene scene2 = new Scene(bpWon, 300, 200);
            stage2.setScene(scene2);
            stage2.initModality(Modality.APPLICATION_MODAL);
            stage2.initOwner(this.getScene().getWindow());
            isLoadedBefore = true;
        }

        //changeable
        if (endGame) {
            btnCheck.setVisible(false);
            for (int i = 0; i < 9; i++) {
                Image image = new Image("/gui/images/firework.gif", 200, 200, false, false);
                ImageView imgFirework = new ImageView(image);
                SecureRandom _rand = new SecureRandom();
                timeline[i] = new Timeline(new KeyFrame(
                        Duration.millis(3680),
                        ae -> {
                            double width = getWidth();
                            double height = getHeight();
                            imgFirework.setTranslateX(_rand.nextInt((int) width) - width / 2);
                            imgFirework.setTranslateY(_rand.nextInt((int) height) - height / 2);
                        }));
                timeline[i].setCycleCount(Animation.INDEFINITE);
                timeline[i].play();
                getChildren().add(imgFirework);
            }
            isFireWorkMade = true;
            controller.spelGewonnen(1);
            lblHeader.setText(r.getString("Won"));
            lblWinnerText.setText(String.format(r.getString("WonGameOutput"), r.getString(level == 1 ? "Easy" : level == 2 ? "Normal" : "Difficult")));
        } else {
            controller.spelGewonnen(0);
            lblHeader.setText(r.getString("LostGame"));
            lblWinnerText.setText(r.getString("Lost"));
        }
        if (vorigeCircle != null) {
            fadeTransition.stop();
            ((Circle) vorigeCircle).setOpacity(1);
            vorigeCircle = null;
        }
        lblStars.setText(controller.aantalStars());
        if (20 - controller.aantalWins() + 1 > 0) {
            lblMoreWins.setText(String.format(r.getString("WonMoreGamesOutput"), 20 - controller.aantalWins() + 1, r.getString(level == 1 ? "Normal" : "Difficult")));
        }
        if (isLoadedGame && !isChallenge) {
            controller.verwijderOpgeslagenSpel();
        }
        stage2.setOnCloseRequest(e -> e.consume());
        stage2.showAndWait();
    }
}
