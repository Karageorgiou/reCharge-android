package gr.gov.yme.reCharge.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import gr.gov.yme.R;

public class DisclaimerDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage(getText(R.string.disclaimer))
                .setPositiveButton(getText(R.string.ok), (dialog, which) -> {} )
                .create();

    }


    public static String TAG = "DisclaimerDialog";
}