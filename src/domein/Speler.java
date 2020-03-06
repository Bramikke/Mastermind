package domein;

import exceptions.VerplichtVeldException;
import java.time.LocalDate;

/**
 *
 * @author Groep2
 */
public class Speler {

    private int id;
    private String naam;
    private String voornaam;
    private String email;
    private String wachtwoord;
    private LocalDate geboorteDatum;
    private String profielfoto;
    private String achtergrond;

    /**
     * Initialize Speler
     * 
     * @param id ID of player
     * @param naam last name of player
     * @param voornaam first name of player
     * @param emailadres email address
     * @param geboorteDatum date of birth 
     * @param wachtwoord password
     * @param profielfoto profile picture
     * @param achtergrond background picture
     */
    public Speler(int id, String naam, String voornaam, String emailadres, LocalDate geboorteDatum, String wachtwoord, String profielfoto, String achtergrond) {
        setId(id);
        setNaam(naam);
        setVoornaam(voornaam);
        setEmail(emailadres);
        setGeboorteDatum(geboorteDatum);
        setWachtwoord(wachtwoord);
        setProfielfoto(profielfoto);
        setAchtergrond(achtergrond);
    }

    /**
     * Initialize Speler
     * 
     * @param id ID of player
     * @param naam last name of player
     * @param voornaam first name of player
     * @param emailadres email address
     * @param geboorteDatum date of birth 
     * @param wachtwoord password
     */
    public Speler(int id, String naam, String voornaam, String emailadres, LocalDate geboorteDatum, String wachtwoord) {
        this(id, naam, voornaam, emailadres, geboorteDatum, wachtwoord, null, null);
    }

    /**
     * Initialize Speler
     * 
     * @param naam last name of player
     * @param voornaam first name of player
     * @param emailadres email address
     * @param geboorteDatum date of birth 
     * @param wachtwoord password
     */
    public Speler(String naam, String voornaam, String emailadres, LocalDate geboorteDatum, String wachtwoord) {
        this(0, naam, voornaam, emailadres, geboorteDatum, wachtwoord);
    }

    /**
     * gives ID of player
     * 
     * @return ID of player
     */
    public int getId() {
        return this.id;
    }

    /**
     *set ID of player
     * 
     * @param id ID of player
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * gives last name of player
     * 
     * @return name of player
     */
    public String getNaam() {
        return this.naam;
    }

    /**
     * set last name of player
     * 
     * @param naam last name of player
     */
    private void setNaam(String naam) {
        if (naam == null || naam.length() == 0) {
            throw new VerplichtVeldException("Elke speler heeft verplicht een naam.");
        }
        this.naam = naam;
    }

    /**
     * gives first name of player
     *
     * @return first name of player
     */
    public String getVoornaam() {
        return this.voornaam;
    }

    /**
     * set first name of player
     * 
     * @param voornaam first name of player
     */
    private void setVoornaam(String voornaam) {
        if (voornaam == null || voornaam.length() == 0) {
            throw new VerplichtVeldException("Elke speler heeft verplicht een voornaam.");
        }
        this.voornaam = voornaam;
    }

    /**
     * gives email address of player
     *
     * @return email address of player
     */
    public String getEmailadres() {
        return this.email;
    }

    /**
     * set email address of player
     * 
     * @param email email address of player
     */
    private void setEmail(String email) {
        if (email == null || email.length() == 0) {
            throw new VerplichtVeldException("Elke speler heeft verplicht een e-mailadres.");
        }
        this.email = email;
    }

    /**
     * gives password of player
     *
     * @return password of player
     */
    public String getWachtwoord() {
        return this.wachtwoord;
    }

    /**
     * set password of player
     * 
     * @param email password of player
     */
    private void setWachtwoord(String wachtwoord) {
        if (wachtwoord == null || wachtwoord.length() == 0) {
            throw new VerplichtVeldException("Elke speler heeft verplicht een wachtwoord.");
        }
        this.wachtwoord = wachtwoord;
    }

    /**
     * gives date of birth of player
     *
     * @return date of birth of player
     */
    public LocalDate getGeboorteDatum() {
        return this.geboorteDatum;
    }
    
    /**
     * set date of birth of player
     * 
     * @param email date of birth of player
     */
    private void setGeboorteDatum(LocalDate geboorteDatum) {
        this.geboorteDatum = geboorteDatum;
    }

    /**
     * set profile picture of player
     *
     * @param profielfoto profile picture
     */
    public void setProfielfoto(String profielfoto) {
        this.profielfoto = profielfoto;
    }

    /**
     * set background picture
     *
     * @param achtergrond background picture
     */
    public void setAchtergrond(String achtergrond) {
        this.achtergrond = achtergrond;
    }

    /**
     * gives profile picture of player
     *
     * @return profile picture of player
     */
    public String getProfielfoto() {
        return profielfoto;
    }

    /**
     * gives background picture of player
     *
     * @return background picture of player
     */
    public String getAchtergrond() {
        return achtergrond;
    }
}
