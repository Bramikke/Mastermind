package persistentie;

import domein.GewonnenSpellen;
import domein.Speler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GewonnenSpellenMapper {

    private static final String INSERT_SPEL = "INSERT INTO GewonnenSpellen (SpelerID, MoeilijkheidsGraad, Gewonnen, AantalPogingen)"
            + "VALUES (?, ?, ?, ?)";

    private static final String GEWONNEN_GAMES = "SELECT MoeilijkheidsGraad, COUNT(Gewonnen) AS TotGewonnen FROM GewonnenSpellen left join Speler on GewonnenSpellen.SpelerID = Speler.spelerID WHERE (Speler.SpelerID = ? AND Gewonnen = 1) "
            + "GROUP BY MoeilijkheidsGraad "
            + "ORDER BY MoeilijkheidsGraad";

    private static final String AANTAL_WINS = "SELECT COUNT(Gewonnen) AS Gewonnen FROM GewonnenSpellen WHERE SpelerID = ? AND Gewonnen = 1 AND MoeilijkheidsGraad = ? "
            + "GROUP BY MoeilijkheidsGraad ORDER BY MoeilijkheidsGraad";
    
    private static final String VIND_SPELERS = "SELECT Speler.SpelerID, voornaam, naam, profielfoto "
            + "FROM GewonnenSpellen INNER JOIN Speler ON Speler.spelerID = GewonnenSpellen.SpelerID "
            + "WHERE Gewonnen = 1 AND MoeilijkheidsGraad=?-1 AND Speler.SpelerID != ? "
            + "GROUP BY SpelerID, MoeilijkheidsGraad HAVING count(Gewonnen)>=20 ORDER BY MoeilijkheidsGraad";

    public void voegToe(GewonnenSpellen spel) {
        try (
                Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(INSERT_SPEL)) {
            query.setInt(1, spel.getSpelerId());
            query.setInt(2, spel.getMoeilijkheidsGraad());
            query.setInt(3, spel.getGewonnen());
            query.setInt(4, spel.getAantalPogingen());
            query.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public List<String[]> vindSpelers(int level, int id){
        List<String[]> UitTeDagenSpelers = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(VIND_SPELERS)) {
            query.setInt(1, level);
            query.setInt(2, id);
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    String[] Speler = new String[4];
                    Speler[0] = ""+ rs.getInt("SpelerID");
                    Speler[1] = rs.getString("voornaam");
                    Speler[2] = rs.getString("naam");
                    Speler[3] = rs.getString("profielfoto");
                    UitTeDagenSpelers.add(Speler);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return UitTeDagenSpelers;
    } 

    public int aantalWins(GewonnenSpellen spel) {
        int gewonnen = 0;
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(AANTAL_WINS)) {
            query.setInt(1, spel.getSpelerId());
            query.setInt(2, spel.getMoeilijkheidsGraad());
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    gewonnen = rs.getInt("Gewonnen");
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return gewonnen + 1;
    }

    public int[][] geefGewonnenSpellen(Speler speler) {
        int[][] spellen = new int[3][2];
        int rij = 0;

        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(GEWONNEN_GAMES)) {
            query.setInt(1, speler.getId());
            try (ResultSet rs = query.executeQuery()) {

                while (rs.next()) {
                    int moeilijkheidsgraad = rs.getInt("MoeilijkheidsGraad");
                    int gewonnen = rs.getInt("TotGewonnen");
                    spellen[rij][0] = moeilijkheidsgraad;
                    spellen[rij][1] = gewonnen;
                    rij++;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        //spellen[1][1] += 20; //CHEAT
        return spellen;
    }
}
