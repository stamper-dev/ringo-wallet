package wannabit.io.ringowallet.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.MainActivity;
import wannabit.io.ringowallet.utils.WUtils;

public class Dialog_No_Mnemonic extends DialogFragment {

    public static Dialog_No_Mnemonic newInstance() {
        Dialog_No_Mnemonic frag = new Dialog_No_Mnemonic();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view  = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_no_mnemonic, null);
        ((TextView)view.findViewById(R.id.no_mnemonic_title)).setTypeface(WUtils.getTypefaceRegular(getActivity()));
        ((TextView)view.findViewById(R.id.no_mnemonic_msg)).setTypeface(WUtils.getTypefaceRegular(getActivity()));

        Button btn_negative     = view.findViewById(R.id.btn_nega);
        Button btn_positive     = view.findViewById(R.id.btn_posi);
        btn_negative.setTypeface(WUtils.getTypefaceRegular(getActivity()));
        btn_positive.setTypeface(WUtils.getTypefaceRegular(getActivity()));



        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).onInitMnemoncis();
                getDialog().dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }

}