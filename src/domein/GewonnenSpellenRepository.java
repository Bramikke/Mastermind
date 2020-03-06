package domein;

import java.util.List;
import persistentie.GewonnenSpellenMapper;

/**
 *
 * @author Groep2
 */
public class GewonnenSpellenRepository {

    private static GewonnenSpellenMapper mapper;

    /**
     * Initialize GewonnenSpellenRapository
     */
    public GewonnenSpellenRepository() {
        mapper = new GewonnenSpellenMapper();
    }
    
    /**
     * Adds the game to the database
     * 
     * @param spellen ID of player, level of game, win of lost and number of attempts
     */
    public static void voegToe(GewonnenSpellen spellen) {
        mapper.voegToe(spellen);
    }
    
    /**
     * finds players on level and ID
     * 
     * @param level
     * @param id
     * @return ArrayList of found players
     */
    public List<String[]> vindSpelers(int level, int id){
        return mapper.vindSpelers(level, id);
    }
    
    /**
     * returns won games of the logged in player per difficulty level
     * 
     * @param speler
     * @return multi dimensional array of won games
     */
    public int[][] geefGewonnenSpellen(Speler speler){
        return mapper.geefGewonnenSpellen(speler);
    }
    
    /**
     * number of wins in level of game played
     * 
     * @param spel
     * @return int number of wins
     */
    public int aantalWins(GewonnenSpellen spel) {
        return mapper.aantalWins(spel);
    }
}
