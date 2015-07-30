package kristijandelivuk.com.nadjiprijevoz.model;

import java.util.ArrayList;

/**
 * Created by kdelivuk on 17/07/15.
 */
public class RouteModel {

    private String mDestination;
    private String mStartingPoint;
    private User mCreator;
    private ArrayList<User> mPassangers;
    private ArrayList<PointModel> mPoints;
    private int mSpacesAvailable;

    public RouteModel(String destination, String startingPoint, User creator, ArrayList<User> passangers, ArrayList<PointModel> points, int spacesAvailable) {
        mDestination = destination;
        mStartingPoint = startingPoint;
        mCreator = creator;
        mPassangers = passangers;
        mPoints = points;
        mSpacesAvailable = spacesAvailable;
    }

    public void setPassangers(ArrayList<User> passangers) {
        mPassangers = passangers;
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
