package domein;

import exceptions.EmailInGebruikException;
import java.util.List;
import persistentie.SpelerMapper;

/**
 *
 * @author Groep2
 */
public class SpelerRepository {

    private final SpelerMapper mapper;

    /**
     * Initialize SpelerRepository
     */
    public SpelerRepository() {
        mapper = new SpelerMapper();
    }

    /**
     * adds new player to database
     *
     * @param speler new player
     */
    public void voegToe(Speler speler) {
        if (bestaatSpeler(speler.getEmailadres())) {
            throw new EmailInGebruikException();
        }

        mapper.voegToe(speler);
    }

    /**
     * checks if player already exists in database
     *
     * @param emailadres email address player
     */
    private boolean bestaatSpeler(String emailadres) {
        return mapper.geefSpeler(emailadres) != null;
    }

    /**
     * gives player with given email address
     *
     * @param emailadres email address player
     * @return player
     */
    public Speler geefSpeler(String emailadres) {
        return mapper.geefSpeler(emailadres);
    }

    /**
     * gives list of players except with this playerID
     *
     * @param spelerId Id of player not in list
     * @return list of players
     */
    public List<String[]> geefSpelersToString(int spelerId) {
        return mapper.geefSpelersToString(spelerId);
    }
    
    /**
     * change last logged in date in database
     *
     * @param spelerId ID of player
     */
    public void updateLastLogin(int spelerId){
        mapper.updateLastLogin(spelerId);
    }
    
    /**
     * update profile with new parameters
     *
     * @param x string of all new parameters for database
     * @param spelerId ID of player
     */
    public void updateProfiel(String x, int spelerId){
        mapper.updateProfiel(x, spelerId);
    }
}
