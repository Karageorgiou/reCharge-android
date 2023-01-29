package gr.gov.yme.reCharge.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import gr.gov.yme.R;
import gr.gov.yme.reCharge.activities.MapActivity;

public class EnableGPSDialogFragment extends DialogFragment {

    public MapActivity mapActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.EnableGPSTheme);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapActivity.blur.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final String TAG = "EnableGPSDialogFragment";
        mapActivity = (MapActivity) getActivity();
        assert mapActivity != null;
        mapActivity.blur.setVisibility(View.VISIBLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setMessage("Για να βρεθεί η τοποθεσία της συσκευής σας θα πρέπει να ενεργοποιήσετε το GPS της συσκευής σας.")
                .setPositiveButton("Προχωρήστε στις ρυθμίσεις", (dialog, which) -> {
                    mapActivity.blur.setVisibility(View.GONE);
                    Intent getLocationPermission = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(getLocationPermission);
                } )
                .setNegativeButton("Ακύρωση", (dialog, which) -> {
                    mapActivity.blur.setVisibility(View.GONE);
                })
                .setIcon(R.drawable.person)
                .setTitle("Οι υπηρεσίες τοποθεσίας είναι απενεργοποιημένες");

        builder.getContext().getTheme().applyStyle(R.style.EnableGPSTheme, true);
        return builder.create();
    }
}