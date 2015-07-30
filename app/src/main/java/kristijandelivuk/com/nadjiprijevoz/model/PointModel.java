package kristijandelivuk.com.nadjiprijevoz.model;

/**
 * Created by kdelivuk on 25/07/15.
 */
public class PointModel {

    private double mLatitude;
    private double mLongitude;

    public PointModel(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }
}
