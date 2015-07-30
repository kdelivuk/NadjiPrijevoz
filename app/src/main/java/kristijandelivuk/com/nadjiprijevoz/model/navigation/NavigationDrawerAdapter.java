package kristijandelivuk.com.nadjiprijevoz.model.navigation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;

/**
 * Created by kdelivuk on 30/07/15.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.NavigationViewHolder> {

    List<NavigationDrawerItem> mNavigationItems;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.navigation_item_row, parent, false);
        NavigationViewHolder holder = new NavigationViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return mNavigationItems.size();
    }

    @Override
    public void onBindViewHolder(NavigationViewHolder holder, int position) {
        NavigationDrawerItem currentItem = mNavigationItems.get(position);
        holder.mText.setText(currentItem.getTitle());
    }

    class NavigationViewHolder extends RecyclerView.ViewHolder {

        TextView mText;
        ImageView mImageView;


        public NavigationViewHolder(View itemView) {
            super(itemView);

            mText = (TextView) itemView.findViewById(R.id.rowText);
            mImageView = (ImageView) itemView.findViewById(R.id.rowIcon);

        }
    }
}
