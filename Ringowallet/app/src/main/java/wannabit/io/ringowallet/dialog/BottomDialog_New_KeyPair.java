package wannabit.io.ringowallet.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.MainActivity;
import wannabit.io.ringowallet.utils.WUtils;

public class BottomDialog_New_KeyPair extends BottomSheetDialogFragment {

    public static BottomDialog_New_KeyPair getInstance() {
        return new BottomDialog_New_KeyPair();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_bottom_new_keypair, container, false);

        final ImageView icon    = view.findViewById(R.id.dialog_bottom_add_keypair_img);
        TextView title          = view.findViewById(R.id.dialog_bottom_add_keypair_title);
        TextView msg            = view.findViewById(R.id.dialog_bottom_add_keypair_msg);
        Button gMnemonic        = view.findViewById(R.id.btn_gen_from_mnemonic);
        Button gPrivateKey      = view.findViewById(R.id.btn_gen_from_private_key);


        title.setTypeface(WUtils.getTypefaceRegular(getActivity()));
        msg.setTypeface(WUtils.getTypefaceRegular(getActivity()));
        gMnemonic.setTypeface(WUtils.getTypefaceRegular(getActivity()));
        gPrivateKey.setTypeface(WUtils.getTypefaceRegular(getActivity()));

        msg.setText(String.format(getString(R.string.msg_new_key_pair), getArguments().getString("symbol")));
        if(TextUtils.isEmpty(getArguments().getString("imageUrl"))) {
            icon.setImageDrawable(getResources().getDrawable(getArguments().getInt("imgId")));

        } else {
            icon.post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(getActivity())
                            .load(getArguments().getString("imageUrl"))
                            .centerCrop()
                            .override(78, 78)
                            .placeholder(R.drawable.dialog_bg)
                            .into(icon);
                }
            });
        }

        gMnemonic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ((MainActivity)getActivity()).onGenerateKeyPairWithMnemonic(getArguments());

            }
        });

        gPrivateKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ((MainActivity)getActivity()).onInsertNewPrivateKey(getArguments());

            }
        });


        return view;
    }
}
