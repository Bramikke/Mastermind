package domein;

import exceptions.EmailException;
import persistentie.Connectie;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Groep2
 */
public class DomeinController {
    private GewonnenSpellen GewonnenSpel;
    private int aantalPogingen;
    private List<String> codeListPlayer0 = new ArrayList<>();
    private List<String> codeListPlayer1 = new ArrayList<>();
    private String[] kleuren;
    private ResourceBundle r;
    private Spel spel;
    private final SpelOpslaanRepository spelOpslaanRepository;
    private Speler speler;
    private final SpelerRepository spelerRepository;
    private final GewonnenSpellenRepository spellenRepository;
    private final GewonnenUitdagingenRepository uitdagingenRepository;

    /**
     * Initialize domeincontroller
     */
    public DomeinController() {
        spelerRepository = new SpelerRepository();
        spellenRepository = new GewonnenSpellenRepository();
        uitdagingenRepository = new GewonnenUitdagingenRepository();
        spelOpslaanRepository = new SpelOpslaanRepository();
        Connectie.initDB();
        Locale l = new Locale("nl", "NL");
        r = ResourceBundle.getBundle("messagesBundles/MessagesBundle", l);
    }
    /**
     *  calculate number of stars at the end of the game
     *
     * @return stars
     */
    public String aantalStars() {
        String outputStars;
        int aantalWins = spellenRepository.aantalWins(GewonnenSpel);
        if (aantalWins >= 250) {
            outputStars = "★★★★★";
        } else if (aantalWins >= 100) {
            outputStars = "★★★★☆";
        } else if (aantalWins >= 50) {
            outputStars = "★★★☆☆";
        } else if (aantalWins >= 20) {
            outputStars = "★★☆☆☆";
        } else if (aantalWins >= 10) {
            outputStars = "★☆☆☆☆";
        } else {
            outputStars = "☆☆☆☆☆";
        }
        return outputStars;
    }
    /**
     * number of wins in level of game played
     *
     * @return number of wins
     */
    public int aantalWins() {
        return spellenRepository.aantalWins(GewonnenSpel);
    }
    /**
     *  split string array from databasae to multidimensional list
     * 
     * @param stringArr array as string
     * @param newArray new array where string comes in
     * @return the new generated array
     */
    public List<List<Integer>> arraySplit(String stringArr, List<List<Integer>> newArray) {
        List<String> row = Arrays.asList(stringArr.substring(2, stringArr.length() - 2).split("\\], \\["));
        for (String element : row) {
            List<String> elementRowString = Arrays.asList(element.split(", "));
            List<Integer> elementRowInt = new ArrayList<>();
            for (String e : elementRowString) {
                elementRowInt.add(Integer.parseInt(e));
            }
            newArray.add(elementRowInt);
        }
        return newArray;
    }
    /**
     * check attempt with code
     * 
     * @param Poging array of the colors in attempt
     * @param pogingNummer number of attempt
     * @return array with the numbers of the correction code
     */
    public int[] controleerPoging(int[] Poging, int pogingNummer) {
        int[] correctie = spel.poging(Poging, pogingNummer);
        aantalPogingen++;
        return correctie;
    }
    /**
     *  turn String from database into code array
     * 
     * @param codeList list with code
     * @return array with code
     */
    public int[] databaseCodeToArray(List<String> codeList) {
        int[] code = new int[codeList.size()];
        int i = 0;
        for (String element : codeList) {
            code[i] = Integer.parseInt(element);
            i++;
        }
        return code;
    }
    /**
     *  save score in database if only one of the players have ended the game
     * 
     * @param naam name of player
     * @param gewonnen won or lost, 1 or 0 (1: won, 0: lost)
     * @param voltooid ended or not ended, 1 or 0 (1: completed, 0: not completed)
     */
    public void eindeChallenge(String naam, int gewonnen, int voltooid) {
        spelOpslaanRepository.eindeChallenge(spel, speler, naam, gewonnen, voltooid);
    }
    /**
     *  calculate winner and loser in the challenge
     * 
     * @param UitdagerID id of the challenger
     * @param titel title of the game
     * @param level level of the game (1-3)
     * @param won if the player has won the game or lost (1: won, 0: lost)
     */
    public void eindeChallenge(int UitdagerID, String titel, int level, int won) {
        List<String[]> challenges = geefChallenges(UitdagerID, titel);
        if (Integer.parseInt(challenges.get(0)[7]) + Integer.parseInt(challenges.get(1)[7]) >= 1) {
            int tegenSpelerID;
            int thisSpelerID = geefSpeler().getId();
            if (thisSpelerID == UitdagerID) {
                if (Integer.parseInt(challenges.get(0)[1]) != UitdagerID) {
                    tegenSpelerID = Integer.parseInt(challenges.get(0)[1]);
                } else {
                    tegenSpelerID = Integer.parseInt(challenges.get(1)[1]);
                }
            } else {
                tegenSpelerID = thisSpelerID;
            }
            if (challenges.get(0)[4] != null) {
                codeListPlayer0 = new ArrayList<>(Arrays.asList(challenges.get(0)[4].substring(2, challenges.get(0)[4].length() - 2).split("\\], \\[")));
            }
            if (challenges.get(1)[4] != null) {
                codeListPlayer1 = new ArrayList<>(Arrays.asList(challenges.get(1)[4].substring(2, challenges.get(1)[4].length() - 2).split("\\], \\[")));
            }
            int winner;
            int loser;
            if (codeListPlayer0.size() < codeListPlayer1.size()) {
                winner = Integer.parseInt(challenges.get(0)[1]);
                loser = Integer.parseInt(challenges.get(1)[1]);
            } else if (codeListPlayer0.size() > codeListPlayer1.size()) {
                winner = Integer.parseInt(challenges.get(1)[1]);
                loser = Integer.parseInt(challenges.get(0)[1]);
            } else {
                winner = UitdagerID;
                loser = tegenSpelerID;
            }
            voegGewonnenUitdagingToe(winner, 3, level);
            voegGewonnenUitdagingToe(loser, -1, level);
            verwijderChallenges(Integer.parseInt(challenges.get(0)[0]), Integer.parseInt(challenges.get(1)[0]));
        } else {
            eindeChallenge(titel, won, 1);
        }
    }
    /**
     *  returns all the names of the saved challenges for the logged in player
     * 
     * @return list with names of challenges
     */
    public List<String[]> geefBestaandeOpgeslagenChallengeNamen() {
        return spelOpslaanRepository.geefBestaandeOpgeslagenChallengeNamen(speler);
    }
    /**
     *  returns challenges of a specific challenger with specific name
     * 
     * @param uitdagerID id of the challenger
     * @param gameNaam name of the challenge
     * @return list with attributes from the challenge
     */
    public List<String[]> geefChallenges(int uitdagerID, String gameNaam) {
        return spelOpslaanRepository.geefChallenges(uitdagerID, gameNaam);
    }
    /**
     *  returns won games of the logged in player per difficulty level
     * 
     * @return multi dimensional array of integers with 
     */
    public int[][] geefGewonnenSpellen() {
        return spellenRepository.geefGewonnenSpellen(speler);
    }
    /**
     *  returns level of the game playing
     * 
     * @return integer: level of the game
     */
    public int geefLevel() {
        return spel.getLevel();
    }
    /**
     *  returns list of challenges for logged in player which are not completed
     *  
     * @return gameName, level, challengerID, lastname, firstname
     */
    public List<String[]> geefOpgeslagenChallengeNamen() {
        return spelOpslaanRepository.geefOpgeslagenChallengeNamen(speler);
    }
    /**
     * returns array with data of a saved game 
     * 
     * @param opslagID id of the saved game
     * @return id of the saved game, player ID, name, level, solution code, previous codes, correction codes
     */
    public String[] geefOpgeslagenSpel(String opslagID) {
        return spelOpslaanRepository.geefOpgeslagenSpel(speler, opslagID);
    }
    /**
     *  returns names of the saved games for the logged in player
     * 
     * @return save id, name, level
     */
    public List<String[]> geefOpgeslagenSpellenNamen() {
        return spelOpslaanRepository.geefOpgeslagenSpellenNamen(speler);
    }
    /**
     *  returns attempts of the current game (colors in numbers)
     * 
     * @return list with numbers of the attempts
     */
    public List<int[]> geefPogingen() {
        return spel.getPogingen();
    }
    /**
     *  returns scores of the players in a level
     * 
     * @param level 1-3 (easy, normal, difficult)
     * @return firstname, lastname, total points, number of wins, level
     */
    public List<String[]> geefScores(int level) {
        return uitdagingenRepository.geefScores(level);
    }
    /**
     *  returns current player
     * 
     * @return player
     */
    public Speler geefSpeler() {
        return speler;
    }
    /**
     *  give array of the player with firstname and lastname
     * 
     * @return array(firstname, lastname)
     */
    public String[] geefSpelerDetails() {
        if (speler == null) {
            return null;
        }

        String[] spelerS = new String[3];
        spelerS[0] = speler.getVoornaam();
        spelerS[1] = speler.getNaam();
        return spelerS;
    }
    /**
     *  give the number of attempts
     *
     * @return number of attempts
     */
    public int getAantalPogingen() {
        return aantalPogingen;
    }

