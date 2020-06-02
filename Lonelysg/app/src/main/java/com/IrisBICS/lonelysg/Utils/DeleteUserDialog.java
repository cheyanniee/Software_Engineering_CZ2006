package com.IrisBICS.lonelysg.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.IrisBICS.lonelysg.R;

public class DeleteUserDialog extends DialogFragment {

    private DeleteUserDialog.DialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        View view = getActivity().getLayoutInflater().inflate(R.layout.delete_user_dialog, null);

        final EditText pwInput = view.findViewById(R.id.pwInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete User")
                .setView(view)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String pw = pwInput.getText().toString();
                        listener.deleteFirebaseUser(pw);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            listener = (DeleteUserDialog.DialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"must implement DialogListener");
        }
    }

    public interface DialogListener{
        void deleteFirebaseUser(String pw);
    }
}
