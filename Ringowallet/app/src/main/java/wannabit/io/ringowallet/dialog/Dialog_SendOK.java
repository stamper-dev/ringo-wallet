package wannabit.io.ringowallet.dialog;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.PasswordActivity;
import wannabit.io.ringowallet.utils.WUtils;

public class Dialog_SendOK extends DialogFragment {

    private TextView mTxidTextView;

    public static Dialog_SendOK newInstance(Bundle bundle) {
        Dialog_SendOK frag = new Dialog_SendOK();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view  = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_send_ok, null);
        ((TextView)view.findViewById(R.id.send_ok_title)).setTypeface(WUtils.getTypefaceRegular(getActivity()));
        ((TextView)view.findViewById(R.id.send_ok_msg)).setTypeface(WUtils.getTypefaceRegular(getActivity()));

        mTxidTextView           = view.findViewById(R.id.send_ok_txid);
        Button btn_negative     = view.findViewById(R.id.btn_nega);
        Button btn_positive     = view.findViewById(R.id.btn_posi);
        mTxidTextView.setTypeface(WUtils.getTypefaceLight(getActivity()));
        btn_negative.setTypeface(WUtils.getTypefaceRegular(getActivity()));
        btn_positive.setTypeface(WUtils.getTypefaceRegular(getActivity()));

        mTxidTextView.setText(getArguments().getString("txid"));


        mTxidTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClipTxid();
                Toast.makeText(getActivity(), R.string.msg_copied, Toast.LENGTH_SHORT).show();
            }
        });

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClipTxid();
                ((PasswordActivity)getActivity()).onSuccessTransaction(null);
                getDialog().dismiss();
            }
        });

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClipTxid();
                ((PasswordActivity)getActivity()).onSuccessTransaction(getArguments());
                getDialog().dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }


    private void onClipTxid() {
        ClipboardManager clipboard = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("txid ", mTxidTextView.getText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(), R.string.msg_copied, Toast.LENGTH_SHORT).show();
    }

}