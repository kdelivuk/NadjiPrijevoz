package kristijandelivuk.com.nadjiprijevoz.Screens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import kristijandelivuk.com.nadjiprijevoz.R;
import kristijandelivuk.com.nadjiprijevoz.helper.TypefaceSpan;

/**
 * Created by kdelivuk on 26/08/15.
 */
class PopupAdapter implements GoogleMap.InfoWindowAdapter {
    private View popup=null;
    private LayoutInflater inflater=null;
    Context context = null;

    PopupAdapter(LayoutInflater inflater, Context context) {
        this.inflater=inflater;
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.layout_map_popup, null);
        }

        Typeface choplin = Typeface.createFromAsset(context.getAssets(), "fonts/Choplin.otf");
        Typeface gidole = Typeface.createFromAsset(context.getAssets(), "fonts/Gidole_Regular.ttf");

        TextView textSnippet = (TextView)popup.findViewById(R.id.textDescription);
        TextView textTitle =(TextView)popup.findViewById(R.id.textTitle);

        textSnippet.setTypeface(gidole);
        textTitle.setTypeface(choplin);
        textTitle.setTextSize(8);
        textTitle.setText(marker.getTitle());

        textSnippet.setText(marker.getSnippet());
        return(popup);
    }
}