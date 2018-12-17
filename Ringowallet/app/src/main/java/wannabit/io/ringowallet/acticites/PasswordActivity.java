package wannabit.io.ringowallet.acticites;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import java.util.ArrayList;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.crypto.CryptoHelper;
import wannabit.io.ringowallet.dialog.Dialog_PassWithBio;
import wannabit.io.ringowallet.dialog.Dialog_SendOK;
import wannabit.io.ringowallet.dialog.Dialog_UsingBio;
import wannabit.io.ringowallet.fragments.AlphabetKeyBoardFragment;
import wannabit.io.ringowallet.fragments.KeyboardFragment;
import wannabit.io.ringowallet.fragments.NumberKeyBoardFragment;
import wannabit.io.ringowallet.model.Password;
import wannabit.io.ringowallet.task.CheckKeyTask;
import wannabit.io.ringowallet.task.CheckMnemonicTask;
import wannabit.io.ringowallet.task.InitPasswordTask;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.task.UnLockTask;
import wannabit.io.ringowallet.task.send.SendTaskFactory;
import wannabit.io.ringowallet.utils.KeyboardListener;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class PasswordActivity extends BaseActivity implements KeyboardListener, TaskCallback {

    private Toolbar                     mToolbar;
    private TextView                    mTitle;
    private LinearLayout                mLayerContents;
    private ImageView                   mIvLock;
    private TextView                    mTvMsg01, mTvMsg02;
    private ImageView[]                 mIvCircle = new ImageView[5];
    private AHBottomNavigationViewPager mViewPager;

    private String                      mUserInput = "";
    private String                      mConfirmInput = "";
    private boolean                     mIsConfirmSequence;

    private KeyboardPagerAdapter        mAdapter;
    private int                         mType;

    private String                      mKeyUuid;
    private Bundle                      mSendBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        mToolbar        = findViewById(R.id.tool_bar);
        mTitle          = findViewById(R.id.toolbar_title);
        mLayerContents  = findViewById(R.id.layer_contents);
        mIvLock         = findViewById(R.id.iv_lock);
        mTvMsg01        = findViewById(R.id.tv_password_msg0);
        mTvMsg02        = findViewById(R.id.tv_password_msg1);
        mViewPager      = findViewById(R.id.keyboard_pager);
        for(int i = 0; i < mIvCircle.length; i++) {
            mIvCircle[i] = findViewById(getResources().getIdentifier("img_circle" + i , "id", getPackageName()));
        }

        mTitle.setTypeface(WUtils.getTypefaceRegular(this));
        mTvMsg01.setTypeface(WUtils.getTypefaceRegular(this));
        mTvMsg02.setTypeface(WUtils.getTypefaceRegular(this));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mViewPager.setOffscreenPageLimit(2);
        mAdapter = new KeyboardPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mType           = getIntent().getIntExtra(BaseConstant.CONST_PW_PURPOSE, BaseConstant.CONST_PW_UNLOUCK);
        mKeyUuid        = getIntent().getStringExtra("uuid");
        mSendBundle     = getIntent().getBundleExtra("bundle");

        onInitView();
    }

    @Override
    public void onBackPressed() {
        if(mUserInput != null && mUserInput.length() > 0) {
            onDeleteKey();
        } else if (mType == BaseConstant.CONST_PW_INIT && mIsConfirmSequence) {
            mIsConfirmSequence = false;
            mConfirmInput = "";
            onInitView();

        } else if (mType == BaseConstant.CONST_PW_UNLOUCK) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);

        } else {
            super.onBackPressed();
        }
    }

    private void onSetTitle(String title) {
        mTitle.setText(title);

    }

    private void onInitView() {
        mIvLock.setImageDrawable(getDrawable(R.drawable.password_img));
        if (mType == BaseConstant.CONST_PW_INIT) {
            mTvMsg01.setText(getString(R.string.msg_password_init));
            onSetTitle(getString(R.string.title_set_password));

        } else if (mType == BaseConstant.CONST_PW_SEND) {
            mTvMsg01.setText(getString(R.string.msg_password_send));
            onSetTitle(getString(R.string.title_check_password));

        } else if (mType == BaseConstant.CONST_PW_CHECK_KEY || mType == BaseConstant.CONST_PW_CHECK_MNEMONIC) {
            mTvMsg01.setText(getString(R.string.msg_password_check));
            onSetTitle(getString(R.string.title_check_password));

        } else if( mType == BaseConstant.CONST_PW_UNLOUCK) {
            mTvMsg01.setText(getString(R.string.msg_password_unlock));
            onSetTitle(getString(R.string.title_check_password));
            onCheckFingerPrint();

        }

        mIsConfirmSequence = false;
        mUserInput = "";
        mConfirmInput = "";

        for(int i = 0; i < mIvCircle.length; i++) {
            mIvCircle[i].setBackground(getDrawable(R.drawable.ic_pass_gr));
        }
        mViewPager.setCurrentItem(0, true);
    }

    private void onConfirmView(){
        mIvLock.setImageDrawable(getDrawable(R.drawable.password_done_img));
        mTvMsg01.setText(getString(R.string.msg_password_confirm));
        if(mAdapter != null && mAdapter.getFragments() != null) {
            for (KeyboardFragment frag: mAdapter.getFragments()) {
                if(frag != null)
                    frag.onShuffleKeyboard();
            }
        }
        mIsConfirmSequence = true;
        mConfirmInput = mUserInput;
        mUserInput = "";
        for(int i = 0; i < mIvCircle.length; i++) {
            mIvCircle[i].setBackground(getDrawable(R.drawable.ic_pass_gr));
        }
        mViewPager.setCurrentItem(0, true);
    }

    private void onFinishInput() {
        if (mType == BaseConstant.CONST_PW_INIT) {
            if(mIsConfirmSequence) {
                if(mConfirmInput.equals(mUserInput)) {
                    onShowWaitDialog();
                    new InitPasswordTask(getBaseApplication(), this).execute(mConfirmInput);

                } else {
                    onShakeView();
                    Toast.makeText(getBaseContext(), getString(R.string.msg_error_password_not_same), Toast.LENGTH_SHORT).show();
                }

            } else {
                onConfirmView();
            }

        } else if (mType == BaseConstant.CONST_PW_SEND) {
            onShowWaitDialog();
            mSendBundle.putString("pincode", mUserInput);
            SendTaskFactory.getSendTask(getBaseApplication(), this, mSendBundle.getString("type")).execute(mSendBundle);

        } else if (mType == BaseConstant.CONST_PW_CHECK_KEY) {
            onShowWaitDialog();
            new CheckKeyTask(getBaseApplication(), this).execute(mUserInput, mKeyUuid);

        } else if( mType == BaseConstant.CONST_PW_CHECK_MNEMONIC) {
            onShowWaitDialog();
            new CheckMnemonicTask(getBaseApplication(), this).execute(mUserInput);

        } else if( mType == BaseConstant.CONST_PW_UNLOUCK) {
            onShowWaitDialog();
            new UnLockTask(getBaseApplication(), this).execute(mUserInput);
        }

    }

    private void onUpdateCnt() {
        if(mUserInput == null)
            mUserInput = "";

        final int inputLength = mUserInput.length();
        for(int i = 0; i < mIvCircle.length; i++) {
            if(i < inputLength)
                mIvCircle[i].setBackground(getDrawable(R.drawable.ic_pass_pu));
            else
                mIvCircle[i].setBackground(getDrawable(R.drawable.ic_pass_gr));
        }
    }

    private void onShakeView() {
        mLayerContents.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
        animation.reset();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                onInitView();
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        mLayerContents.startAnimation(animation);
    }


    @Override
    public void onInsertKey(char input) {
        if(mUserInput == null || mUserInput.length() == 0) {
            mUserInput = String.valueOf(input);

        } else if (mUserInput.length() < 5) {
            mUserInput = mUserInput + input;
        }

        if (mUserInput.length() == 4) {
            mViewPager.setCurrentItem(1, true);

        } else if (mUserInput.length() == 5 && WUtils.checkPasscodePattern(mUserInput)) {
            onFinishInput();

        } else if (mUserInput.length() == 5 && !WUtils.checkPasscodePattern(mUserInput)) {
            onInitView();
            return;
        }
        onUpdateCnt();
    }

    @Override
    public void onDeleteKey() {
        if(mUserInput == null || mUserInput.length() <= 0) {
            onBackPressed();
        } else if (mUserInput.length() == 4) {
            mUserInput = mUserInput.substring(0, mUserInput.length()-1);
            mViewPager.setCurrentItem(0, true);
        } else {
            mUserInput = mUserInput.substring(0, mUserInput.length()-1);
        }
        onUpdateCnt();
    }

    public class KeyboardPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<KeyboardFragment> mFragments = new ArrayList<>();

        public KeyboardPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments.clear();
            NumberKeyBoardFragment number = NumberKeyBoardFragment.newInstance();
            number.setListener(PasswordActivity.this);
            mFragments.add(number);

            AlphabetKeyBoardFragment alphabet = AlphabetKeyBoardFragment.newInstance();
            alphabet.setListener(PasswordActivity.this);
            mFragments.add(alphabet);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        public ArrayList<KeyboardFragment> getFragments() {
            return mFragments;
        }
    }


    @Override
    public void onTaskResponse(TaskResult result) {
        if(isFinishing()) return;
        onHideWaitDialog();
        if (result.taskType == BaseConstant.TASK_INIT_PW) {
            if(result.isSuccess) {
                Toast.makeText(this, R.string.msg_password_init_success, Toast.LENGTH_SHORT).show();
                Dialog_UsingBio dialog = Dialog_UsingBio.newInstance();
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "dialog");

            } else {}

        } else if (result.taskType == BaseConstant.TASK_SEND) {
            if(result.isSuccess) {
                Bundle bundle = new Bundle();
                bundle.putString("txid",    result.resultData3);
                bundle.putString("type",    mSendBundle.getString("type"));
                bundle.putString("symbol",  mSendBundle.getString("symbol"));
                Dialog_SendOK dialog = Dialog_SendOK.newInstance(bundle);
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "dialog");

            } else {
                if(result.resultCode == -99) {
                    Toast.makeText(this, R.string.msg_password_wrong, Toast.LENGTH_SHORT).show();
                    super.onBackPressed();
                }

            }
        } else if (result.taskType == BaseConstant.CONST_PW_CHECK_KEY) {
            if(result.isSuccess) {
                Intent intent = new Intent(this, RemindActivity.class);
                intent.putExtra(BaseConstant.CONST_PW_PURPOSE, BaseConstant.CONST_PW_CHECK_KEY);
                intent.putExtra("uuid", mKeyUuid);
                intent.putExtra("data", result.resultData3);
                startActivity(intent);

            } else {
                if(result.resultCode == -99) {
                    Toast.makeText(this, R.string.msg_password_wrong, Toast.LENGTH_SHORT).show();
                    super.onBackPressed();
                }

            }

        } else if (result.taskType == BaseConstant.CONST_PW_CHECK_MNEMONIC) {
            if(result.isSuccess) {
                Intent intent = new Intent(this, RemindActivity.class);
                intent.putExtra(BaseConstant.CONST_PW_PURPOSE, BaseConstant.CONST_PW_CHECK_MNEMONIC);
                intent.putExtra("data", result.resultData3);
                startActivity(intent);

            } else {
                if(result.resultCode == -99) {
                    Toast.makeText(this, R.string.msg_password_wrong, Toast.LENGTH_SHORT).show();
                    super.onBackPressed();
                }

            }
        } else if (result.taskType == BaseConstant.TASK_UNLOCK) {
            if(result.isSuccess) {
                finish();

            } else {
                if(result.resultCode == -99) {
                    Toast.makeText(this, R.string.msg_password_wrong, Toast.LENGTH_SHORT).show();
                    super.onBackPressed();
                }
            }
        }
    }

    public void onSuccessTransaction(Bundle bundle) {
        if(bundle != null) {
            Intent webintent = new Intent(this, WebActivity.class);
            webintent.putExtra("type", bundle.getString("type"));
            webintent.putExtra("txid", bundle.getString("txid"));
            webintent.putExtra("goMain", true);
            startActivity(webintent);

        } else {
            onStartMainActivity();
        }

    }


    public void onNextPage() {
        if(getBaseDao().hasAnyKey()) {
            onStartMainActivity();

        } else {
            Intent intent = new Intent(this, CreateWalletActivity.class);
            intent.putExtra(BaseConstant.CONST_CREATE_PURPOSE, BaseConstant.CONST_CREATE_PURPOSE_INIT);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void onCheckFingerPrint() {
        FingerprintManagerCompat mFingerprintManagerCompat = FingerprintManagerCompat.from(this);
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                mFingerprintManagerCompat.isHardwareDetected() &&
                mFingerprintManagerCompat.hasEnrolledFingerprints() &&
                getBaseDao().getUsingFingerprint()) {

            Dialog_PassWithBio dialog = Dialog_PassWithBio.newInstance();
            dialog.setCancelable(true);
            dialog.show(getSupportFragmentManager(), "dialog");

        }
    }
}
