package wannabit.io.ringowallet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.PasswordActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.utils.WUtils;

public class WelcomeFragment extends BaseFragment {

    public static WelcomeFragment newInstance(Bundle bundle) {
        WelcomeFragment fragment = new WelcomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view                   = inflater.inflate(R.layout.fragment_welcome, container, false);
        ImageView bg                = view.findViewById(R.id.bg);
        ImageView logo              = view.findViewById(R.id.logo);
        LinearLayout desLayer       = view.findViewById(R.id.desLayer);
        TextView title              = view.findViewById(R.id.titleTv);
        TextView msg0               = view.findViewById(R.id.msg01Tv);
        TextView msg1               = view.findViewById(R.id.msg02Tv);
        Button btnStart             = view.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseActivity(), PasswordActivity.class);
                intent.putExtra(BaseConstant.CONST_PW_PURPOSE, BaseConstant.CONST_PW_INIT);
                startActivity(intent);
            }
        });

        title.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        msg0.setTypeface(WUtils.getTypefaceLight(getBaseActivity()));
        msg1.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        btnStart.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));

        if (getArguments().getInt("page") == 0) {
            bg.setImageDrawable(getResources().getDrawable(R.drawable.welcome001));
            logo.setVisibility(View.VISIBLE);
            desLayer.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);

        } else if (getArguments().getInt("page") == 1) {
            bg.setImageDrawable(getResources().getDrawable(R.drawable.welcome002));
            logo.setVisibility(View.GONE);
            desLayer.setVisibility(View.VISIBLE);
            title.setText(R.string.msg_welcome_title_1);
            msg0.setText(R.string.msg_welcome_msg1_1);
            msg1.setText(R.string.msg_welcome_msg2_1);
            btnStart.setVisibility(View.GONE);

        } else if (getArguments().getInt("page") == 2) {
            bg.setImageDrawable(getResources().getDrawable(R.drawable.welcome003));
            logo.setVisibility(View.GONE);
            desLayer.setVisibility(View.VISIBLE);
            title.setText(R.string.msg_welcome_title_2);
            msg0.setText(R.string.msg_welcome_msg1_2);
            msg1.setText(R.string.msg_welcome_msg2_2);
            btnStart.setVisibility(View.GONE);

        } else if (getArguments().getInt("page") == 3) {
            bg.setImageDrawable(getResources().getDrawable(R.drawable.welcome004));
            logo.setVisibility(View.GONE);
            desLayer.setVisibility(View.VISIBLE);
            title.setText(R.string.msg_welcome_title_3);
            msg0.setText(R.string.msg_welcome_msg1_3);
            msg1.setText(R.string.msg_welcome_msg2_3);
            btnStart.setVisibility(View.VISIBLE);

        }

        return view;
    }
}
