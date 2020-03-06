package gui;

import domein.DomeinController;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class HoofdPaneel extends BorderPane {

    //variabelen
    private final DomeinController controller;
    private final RegistratiePaneel registratie;
    private final AanmeldPaneel aanmelden;
    private SettingsPaneel settings;
    private SpelPaneel spel;
    private SpelConfigPaneel spelConfig;
    private MenuButton Language, MBPlayer;
    private ResourceBundle r;
    private final HBox status = new HBox();
    private final Label lblstatus = new Label();
    private final HBox langBox = new HBox();
    private int spelerId, counter = 0;
    private ImageView img;
    private Background bg;
    private final StackPane sp = new StackPane();
    private Thread tb;

    public HoofdPaneel(DomeinController controller) { //header maken
        this.controller = controller;
        this.registratie = new RegistratiePaneel(controller, this);
        this.aanmelden = new AanmeldPaneel(controller, this);
        setCenter(sp);
        voegComponentenToe();
    }

    public void clearImages() {
        bg = null;
        img = null;
    }

    private void voegComponentenToe() { //elementen voor header toevoegen
        ImageView mastermind = new ImageView(getClass().getResource("/gui/images/Mastermind.png").toExternalForm());
        Text titel = new Text();
        titel.setId("titel");
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        //MENU TALEN
        ImageView nl = new ImageView("/gui/images/Nederlands.png");
        ImageView en = new ImageView("/gui/images/English.png");
        ImageView fr = new ImageView("/gui/images/Francais.png");
        MenuItem menuItemNL = new MenuItem("Nederlands", nl);
        MenuItem menuItemEN = new MenuItem("English", en);
        MenuItem menuItemFR = new MenuItem("Francais", fr);
        Language = new MenuButton();
        Language.setFocusTraversable(false);
        Language.setGraphic(new ImageView("/gui/images/Nederlands.png"));
        Language.getItems().addAll(menuItemNL, menuItemEN, menuItemFR);
        menuItemNL.setOnAction(e -> {
            Language.setGraphic(new ImageView("/gui/images/Nederlands.png"));
            controller.setLanguage("nl");
            teksten();
            aanmelden.teksten();
            registratie.teksten();
        });
        menuItemEN.setOnAction(e -> {
            Language.setGraphic(new ImageView("/gui/images/English.png"));
            controller.setLanguage("en");
            teksten();
            aanmelden.teksten();
            registratie.teksten();
        });
        menuItemFR.setOnAction(e -> {
            Language.setGraphic(new ImageView("/gui/images/Francais.png"));
            controller.setLanguage("fr");
            teksten();
            aanmelden.teksten();
            registratie.teksten();
        });
        langBox.getChildren().add(Language);
        langBox.setAlignment(Pos.BASELINE_RIGHT);
        HBox titelBox = new HBox(mastermind, titel, region, langBox);
        titelBox.setId("titelBox");
        titelBox.setSpacing(10);
        titelBox.setPadding(new Insets(10));
        titelBox.setMaxWidth(Double.MAX_VALUE);
        status.setId("status");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);
        status.setPadding(new Insets(10));
        lblstatus.setId("lblstatus");
        Region rgStatus = new Region();
        status.getChildren().addAll(lblstatus, rgStatus);
        HBox.setHgrow(rgStatus, Priority.ALWAYS);
        VBox hoofding = new VBox(titelBox, status);
        setTop(hoofding);
        sp.getChildren().clear();
        sp.getChildren().add(aanmelden);
        //setCenter(aanmelden);
        teksten();
    }

    public void AddInfo(StackPane info) { //INFO BUTTON
        status.getChildren().add(info);
    }

    public void RemoveInfo(StackPane info) {
        status.getChildren().remove(info);
    }

    public void teksten() { //status aanpassen op aanmeldscherm
        r = controller.getLanguage();
        lblstatus.setText(r.getString("Welcome"));
    }

    public void spelerIsAangemeld() { //SpelConfigPaneel tonen
        this.spelConfig = new SpelConfigPaneel(controller, this);
//        if(tb != null && tb.isAlive()){
//            tb.stop();
//        }
        if (bg == null) {
            setBackground();
        }
        settings = null;
        spelerId = controller.geefSpeler().getId();
        langBox.getChildren().clear();

        //MENU PLAYER (logout)
        MenuItem LogOut = new MenuItem(r.getString("LogOut"));
        MenuItem Settings = new MenuItem(r.getString("Settings"));
        LogOut.setOnAction(e -> {
            sp.setBackground(Background.EMPTY);
            toonAanmelden();
        });
        Settings.setOnAction(e -> {
            toonSettings();
        });
        if (img == null) {
            img = new ImageView("/gui/images/profile.png");
        }
        MBPlayer = new MenuButton(controller.geefSpelerDetails()[0], img, Settings, LogOut);
        MBPlayer.setFocusTraversable(false);
        langBox.getChildren().add(MBPlayer);

        new Thread(() -> {
            if (controller.heeftProfielfoto() != null) {
                img = new ImageView("https://app-1524829407.000webhostapp.com/" + spelerId + "/profilePic." + controller.heeftProfielfoto());
                if (!img.getImage().isError()) {
                    img.setFitHeight(17);
                    img.setFitWidth(17);
                    Platform.runLater(() -> {
                        MBPlayer.setGraphic(img);
                    });
                }
            }
        }).start();
        vernieuwStatus();
        sp.getChildren().clear();
        sp.getChildren().add(spelConfig);
        //setCenter(spelConfig);

        this.spel = new SpelPaneel(controller, this);

        counter++;
    }

    private void setBackground() {
        tb = new Thread(() -> {
            if (controller.heeftAchtergrond() != null) {
                Image bgIm = new Image("https://app-1524829407.000webhostapp.com/" + spelerId + "/backgroundPic." + controller.heeftAchtergrond());
                BackgroundSize bs = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, false, true);
                BackgroundImage bgi = new BackgroundImage(bgIm, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bs);
                bg = new Background(bgi);
                Platform.runLater(() -> {
                    if (!"gui.SettingsPaneel".equals(((StackPane) getCenter()).getChildren().get(0).getClass().getCanonicalName())) {
                        sp.setBackground(bg);
                    }
                });
            }
        });
        tb.start();
    }

    public void toonSpelConfigPaneel() {
        if (sp.getBackground() == Background.EMPTY && controller.heeftAchtergrond() != null) {
            sp.setBackground(bg);
        }
        sp.getChildren().clear();
        sp.getChildren().add(spelConfig);
    }

    public void toonRegistratie() { //RegistratiePaneel tonen
        sp.getChildren().clear();
        sp.getChildren().add(registratie);
    }

    public void toonAanmelden() { //AanmeldPaneel tonen
        if (!langBox.getChildren().get(0).equals(Language)) {
            langBox.getChildren().remove(MBPlayer);
            langBox.getChildren().add(Language);
        }
        lblstatus.setText(r.getString("Welcome"));
        sp.getChildren().clear();
        sp.getChildren().add(aanmelden);
    }

    public void toonSettings() {
        sp.setBackground(Background.EMPTY);
        if (settings == null) {
            this.settings = new SettingsPaneel(controller, this, aanmelden);
        }
        sp.getChildren().clear();
        sp.getChildren().add(settings);
    }

    public void speelSpel() { //SpelPaneel tonen
        langBox.getChildren().remove(MBPlayer);
        sp.getChildren().clear();
        sp.getChildren().add(spel);
        //setCenter(spel);
        //new Thread(rb).start();
        lblstatus.setText(String.format("%S: %s", r.getString("LevelCap"), controller.geefLevel() == 1 ? r.getString("Easy") : controller.geefLevel() == 2 ? r.getString("Normal") : r.getString("Difficult")));
        spel.voegComponentenToe();
    }

    public void laadSpel(List<List<Integer>> zetten, List<List<Integer>> correcties) {
        langBox.getChildren().remove(MBPlayer);
        sp.getChildren().clear();
        sp.getChildren().add(spel);
        //setCenter(spel);
        //new Thread(rb).start();
        lblstatus.setText(String.format("%S: %s", r.getString("Level"), controller.geefLevel() == 1 ? r.getString("Easy") : controller.geefLevel() == 2 ? r.getString("Normal") : r.getString("Difficult")));
        spel.voegComponentenToe();
        spel.laadSpel(zetten, correcties);
    }

    public void laadChallenge(List<List<Integer>> zetten, List<List<Integer>> correcties, String titel, int UitdagerID) {
        langBox.getChildren().remove(MBPlayer);
        sp.getChildren().clear();
        sp.getChildren().add(spel);
        //setCenter(spel);
        //new Thread(rb).start();
        lblstatus.setText(String.format("%S: %s", r.getString("Level"), controller.geefLevel() == 1 ? r.getString("Easy") : controller.geefLevel() == 2 ? r.getString("Normal") : r.getString("Difficult")));
        spel.setIsChallenge(true);
        spel.setTitel(titel);
        spel.setUitdagerID(UitdagerID);
        spel.voegComponentenToe();
        spel.laadSpel(zetten, correcties);
    }

    public void speelChallenge(String titel) {
        langBox.getChildren().remove(MBPlayer);
        sp.getChildren().clear();
        sp.getChildren().add(spel);
        //setCenter(spel);
        //new Thread(rb).start();
        lblstatus.setText(String.format("%S: %s", r.getString("Level"), controller.geefLevel() == 1 ? r.getString("Easy") : controller.geefLevel() == 2 ? r.getString("Normal") : r.getString("Difficult")));
        spel.setIsChallenge(true);
        spel.setTitel(titel);
        spel.setUitdagerID(controller.geefSpeler().getId());
        spel.voegComponentenToe();
    }

    public void vernieuwStatus() {
        String voornaam = controller.geefSpelerDetails()[0];
        lblstatus.setText(String.format("%s %s. ", r.getString("WelcomePlayer"), voornaam));
    }
}
