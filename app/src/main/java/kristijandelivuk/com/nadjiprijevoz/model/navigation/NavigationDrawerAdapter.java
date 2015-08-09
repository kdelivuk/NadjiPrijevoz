package kristijandelivuk.com.nadjiprijevoz.model.navigation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;

/**
 * Created by kdelivuk on 30/07/15.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.NavigationViewHolder> {

    List<NavigationDrawerItem> mNavigationItems = Collections.emptyList();

    private LayoutInflater mLayoutInflater;
    private ClickListener mListener;

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> navigationItems) {
        mLayoutInflater = LayoutInflater.from(context);
        mNavigationItems = navigationItems;
    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.navigation_item_row, parent, false);
        NavigationViewHolder holder = new NavigationViewHolder(view);
        return holder;
    }

    public void setOnClickListener(NavigationDrawerAdapter.ClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mNavigationItems.size();
    }

    @Override
    public void onBindViewHolder(NavigationViewHolder holder, int position) {
        NavigationDrawerItem currentItem = mNavigationItems.get(position);
        holder.mText.setText(currentItem.getTitle());
        //holder.mImageView.setImageResource(currentItem.getResId());
    }

    class NavigationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mText;
        ImageView mImageView;


        public NavigationViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            mText = (TextView) itemView.findViewById(R.id.rowText);
            mImageView = (ImageView) itemView.findViewById(R.id.rowIcon);

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
