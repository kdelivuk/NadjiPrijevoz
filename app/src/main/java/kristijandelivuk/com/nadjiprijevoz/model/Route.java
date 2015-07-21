package kristijandelivuk.com.nadjiprijevoz.model;

/**
 * Created by kdelivuk on 17/07/15.
 */
public class Route {

    private String mDestination;
    private String mStartingPoint;
    private User mCreator;
    private User[] mPassangers;
    private int mSpacesAvailable;

    public Route(String destination, String startingPoint, User creator, int spacesAvailable) {
        mDestination = destination;
        mStartingPoint = startingPoint;
        mCreator = creator;
        mSpacesAvailable = spacesAvailable;
        mPassangers = new User[spacesAvailable];
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

    public User[] getPassangers() {
        return mPassangers;
    }

    public void setPassangers(User[] passangers) {
        mPassangers = passangers;
    }
}
