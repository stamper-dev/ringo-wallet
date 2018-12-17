package wannabit.io.ringowallet.acticites;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.ArrayList;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.fragments.WelcomeFragment;

public class WelcomeActivity extends BaseActivity {

    private ViewPager               pager;
    private WelcomePagerAdapter     pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        pager = findViewById(R.id.welcome_pager);
        pagerAdapter = new WelcomePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(0);
        pager.setPageMargin(100);
    }




    public class WelcomePagerAdapter extends FragmentPagerAdapter {

        private ArrayList<BaseFragment> mFragments = new ArrayList<>();
        private BaseFragment mCurrentFragment;

        public WelcomePagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments.clear();
            Bundle bundle0 = new Bundle();
            bundle0.putInt("page", 0);
            mFragments.add(WelcomeFragment.newInstance(bundle0));

            Bundle bundle1 = new Bundle();
            bundle1.putInt("page", 1);
            mFragments.add(WelcomeFragment.newInstance(bundle1));

            Bundle bundle2 = new Bundle();
            bundle2.putInt("page", 2);
            mFragments.add(WelcomeFragment.newInstance(bundle2));

            Bundle bundle3 = new Bundle();
            bundle3.putInt("page", 3);
            mFragments.add(WelcomeFragment.newInstance(bundle3));
        }

        @Override
        public BaseFragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((BaseFragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

        public BaseFragment getCurrentFragment() {
            return mCurrentFragment;
        }
    }

}
