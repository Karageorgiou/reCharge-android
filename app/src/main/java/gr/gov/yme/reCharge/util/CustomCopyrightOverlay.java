package gr.gov.yme.reCharge.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;

import java.net.URL;

import gr.gov.yme.R;

public class CustomCopyrightOverlay extends CopyrightOverlay {
    private static final String TAG = "CustomCopyrightOverlay";
    Context ctx;
    public CustomCopyrightOverlay(Context context) {
        super(context);
        ctx = context;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        Log.d(TAG,"clicked copyright notice");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.openstreetmap.org/copyright"));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ctx.startActivity(browserIntent);
        return super.onSingleTapConfirmed(e, mapView);
    }

    @Override
    public void draw(Canvas canvas, MapView map, boolean shadow) {
        setCopyrightNotice(map.getTileProvider().getTileSource().getCopyrightNotice());
        draw(canvas, map.getProjection());

    }
}
