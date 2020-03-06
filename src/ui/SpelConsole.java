package ui;

import domein.DomeinController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class SpelConsole {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BOLD = "\033[0;1m";
    private final DomeinController controller;
    private final Scanner sc = new Scanner(System.in);
    private final ResourceBundle r;
    private int level, UitdagerID;
    private String output, titel;

    public SpelConsole(DomeinController controller) {//instellen controller + taal
        this.controller = controller;
        r = controller.getLanguage();
        StartSpel();
    }

    private void StartSpel() {
        output = "";
        int LoadOrSave = 0;
        //welkom
        System.out.printf("%n%s %s!%n", r.getString("WelcomePlayer"), controller.geefSpelerDetails()[0]);
        //kies spel-mode
        System.out.printf("1. %s %n2. %s %n3. %s %n4. %s %n5. %s %n> ",
                r.getString("StartNewGame"), r.getString("ChallengePlayer"), r.getString("LoadGame"), r.getString("LoadChallenge"), r.getString("ShowRankings"));
        try {
            LoadOrSave = Integer.parseInt(sc.nextLine());
            while (LoadOrSave < 1 || LoadOrSave > 5) {
                System.out.printf(ANSI_RED + "%s%n" + ANSI_RESET, r.getString("ErrWrongInput"));
                System.out.printf("1. %s %n2. %s %n3. %s %n4. %s %n5. %s %n> ",
                        r.getString("StartNewGame"), r.getString("ChallengePlayer"), r.getString("LoadGame"), r.getString("LoadChallenge"), r.getString("ShowRankings"));
                LoadOrSave = Integer.parseInt(sc.nextLine());

            }
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + r.getString("ErrWrongInput") + ANSI_RESET);
            StartSpel();
        }
        switch (LoadOrSave) {
            case 1:
                NieuwSpel();
                break;
            case 2:
                DaagUit();
                break;
            case 3:
                LaadSpel();
                break;
            case 4:
                LaadUitdagingen();
                break;
            case 5:
                ToonScoreBord();
                break;
        }
    }

    private void NieuwSpel() {
        //selecteer level
        SelectLevel();
        //start nieuw spel met bepaald level (maakt geheime code)
        controller.startNieuwSpel(level);
        //initialize colors
        controller.setKleuren();
        SpeelSpel(0);
    }

    private void DaagUit() {
        int i, enteredOption = -1;
//selecteer level
        System.out.println();
        SelectLevel();
//vind spelers
        List<String[]> andereSpelers = controller.vindSpelers(level);
        if (andereSpelers.isEmpty()) {
            System.out.println(r.getString("ErrNoOpponents"));
            DaagUit();
        } else {
            i = 0;
            System.out.printf("%n%s:%n", r.getString("ChooseChallengePlayer"));
            for (String[] andereSpeler : andereSpelers) {
                i++;
                System.out.printf("%d: %s %s%n", i, andereSpeler[1], andereSpeler[2]);
            }
            System.out.print("> ");
            try {
                enteredOption = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + r.getString("ErrWrongInput") + ANSI_RESET);
                DaagUit();
            }
            while (enteredOption < 1 || enteredOption > i) {
                System.out.printf(ANSI_RED + "%s%n" + ANSI_RESET, r.getString("ErrWrongInput"));
                System.out.print("> ");
                try {
                    enteredOption = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println(ANSI_RED + r.getString("ErrWrongInput") + ANSI_RESET);
                    DaagUit();
                }
            }
            i = 0;
            int uitgedaagdeSpeler = Integer.parseInt(andereSpelers.get(enteredOption - 1)[0]);
//geef game naam
            List<String[]> namen = controller.geefBestaandeOpgeslagenChallengeNamen();
            System.out.printf("%s: %n> ", r.getString("EnterTitle"));
            titel = sc.nextLine();
            boolean titelBestaat = false;
            for (String[] naam : namen) {
                if (naam[1].equals(titel)) {
                    titelBestaat = true;
                }
            }
            while (titelBestaat) {
                System.out.printf(ANSI_RED + "%s%n" + ANSI_RESET, r.getString("ErrSaveGameExists"));
                System.out.printf("%s: %n> ", r.getString("EnterTitle"));
                titel = sc.nextLine();
                titelBestaat = false;
                for (String[] naam : namen) {
                    if (naam[1].equals(titel)) {
                        titelBestaat = true;
                    }
                }
            }
//start nieuw spel met bepaald level (maakt geheime code)
            controller.startNieuwSpel(level);
//initialize colors
            controller.setKleuren();
            controller.voegUitdagingToe(uitgedaagdeSpeler, titel);
            UitdagerID = controller.geefSpeler().getId();
//speel spel
            SpeelSpel(1);
        }
    }

    private void LaadSpel() {
        String[] opgeslagenSpel = null;
        //selecteer opgeslagen game
        List<String[]> namen = controller.geefOpgeslagenSpellenNamen();
        if (namen.isEmpty()) { //no saveGames
            System.out.printf("%n%s%n", r.getString("ErrNoSavedGames"));
            StartSpel();
        } else { //there are savegames
            int selectedGame = -1, i;
            System.out.printf("%n%s: %n", r.getString("SelectSavedGame"));
            i = 1;
            for (String[] naam : namen) {
                System.out.printf("%d. %s - %s %s%n", i, naam[1], r.getString("Level"), naam[2]);
                i++;
            }
            System.out.print("> ");
            try {
                selectedGame = Integer.parseInt(sc.nextLine());
                //haal game op
                opgeslagenSpel = controller.geefOpgeslagenSpel(namen.get(selectedGame - 1)[0]);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.out.println(ANSI_RED + r.getString("ErrWrongInput") + ANSI_RESET);
                LaadSpel();
            }
            level = Integer.parseInt(opgeslagenSpel[3]);
            LaadSavegame(opgeslagenSpel);
            //speel spel (loadedgame = true)
            SpeelSpel(2);
        }
    }

    private void LaadUitdagingen() {
        //selecteer opgeslagen game
        List<String[]> namen = controller.geefOpgeslagenChallengeNamen();
        if (namen.isEmpty()) { //no saveGames
            System.out.printf("%n%s%n", r.getString("ErrNoSavedGames"));
            StartSpel();
        } else { //there are savegames
            int selectedGame = -1, i;
            System.out.printf("%n%s: %n", r.getString("SelectSavedGame"));
            i = 1;
            for (String[] naam : namen) {
                System.out.printf("%d. %s - %s %s - %s %s %s%n", i, naam[1], r.getString("Level"), naam[2], "van", naam[4], naam[5]);
                i++;
            }
            System.out.print("> ");
            try {
                selectedGame = Integer.parseInt(sc.nextLine());
                //haal game op
                UitdagerID = Integer.parseInt(namen.get(selectedGame - 1)[3]);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.out.println(ANSI_RED + r.getString("ErrWrongInput") + ANSI_RESET);
                LaadUitdagingen();
            }
            String[] opgeslagenSpel = controller.geefOpgeslagenSpel(namen.get(selectedGame - 1)[0]);
            titel = opgeslagenSpel[2];
            level = Integer.parseInt(opgeslagenSpel[3]);
            LaadSavegame(opgeslagenSpel);
            //speel spel (loadedgame = true)
            SpeelSpel(3);
        }
    }

    private void ToonScoreBord() {
        int selectedLevel;
        System.out.printf("%s (1-3): %n> ", r.getString("SelectLevel"));
        selectedLevel = Integer.parseInt(sc.nextLine());
        while (selectedLevel < 1 || selectedLevel > 3) {
            System.out.printf(ANSI_RED + "%s!" + ANSI_RESET, r.getString("ErrWrongInput"));
            System.out.printf("%s (1-3): %n> ", r.getString("SelectLevel"));
            selectedLevel = Integer.parseInt(sc.nextLine());
        }
        List<String[]> scoreBoard = controller.geefScores(selectedLevel);
        if (scoreBoard.isEmpty()) {
            System.out.printf("%n%s%n", r.getString("ErrNoScores"));
        } else {
            int i = 1;
            System.out.printf("%13s %-19s %n", r.getString("Score"), r.getString("Name"));
            for (String[] scoreBoarsLine : scoreBoard) {
                if (Integer.parseInt(scoreBoarsLine[0]) == controller.geefSpeler().getId()) {
                    System.out.printf(ANSI_BOLD + "%2d: %9s %-19s %n" + ANSI_RESET,
                            i, scoreBoarsLine[3], scoreBoarsLine[1] + " " + scoreBoarsLine[2]);

                } else {
                    System.out.printf("%2d. %9s %-19s %n", i, scoreBoarsLine[3], scoreBoarsLine[1] + " " + scoreBoarsLine[2]);
                }
                i++;
            }
            sc.nextLine();
        }
        StartSpel();
    }

    private void LaadSavegame(String[] opgeslagenSpel) {
        //nieuw spel (level, spelID)
        controller.laadNieuwSpel(Integer.parseInt(opgeslagenSpel[3]), Integer.parseInt(opgeslagenSpel[0]));
        //initialize colors
        controller.setKleuren();
        //code uit database naar CODE array
        List<String> codeList = Arrays.asList(opgeslagenSpel[4].substring(1, opgeslagenSpel[4].length() - 1).split(", "));
        int[] code = controller.databaseCodeToArray(codeList);
        //zetten + correcties
        List<List<Integer>> zetten = new ArrayList<>();
        List<List<Integer>> correcties = new ArrayList<>();
        try {
            //pogingen uit database naar ZETTEN array
            zetten = controller.arraySplit(opgeslagenSpel[5], zetten);
            //correcties uit database naar CORRECTIES array
            correcties = controller.arraySplit(opgeslagenSpel[6], correcties);
            //vorige zetten
            System.out.printf("%n%s:", r.getString("PreviousMoves"));
            List<String[]> correctiesOutput = new ArrayList<>();
            //zet resultaten array om naar bolletjes (gevuld of niet)
            int i;
            for (List<Integer> correctieRegel : correcties) {
                i = 0;
                String[] correctieOutput = new String[Integer.parseInt(opgeslagenSpel[3]) < 3 ? 4 : 5];
                for (int correctieGetal : correctieRegel) {
                    switch (correctieGetal) {
                        case 1:
                            correctieOutput[i] = "\u26AB";
                            break;
                        case 2:
                            correctieOutput[i] = "\u26AA";
                            break;
                        case 3:
                            correctieOutput[i] = "\u2A02";
                            break;
                    }
                    i++;
                }
                correctiesOutput.add(correctieOutput);
            }
            output = "%n";
            for (int k = 0; k < zetten.size(); k++) {
                List<Integer> zettenk = zetten.get(k);
                int[] kleurenInt = zettenk.stream().mapToInt(l -> l).toArray();
                String[] kleurenString = controller.zetKleurenOm(kleurenInt);
                for (String kleurenArray : kleurenString) {
                    output += String.format("%8s", kleurenArray);
                }
                output += String.format("%9s", " ");
                for (String correctieArray : correctiesOutput.get(k)) {
                    output += String.format("%s", correctieArray);
                }
                output += String.format("%n");

            }
            System.out.printf(output + "%n");
        } catch (NullPointerException e) {
            System.out.printf("%s.", r.getString("NoPreviousMoves"));
        }
        controller.zetCode(code);
        controller.setAantalPogingen(zetten.size());
        if (!zetten.isEmpty()) {
            for (List<Integer> pogingLine : zetten) {
                int[] zetArray = new int[pogingLine.size()];
                int k = 0;
                for (int poging : pogingLine) {
                    zetArray[k] = poging;
                    k++;
                }
                controller.voegPogingToe(zetArray);
            }
        }
        if (!correcties.isEmpty()) {
            for (List<Integer> correctieLine : correcties) {
                int[] correctieArray = new int[correctieLine.size()];
                int k = 0;
                for (int correctie : correctieLine) {
                    correctieArray[k] = correctie;
                    k++;
                }
                controller.voegControleToe(correctieArray);
            }
        }
    }

    private void SpeelSpel(int type) {
        int i;
        System.out.printf("%n%s %s %n%n", r.getString("PossibleColors"), Arrays.toString(controller.getKleuren()));
        for (i = controller.getAantalPogingen() + 1; i <= 12; i++) { //laat speler 12 pogingen (max) doen
            if (type == 1 || type == 2 || type == 3) { //stoppen when loadedgame of challenge
                if (type == 1) {
                    controller.overschrijfChallenge(titel);
                } else if (type == 2 || type == 3) {
                    controller.overschrijfSpel();
                }
                System.out.printf("%s? (%s %s %s) %n> ", r.getString("Stop"), r.getString("Yes"), r.getString("Or"), r.getString("No"));
                String stop = sc.nextLine();
                if (!"".equals(stop) && stop.toUpperCase().charAt(0) == r.getString("Yes").charAt(0)) {
                    i = 200;
                    break;
                }
            } else { //save 
                System.out.printf("%s? (%s %s %s)%n> ", r.getString("Save"), r.getString("Yes"), r.getString("Or"), r.getString("No"));
                String save = sc.nextLine();
                if (!"".equals(save) && save.toUpperCase().charAt(0) == r.getString("Yes").charAt(0)) {
                    List<String[]> namen = controller.geefOpgeslagenSpellenNamen();
                    System.out.printf("%s: %n> ", r.getString("EnterTitle"));
                    String titel = sc.nextLine();
                    boolean titelBestaat = false;
                    for (String[] naam : namen) {
                        if (naam[1].equals(titel)) {
                            titelBestaat = true;
                        }
                    }
                    while (titelBestaat) {
                        System.out.printf(ANSI_RED + "%s%n" + ANSI_RESET, r.getString("ErrSaveGameExists"));
                        System.out.printf("%s: %n> ", r.getString("EnterTitle"));
                        titel = sc.nextLine();
                        titelBestaat = false;
                        for (String[] naam : namen) {
                            if (naam[1].equals(titel)) {
                                titelBestaat = true;
                            }
                        }
                    }
                    controller.spelOpslaan(titel);
                    i = 200;
                    break;
                }
            }

            System.out.printf("%s %d: %n", r.getString("Attempt"), i);
            DoePoging(i); //poging 
            if (controller.isEindeSpel()) { //speler heeft oplossing gevonden
                i = 99;
            }
        }
        switch (i) {
            case 100:
                //als speler oplossing had gevonden
                System.out.println(r.getString("Won"));
                controller.spelGewonnen(1);
                if (type == 2) {
                    controller.verwijderOpgeslagenSpel();
                } else if (type == 1 || type == 3) {
                    int won = 1;
                    controller.eindeChallenge(UitdagerID, titel, level, won);
                }
                System.out.printf("%s: %s%n", r.getString(level == 1 ? "Easy" : level == 2 ? "Normal" : "Difficult"), controller.aantalStars());
                break;
            case 200:
                break;
            default:
                //als speler verloren heeft
                System.out.println(r.getString("Lost"));
                controller.spelGewonnen(0);
                if (type == 2) {
                    controller.verwijderOpgeslagenSpel();
                } else if (type == 1 || type == 3) {
                    int won = 0;
                    controller.eindeChallenge(UitdagerID, titel, level, won);
                }
                System.out.println(controller.aantalStars());
                break;
        }
        //start nieuw spel?
        System.out.printf("%s (%s %s %s)%n> ", r.getString("Again"), r.getString("Yes"), r.getString("Or"), r.getString("No"));
        String Again = sc.nextLine().toUpperCase();
        if ("".equals(Again) || Again.charAt(0) == r.getString("Yes").charAt(0)) {
            StartSpel();
        }
    }

    private void DoePoging(int pogingNummer) {
        String kleur;
        String[] colors = controller.getKleuren();
        int[] gekozenkleuren = new int[level < 3 ? 4 : 5];
        String[] correctieOutput = new String[level < 3 ? 4 : 5];
        //geef 4 of 5 kleuren in
        System.out.printf("%s: %n", String.format(r.getString("Enter4Colors"), level < 3 ? 4 : 5));
        for (int i = 0; i < gekozenkleuren.length; i++) {
            do {//doe zolang men geen kleur uit de array op geeft
                System.out.print((i + 1) + ": ");
                //haal spaties weg + convert naar uppercase
                kleur = sc.nextLine().replaceAll("\\s+", "").toUpperCase();

            } while (!Arrays.asList(colors).contains(kleur));
            gekozenkleuren[i] = Arrays.asList(colors).indexOf(kleur);
        }
        //kijk gekozen kleuren na
        int[] correctie = controller.controleerPoging(gekozenkleuren, pogingNummer);
        int i = 0;
        //zet resultaten array om naar bolletjes (gevuld of niet)
        for (int getal : correctie) {
            switch (getal) {
                case 1:
                    correctieOutput[i] = "\u26AB";
                    break;
                case 2:
                    correctieOutput[i] = "\u26AA";
                    break;
                case 3:
                    correctieOutput[i] = "\u2A02";
                    break;
            }
            i++;
        }

        for (String gekozenkleuren1 : controller.zetKleurenOm(gekozenkleuren)) { //print alle ingegeven kleuren
            output += String.format("%8s", gekozenkleuren1);
        }
        output += String.format("%9s", " ");
        for (int j = 0;
                j < correctie.length;
                j++) { //print resultaten array (bolletjes)
            output += String.format("%s", correctieOutput[j]);
        }
        output += String.format("%n");
        System.out.printf(output + "%n");
    }

    private void SelectLevel() { //selecteer een level
        int[][] spellen = controller.geefGewonnenSpellen();
        //print tabel met scores
        System.out.printf("%s: %n%10s %10s %10s%n%10d %10d %10d%n",
                r.getString("WonGames"), r.getString("Easy"), r.getString("Normal"), r.getString("Difficult"),
                spellen[0][1], spellen[1][1], spellen[2][1]);
        System.out.printf("%s (1-3): ", r.getString("SelectLevel"));
        level = Integer.parseInt(sc.nextLine());
        while (level > 1 && level < 4 && (spellen[level - 2][1] < 20)) { //indien player geen 20 wins heeft in makkelijker level
            System.out.println(ANSI_RED + r.getString("Err20Wins") + ANSI_RESET);
            System.out.printf("%s (1-3): ", r.getString("SelectLevel"));
            level = Integer.parseInt(sc.nextLine());
        }
    }
}
