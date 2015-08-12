package kristijandelivuk.com.nadjiprijevoz.model.navigation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.Collections;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;

/**
 * Created by kdelivuk on 30/07/15.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.NavigationViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    List<NavigationDrawerItem> mNavigationItems = Collections.emptyList();

    private LayoutInflater mLayoutInflater;
    private ClickListener mListener;

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> navigationItems) {
        mLayoutInflater = LayoutInflater.from(context);
        mNavigationItems = navigationItems;
    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View view = mLayoutInflater.inflate(R.layout.navigation_item_row, parent, false);

            NavigationViewHolder holder = new NavigationViewHolder(view,viewType); //Creating ViewHolder and passing the object of type view

            return holder; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_header_item,parent,false); //Inflating the layout

            NavigationViewHolder vhHeader = new NavigationViewHolder(view,viewType); //Creating ViewHolder and passing the object of type view

            return vhHeader; //returning the object created


        }

        return null;
    }

    public void setOnClickListener(NavigationDrawerAdapter.ClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mNavigationItems.size() + 1;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(NavigationViewHolder holder, int position) {

        if(holder.holderId ==1) {
            holder.mText.setText(mNavigationItems.get(position - 1).getTitle()); // Setting the Text with the array of our Titles
            holder.mImageView.setImageResource(mNavigationItems.get(position - 1).getResId());// Settimg the image with array of our icons
        }
        else{

            //holder.profile.setImageResource(R.mipmap.profile_icon);           // Similarly we set the resources for header view
            //holder.Name.setText(ParseUser.getCurrentUser().getString("name"));
            //holder.email.setText(ParseUser.getCurrentUser().getString("email"));
        }

    }

    class NavigationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int holderId;

        TextView mText;
        ImageView mImageView;

        ImageView profile;
        TextView Name;
        TextView email;

        public NavigationViewHolder(View itemView, int ViewType) {
            super(itemView);

            if(ViewType == TYPE_ITEM) {
                itemView.setOnClickListener(this);
                mText = (TextView) itemView.findViewById(R.id.rowText);
                mImageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                holderId = 1;
            } else{


                Name = (TextView) itemView.findViewById(R.id.name);         // Creating Text View object from header.xml for name
                email = (TextView) itemView.findViewById(R.id.email);       // Creating Text View object from header.xml for email
                profile = (ImageView) itemView.findViewById(R.id.circleView);// Creating Image view object from header.xml for profile pic
                holderId = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }

        }


        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.selectedItem(v , getPosition());
            }
        }
    }

    public interface ClickListener {

        void selectedItem(View view, int position);
    }
}
