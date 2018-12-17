package wannabit.io.ringowallet.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class Dialog_PassWithBio extends DialogFragment {

    private TextView    mHelp;


    public static Dialog_PassWithBio newInstance() {
        Dialog_PassWithBio frag = new Dialog_PassWithBio();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view  = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_passwithbio, null);
        ((TextView)view.findViewById(R.id.pass_bio_title)).setTypeface(WUtils.getTypefaceRegular(getActivity()));
        ((TextView)view.findViewById(R.id.pass_bio_msg)).setTypeface(WUtils.getTypefaceRegular(getActivity()));
        Button btn_negative = view.findViewById(R.id.btn_nega);
        btn_negative.setTypeface(WUtils.getTypefaceRegular(getActivity()));

        mHelp = view.findViewById(R.id.pass_bio_help);
        mHelp.setTypeface(WUtils.getTypefaceRegular(getActivity()));

        FingerprintManagerCompat mFingerprintManagerCompat = FingerprintManagerCompat.from(getActivity());
        mFingerprintManagerCompat.authenticate(null, 0, new CancellationSignal(), new FingerprintManagerCompat.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                super.onAuthenticationError(errMsgId, errString);
                Toast.makeText(getActivity(), errString, Toast.LENGTH_SHORT).show();
                if(getDialog() != null)
                    getDialog().dismiss();
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                mHelp.setText(helpString);
                super.onAuthenticationHelp(helpMsgId, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if(getDialog() != null)
                    getDialog().dismiss();
                getActivity().finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        }, null);

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }


}