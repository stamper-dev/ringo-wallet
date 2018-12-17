package wannabit.io.ringowallet.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import wannabit.io.ringowallet.acticites.IntroActivity;
import wannabit.io.ringowallet.acticites.MainActivity;
import wannabit.io.ringowallet.acticites.PasswordActivity;
import wannabit.io.ringowallet.dialog.Dialog_NetWorkError;
import wannabit.io.ringowallet.dialog.Dialog_Wait;

public class BaseActivity extends AppCompatActivity {

    protected BaseApplication               mApplication;
    protected BaseDao                       mDao;
    protected Dialog_Wait                   mDialogWait;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(this instanceof PasswordActivity) && !(this instanceof IntroActivity)) {
            if(getBaseApplication().needShowLockScreen()) {
                Intent intent = new Intent(this, PasswordActivity.class);
                intent.putExtra(BaseConstant.CONST_PW_PURPOSE, BaseConstant.CONST_PW_UNLOUCK);
                startActivity(intent);
                return;
            }
        }
    }

    public BaseApplication getBaseApplication() {
        if (mApplication == null)
            mApplication = (BaseApplication) getApplication();
        return mApplication;
    }

    public BaseDao getBaseDao() {
        if (mDao == null)
            mDao = getBaseApplication().getBaseDao();
        return mDao;
    }


    public void onShowWaitDialog() {
        mDialogWait = new Dialog_Wait();
        mDialogWait.setCancelable(false);
        getSupportFragmentManager().beginTransaction().add(mDialogWait, "wait").commitNowAllowingStateLoss();
    }

    public void onHideWaitDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("wait");
        if (prev != null) {
            ft.remove(prev).commitNowAllowingStateLoss();
        }
    }

    public void onNetWorkErrorDialog() {
        Dialog_NetWorkError dialog = new Dialog_NetWorkError();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    public void onHideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        View v = getCurrentFocus();
        if (v == null) {
            v = new View(getBaseContext());
        }
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void onStartMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
