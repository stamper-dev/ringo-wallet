package wannabit.io.ringowallet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class MainPriceFragment extends BaseFragment {

    public static MainPriceFragment newInstance() {
        MainPriceFragment fragment = new MainPriceFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_price, container, false);

        TextView msg0 = rootView.findViewById(R.id.price_msg0);
        TextView msg1 = rootView.findViewById(R.id.price_msg1);

        msg0.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        msg1.setTypeface(WUtils.getTypefaceLight(getBaseActivity()));
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefreshTab(boolean deep) {
        super.onRefreshTab(deep);
    }
}
