package kristijandelivuk.com.nadjiprijevoz.Screens;

/**
 * Created by kdelivuk on 29/08/15.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.model.CommentModel;
import kristijandelivuk.com.nadjiprijevoz.model.navigation.NavigationDrawerItem;

/**
 * Created by kdelivuk on 30/07/15.
 */
class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    ArrayList<CommentModel> comments;

    CommentAdapter(ArrayList<CommentModel> comments){
        this.comments = comments;
    }

    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_detail_comment_item, parent, false);
        CommentViewHolder cvh = new CommentViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(CommentAdapter.CommentViewHolder holder, int position) {
        holder.commentText.setText(comments.get(position).getComment());
        holder.commentAuthor.setText(" - " + comments.get(position).getCreator().getName() + " " + comments.get(position).getCreator().getSurname());
    }

    public int getItemCount() {
        Log.v("mcomments", comments.size() + "");
        return comments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView commentText;
        TextView commentAuthor;

        CommentViewHolder(View itemView) {
            super(itemView);

            Typeface gidole = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Gidole_Regular.ttf");

            commentText = (TextView)itemView.findViewById(R.id.textComment);
            commentAuthor = (TextView)itemView.findViewById(R.id.textAuthor);

            commentText.setTypeface(gidole);
            commentAuthor.setTypeface(gidole);

        }
    }
}

