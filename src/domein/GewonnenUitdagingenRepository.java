package domein;

import java.util.List;
import persistentie.GewonnenUitdagingenMapper;

/**
 *
 * @author Groep2
 */
public class GewonnenUitdagingenRepository {

    private static GewonnenUitdagingenMapper mapper;
    
    /**
     * Initialize GewonnenUitdagingenRepository
     */
    public GewonnenUitdagingenRepository() {
        mapper = new GewonnenUitdagingenMapper();
    }

    /**
     * Adds a won challange to the database
     * 
     * @param spelerID ID of player
     * @param punten number of points (3 or -1)
     * @param level level of the game
     */
    public void voegToe(int spelerID, int punten, int level) {
        mapper.voegToe(spelerID, punten, level);
    }
    
    /**
     * returns scores of the player in a level
     * 
     * @param level 1-3 (easy, normal, difficult)
     * @return firstname, lastname, total points, number of wins, level
     */
    public List<String[]> geefScores(int level) {
        return mapper.geefScores(level);
    }
}
