package domein;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Groep2
 */
public class Spel {

    private final Speler speler;
    private int[] code;
    private final List<int[]> pogingen= new ArrayList<>();
    private final List<int[]> controles= new ArrayList<>();
    private boolean eindeSpel = false;
    private int opgeslagenSpelID; 
    private int level;

    /**
     * Initialize Spel
     * 
     * @param speler player
     * @param level level of game
     * @param opgeslagenSpelID saved game ID
     * @param loaded boolean if game is saved
     */
    public Spel(Speler speler, int level,int opgeslagenSpelID, boolean loaded) {
        this.speler = speler;
        this.level = level;
        this.opgeslagenSpelID = opgeslagenSpelID;
        if (loaded == false) {
            genereerCode();
        }
        //System.out.println("\n" + Arrays.toString(code) + "\n"); //moet nadien weg!    
    }
        
    /**
     * Initialize Spel
     * 
     * @param speler player
     * @param level level of game
     */
    public Spel(Speler speler, int level) {
        this(speler, level, 0, false);
    }
    
    /**
     * generates code
     */
    private void genereerCode() {
        SecureRandom _r = new SecureRandom();
        int kleur;
        int randomLength;
        if (level <= 2) {
            code = new int[4];
            randomLength = 8;
        } else {
            code = new int[5];
            randomLength = 9;
        }
        for(int i = 0; i< code.length; i++){
            code[i]=-1;
        }
        boolean kleurInGebruik = false;
        for (int i = 0; i < code.length; i++) {
            do {
                kleur = _r.nextInt(randomLength);
                if (level <= 1) {
                    kleurInGebruik = false;
                    if (Arrays.toString(code).matches(".*[\\[ ]" + kleur + "[\\],].*")) {
                        kleurInGebruik = true;
                    }
                }
            } while (kleurInGebruik);
            code[i] = kleur;
        }
    }
    
    //zwart = 1, wit = 2, kruis = 3

    /**
     * returns evaluation of attempt
     * 
     * @param poging array of colors in attempt
     * @param pogingNummer attemptnummer
     * @return evaluation of attempt
     */
    public int[] poging(int[] poging, int pogingNummer) {
        pogingen.add(poging);
        int i = 0;
        int[] correctie = new int[poging.length];
        for (int poging1 : poging) {
            if (poging1 == code[i]) {
                correctie[i] = 1;
            } else if (Arrays.toString(code).matches(".*[\\[ ]" + poging1 + "[\\],].*")){
                correctie[i] = 2;
            } else {
                correctie[i] = 3;
            }
            i++;
        }
        if (level > 1) {
            Arrays.sort(correctie);
        }
        controles.add(correctie);
        eindeSpel = true;
        for (int j = 0; j < correctie.length; j++) {
            if (correctie[j] != 1) {
                eindeSpel = false;
            }
        }
        return correctie;
    }

    /**
     * add new attempt to the game
     * 
     * @param poging array integer attempt
     */
    public void voegPogingToe(int[] poging) {
        pogingen.add(poging);
    }

    /**
     * add a new check array to the game
     * 
     * @param controle array integer with check of the attempt
     */
    public void voegControleToe(int[] controle) {
        controles.add(controle);
    }

    /**
     * returns attempts
     * 
     * @return tried attempts
     */
    public List<int[]> getPogingen() {
        return pogingen;
    }

    /**
     * returns evaluations
     * 
     * @return previous evaluations
     */
    public List<int[]> getControles() {
        return controles;
    }

    /**
     * returns true if the game has ended
     * 
     * @return true: game ended; false: game not ended
     */
    public boolean isEindeSpel() {
        return eindeSpel;
    }
    
    /**
     * gives the code of the game
     * 
     * @return code
     */
    public int[] getCode() {
        return code;
    }

    /**
     * set code
     * 
     * @param code Array of integers
     */
    public void setCode(int[] code) {
        this.code = code;
    }
    
    /**
     * gives the level of the game
     * 
     * @return
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * gives ID of saved game
     * 
     * @return
     */
    public int getOpgeslagenSpelID() {
        return opgeslagenSpelID;
    }
}
