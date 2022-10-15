package gr.gov.yme.util;

import android.content.Context;
import android.graphics.Canvas;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;

import gr.gov.yme.R;

public class CustomCopyrightOverlay extends CopyrightOverlay {
    Context ctx;
    public CustomCopyrightOverlay(Context context) {
        super(context);
        ctx = context;
    }

    @Override
    public void draw(Canvas canvas, MapView map, boolean shadow) {
        setCopyrightNotice(ctx.getResources().getString(R.string.copyright));
        draw(canvas, map.getProjection());

    }
}
