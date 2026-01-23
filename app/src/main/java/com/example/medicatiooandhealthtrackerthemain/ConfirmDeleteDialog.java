package com.example.medicatiooandhealthtrackerthemain;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmDeleteDialog extends DialogFragment {

    public interface Listener {
        void onConfirmDelete(int medId);
    }

    private static final String ARG_MED_ID = "medId";

    public static ConfirmDeleteDialog newInstance(int medId) {
        ConfirmDeleteDialog dialog = new ConfirmDeleteDialog();
        Bundle b = new Bundle();
        b.putInt(ARG_MED_ID, medId);
        dialog.setArguments(b);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        int medId = requireArguments().getInt(ARG_MED_ID, -1);

        return new AlertDialog.Builder(requireContext())
                .setTitle("Delete Medication")
                .setMessage("Are you sure you want to delete this medication?")
                .setPositiveButton("Yes", (d, which) -> {
                    // نرسل للـ Fragment (الأب)
                    if (getParentFragment() instanceof Listener) {
                        ((Listener) getParentFragment()).onConfirmDelete(medId);
                    }
                })
                .setNegativeButton("No", (d, which) -> d.dismiss())
                .create();
    }
}
