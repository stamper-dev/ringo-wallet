package wannabit.io.ringowallet.acticites;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.fragments.CreateTypeFragment;
import wannabit.io.ringowallet.fragments.GenerateMnemonicFragment;
import wannabit.io.ringowallet.fragments.RestoreByKeyFragment;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class CreateWalletActivity extends BaseActivity {

    private Toolbar             mToolbar;
    private TextView            mTitle;
    private int                 mPurpose;

    private FrameLayout         mBottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        mToolbar            = findViewById(R.id.tool_bar);
        mTitle              = findViewById(R.id.toolbar_title);
        mBottomSheet        = findViewById(R.id.bottom_sheet);
        mTitle.setTypeface(WUtils.getTypefaceRegular(this));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mPurpose = getIntent().getIntExtra(BaseConstant.CONST_CREATE_PURPOSE , -1);
        if(mPurpose < 0) mPurpose = 0;

        onInitContainer();

    }


    public void onInitContainer() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mPurpose == BaseConstant.CONST_CREATE_PURPOSE_ADD_KEY) {
            fragmentTransaction.replace(R.id.main_container, RestoreByKeyFragment.newInstance(getIntent().getBundleExtra("bundle")));

        } else if (mPurpose == BaseConstant.CONST_CREATE_PURPOSE_MNEMONIC) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("mnemonic", true);
            fragmentTransaction.replace(R.id.main_container, CreateTypeFragment.newInstance(bundle));

        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("mnemonic", false);
            fragmentTransaction.replace(R.id.main_container, CreateTypeFragment.newInstance(bundle));

        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if(fragment instanceof RestoreByKeyFragment) {
            ((RestoreByKeyFragment)fragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public  void onUpdateTitle(String title) {
        mTitle.setText(title);
    }

    public FrameLayout getBottomSheet() {
        return mBottomSheet;
    }
}

