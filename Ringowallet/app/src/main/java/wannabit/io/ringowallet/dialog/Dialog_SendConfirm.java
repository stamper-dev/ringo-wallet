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

import java.math.BigDecimal;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.SendActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class Dialog_SendConfirm extends DialogFragment {

    public static Dialog_SendConfirm newInstance(Bundle bundle) {
        Dialog_SendConfirm frag = new Dialog_SendConfirm();
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
        View view  = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_send_confirm, null);
        ((TextView)view.findViewById(R.id.send_msg)).setTypeface(WUtils.getTypefaceRegular(getActivity()));
        ((TextView)view.findViewById(R.id.recipient_address_title)).setTypeface(WUtils.getTypefaceRegular(getActivity()));
        ((TextView)view.findViewById(R.id.amount_title)).setTypeface(WUtils.getTypefaceRegular(getActivity()));
        ((TextView)view.findViewById(R.id.fee_title)).setTypeface(WUtils.getTypefaceRegular(getActivity()));
        ((TextView)view.findViewById(R.id.remain_title)).setTypeface(WUtils.getTypefaceRegular(getActivity()));

        TextView title                  = view.findViewById(R.id.send_title);
        TextView targetAddress          = view.findViewById(R.id.recipient_address);
        TextView sendAmount             = view.findViewById(R.id.amount);
        TextView totalFee               = view.findViewById(R.id.fee);
        TextView remainAmount           = view.findViewById(R.id.remain);
        title.setTypeface(WUtils.getTypefaceRegular(getActivity()));
        targetAddress.setTypeface(WUtils.getTypefaceLight(getActivity()));
        sendAmount.setTypeface(WUtils.getTypefaceLight(getActivity()));
        totalFee.setTypeface(WUtils.getTypefaceLight(getActivity()));
        remainAmount.setTypeface(WUtils.getTypefaceLight(getActivity()));

//        WLog.w("bundle address : " + getArguments().getString("address"));
//        WLog.w("bundle amount : " + getArguments().getString("amount"));
//        WLog.w("bundle fee : " + getArguments().getString("fee"));
//        WLog.w("bundle remain : " + getArguments().getString("remain"));
//        WLog.w("bundle symbol : " + getArguments().getString("symbol"));
//        WLog.w("bundle decimal : " + getArguments().getInt("decimal"));
        title.setText(getString(R.string.str_send) + "(" + getArguments().getString("symbol") + ")");
        targetAddress.setText(getArguments().getString("address"));
        sendAmount.setText(WUtils.getSendDpDialog(getActivity(), new BigDecimal(getArguments().getString("amount")), getArguments().getString("symbol"), getArguments().getInt("decimal")));

        String feeUnit = getArguments().getString("symbol");
        if (getArguments().getString("type").equals(BaseConstant.COIN_ERC20)) {
            feeUnit = BaseConstant.COIN_ETH;
        } else if (getArguments().getString("type").equals(BaseConstant.COIN_QTUM)) {
            feeUnit = BaseConstant.COIN_QTUM;
        }
        totalFee.setText(WUtils.getSendDpDialog(getActivity(), new BigDecimal(getArguments().getString("fee")), feeUnit, getArguments().getInt("decimal")));
        remainAmount.setText(WUtils.getSendDpDialog(getActivity(), new BigDecimal(getArguments().getString("remain")), getArguments().getString("symbol"), getArguments().getInt("decimal")));



        Button btn_negative = view.findViewById(R.id.btn_nega);
        Button btn_positive = view.findViewById(R.id.btn_posi);
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
                ((SendActivity)getActivity()).onStartSendSequence(getArguments());
                getDialog().dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }


}