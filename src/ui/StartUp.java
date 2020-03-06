package ui;

import domein.DomeinController;
import java.util.ResourceBundle;
import java.util.Scanner;

public class StartUp {

    private static RegistratieConsole registratie;
    private static LoginConsole login;
    private static SpelConsole spel;
    private static ResourceBundle r;

    public static void main(String[] args) {
        DomeinController controller = new DomeinController();
        Scanner sc = new Scanner(System.in);
        String currentLang = "en";
        // SELECT LANGUAGE
        System.out.printf("Choose language (1-3): %n1.English %n2.Dutch %n3.French %n> ");
        switch (sc.nextInt()) {
            case 1:
                currentLang = "en";
                break;
            case 2:
                currentLang = "nl";
                break;
            case 3:
                currentLang = "fr";
                break;
        }

        controller.setLanguage(currentLang);
        r = controller.getLanguage();
        // REGISTER OR LOGIN?
        boolean register = false;
        System.out.printf("%s (1-2): %n1.%s %n2.%s %n> ", r.getString("LoginOrRegister"), r.getString("Login"), r.getString("Registration"));
        try {
            register = sc.nextInt() != 1;
        } catch (Exception e) {
        }
        if (register) {
            registratie = new RegistratieConsole(controller);
            //GAME
            if (registratie.isPlayerLoggedIn() == true) {
                spel = new SpelConsole(controller);
            }
        } else {
            login = new LoginConsole(controller);
            //GAME
            if (login.isPlayerLoggedIn() == true) {
                spel = new SpelConsole(controller);
            }
        }
    }
}
