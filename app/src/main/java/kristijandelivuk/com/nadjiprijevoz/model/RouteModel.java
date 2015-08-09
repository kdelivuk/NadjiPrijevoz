package kristijandelivuk.com.nadjiprijevoz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by kdelivuk on 17/07/15.
 */
public class RouteModel implements Parcelable {

    private String mDestination;
    private String mStartingPoint;
    private User mCreator;
    private ArrayList<User> mPassangers;
    private ArrayList<PointModel> mPoints;
    private int mSpacesAvailable;
    private String id;

    public RouteModel(String destination, String startingPoint, User creator, ArrayList<User> passangers, ArrayList<PointModel> points, int spacesAvailable) {
        mDestination = destination;
        mStartingPoint = startingPoint;
        mCreator = creator;
        mPassangers = passangers;
        mPoints = points;
        mSpacesAvailable = spacesAvailable;
    }

    protected RouteModel(Parcel in) {
        mDestination = in.readString();
        mStartingPoint = in.readString();
        mCreator = (User) in.readValue(User.class.getClassLoader());
        if (in.readByte() == 0x01) {
            mPassangers = new ArrayList<User>();
            in.readList(mPassangers, User.class.getClassLoader());
        } else {
            mPassangers = null;
        }
        if (in.readByte() == 0x01) {
            mPoints = new ArrayList<PointModel>();
            in.readList(mPoints, PointModel.class.getClassLoader());
        } else {
            mPoints = null;
        }
        mSpacesAvailable = in.readInt();
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDestination);
        dest.writeString(mStartingPoint);
        dest.writeValue(mCreator);
        if (mPassangers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mPassangers);
        }
        if (mPoints == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mPoints);
        }
        dest.writeInt(mSpacesAvailable);
        dest.writeString(id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RouteModel> CREATOR = new Parcelable.Creator<RouteModel>() {
        @Override
        public RouteModel createFromParcel(Parcel in) {
            return new RouteModel(in);
        }

        @Override
        public RouteModel[] newArray(int size) {
            return new RouteModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassangers(ArrayList<User> passangers) {
        mPassangers = passangers;
    }

    public ArrayList<User> getPassangers() {
        return mPassangers;
    }

    public ArrayList<PointModel> getPoints() {
        return mPoints;
    }

    public void setPoints(ArrayList<PointModel> points) {
        mPoints = points;
    }

    public int getSpacesAvailable() {
        return mSpacesAvailable;
    }

    public void setSpacesAvailable(int spacesAvailable) {
        mSpacesAvailable = spacesAvailable;
    }

    public String getDestination() {
        return mDestination;
    }

    public void setDestination(String destination) {
        mDestination = destination;
    }

    public String getStartingPoint() {
        return mStartingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        mStartingPoint = startingPoint;
    }

    public User getCreator() {
        return mCreator;
    }

    public void setCreator(User creator) {
        mCreator = creator;
    }

}
