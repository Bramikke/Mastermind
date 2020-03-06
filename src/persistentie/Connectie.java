package persistentie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Connectie {

    public static final String JDBC_URL = "jdbc:sqlite:database.db";

    public static void initDB() {
        Connection conn = null;
        Statement stm = null;
        try {
            conn = DriverManager.getConnection(JDBC_URL);
            System.out.println("Connection to SQLite has been established.");

            stm = conn.createStatement();
            stm.execute("CREATE TABLE IF NOT EXISTS `GewonnenSpellen` (\n" +
                    "  `SpelID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "  `SpelerID` INTEGER NOT NULL,\n" +
                    "  `MoeilijkheidsGraad` INTEGER NOT NULL,\n" +
                    "  `Gewonnen` INTEGER NOT NULL DEFAULT '0',\n" +
                    "  `AantalPogingen` INTEGER NOT NULL,\n" +
                    "  UNIQUE(`SpelID`)\n" +
                    ");");
            stm.execute("CREATE TABLE IF NOT EXISTS `GewonnenUitdagingen` (\n" +
                    "  `UitdagingID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "  `SpelerID` INTEGER NOT NULL,\n" +
                    "  `Punten` INTEGER NOT NULL,\n" +
                    "  `Level` INTEGER NOT NULL\n" +
                    ");");
            stm.execute("CREATE TABLE IF NOT EXISTS `SpelOpslaan` (\n" +
                    "  `OpslagID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "  `SpelerID` INTEGER NOT NULL,\n" +
                    "  `Naam` text DEFAULT NULL,\n" +
                    "  `Level` INTEGER DEFAULT NULL,\n" +
                    "  `Oplossingscode` text DEFAULT NULL,\n" +
                    "  `VorigeCodes` text DEFAULT NULL,\n" +
                    "  `CorrectieCodes` text DEFAULT NULL,\n" +
                    "  `UitdagingVanSpeler` INTEGER DEFAULT NULL,\n" +
                    "  `Gewonnen` INTEGER DEFAULT NULL,\n" +
                    "  `UitdagingVoltooid` INTEGER DEFAULT NULL\n" +
                    ");");
            stm.execute("CREATE TABLE IF NOT EXISTS `Speler` (\n" +
                    "  `spelerID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "  `naam` text NOT NULL,\n" +
                    "  `voornaam` text NOT NULL,\n" +
                    "  `emailadres` text NOT NULL,\n" +
                    "  `wachtwoord` text NOT NULL,\n" +
                    "  `geboortedatum` date NOT NULL,\n" +
                    "  `laatsteLogin` date NOT NULL,\n" +
                    "  `profielfoto` text DEFAULT NULL,\n" +
                    "  `achtergrond` text DEFAULT NULL,\n" +
                    "  UNIQUE(`emailadres`)\n" +
                    ");");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if(stm != null) {
                    stm.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
