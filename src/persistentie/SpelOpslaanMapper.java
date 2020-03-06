package persistentie;

import domein.Spel;
import domein.Speler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpelOpslaanMapper {

    private static final String INSERT_SAVEGAME = "INSERT INTO SpelOpslaan (SpelerID, Naam, Level, Oplossingscode, VorigeCodes, CorrectieCodes)"
            + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String OVERRIDE_SAVEGAME = "UPDATE SpelOpslaan SET VorigeCodes=?, CorrectieCodes=? WHERE OpslagID=?";
    private static final String OVERRIDE_CHALLENGE = "UPDATE SpelOpslaan SET VorigeCodes=?, CorrectieCodes=? WHERE Naam=? AND SpelerID=?";
    private static final String END_CHALLENGE = "UPDATE SpelOpslaan SET VorigeCodes=?, CorrectieCodes=?, Gewonnen=?, UitdagingVoltooid=? WHERE Naam=? AND SpelerID=?";
    private static final String OPGESLAGEN_NAMEN = "SELECT OpslagID , Naam , Level FROM SpelOpslaan WHERE SpelerID=? AND UitdagingVanSpeler is null";
    private static final String OPGESLAGEN_CHALLENGE_NAMEN = "SELECT OpslagID , Naam , Level FROM SpelOpslaan WHERE UitdagingVanSpeler=? AND SpelerID = ?";
    private static final String OPGESLAGEN_SPEL = "SELECT * FROM SpelOpslaan WHERE SpelerID = ? AND OpslagID = ?;";
    private static final String VERWIJDER_SPEL = "DELETE FROM SpelOpslaan WHERE OpslagID=?";
    private static final String VERWIJDER_CHALLENGE = "DELETE FROM SpelOpslaan "
            + "WHERE Naam = ? AND UitdagingVanSpeler = ?";
    private static final String ADD_UITDAGING = "INSERT INTO SpelOpslaan (SpelerID, Naam, Level, Oplossingscode, UitdagingVanSpeler, Gewonnen, UitdagingVoltooid) "
            + "VALUES (?, ?, ?, ?, ?,0, 0); ";
    private static final String OPGESLAGEN_CHALLENGES = "SELECT OpslagID , SpelOpslaan.Naam AS SpelNaam , Level, UitdagingVanSpeler, Speler.naam, voornaam "
            + "FROM SpelOpslaan INNER JOIN Speler ON SpelOpslaan.UitdagingVanSpeler = Speler.spelerID "
            + "WHERE SpelOpslaan.SpelerID=? AND UitdagingVoltooid = 0";
    private static final String GEEF_CHALLENGE = "SELECT OpslagID, SpelerID, Naam, Level, VorigeCodes, UitdagingVanSpeler, Gewonnen, UitdagingVoltooid "
            + "FROM SpelOpslaan WHERE Naam = ? and UitdagingVanSpeler = ?";

    public void voegToe(Speler speler, Spel spel, String naam) {
        List<int[]> pogingen = spel.getPogingen();
        String outputPogingen = ListIntToString(pogingen);
        List<int[]> controles = spel.getControles();
        String outputCorrectie = ListIntToString(controles);
        try (
                Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(INSERT_SAVEGAME)) {
            query.setInt(1, speler.getId());
            query.setString(2, naam);
            query.setInt(3, spel.getLevel());
            query.setString(4, Arrays.toString(spel.getCode()));
            query.setString(5, outputPogingen);
            query.setString(6, outputCorrectie);
            query.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void overschrijfChallenge(Spel spel, Speler speler, String naam) {
        List<int[]> pogingen = spel.getPogingen();
        String outputPogingen = ListIntToString(pogingen);
        List<int[]> controles = spel.getControles();
        String outputCorrectie = ListIntToString(controles);
        int spelerID = speler.getId();
        try (
                Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(OVERRIDE_CHALLENGE)) {
            query.setString(1, outputPogingen);
            query.setString(2, outputCorrectie);
            query.setString(3, naam);
            query.setInt(4, spelerID);
            query.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void eindeChallenge(Speler speler, Spel spel, String naam, int gewonnen, int voltooid) {
        List<int[]> pogingen = spel.getPogingen();
        String outputPogingen = ListIntToString(pogingen);
        List<int[]> controles = spel.getControles();
        String outputCorrectie = ListIntToString(controles);
        int spelerID = speler.getId();
        try (
                Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(END_CHALLENGE)) {
            query.setString(1, outputPogingen);
            query.setString(2, outputCorrectie);
            query.setInt(3, gewonnen);
            query.setInt(4, voltooid);
            query.setString(5, naam);
            query.setInt(6, spelerID);
            query.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public List<String[]> geefChallenges(int UitdagerID, String gameNaam){
        List<String[]> challenges = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(GEEF_CHALLENGE)) {
            query.setString(1, gameNaam);
            query.setInt(2, UitdagerID);
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    int opslagID = rs.getInt("OpslagID");
                    int spelerID = rs.getInt("SpelerID");
                    String naam = rs.getString("Naam");
                    int level = rs.getInt("Level");
                    String vorigeCodes = rs.getString("VorigeCodes");
                    int uitdagingVanSpeler = rs.getInt("UitdagingVanSpeler");
                    int gewonnen = rs.getInt("Gewonnen");
                    int uitdagingVoltooid= rs.getInt("UitdagingVoltooid");
                    challenges.add(new String[]{"" + opslagID, "" + spelerID, naam, "" + level, vorigeCodes, ""+uitdagingVanSpeler, ""+gewonnen, ""+uitdagingVoltooid});
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return challenges;
    }
    
    public void verwijderChallenges(int Challenge1ID, int Challenge2ID){
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(VERWIJDER_SPEL)) {
            query.setInt(1, Challenge1ID);
            query.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(VERWIJDER_SPEL)) {
            query.setInt(1, Challenge2ID);
            query.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void overschrijfSpel(Spel spel) {
        List<int[]> pogingen = spel.getPogingen();
        String outputPogingen = ListIntToString(pogingen);
        List<int[]> controles = spel.getControles();
        String outputCorrectie = ListIntToString(controles);
        try (
                Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(OVERRIDE_SAVEGAME)) {
            query.setString(1, outputPogingen);
            query.setString(2, outputCorrectie);
            query.setInt(3, spel.getOpgeslagenSpelID());
            query.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void verwijderSpelManueel(int saveID) {
        try (
                Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(VERWIJDER_SPEL)) {
            query.setInt(1, saveID);
            query.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void verwijderChallengeManueel(String titel, int uitdagerID) {
        try (
                Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(VERWIJDER_CHALLENGE)) {
            query.setString(1, titel);
            query.setInt(2, uitdagerID);
            query.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void verwijder(Spel spel) {
        try (
                Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(VERWIJDER_SPEL)) {
            query.setInt(1, spel.getOpgeslagenSpelID());
            query.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public List<String[]> geefBestaandeOpgeslagenChallengeNamen(Speler speler){
         List<String[]> namen = new ArrayList<>();
        int rij = 0;
        int spelerID = speler.getId();
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(OPGESLAGEN_CHALLENGE_NAMEN)) {
            query.setInt(1, spelerID);
            query.setInt(2, spelerID);
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    String[] naamRij = new String[3];
                    int opslagID = rs.getInt("OpslagID");
                    String naam = rs.getString("Naam");
                    String level = rs.getString("Level");
                    naamRij[0] = "" + opslagID;
                    naamRij[1] = naam;
                    naamRij[2] = level;
                    namen.add(naamRij);
                    rij++;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return namen;
    }

    public List<String[]> geefOpgeslagenSpellenNamen(Speler speler) {
        List<String[]> namen = new ArrayList<>();
        int rij = 0;
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(OPGESLAGEN_NAMEN)) {
            query.setInt(1, speler.getId());
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    String[] naamRij = new String[3];
                    int opslagID = rs.getInt("OpslagID");
                    String naam = rs.getString("Naam");
                    String level = rs.getString("Level");
                    naamRij[0] = "" + opslagID;
                    naamRij[1] = naam;
                    naamRij[2] = level;
                    namen.add(naamRij);
                    rij++;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return namen;
    }

    public List<String[]> geefOpgeslagenChallengeNamen(Speler speler) {
        List<String[]> namen = new ArrayList<>();
        int rij = 0;
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(OPGESLAGEN_CHALLENGES)) {
            query.setInt(1, speler.getId());
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    String[] naamRij = new String[6];
                    int opslagID = rs.getInt("OpslagID");
                    String SpelNaam = rs.getString("SpelNaam");
                    String level = rs.getString("Level");
                    int uitdagerID = rs.getInt("UitdagingVanSpeler");
                    String naam = rs.getString("naam");
                    String voornaam = rs.getString("voornaam");
                    naamRij[0] = "" + opslagID;
                    naamRij[1] = SpelNaam;
                    naamRij[2] = level;
                    naamRij[3] = ""+uitdagerID;
                    naamRij[4] = naam;
                    naamRij[5] = voornaam;
                    namen.add(naamRij);
                    rij++;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return namen;
    }

    public String[] geefOpgeslagenSpel(Speler speler, String opslagID) {
        String[] opgeslagenSpel = new String[6];
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(OPGESLAGEN_SPEL)) {
            query.setInt(1, speler.getId());
            query.setString(2, opslagID);
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    int spelerID = rs.getInt("SpelerID");
                    String naam = rs.getString("Naam");
                    int level = rs.getInt("Level");
                    String oplossingscode = rs.getString("Oplossingscode");
                    String vorigeCodes = rs.getString("VorigeCodes");
                    String correctieCodes = rs.getString("CorrectieCodes");
                    opgeslagenSpel = new String[]{opslagID, "" + spelerID, naam, "" + level, oplossingscode, vorigeCodes, correctieCodes};
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return opgeslagenSpel;
    }

    public String ListStringToString(List<String[]> list) {
        String output;
        if (list.size() > 0) {
            output = "[";
            for (String[] listitem : list) {
                output += Arrays.toString(listitem) + ", ";
            }
            output = output.substring(0, output.length() - 2);
            output += "]";
            return output;
        }
        return null;
    }

    public String ListIntToString(List<int[]> list) {
        String output;

        if (list.size() > 0) {
            output = "[";
            for (int[] correctie : list) {
                output += Arrays.toString(correctie) + ", ";
            }
            output = output.substring(0, output.length() - 2);
            output += "]";
            return output;
        }
        return null;
    }

    public void voegUitdagingToe(int uitgedaagdeSpeler, String naam, int spelerId, Spel spel) {
        int level = spel.getLevel();
        String code = Arrays.toString(spel.getCode());
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(ADD_UITDAGING)) {
            query.setInt(1, spelerId);
            query.setString(2, naam);
            query.setInt(3, level);
            query.setString(4, code);
            query.setInt(5, spelerId);
            query.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(ADD_UITDAGING)) {
            query.setInt(1, uitgedaagdeSpeler);
            query.setString(2, naam);
            query.setInt(3, level);
            query.setString(4, code);
            query.setInt(5, spelerId);
            query.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