    /**
     *  return colors
     * 
     * @return string of all colors 
     */
    public String[] getKleuren() {
        return kleuren;
    }
    /**
     *  return selected language
     * 
     * @return resourcebundle language
     */
    public ResourceBundle getLanguage() {
        return r;
    }
    /**
     *  return the extension of the background of the logged in player
     * 
     * @return string (.jpg or .png)
     */
    public String heeftAchtergrond() {
        return speler.getAchtergrond();
    }
    /**
     *  return the extension of the profile picture of the logged in player
     * 
     * @return string (.jpg or.png)
     */
    public String heeftProfielfoto() {
        return speler.getProfielfoto();
    }

    /**
     *  return true if the game has ended
     * 
     * @return true: game ended, false: game not ended, keep going!
     */
    public boolean isEindeSpel() {
        return spel.isEindeSpel();
    }
    /**
     *  load game
     * 
     * @param level level of the game
     * @param opgeslagenSpelID ID of the game that was saved
     */
    public void laadNieuwSpel(int level, int opgeslagenSpelID) {
        setSpel(new Spel(speler, level, opgeslagenSpelID, true));
    }
    /**
     * Search email in database, if email is found and password is correct, set player and return boolean player is logged in
     *
     * @param emailadres email of player
     * @param wachtwoord password of player
     * @return player is logged in
     */
    public boolean meldAan(String emailadres, String wachtwoord) {
        Speler gevondenSpeler = spelerRepository.geefSpeler(emailadres);
        if (gevondenSpeler != null) {
            if (gevondenSpeler.getWachtwoord().equals(wachtwoord)) {
                setSpeler(gevondenSpeler);
            } else {
                setSpeler(null);
            }
            return true;
        }
        return false;
    }
    /**
     *  overwrite the challenge when player stops
     * 
     * @param naam name of the game
     */
    public void overschrijfChallenge(String naam) {
        spelOpslaanRepository.overschrijfChallenge(spel, speler, naam);
    }
    /**
     *  overwrite saved game
     */
    public void overschrijfSpel() {
        spelOpslaanRepository.overschrijfSpel(spel);
    }
    /**
     * Register new player to the database and set Speler with the data of the new player.
     *
     * @param naam name of player
     * @param voornaam first name of player
     * @param email email of player
     * @param geboortedatum date of birth
     * @param wachtwoord password
     * @param wachtwoordBevestiging password again (should be the same as wachtwoord)
     */
    public void registreer(String naam, String voornaam, String email, LocalDate geboortedatum, String wachtwoord, String wachtwoordBevestiging) {
        if (!wachtwoord.equals(wachtwoordBevestiging)) {
            throw new EmailException();
        }
        
        Speler nieuweSpeler = new Speler(naam, voornaam, email, geboortedatum, wachtwoord);
        spelerRepository.voegToe(nieuweSpeler);
        int id = spelerRepository.geefSpeler(email).getId();
        nieuweSpeler.setId(id);
        setSpeler(nieuweSpeler);
        
    }
    /**
     *  set number of attempts
     * 
     * @param aantalPogingen number of attempts
     */
    public void setAantalPogingen(int aantalPogingen) {
        this.aantalPogingen = aantalPogingen;
    }
    /**
     *  set colors of the game to correspond the language
     */
    public final void setKleuren() {
        switch (r.getLocale().getLanguage()) {
            case "nl":
                kleuren = new String[]{"ROOD", "ORANJE", "GEEL", "GROEN", "BLAUW", "PAARS", "ZWART", "WIT"};
                break;
            case "fr":
                kleuren = new String[]{"ROUGE", "ORANGE", "JAUNE", "VERT", "BLEU", "VIOLET", "NOIR", "BLANC"};
                break;
            default:
                kleuren = new String[]{"RED", "ORANGE", "YELLOW", "GREEN", "BLUE", "PURPLE", "BLACK", "WHITE"};
                break;
        }
        if (spel.getLevel() > 2) {
            List<String> tempList = new ArrayList<>(Arrays.asList(kleuren));
            tempList.add("");

            //Convert list back to array
            String[] tempArray = new String[tempList.size()];
            kleuren = tempList.toArray(tempArray);
        }
    }

