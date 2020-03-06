package ui;

import domein.DomeinController;
import exceptions.EmailInGebruikException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistratieConsole {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private final DomeinController controller;
    private final ResourceBundle r;
    private boolean PlayerLoggedIn = false;

    public RegistratieConsole(DomeinController controller) {  //controller & language instellen
        this.controller = controller;
        this.r = controller.getLanguage();
        this.registreer();
    }

    private void registreer() { //registreer nieuwe gebruiker
        Scanner sc = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        String voornaam, naam, email, password, passwordVerify;
        LocalDate dateOfBirth;        
        
        //voornaam & achternaam
        System.out.printf("%s: ", r.getString("FirstName"));
        voornaam = sc.nextLine();
        System.out.printf("%s: ", r.getString("LastName"));
        naam = sc.nextLine();
        while ("".equals(voornaam) || "".equals(naam)) {
            System.out.printf(ANSI_RED + "%s%n" + ANSI_RESET, r.getString("ErrName"));
            System.out.printf("%s: ", r.getString("FirstName"));
            voornaam = sc.nextLine();
            System.out.printf("%s: ", r.getString("LastName"));
            naam = sc.nextLine();
        }
        //email
                
        Pattern patternEmail = Pattern.compile("^(.+)@(.+)$");
        System.out.printf("%s: ", r.getString("Email"));
        email = sc.nextLine();
        while (!patternEmail.matcher(email).matches()) {
            System.out.printf(ANSI_RED + "%s%n" + ANSI_RESET, r.getString("ErrEmailEmpty"));
            System.out.printf("%s: ", r.getString("Email"));
            email = sc.nextLine();
        }
        //geboortedatum
        System.out.printf("%s: ", r.getString("DateOfBirth"));
        try {
            dateOfBirth = LocalDate.parse(sc.nextLine(), formatter);
            while (dateOfBirth == null) {
                System.out.printf(ANSI_RED + "%s%n" + ANSI_RESET, r.getString("ErrDateOfBirthEmpty"));
                System.out.printf("%s: ", r.getString("DateOfBirth"));
                dateOfBirth = LocalDate.parse(sc.nextLine(), formatter);
            }
            while (dateOfBirth.isAfter((LocalDate.now().minusYears(8)))) {
                System.out.printf(ANSI_RED + "%s%n" + ANSI_RESET, r.getString("ErrDateOfBirth8"));
                System.out.printf("%s: ", r.getString("DateOfBirth"));
                dateOfBirth = LocalDate.parse(sc.nextLine(), formatter);
            }
            //catch voor niet-datum notatie
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Geen correcte input");
        }
        //wachtwoord
        Pattern pattern = Pattern.compile("\\d{1}[a-zA-Z]{6,}\\d{1}");
        System.out.printf("%s: ", r.getString("Password"));
        password = sc.nextLine();
        
        while (!pattern.matcher(password).matches()) {
            System.out.printf(ANSI_RED + "%s%n" + ANSI_RESET, r.getString("ErrEmptyPassword"));
            System.out.printf("%s: ", r.getString("Password"));
            password = sc.nextLine();
        }
        //bevestiging wachtwoord
        System.out.printf("%s: ", r.getString("ConfirmPassword"));
        passwordVerify = sc.nextLine();
        while (!password.equals(passwordVerify)) {
            System.out.printf(ANSI_RED + "%s%n" + ANSI_RESET, r.getString("ErrPasswordNotConfirmed"));
            System.out.printf("%s: ", r.getString("ConfirmPassword"));
            passwordVerify = sc.nextLine();
        }
        //aanmaken nieuwe speler
        try {
            controller.registreer(naam, voornaam, email.toLowerCase(), dateOfBirth, password, passwordVerify);
            PlayerLoggedIn = true;
        } catch (EmailInGebruikException e) {
            throw new IllegalArgumentException(r.getString("ErrEmailNotUnique"));
        }
    }

    public boolean isPlayerLoggedIn() { //als speler succesvol is aangemaakt: speler ingelogd
        return PlayerLoggedIn;
    }
}
