package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.helper.Route;
import kristijandelivuk.com.nadjiprijevoz.model.PointModel;
import kristijandelivuk.com.nadjiprijevoz.model.RouteModel;

/**
 * Created by kdelivuk on 17/08/15.
 */
class ProfileMapAdapter extends RecyclerView.Adapter<ProfileMapAdapter.ProfileRouteViewHolder> {

    Context mContext;
    List<RouteModel> routesCV;
    Route rt;
    private ListClickListener mListener;


    public ProfileMapAdapter(Context context , List<RouteModel> routes) {
        this.mContext = context;
        this.routesCV = routes;
    }

    public void setListClickListener(ListClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public ProfileRouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_cardview_item, parent, false);
        ProfileRouteViewHolder pvh = new ProfileRouteViewHolder(v, routesCV, mContext);
        return pvh;
    }

    private void zoomMapToLatLngBounds(final LinearLayout layout,final GoogleMap mMap, final ArrayList<LatLng> bounds){

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (int i=0; i<bounds.size(); i++) {
            boundsBuilder.include(bounds.get(i));
        }

        final LatLngBounds boundsfinal = boundsBuilder.build();

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsfinal, 25));
            }
        });

    }

    @Override
    public void onBindViewHolder(ProfileRouteViewHolder holder, int position) {
        holder.destination.setText(" - " + routesCV.get(position).getDestination());
        holder.startingPoint.setText(routesCV.get(position).getStartingPoint());
        holder.dateAndTime.setText(routesCV.get(position).getDate() + "  " + routesCV.get(position).getTime());

        GoogleMap gMap = holder.map.getMap();

        if (gMap != null) {

            ArrayList<PointModel> mapPosition = routesCV.get(position).getPoints();
            ArrayList<LatLng> points = new ArrayList<LatLng>();



            for (PointModel item : mapPosition) {
                points.add(new LatLng(item.getLatitude(),item.getLongitude()));
            }

            for (LatLng item : points) {
                gMap.addMarker(new MarkerOptions().position(item));
            }

            rt = new Route();

            rt.drawRoute(gMap, mContext, new ArrayList<LatLng>(points), "en", true);

            zoomMapToLatLngBounds(holder.linearlayout, gMap, points);
        }

    }

    @Override
    public void onViewRecycled(ProfileRouteViewHolder holder)
    {
        if (holder.map != null)
        {
            holder.map.getMap().clear();
        }
    }

    @Override
    public int getItemCount() {
        return routesCV.size();
    }

    class ProfileRouteViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback, View.OnClickListener {

        Context mContext;
        CardView cv;
        TextView destination;
        TextView startingPoint;
        TextView dateAndTime;
        MapView map;
        List<RouteModel> routesCV;
        LinearLayout linearlayout;

        ProfileRouteViewHolder(View itemView, List<RouteModel> routesCV, Context context) {
            super(itemView);

            mContext = context;
            this.routesCV = routesCV;
            cv = (CardView) itemView.findViewById(R.id.cv);
            destination = (TextView) itemView.findViewById(R.id.destinationCV);
            startingPoint = (TextView) itemView.findViewById(R.id.startingPointCV);
            dateAndTime = (TextView) itemView.findViewById(R.id.textDateAndTimeCV);

            Typeface gidole = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Gidole_Regular.ttf");

            destination.setTypeface(gidole);
            startingPoint.setTypeface(gidole);
            dateAndTime.setTypeface(gidole);

            map = (MapView) itemView.findViewById(R.id.map_view);
            linearlayout = (LinearLayout) itemView.findViewById(R.id.linearlayout);
            itemView.setOnClickListener(this);
            map.onCreate(null);
            map.onResume();
            map.getMapAsync(this);

        }


        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            MapsInitializer.initialize(mContext);

        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.selectItem(v , getPosition());
            }
        }
    }

    public interface ListClickListener {
        void selectItem(View v, int position);
    }
}