package wannabit.io.ringowallet.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.setting.CurrencySetActivity;
import wannabit.io.ringowallet.acticites.setting.DecimalSetActivity;
import wannabit.io.ringowallet.acticites.setting.KeyPairSetActivity;
import wannabit.io.ringowallet.acticites.setting.MnemonicActivity;
import wannabit.io.ringowallet.acticites.setting.PrivateSetActivity;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.utils.WUtils;

public class MainSettingFragment extends BaseFragment implements View.OnClickListener{

    private RelativeLayout  mBtnMnemonic, mBtnKeyPair, mBtnCurrency, mBtnDecimal, mBtnPrivate,
                            mBtnAboutUs, mBtnTelegram, mBtnGithub, mBtnLicence;

    public static MainSettingFragment newInstance() {
        MainSettingFragment fragment = new MainSettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_setting, container, false);
        ((TextView)rootView.findViewById(R.id.set_mnemonic_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        ((TextView)rootView.findViewById(R.id.set_keypair_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        ((TextView)rootView.findViewById(R.id.set_currency_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        ((TextView)rootView.findViewById(R.id.set_decimal_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        ((TextView)rootView.findViewById(R.id.set_private_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        ((TextView)rootView.findViewById(R.id.set_aboutus_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        ((TextView)rootView.findViewById(R.id.set_telegram_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        ((TextView)rootView.findViewById(R.id.set_github_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        ((TextView)rootView.findViewById(R.id.set_licence_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));

        mBtnMnemonic    = rootView.findViewById(R.id.set_mnemonic);
        mBtnKeyPair     = rootView.findViewById(R.id.set_keypair);
        mBtnCurrency    = rootView.findViewById(R.id.set_currency);
        mBtnDecimal     = rootView.findViewById(R.id.set_decimal);
        mBtnPrivate     = rootView.findViewById(R.id.set_private);
        mBtnAboutUs     = rootView.findViewById(R.id.set_aboutus);
        mBtnTelegram    = rootView.findViewById(R.id.set_telegram);
        mBtnGithub      = rootView.findViewById(R.id.set_github);
        mBtnLicence     = rootView.findViewById(R.id.set_licence);

        mBtnMnemonic.setOnClickListener(this);
        mBtnKeyPair.setOnClickListener(this);
        mBtnCurrency.setOnClickListener(this);
        mBtnDecimal.setOnClickListener(this);
        mBtnPrivate.setOnClickListener(this);
        mBtnAboutUs.setOnClickListener(this);
        mBtnTelegram.setOnClickListener(this);
        mBtnGithub.setOnClickListener(this);
        mBtnLicence.setOnClickListener(this);

        if(getBaseDao().onHasMnemonic()) {
            mBtnMnemonic.setVisibility(View.VISIBLE);
        } else {
            mBtnMnemonic.setVisibility(View.GONE);
        }

        if(getBaseDao().onSelectAllRawKeys().size() > 0 ) {
            mBtnKeyPair.setVisibility(View.VISIBLE);
        } else {
            mBtnKeyPair.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBtnMnemonic)) {
            startActivity(new Intent(getBaseActivity(), MnemonicActivity.class));

        } else if (v.equals(mBtnKeyPair)) {
            startActivity(new Intent(getBaseActivity(), KeyPairSetActivity.class));

        } else if (v.equals(mBtnCurrency)) {
            startActivity(new Intent(getBaseActivity(), CurrencySetActivity.class));

        } else if (v.equals(mBtnDecimal)) {
            startActivity(new Intent(getBaseActivity(), DecimalSetActivity.class));

        } else if (v.equals(mBtnPrivate)) {
            startActivity(new Intent(getBaseActivity(), PrivateSetActivity.class));

        } else if (v.equals(mBtnAboutUs)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wannabit.io/"));
            startActivity(intent);

        } else if (v.equals(mBtnTelegram)) {
            Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse("https://t.me/wannabitlabs"));
            startActivity(telegram);

        } else if (v.equals(mBtnGithub)) {

        } else if (v.equals(mBtnLicence)) {

        }

    }
}
