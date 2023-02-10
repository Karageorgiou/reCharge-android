package gr.gov.yme.reCharge.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import gr.gov.yme.R;
import gr.gov.yme.reCharge.activities.MapActivity;

public class RequestPermissionsDialogFragment  extends DialogFragment {

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
        mapActivity.requestPermissions();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final String TAG = "RequestPermissionsDialogFragment";
        mapActivity = (MapActivity) getActivity();
        assert mapActivity != null;
        mapActivity.blur.setVisibility(View.VISIBLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setMessage("Θα χρειαστούμε τη συγκατάθεσή σας για τη χρήση του των υπηρεσιών τοποθεσίας αν θέλετε να χρησιμοποιήσετε το κουμπί εύρεσης τοποθεσίας.")
                .setPositiveButton("Εντάξει", (dialog, which) -> {
                    mapActivity.blur.setVisibility(View.GONE);
                } )
                .setNegativeButton("Ακύρωση", (dialog, which) -> {
                    mapActivity.blur.setVisibility(View.GONE);
                })
                .setIcon(R.drawable.person)
                .setTitle("Άδεια χρήσης τοποθεσίας");

        builder.getContext().getTheme().applyStyle(R.style.EnableGPSTheme, true);
        return builder.create();
    }
}