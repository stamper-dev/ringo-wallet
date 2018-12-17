package wannabit.io.ringowallet.acticites.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.utils.WUtils;

public class DecimalSetActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout      mBtnFull, mBtn6, mBtn4;
    private ImageView           mImgFull, mImg6, mImg4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_decimal);
        ((TextView)findViewById(R.id.toolbar_title)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
        ((TextView)findViewById(R.id.decimal_full_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
        ((TextView)findViewById(R.id.decimal_6_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
        ((TextView)findViewById(R.id.decimal_4_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseContext()));

        mBtnFull        = findViewById(R.id.set_decimal_full);
        mBtn6           = findViewById(R.id.set_decimal_6);
        mBtn4           = findViewById(R.id.set_decimal_4);
        mImgFull        = findViewById(R.id.decimal_full_img);
        mImg6           = findViewById(R.id.decimal_6_tv_img);
        mImg4           = findViewById(R.id.decimal_4_tv_img);
        mBtnFull.setOnClickListener(this);
        mBtn6.setOnClickListener(this);
        mBtn4.setOnClickListener(this);

        onInitView();
    }

    private void onInitView() {
        mImgFull.setImageDrawable(getDrawable(R.drawable.ic_checkbox_unchecked));
        mImg6.setImageDrawable(getDrawable(R.drawable.ic_checkbox_unchecked));
        mImg4.setImageDrawable(getDrawable(R.drawable.ic_checkbox_unchecked));

        if (getBaseDao().getDecimal() == 0) {
            mImgFull.setImageDrawable(getDrawable(R.drawable.ic_checkbox_checked));

        } else if (getBaseDao().getDecimal() == 1) {
            mImg6.setImageDrawable(getDrawable(R.drawable.ic_checkbox_checked));

        } else {
            mImg4.setImageDrawable(getDrawable(R.drawable.ic_checkbox_checked));

        }

    }


    @Override
    public void onClick(View v) {
        if (v.equals(mBtnFull)) {
            getBaseDao().setDecimal(0);

        } else if (v.equals(mBtn6)) {
            getBaseDao().setDecimal(1);

        } else if (v.equals(mBtn4)) {
            getBaseDao().setDecimal(2);

        }
        onInitView();
    }
}
