package domein;

import java.util.List;
import persistentie.SpelOpslaanMapper;

/**
 *
 * @author Groep2
 */
public class SpelOpslaanRepository {

    private static SpelOpslaanMapper mapper;

    /**
     * Initialize SpelOpslaanRepository
     */
    public SpelOpslaanRepository() {
        mapper = new SpelOpslaanMapper();
    }

    /**
     * save game to database
     * 
     * @param speler player playing this game
     * @param spel game
     * @param naam name of the new saved game
     */
    public static void voegToe(Speler speler, Spel spel, String naam) {
        mapper.voegToe(speler, spel, naam);
    }

    /**
     * returns names of the saved games for the logged in player
     * 
     * @param speler player
     * @return save id, name, level
     */
    public List<String[]> geefOpgeslagenSpellenNamen(Speler speler) {
        return mapper.geefOpgeslagenSpellenNamen(speler);
    }

    /**
     * returns list of challenges for logged in player which are not completed
     * 
     * @param speler
     * @return gameName, level, challengerID, lastname, firstname
     */
    public List<String[]> geefOpgeslagenChallengeNamen(Speler speler){
        return mapper.geefOpgeslagenChallengeNamen(speler);
    }
    
    /**
     * returns array with data of a saved game 
     * 
     * @param speler player of the saved game
     * @param opslagID id of the saved game
     * @return id of the saved game, player ID, name, level, solution code, previous codes, correction codes
     */
    public String[] geefOpgeslagenSpel(Speler speler, String opslagID) {
        return mapper.geefOpgeslagenSpel(speler, opslagID);
    }
    
    /**
     * overwrite the challenge when player stops
     * 
     * @param spel the new game
     * @param speler player of the game
     * @param naam name of the game
     */
    public void overschrijfChallenge(Spel spel, Speler speler, String naam){
        mapper.overschrijfChallenge(spel, speler, naam);
    }

    /**
     * overwrite saved game
     * 
     * @param spel the new game
     */
    public void overschrijfSpel(Spel spel) {
        mapper.overschrijfSpel(spel);
    }
    
    /**
     * remove saved game from database
     * 
     * @param saveID id of the saved game
     */
    public void verwijderSpelManueel(int saveID) {
        mapper.verwijderSpelManueel(saveID);
    }
    
    /**
     * remove challenge from database
     * 
     * @param titel title of the game
     * @param uitdagerID ID of the challenger
     */
    public void verwijderChallengeManueel(String titel, int uitdagerID) {
        mapper.verwijderChallengeManueel(titel, uitdagerID);
    }

    /**
     * removes the game from database
     * 
     * @param spel game that needs to be
     */
    public void verwijder(Spel spel) {
        mapper.verwijder(spel);
    }

    /**
     * add new challenge to database
     * 
     * @param uitgedaagdeSpeler challenged player
     * @param naam name of the challenge
     * @param spelerId ID of challanger
     * @param spel game played in this challange
     */
    public void voegUitdagingToe(int uitgedaagdeSpeler, String naam, int spelerId, Spel spel) {
        mapper.voegUitdagingToe(uitgedaagdeSpeler, naam, spelerId, spel);
    }
    
    /**
     * save score in database if only one of the players have ended the game
     * 
     * @param spel played game
     * @param speler player who played the challange
     * @param naam name of player
     * @param gewonnen  won or lost, 1 or 0 (1: won, 0: lost)
     * @param voltooid ended or not ended, 1 or 0 (1: completed, 0: not completed)
     */
    public void eindeChallenge(Spel spel, Speler speler, String naam, int gewonnen, int voltooid){
        mapper.eindeChallenge(speler, spel, naam, gewonnen, voltooid);
    }
    
    /**
     * returns challenges of a specific challenger with specific name
     * 
     * @param uitdagerID id of the challenger
     * @param gameNaam name of the challenge
     * @return list with attributes from the challenge
     */
    public List<String[]> geefChallenges(int uitdagerID, String gameNaam){
        return mapper.geefChallenges(uitdagerID, gameNaam);
    }
    
    /**
     * remove challenge from database if challenge has ended
     * 
     * @param Challenge1ID id of challenge record 1 in database
     * @param Challenge2ID id of challenge record 2 in database
     */
    public void verwijderChallenges(int Challenge1ID, int Challenge2ID){
        mapper.verwijderChallenges(Challenge1ID, Challenge2ID);
    }
    
    /**
     * returns all the names of the saved challenges for the logged in player
     * 
     * @param speler player
     * @return list with names of challenges
     */
    public List<String[]> geefBestaandeOpgeslagenChallengeNamen(Speler speler){
        return mapper.geefBestaandeOpgeslagenChallengeNamen(speler);
    }
}