    /**
     *  set language
     * 
     * @param Lang en, nl, or fr
     */
    public void setLanguage(String Lang) {
        String currentLang = "en", country = "US";
        switch (Lang) {
            case "en":
                currentLang = "en";
                country = "US";
                break;
            case "nl":
                currentLang = "nl";
                country = "NL";
                break;
            case "fr":
                currentLang = "fr";
                country = "FR";
                break;
        }
        Locale l = new Locale(currentLang, country);
        r = ResourceBundle.getBundle("MessagesBundles/MessagesBundle", l);
    }
    /**
     * set new game
     * 
     * @param spel new game
     */
    private void setSpel(Spel spel) {
        this.spel = spel;
    }
    /**
     * set new player
     * 
     * @param speler new player
     */
    private void setSpeler(Speler speler) {
        this.speler = speler;
    }
    /**
     *  if game ends, add ended game to the database
     *
     * @param gewonnen 1 or 0 (1:won, 0:lost)
     */
    public void spelGewonnen(int gewonnen) {
        GewonnenSpel = new GewonnenSpellen(speler.getId(), spel.getLevel(), gewonnen, aantalPogingen);
        new Thread(() -> {
            GewonnenSpellenRepository.voegToe(GewonnenSpel);
        }).start();
    }
    /**
     *  save game to database
     * 
     * @param naam name of the new saved game
     */
    public void spelOpslaan(String naam) {
        SpelOpslaanRepository.voegToe(speler, spel, naam);
    }
    /**
     *  create new game
     *
     * @param level level of the game
     */
    public void startNieuwSpel(int level) {
        setSpel(new Spel(speler, level));
        aantalPogingen = 0;
    }
    /**
     *  change last logged in date in database
     */
    public void updateLastLogin() {
        spelerRepository.updateLastLogin(speler.getId());
    }
    /**
     *  update profile with new parameters
     * 
     * @param x string of all new parameters for database
     */
    public void updateProfiel(String x) {
        spelerRepository.updateProfiel(x, speler.getId());
    }
    /**
     *  remove challenge from database
     * 
     * @param titel title of the game
     * @param uitdagerID id of the challenger
     */
    public void verwijderChallengeManueel(String titel, int uitdagerID) {
        spelOpslaanRepository.verwijderChallengeManueel(titel, uitdagerID);
    }
    /**
     *  remove challenge from database if challenge has ended
     * 
     * @param Challenge1ID id of challenge record 1 in database
     * @param Challenge2ID id of challenge record 2 in database
     */
    public void verwijderChallenges(int Challenge1ID, int Challenge2ID) {
        spelOpslaanRepository.verwijderChallenges(Challenge1ID, Challenge2ID);
    }
    /**
     *  remove current game from database
     */
    public void verwijderOpgeslagenSpel() {
        spelOpslaanRepository.verwijder(spel);
    }

