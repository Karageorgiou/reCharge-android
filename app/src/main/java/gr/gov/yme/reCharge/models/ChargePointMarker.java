package gr.gov.yme.reCharge.models;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Objects;

import gr.gov.yme.R;
import gr.gov.yme.reCharge.models.feature.Feature;

public class ChargePointMarker extends Marker {
    Double latitude;
    Double longitude;

    public ChargePointMarker(MapView mapView, Feature feature, Context ctx) {
        super(mapView);
        latitude = feature.geometry.coordinates.get(0);
        longitude = feature.geometry.coordinates.get(1);
        GeoPoint point = new GeoPoint(latitude, longitude);
        setPosition(point);
        setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM - 0.2f);
        updateIcon(feature, ctx);
        setTextLabelFontSize(44);
        setId(feature.properties.locationId);
    }

    public void updateIcon(Feature feature, Context ctx) {
        Drawable pin = ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.pin, null);
        Drawable pin_red = ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.pin_red, null);
        Drawable pin_orange = ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.pin_orange, null);
        Drawable pin_green = ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.pin_green, null);

        if (Objects.equals(feature.properties.dateTimeStatus, "danger")) {
            setIcon(pin_red);
        } else if (Objects.equals(feature.properties.dateTimeStatus, "warning")) {
            setIcon(pin_orange);
        } else if (Objects.equals(feature.properties.dateTimeStatus, "success")) {
            setIcon(pin_green);
        } else {
            setIcon(pin);
        }
    }
}
