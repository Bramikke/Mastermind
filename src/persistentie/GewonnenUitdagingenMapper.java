package persistentie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GewonnenUitdagingenMapper {

    private static final String INSERT_CHALLENGE = "INSERT INTO GewonnenUitdagingen (SpelerID, Punten, Level) "
            + "VALUES (?, ?, ?);";
    private static final String GET_SCORES = "SELECT Speler.SpelerID, naam, voornaam, SUM(Punten) AS PuntenTotaal, COUNT(case when Punten = 3 then 1 else null end) AS AantalWins, Level "
            + "FROM GewonnenUitdagingen INNER JOIN Speler ON GewonnenUitdagingen.SpelerID = Speler.spelerID "
            + "WHERE Level = ? "
            + "GROUP BY SpelerID, Level "
            + "ORDER BY PuntenTotaal DESC, AantalWins DESC";

    public void voegToe(int spelerID, int punten, int level) {
        try (
                Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(INSERT_CHALLENGE)) {
            query.setInt(1, spelerID);
            query.setInt(2, punten);
            query.setInt(3, level);
            query.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public List<String[]> geefScores(int level) {
        List<String[]> scoreBoard = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(GET_SCORES)) {
                query.setInt(1, level);
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    String[] scoreBoardRecord = new String[6];
                    scoreBoardRecord[0] = ""+ rs.getInt("SpelerID");
                    scoreBoardRecord[1] = rs.getString("naam");
                    scoreBoardRecord[2] = rs.getString("voornaam");
                    scoreBoardRecord[3] = rs.getString("PuntenTotaal");
                    scoreBoardRecord[4] = rs.getString("AantalWins");
                    scoreBoardRecord[5] = rs.getString("Level");
                    scoreBoard.add(scoreBoardRecord);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return scoreBoard;
    }
}
