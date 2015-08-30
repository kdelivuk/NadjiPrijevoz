package kristijandelivuk.com.nadjiprijevoz.Screens;

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

import java.util.ArrayList;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.model.User;

/**
 * Created by kdelivuk on 08/08/15.
 */
public class RouteDetailNavigationAdapter extends RecyclerView.Adapter<RouteDetailNavigationAdapter.RouteDetailNavigationViewHolder>{


    List<User> mPassangers = new ArrayList<>();

    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private ClickListener mListener;

    public RouteDetailNavigationAdapter(Context context, ArrayList<User> passangers) {
        mLayoutInflater = LayoutInflater.from(context);
        mPassangers = passangers;
        Log.v("mPassangers" , mPassangers.size() + "");

        for (User passanger : mPassangers) {
            Log.v("mPassanger user" , passanger.getUsername());
        }
    }

    @Override
    public RouteDetailNavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.route_detail_navigation_item, parent, false);
        RouteDetailNavigationViewHolder holder = new RouteDetailNavigationViewHolder(view);
        return holder;
    }

    public void setOnClickListener(RouteDetailNavigationAdapter.ClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mPassangers.size();
    }

    @Override
    public void onBindViewHolder(RouteDetailNavigationViewHolder holder, int position) {
        User currentItem = mPassangers.get(position);
        holder.mTextNameSurname.setText(currentItem.getName() + " " + currentItem.getSurname());
        holder.mTextEmail.setText(currentItem.getEmail());
        holder.mTextPhone.setText(currentItem.getPhoneNumber());


        byte[] data = currentItem.getData();

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        holder.mImageProfile.setImageBitmap(bitmap);

    }

    class RouteDetailNavigationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageProfile;
        TextView mTextNameSurname;
        TextView mTextPhone;
        TextView mTextEmail;


        public RouteDetailNavigationViewHolder(View itemView) {
            super(itemView);

            Typeface gidole = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Gidole_Regular.ttf");

            itemView.setOnClickListener(this);
            mTextNameSurname = (TextView) itemView.findViewById(R.id.rowRouteDetailText);
            mTextPhone = (TextView) itemView.findViewById(R.id.rowPhoneNumberText);
            mTextEmail = (TextView) itemView.findViewById(R.id.rowEmailText);
            mImageProfile = (ImageView) itemView.findViewById(R.id.imageProfileSmall);

            mTextNameSurname.setTypeface(gidole);
            mTextPhone.setTypeface(gidole);
            mTextEmail.setTypeface(gidole);

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