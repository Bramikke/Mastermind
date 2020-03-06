package ui;

import domein.DomeinController;
import java.io.Console;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;

public class LoginConsole {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    private final DomeinController controller;
    private boolean EmailInDB = false;
    private boolean PlayerLoggedIn = false;
    private final ResourceBundle r;

    public LoginConsole(DomeinController controller) { //instellen controller & taal
        this.controller = controller;
        r = controller.getLanguage();
        login();
    }

    private void login() { //Inloggen
        Scanner sc = new Scanner(System.in);
        String login;
        System.out.printf("%n%n");
        System.out.printf("%s%n", r.getString("Welcome"));
        //zolang speler niet is ingelogd
        while (!PlayerLoggedIn) {
            //geef email in
            System.out.printf("%s: ", r.getString("Email"));
            login = sc.nextLine();
            while (login == null || "".equals(login)) {
                System.out.println(ANSI_RED + r.getString("ErrEmailEmpty") + ANSI_RESET);
                System.out.printf("%s: ", r.getString("Email"));
                login = sc.nextLine();
            }

            //geef wachtwoord in
            System.out.printf("%s: ", r.getString("Password"));
            String password = sc.nextLine();
            while (password == null || "".equals(password)) {
                System.out.println(ANSI_RED + r.getString("ErrPasswordNotOK") + ANSI_RESET);
                System.out.printf("%s: ", r.getString("Password"));
                password = sc.nextLine();
            }

            boolean emailBestaat = controller.meldAan(login.toLowerCase(), password);

            //check player is ingelogd
            if (!emailBestaat) {
                System.out.println(ANSI_RED + r.getString("ErrEmailNotExisting") + ANSI_RESET);
            }
            else if (controller.geefSpeler() == null) {
                System.out.println(ANSI_RED + r.getString("ErrPasswordNotOK") + ANSI_RESET);
            } else {
                PlayerLoggedIn = true;
            }
        }
    }

    public boolean isPlayerLoggedIn() {
        return PlayerLoggedIn;
    }
}
