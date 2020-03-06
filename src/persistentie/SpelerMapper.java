package persistentie;

import domein.Speler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SpelerMapper {

    private static final String INSERT_SPELER = "INSERT INTO Speler (naam, voornaam, emailadres, geboortedatum, wachtwoord, laatsteLogin)"
            + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_LOGIN = "UPDATE Speler SET laatsteLogin=? WHERE spelerID=?";
    private static final String PROFILE_PICTURE = "UPDATE Speler SET profielfoto=? WHERE spelerID=?";
    private static final String BACKGROUND_PICTURE = "UPDATE Speler SET achtergrond=? WHERE spelerID=?";

    public void voegToe(Speler speler) {

        try (
                Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(INSERT_SPELER)) {
            query.setString(1, speler.getNaam());
            query.setString(2, speler.getVoornaam());
            query.setString(3, speler.getEmailadres());
            query.setDate(4, java.sql.Date.valueOf(speler.getGeboorteDatum()));
            query.setString(5, speler.getWachtwoord());
            query.setDate(6, java.sql.Date.valueOf(LocalDate.now()));
            query.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<String[]> geefSpelersToString(int spelerId) {
        List<String[]> spelers = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement("SELECT * FROM Speler WHERE spelerID != ?")) {
            query.setInt(1, spelerId);
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    String[] Speler = new String[4];
                    Speler[0] = "" + rs.getInt("SpelerID");
                    Speler[1] = rs.getString("voornaam");
                    Speler[2] = rs.getString("naam");
                    Speler[3] = rs.getString("profielfoto");
                    spelers.add(Speler);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return spelers;
    }

    public List<Speler> geefSpelers() {
        List<Speler> spelers = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement("SELECT * FROM Speler");
                ResultSet rs = query.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("spelerID");
                String naam = rs.getString("naam");
                String voornaam = rs.getString("voornaam");
                String emailadres = rs.getString("emailadres");
                LocalDate geboortedatum = rs.getDate("geboortedatum").toLocalDate();
                String wachtwoord = rs.getString("wachtwoord");

                spelers.add(new Speler(id, naam, voornaam, emailadres, geboortedatum, wachtwoord));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return spelers;
    }

    public Speler geefSpeler(String emailadres) {
        Speler speler = null;

        try (Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement("SELECT * FROM Speler WHERE emailadres = ?")) {
            query.setString(1, emailadres);
            try (ResultSet rs = query.executeQuery()) {
                if (!rs.next()) {
                    return speler;
                } else {
                    int id = rs.getInt("spelerID");
                    String naam = rs.getString("naam");
                    String voornaam = rs.getString("voornaam");
                    LocalDate geboortedatum = rs.getDate("geboortedatum").toLocalDate();
                    String wachtwoord = rs.getString("wachtwoord");
                    String profielfoto = rs.getString("profielfoto");
                    String achtergrond = rs.getString("achtergrond");

                    speler = new Speler(id, naam, voornaam, emailadres, geboortedatum, wachtwoord, profielfoto, achtergrond);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return speler;
    }

    public void updateLastLogin(int spelerId) {
        try (
                Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement(UPDATE_LOGIN)) {
            query.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            query.setInt(2, spelerId);
            query.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void updateProfiel(String x, int spelerId){
        try (
                Connection conn = DriverManager.getConnection(Connectie.JDBC_URL);
                PreparedStatement query = conn.prepareStatement("UPDATE Speler SET "+x+" WHERE spelerID=?")) {
            query.setInt(1, spelerId);
            query.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
