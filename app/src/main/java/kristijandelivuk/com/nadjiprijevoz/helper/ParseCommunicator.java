package kristijandelivuk.com.nadjiprijevoz.helper;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kristijandelivuk.com.nadjiprijevoz.model.CommentModel;
import kristijandelivuk.com.nadjiprijevoz.model.PointModel;
import kristijandelivuk.com.nadjiprijevoz.model.RouteModel;
import kristijandelivuk.com.nadjiprijevoz.model.User;

/**
 * Created by kdelivuk on 17/08/15.
 */
public class ParseCommunicator {

    private static ParseCommunicator singleton = new ParseCommunicator( );

    private ParseCommunicator(){ }

    public static ParseCommunicator getInstance( ) {
        return singleton;
    }

    public RouteModel convertToRouteModel(ParseObject item) {

        ParseUser creator = item.getParseUser("creator");

        ParseFile fileObject = (ParseFile) creator.get("profileImage");
        byte[] creatorData = new byte[0];
        try {
            creatorData = fileObject.getData();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        User creatorUser = new User(
                creator.getUsername(),
                creator.getString("name"),
                creator.getString("surname"),
                creator.getString("phone"),
                creator.getEmail(),
                creatorData
        );

        ArrayList<User> passangers = new ArrayList<>();
        JSONArray jsonArrayPassangers = item.getJSONArray("passangers");

        if (jsonArrayPassangers != null) {
            for (int i = 0; i < jsonArrayPassangers.length(); i++) {

                try {
                    JSONObject object = jsonArrayPassangers.getJSONObject(i);
                    Log.v("objectID", object.getString("objectId"));
                    String id = object.getString("objectId");

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("objectId", id);
                    ParseUser parseUser = query.getFirst();

                    ParseFile passangerFileObject = (ParseFile) parseUser.get("profileImage");
                    byte[] passangerData = new byte[0];
                    try {
                        passangerData = passangerFileObject.getData();
                    } catch (com.parse.ParseException ex) {
                        ex.printStackTrace();
                    }

                    passangers.add(new User(
                            parseUser.getUsername(),
                            parseUser.getString("name"),
                            parseUser.getString("surname"),
                            parseUser.getString("phone"),
                            parseUser.getEmail(),
                            passangerData
                    ));
                } catch (ParseException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }



            }
        }

        JSONArray jsonArrayPoints = item.getJSONArray("points");
        final ArrayList<PointModel> points = new ArrayList<PointModel>();

        for (int i = 0; i < jsonArrayPoints.length(); i++) {

            try {
                JSONObject object = jsonArrayPoints.getJSONObject(i);
                //Log.v("objectID", object.getString("objectId"));

                ParseQuery query = ParseQuery.getQuery("Point");
                query.whereEqualTo("objectId", object.getString("objectId"));
                ParseObject pointP = query.getFirst();
                //Log.v("lat", pointP.get("lat").toString());
                //Log.v("lng", pointP.get("lng").toString());
                points.add(new PointModel(pointP.getDouble("lat"), pointP.getDouble("lng")));
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

        }

        ArrayList<CommentModel> comments = new ArrayList<>();
        JSONArray jsonArrayComments = item.getJSONArray("comments");

        if (jsonArrayComments != null) {
            for (int i = 0; i < jsonArrayComments.length(); i++) {

                try {
                    JSONObject object = jsonArrayComments.getJSONObject(i);
                    Log.v("objectID", object.getString("objectId"));
                    String id = object.getString("objectId");

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
                    query.whereEqualTo("objectId", id);
                    ParseObject parseComment = query.getFirst();

                    ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                    userQuery.whereEqualTo("objectId", parseComment.get("creator").toString());
                    ParseUser parseAuthor = userQuery.getFirst();

                    ParseFile passangerFileObject = (ParseFile) parseAuthor.get("profileImage");
                    byte[] authorData = new byte[0];
                    try {
                        authorData = passangerFileObject.getData();
                    } catch (com.parse.ParseException ex) {
                        ex.printStackTrace();
                    }

                    User author = new User(
                            parseAuthor.getUsername(),
                            parseAuthor.getString("name"),
                            parseAuthor.getString("surname"),
                            parseAuthor.getString("phone"),
                            parseAuthor.getEmail(),
                            authorData
                    );

                    comments.add(new CommentModel(parseComment.getString("message") , author));

                } catch (ParseException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }
        }

        RouteModel route = new RouteModel(
                item.getString("destination"),
                item.getString("startingPoint"),
                creatorUser,
                passangers,
                points,
                item.getInt("numberOfSpaces"),
                item.getObjectId(),
                item.getString("time"),
                item.getString("date"),
                comments
        );

        return route;
    }



}
