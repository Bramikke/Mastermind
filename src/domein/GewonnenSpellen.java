package domein;

import exceptions.VerplichtVeldException;

/**
 *
 * @author Groep2
 */
public class GewonnenSpellen {
    private int spelerID;
    private int moeilijkheidsGraad;
    private int gewonnen;
    private int aantalPogingen;

    /**
     * Initialize GewonnenSpellen
     * 
     * @param spelerID ID of player
     * @param moeilijkheidsGraad difficulty of game
     * @param gewonnen player lost or won the game
     * @param aantalPogingen number of attempts
     */
    public GewonnenSpellen(int spelerID, int moeilijkheidsGraad, int gewonnen, int aantalPogingen) {
        setSpelerId(spelerID);
        setMoeilijkheidsGraad(moeilijkheidsGraad);
        setGewonnen(gewonnen);
        setAantalPogingen(aantalPogingen);
    }

    /**
     * return ID of player
     * 
     * @return int
     */
    public int getSpelerId() {
        return this.spelerID;
    }

    /**
     * set ID of player
     * 
     * @param id ID of player
     */
    private void setSpelerId(int id) {
        if (id == 0) {
            throw new VerplichtVeldException("Elke speler heeft verplicht een id.");
        }
        this.spelerID = id;
    }

    /**
     * return difficulty of game
     * 
     * @return int
     */
    public int getMoeilijkheidsGraad() {
        return this.moeilijkheidsGraad;
    }

    /**
     * set difficulty of game
     * 
     * @param moeilijkheidsGraad difficulty of game
     */
    private void setMoeilijkheidsGraad(int moeilijkheidsGraad) {
        if (moeilijkheidsGraad == 0) {
            throw new VerplichtVeldException("Elke game heeft verplicht een moeilijkheidsgraad");
        }
        this.moeilijkheidsGraad = moeilijkheidsGraad;
    }

    /**
     * return player lost or won the game
     * 
     * @return int that shows if player won or lost
     */
    public int getGewonnen() {
        return this.gewonnen;
    }

    /**
     * set result of game (win of lost)
     * 
     * @param gewonnen player lost or won the game
     */
    private void setGewonnen(int gewonnen) {
        if (gewonnen < 0 || gewonnen > 2) {
            throw new VerplichtVeldException("Een speler kan enkel wel of niet winnen");
        }
        this.gewonnen = gewonnen;
    }

    /**
     * return number of attempts
     * 
     * @return int
     */
    public int getAantalPogingen() {
        return this.aantalPogingen;
    }

    /**
     * set number of attempts
     * 
     * @param aantalPogingen number of attempts
     */
    private void setAantalPogingen(int aantalPogingen) {
        this.aantalPogingen = aantalPogingen;
    }
}