    /**
     *  remove saved game from database
     * 
     * @param saveID id of the saved game
     */
    public void verwijderSpelManueel(int saveID) {
        spelOpslaanRepository.verwijderSpelManueel(saveID);
    }


    /**
     *  return players to challenge in a level
     * 
     * @param level integer level (1-3)
     * @return list of players
     */
    public List<String[]> vindSpelers(int level) {
        if (level > 1) {
            return spellenRepository.vindSpelers(level, speler.getId());
        } else {
            return spelerRepository.geefSpelersToString(speler.getId());
        }
    }
    /**
     *  add a new check array to the game
     * 
     * @param controle array integer with check of the attempt
     */
    public void voegControleToe(int[] controle) {
        spel.voegControleToe(controle);
    }


    /**
     *  add won challenge to the database
     * 
     * @param spelerID id of the player
     * @param punten number of points (3 or -1)
     * @param level level of the game
     */
    public void voegGewonnenUitdagingToe(int spelerID, int punten, int level) {
        uitdagingenRepository.voegToe(spelerID, punten, level);
    }
    /**
     *  add new attempt to the game
     * 
     * @param poging array integer attempt
     */
    public void voegPogingToe(int[] poging) {
        spel.voegPogingToe(poging);
    }
    /**
     *  add new challenge to database
     * 
     * @param uitgedaagdeSpeler challenged player
     * @param naam name of the challenge
     */
    public void voegUitdagingToe(int uitgedaagdeSpeler, String naam) {
        spelOpslaanRepository.voegUitdagingToe(uitgedaagdeSpeler, naam, speler.getId(), spel);
    }
    /**
     *  add code to the game
     *
     * @param code code of the new game
     */
    public void zetCode(int[] code) {
        spel.setCode(code);
    }
    /**
     *  convert integers to color strings
     * 
     * @param kleurenInt array integer colors
     * @return array string colors
     */
    public String[] zetKleurenOm(int[] kleurenInt) {
        String[] kleurenString = new String[kleurenInt.length];
        int i = 0;
        for (int kleur : kleurenInt) {
            kleurenString[i] = kleuren[kleur];
            i++;
        }
        return kleurenString;
    }

}
