package kristijandelivuk.com.nadjiprijevoz.model;

/**
 * Created by kdelivuk on 17/07/15.
 */
public class User {

    private String username;
    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private PointModel currentLocation;

    public User(String username, String name, String surname, String phoneNumber, String email) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
