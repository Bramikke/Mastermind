package gui;

import domein.DomeinController;
import domein.Speler;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SettingsPaneel extends GridPane {

    private final DomeinController controller;
    private final HoofdPaneel hoofdPaneel;
    private final AanmeldPaneel aanmeld;
    private final Speler speler;
    private final ResourceBundle r;
    private final TextField voornaam = new TextField();
    private final TextField naam = new TextField();
    private final TextField email = new TextField();
    private final DatePicker geboortedatum = new DatePicker(LocalDate.now());
    private final PasswordField wachtwoord = new PasswordField();
    private final PasswordField bevestiging = new PasswordField();
    private final Label foutbericht = new Label();
    private final Text txtHeader = new Text();
    private final Label lblVoornaam = new Label(), lblNaam = new Label(),
            lblEmail = new Label(), lblGeboortedatum = new Label(),
            lblWachtwoord = new Label(), lblBevestiging = new Label();
    private final Button btnUpdate = new Button(), btnTerug = new Button();
    private String pathProfile, pathBackground, nameProfile, nameBackground;

    public SettingsPaneel(DomeinController controller, HoofdPaneel hoofdPaneel, AanmeldPaneel aanmeld) {
        this.controller = controller;
        this.hoofdPaneel = hoofdPaneel;
        this.aanmeld = aanmeld;
        this.r = controller.getLanguage();
        this.speler = controller.geefSpeler();
        configureerGrid();
        voegComponentenToe();
    }

    private void configureerGrid() {
        setPadding(new Insets(10));
        setHgap(10);
        setVgap(10);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHalignment(HPos.RIGHT);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHalignment(HPos.RIGHT);

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setHgrow(Priority.ALWAYS);

        getColumnConstraints().addAll(col1, col2, col3, col4);
    }

    private void voegComponentenToe() {
        teksten();
        txtHeader.getStyleClass().add("hoofding");
        GridPane.setHalignment(txtHeader, HPos.LEFT);
        pathBackground = null;
        pathProfile = null;
        FileChooser fileChooser = new FileChooser();

        Button btnProfile = new Button(r.getString("SelectA") + " " + r.getString("ProfilePic") + "...");
        Button btnDeleteProfile = new Button();
        btnDeleteProfile.setGraphic(new ImageView("/gui/images/decline.png"));
        Tooltip tp = new Tooltip(r.getString("Remove") + " " + r.getString("ProfilePic"));
        Tooltip.install(btnDeleteProfile, tp);
        Label lblSelectedProfile = new Label();
        HBox hbxButtonsProfile = new HBox(btnProfile, btnDeleteProfile);
        VBox vbxProfile = new VBox(hbxButtonsProfile, lblSelectedProfile);
        vbxProfile.setPrefWidth(200);
        vbxProfile.setMaxWidth(200);

        Button btnBackground = new Button(r.getString("SelectA") + " " + r.getString("BackgroundPic") + "...");
        Button btnDeleteBackground = new Button();
        btnDeleteBackground.setGraphic(new ImageView("/gui/images/decline.png"));
        Tooltip tb = new Tooltip(r.getString("Remove") + " " + r.getString("BackgroundPic"));
        Tooltip.install(btnDeleteBackground, tb);
        btnDeleteBackground.setPrefSize(btnBackground.getWidth(), btnBackground.getHeight());
        Label lblSelectedBackground = new Label();
        HBox hbxButtonsBackground = new HBox(btnBackground, btnDeleteBackground);
        VBox vbxBackground = new VBox(hbxButtonsBackground, lblSelectedBackground);
        vbxBackground.setPrefWidth(250);
        vbxBackground.setMaxWidth(250);
        HBox hbxImages = new HBox(vbxProfile, vbxBackground);
        hbxImages.setSpacing(20);

        btnDeleteProfile.setOnAction(e -> {
            lblSelectedProfile.setText(r.getString("ProfilePic") + ": " + r.getString("None"));
            btnProfile.setText(r.getString("SelectA") + " " + r.getString("ProfilePic") + "...");
            pathProfile = "Delete";
        });
        btnDeleteBackground.setOnAction(e -> {
            lblSelectedBackground.setText(r.getString("BackgroundPic") + ": " + r.getString("None"));
            btnBackground.setText(r.getString("SelectA") + " " + r.getString("BackgroundPic") + "...");
            pathBackground = "Delete";
        });
        configureFileChooser(fileChooser);
        btnProfile.setOnAction(e -> {
            fileChooser.setTitle(r.getString("SelectA") + " " + r.getString("ProfilePic") + "...");
            File file = fileChooser.showOpenDialog((Stage) this.getScene().getWindow());
            if (file != null) {
                pathProfile = file.toString();
                int i = pathProfile.lastIndexOf('.');
                if (i > 0) {
                    nameProfile = pathProfile.substring(i + 1);
                    foutbericht.setText("");
                    btnProfile.setText(r.getString("ProfilePic") + " " + r.getString("Selected"));
                    lblSelectedProfile.setText(r.getString("SelectedFile") + ": " + file.getName());
                } else {
                    foutbericht.setText(r.getString("ErrWrongFile"));
                    pathProfile = null;
                }
            }
        });
        btnBackground.setOnAction(e -> {
            fileChooser.setTitle(r.getString("SelectA") + " " + r.getString("BackgroundPic") + "...");
            File file = fileChooser.showOpenDialog((Stage) this.getScene().getWindow());
            if (file != null) {
                pathBackground = file.toString();
                int i = pathBackground.lastIndexOf('.');
                if (i > 0) {
                    nameBackground = pathBackground.substring(i + 1);
                    foutbericht.setText("");
                    btnBackground.setText(r.getString("BackgroundPic") + " " + r.getString("Selected"));
                    lblSelectedBackground.setText(r.getString("SelectedFile") + ": " + file.getName());
                } else {
                    foutbericht.setText(r.getString("ErrWrongFile"));
                    pathBackground = null;
                }
            }
        });
        add(txtHeader, 0, 0, 4, 1);
        add(lblVoornaam, 0, 1, 1, 1);
        add(voornaam, 1, 1, 1, 1);
        add(lblNaam, 2, 1, 1, 1);
        add(naam, 3, 1, 1, 1);
        add(lblEmail, 0, 2, 1, 1);
        email.setDisable(true);
        add(email, 1, 2, 3, 1);
        add(lblGeboortedatum, 0, 3, 1, 1);
        geboortedatum.setDisable(true);
        add(geboortedatum, 1, 3, 3, 1);
        add(lblWachtwoord, 0, 4, 1, 1);
        add(wachtwoord, 1, 4, 3, 1);
        add(lblBevestiging, 0, 5, 1, 1);
        add(bevestiging, 1, 5, 3, 1);
        add(hbxImages, 0, 6, 4, 1);
        btnUpdate.setDefaultButton(true);
        btnTerug.setOnAction(this::terug);
        bevestiging.setOnKeyReleased(e -> {
            if (wachtwoord.getText().equals(bevestiging.getText())) {
                if (bevestiging.getStyleClass().contains("error")) {
                    bevestiging.getStyleClass().remove("error");
                }
            } else {
                if (!bevestiging.getStyleClass().contains("error")) {
                    bevestiging.getStyleClass().add("error");
                }
            }
        });
        btnUpdate.setOnAction(e -> {
            String query = "";
            if (!voornaam.getText().trim().equals(speler.getVoornaam()) && !"".equals(voornaam.getText().trim())) {
                query += "voornaam='" + voornaam.getText().trim() + "'";
            }
            if (!naam.getText().trim().equals(speler.getNaam()) && !"".equals(naam.getText().trim())) {
                query += "naam='" + naam.getText().trim() + "'";
            }
            if (!wachtwoord.getText().trim().equals(speler.getWachtwoord()) && !"".equals(wachtwoord.getText().trim())) {
                if (wachtwoord.getText().equals(bevestiging.getText())) {
                    query += "wachtwoord='" + wachtwoord.getText().trim() + "'";
                }
            }

            if (pathProfile != null || pathBackground != null) {
                if ("Delete".equals(pathProfile)) {
                    if (controller.heeftProfielfoto() != null) {
                        query += "profielfoto=null,";
                    }
                }
                if ("Delete".equals(pathBackground)) {
                    if (controller.heeftAchtergrond() != null) {
                        query += "achtergrond=null,";
                    }
                }
                if (!"Delete".equals(pathProfile) || !"Delete".equals(pathBackground)) {
                    Filehandler handler = new Filehandler();
                    if (!"Delete".equals(pathProfile) && pathProfile != null) {
                        handler.uploadFile(pathProfile, "profilePic", nameProfile, speler.getId());
                        if (controller.heeftProfielfoto() == null ? nameProfile != null : !controller.heeftProfielfoto().equals(nameProfile)) {
                            query += "profielfoto='" + nameProfile + "',";
                        }
                    }
                    if (!"Delete".equals(pathBackground) && pathBackground != null) {
                        handler.uploadFile(pathBackground, "backgroundPic", nameBackground, speler.getId());
                        if (controller.heeftAchtergrond() == null ? nameBackground != null : !controller.heeftAchtergrond().equals(nameBackground)) {
                            query += "achtergrond='" + nameBackground + "',";
                        }
                    }
                }
            }
            if (query.length() > 0) {
                controller.updateProfiel(query.substring(0, query.length()-1));
            }
            aanmeld.aanmelden();
        });
        foutbericht.getStyleClass().add("foutbericht");
        HBox controls = new HBox(btnUpdate, btnTerug, foutbericht);
        controls.setSpacing(10);
        add(controls, 0, 7, 4, 1);
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", Arrays.asList("*.jpg", "*.png", "*.jpeg")),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg")
        );
    }

    public void teksten() {
        txtHeader.setText(r.getString("Settings"));
        lblVoornaam.setText(String.format("%s:", r.getString("FirstName")));
        voornaam.setText(speler.getVoornaam());
        lblNaam.setText(String.format("%s:", r.getString("LastName")));
        naam.setText(speler.getNaam());
        lblEmail.setText(String.format("%s:", r.getString("Email")));
        email.setText(speler.getEmailadres());
        lblGeboortedatum.setText(String.format("%s:", r.getString("DateOfBirth")));
        geboortedatum.setValue(speler.getGeboorteDatum());
        lblWachtwoord.setText(String.format("%s:", r.getString("Password")));
        wachtwoord.setText(speler.getWachtwoord());
        lblBevestiging.setText(String.format("%s:", r.getString("ConfirmPassword")));
        btnUpdate.setText("Update profiel");
        btnTerug.setText(r.getString("Back"));
        foutbericht.setText(null);
    }

    private void terug(ActionEvent event) {
        hoofdPaneel.toonSpelConfigPaneel();
    }

}
