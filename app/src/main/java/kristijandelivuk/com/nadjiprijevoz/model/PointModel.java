package kristijandelivuk.com.nadjiprijevoz.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kdelivuk on 25/07/15.
 */
public class PointModel implements Parcelable {

    private double mLatitude;
    private double mLongitude;

    public PointModel(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    protected PointModel(Parcel in) {
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PointModel> CREATOR = new Parcelable.Creator<PointModel>() {
        @Override
        public PointModel createFromParcel(Parcel in) {
            return new PointModel(in);
        }

        @Override
        public PointModel[] newArray(int size) {
            return new PointModel[size];
        }
    };

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
