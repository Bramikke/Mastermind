package gui;

import domein.DomeinController;
import exceptions.EmailInGebruikException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class RegistratiePaneel extends GridPane {

    private final DomeinController controller;
    private final HoofdPaneel hoofdPaneel;
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
    private final Button btnRegistreer = new Button(), btnTerug = new Button();

    public RegistratiePaneel(DomeinController controller, HoofdPaneel hoofdPaneel) {
        this.controller = controller;
        this.hoofdPaneel = hoofdPaneel;

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
        add(txtHeader, 0, 0, 4, 1);
        add(lblVoornaam, 0, 1, 1, 1);
        add(voornaam, 1, 1, 1, 1);
        add(lblNaam, 2, 1, 1, 1);
        add(naam, 3, 1, 1, 1);
        add(lblEmail, 0, 2, 1, 1);
        add(email, 1, 2, 3, 1);
        add(lblGeboortedatum, 0, 3, 1, 1);
        add(geboortedatum, 1, 3, 3, 1);
        add(lblWachtwoord, 0, 4, 1, 1);
        add(wachtwoord, 1, 4, 3, 1);
        add(lblBevestiging, 0, 5, 1, 1);
        add(bevestiging, 1, 5, 3, 1);
        btnRegistreer.setOnAction(this::registreer);
        btnRegistreer.setDefaultButton(true);
        btnTerug.setOnAction(this::terug);
        foutbericht.getStyleClass().add("foutbericht");
        HBox controls = new HBox(btnRegistreer, btnTerug, foutbericht);
        controls.setSpacing(10);
        add(controls, 0, 6, 4, 1);
    }

    public void teksten() {
        ResourceBundle r = controller.getLanguage();
        txtHeader.setText(r.getString("Registration"));
        lblVoornaam.setText(String.format("%s:", r.getString("FirstName")));
        lblNaam.setText(String.format("%s:", r.getString("LastName")));
        lblEmail.setText(String.format("%s:", r.getString("Email")));
        lblGeboortedatum.setText(String.format("%s:", r.getString("DateOfBirth")));
        lblWachtwoord.setText(String.format("%s:", r.getString("Password")));
        lblBevestiging.setText(String.format("%s:", r.getString("ConfirmPassword")));
        btnRegistreer.setText(r.getString("Register"));
        btnTerug.setText(r.getString("Back"));
        foutbericht.setText(null);
    }

    private void registreer(ActionEvent event) {
        ResourceBundle r = controller.getLanguage();
        if (voornaam.getText().trim().isEmpty() || naam.getText().trim().isEmpty()) {
            foutbericht.setText(r.getString("ErrName"));
            return;
        }
        Pattern patternEmail = Pattern.compile("^(.+)@(.+)$");
        if (!patternEmail.matcher(email.getText().trim()).matches()) {
            foutbericht.setText(r.getString("ErrEmailEmpty"));
            return;
        }
        if (geboortedatum.getValue() == null) {
            foutbericht.setText(r.getString("ErrDateOfBirthEmpty"));
            return;
        }
        Pattern pattern = Pattern.compile("\\d{1}[a-zA-Z]{6,}\\d{1}");
        if (!pattern.matcher(wachtwoord.getText().trim()).matches()) {
            foutbericht.setText(r.getString("ErrPasswordEmpty"));
            return;
        }
        if (!wachtwoord.getText().equals(bevestiging.getText())) {
            foutbericht.setText(r.getString("ErrPasswordNotConfirmed"));
            return;
        }
        if (geboortedatum.getValue().isAfter(LocalDate.now().minusYears(8))) {
            foutbericht.setText(r.getString("ErrDateOfBirth8"));
            return;
        }
        try {
            controller.registreer(
                    naam.getText().trim(),
                    voornaam.getText().trim(),
                    email.getText().trim(),
                    geboortedatum.getValue(),
                    wachtwoord.getText().trim(),
                    bevestiging.getText().trim());
            foutbericht.setText(null);
            hoofdPaneel.spelerIsAangemeld();
        } catch (EmailInGebruikException e) {
            foutbericht.setText(r.getString("ErrEmailNotUnique"));
        }

    }

    private void terug(ActionEvent event) {
        hoofdPaneel.toonAanmelden();
    }
}
