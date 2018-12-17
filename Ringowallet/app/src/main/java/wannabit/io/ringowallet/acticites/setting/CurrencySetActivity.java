package wannabit.io.ringowallet.acticites.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.utils.WUtils;

public class CurrencySetActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout  mBtnUsd, mBtnKrw;
    private ImageView       mImgUsd, mImgKrw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_currency);
        ((TextView)findViewById(R.id.toolbar_title)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
        ((TextView)findViewById(R.id.currency_usd_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
        ((TextView)findViewById(R.id.currency_krw_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));

        mBtnUsd         = findViewById(R.id.set_currency_usd);
        mBtnKrw         = findViewById(R.id.set_currency_krw);
        mImgUsd         = findViewById(R.id.currency_usd_img);
        mImgKrw         = findViewById(R.id.currency_krw_img);
        mBtnUsd.setOnClickListener(this);
        mBtnKrw.setOnClickListener(this);

        onInitView();
    }

    private void onInitView() {
        mImgUsd.setImageDrawable(getDrawable(R.drawable.ic_checkbox_unchecked));
        mImgKrw.setImageDrawable(getDrawable(R.drawable.ic_checkbox_unchecked));

        if (getBaseDao().getCurrency() == 0) {
            mImgUsd.setImageDrawable(getDrawable(R.drawable.ic_checkbox_checked));

        } else {
            mImgKrw.setImageDrawable(getDrawable(R.drawable.ic_checkbox_checked));

        }

    }


    @Override
    public void onClick(View v) {
        if (v.equals(mBtnUsd)) {
            getBaseDao().setCurrency(0);

        } else if (v.equals(mBtnKrw)) {
            getBaseDao().setCurrency(1);

        }
        onInitView();

    }
}
