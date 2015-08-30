package kristijandelivuk.com.nadjiprijevoz.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kdelivuk on 29/08/15.
 */
public class CommentModel implements Parcelable {

    private String comment;
    private User creator;

    public CommentModel(String comment, User creator) {
        this.comment = comment;
        this.creator = creator;
    }

    protected CommentModel(Parcel in) {
        comment = in.readString();
        creator = (User) in.readValue(User.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comment);
        dest.writeValue(creator);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CommentModel> CREATOR = new Parcelable.Creator<CommentModel>() {
        @Override
        public CommentModel createFromParcel(Parcel in) {
            return new CommentModel(in);
        }

        @Override
        public CommentModel[] newArray(int size) {
            return new CommentModel[size];
        }
    };

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
