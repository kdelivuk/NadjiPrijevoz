package kristijandelivuk.com.nadjiprijevoz.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;

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
        holder.mText.setText(currentItem.getName() + " " + currentItem.getSurname() + " " + currentItem.getEmail() + " " + currentItem.getPhoneNumber());
        //holder.mImageView.setImageResource(currentItem.getResId());
    }

    class RouteDetailNavigationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mText;


        public RouteDetailNavigationViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            mText = (TextView) itemView.findViewById(R.id.rowRouteDetailText);

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