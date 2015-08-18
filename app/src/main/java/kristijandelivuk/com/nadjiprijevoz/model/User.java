package kristijandelivuk.com.nadjiprijevoz.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kdelivuk on 17/07/15.
 */
public class User implements Parcelable {

    private String username;
    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private PointModel currentLocation;
    private byte[] data;

    public User(String username, String name, String surname, String phoneNumber, String email, byte[] data) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.data = data;
    }

    protected User(Parcel in) {
        username = in.readString();
        name = in.readString();
        surname = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
        data = new byte[in.readInt()];
        in.readByteArray(data);
        currentLocation = (PointModel) in.readValue(PointModel.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(phoneNumber);
        dest.writeString(email);
        dest.writeInt(data.length);
        dest.writeByteArray(data);
        dest.writeValue(currentLocation);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public PointModel getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(PointModel currentLocation) {
        this.currentLocation = currentLocation;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
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
