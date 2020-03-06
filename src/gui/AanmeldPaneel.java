package gui;

import domein.DomeinController;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class AanmeldPaneel extends GridPane {

    private final DomeinController controller;
    private final HoofdPaneel hoofdPaneel;
    private final TextField email = new TextField();
    private final PasswordField wachtwoord = new PasswordField();
    private final Text txtHeader = new Text();
    private final Label lblEmail = new Label(), lblWachtwoord = new Label(), foutbericht = new Label();
    private final Button btnAanmelden = new Button();
    private final Hyperlink hplRegistratie = new Hyperlink();
    private ImageView paypal;

    public AanmeldPaneel(DomeinController controller, HoofdPaneel hoofdPaneel) {
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

        getColumnConstraints().addAll(col1, col2);
    }

    private void voegComponentenToe() {
        teksten();
        txtHeader.getStyleClass().add("hoofding");
        GridPane.setHalignment(txtHeader, HPos.LEFT);
        add(txtHeader, 0, 0, 2, 1);
        add(lblEmail, 0, 1, 1, 1);
        add(email, 1, 1, 1, 1);
        add(lblWachtwoord, 0, 2, 1, 1);
        add(wachtwoord, 1, 2, 1, 1);
        btnAanmelden.setOnAction(e -> {
            aanmelden();
        });
        btnAanmelden.setDefaultButton(true);
        foutbericht.getStyleClass().add("foutbericht");
        HBox controls = new HBox(btnAanmelden, foutbericht);
        controls.setSpacing(10);
        add(controls, 0, 3, 2, 1);

        hplRegistratie.setOnAction(this::registreren);
        GridPane.setHalignment(hplRegistratie, HPos.LEFT);
        add(hplRegistratie, 0, 4, 2, 1);
        GridPane.setValignment(paypal, VPos.BOTTOM);
    }

    public void teksten() {
        r = controller.getLanguage();
        getChildren().remove(paypal);
        paypal = new ImageView(getClass().getResource("/gui/images/donate" + r.getLocale().getCountry() + ".png").toExternalForm());
        paypal.setOnMouseEntered(ev -> {
            getScene().setCursor(Cursor.HAND);
        });
        paypal.setOnMouseExited(ev -> {
            getScene().setCursor(Cursor.DEFAULT);
        });
        paypal.setOnMouseClicked(ev -> {
            try {
                if (null != r.getLocale().getCountry()) {
                    switch (r.getLocale().getCountry()) {
                        case "NL":
                            Desktop.getDesktop().browse(new URI("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=5JNVXUVEBVYRC"));
                            break;
                        case "FR":
                            Desktop.getDesktop().browse(new URI("https://www.paypal.com/fr/cgi-bin/webscr?cmd=_flow&SESSION=jXP1DPVaW9w9i5nuAVhChgKjNFAtGIbvUY3H0SG_O_YsIqMjK2VgBBLkK2O&dispatch=5885d80a13c0db1f8e263663d3faee8db8175432b4df92754f4b4adb5a123d61&rapidsState=Donation__DonationFlow___StateDonationLogin&rapidsStateSignature=6aca1b14b7a117ce2a5b944a9c00b17a172e0fa3"));
                            break;
                        default:
                            Desktop.getDesktop().browse(new URI("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=W7MGWJMTHEQLG&lc=US&item_name=MasterMind&no_note=0&cn=Speciale%20instructies%20toevoegen%20voor%20de%20verkoper%3A&no_shipping=2&currency_code=USD&bn=PP-DonationsBF%3Abtn_donate_SM.gif%3ANonHosted"));
                            break;
                    }
                }
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(SpelPaneel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        add(paypal, 0, 18, 2, 1);
        txtHeader.setText(r.getString("Login"));
        lblEmail.setText(String.format("%s:", r.getString("Email")));
        lblWachtwoord.setText(String.format("%s:", r.getString("Password")));
        btnAanmelden.setText(r.getString("Login"));
        hplRegistratie.setText(r.getString("NoAccount"));
        foutbericht.setText(null);
    }
    private ResourceBundle r;

    public void aanmelden() {
        r = controller.getLanguage();
        if (email.getText().trim().isEmpty()) {
            foutbericht.setText(r.getString("ErrEmailEmpty"));
            return;
        }
        if (wachtwoord.getText().trim().isEmpty()) {
            foutbericht.setText(r.getString("ErrPasswordNotOK"));
            return;
        }

        boolean emailBestaat = controller.meldAan(email.getText().trim(), wachtwoord.getText().trim());

        if (!emailBestaat) {
            foutbericht.setText(r.getString("ErrEmailNotExisting"));
            return;
        }

        if (controller.geefSpeler() == null) {
            foutbericht.setText(r.getString("ErrPasswordNotOK"));
            return;
        }

        foutbericht.setText(null);
        hoofdPaneel.clearImages();
        controller.updateLastLogin();
        hoofdPaneel.spelerIsAangemeld();
    }

    private void registreren(ActionEvent event) {
        hoofdPaneel.toonRegistratie();
    }
}
