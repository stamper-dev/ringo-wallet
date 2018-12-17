package wannabit.io.ringowallet.acticites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wannabit.io.ringowallet.BuildConfig;
import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.dialog.DialogUpdate;
import wannabit.io.ringowallet.dialog.DialogUpdateForce;
import wannabit.io.ringowallet.model.Support;
import wannabit.io.ringowallet.model.Token;
import wannabit.io.ringowallet.network.ApiClient;
import wannabit.io.ringowallet.network.res.ResTokenInfo;
import wannabit.io.ringowallet.task.ERCTokenUpdateTask;
import wannabit.io.ringowallet.task.QrcTokenUpdateTask;
import wannabit.io.ringowallet.utils.WUtils;

public class IntroActivity extends BaseActivity {

    private FirebaseRemoteConfig    mFirebaseRemoteConfig;
    private Support                 mSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_intro);
        ((TextView)findViewById(R.id.greetingTv)).setTypeface(WUtils.getTypefaceRegular(this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getBaseApplication().needShowLockScreen()) {
            Intent intent = new Intent(this, PasswordActivity.class);
            intent.putExtra(BaseConstant.CONST_PW_PURPOSE, BaseConstant.CONST_PW_UNLOUCK);
            startActivity(intent);
        } else {
            onCheckConfig();
        }
    }

    private void onCheckConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(true).build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config);
        mFirebaseRemoteConfig.fetch(36000)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                            mSupport = new Gson().fromJson(mFirebaseRemoteConfig.getString("support_coin_type"), Support.class);
                            getBaseDao().setSupport(mSupport);
                            onCheckVersion();
                            onCheckTokenVersion();
                        } else {
                            onNetWorkErrorDialog();
                        }
                    }
                });
    }

    public void onNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isFinishing()) {
                    onHideWaitDialog();
                    if(getBaseDao().hasPw()) {
                        if(getBaseDao().hasAnyData()) {
                            onStartMainActivity();
                            finish();

                        } else {
                            Intent intent = new Intent(IntroActivity.this, CreateWalletActivity.class);
                            intent.putExtra(BaseConstant.CONST_CREATE_PURPOSE, BaseConstant.CONST_CREATE_PURPOSE_INIT);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(IntroActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }, 1000);
    }



    private void onCheckVersion() {
        int versionCode     = BuildConfig.VERSION_CODE;
        int minversion      = (int)mFirebaseRemoteConfig.getLong(BaseConstant.CONFIG_MIN_VERSION);
        int lastversion     = (int)mFirebaseRemoteConfig.getLong(BaseConstant.CONFIG_LAST_VERSION);
        if(versionCode < minversion) {
            onShowUpdateDialog(true);
        } else if(versionCode < lastversion) {
            onShowUpdateDialog(false);
        } else {
            onNextActivity();
        }
    }

    public void onCheckTokenVersion() {
        if(mSupport.erc20 &&
                (getBaseDao().onSelectErcTokens().size() == 0 || getBaseDao().getErcVersion() < (int)mFirebaseRemoteConfig.getLong(BaseConstant.CONFIG_ERC20_VERSION))) {
            new ERCTokenUpdateTask(getBaseApplication(), (int)mFirebaseRemoteConfig.getLong(BaseConstant.CONFIG_ERC20_VERSION)).execute();
        }

        if(mSupport.qrc20 &&
                (getBaseDao().onSelectQrcTokens().size() == 0 || getBaseDao().getQrcVersion()< (int)mFirebaseRemoteConfig.getLong(BaseConstant.CONFIG_QRC20_VERSION))) {
           new QrcTokenUpdateTask(getBaseApplication(), (int)mFirebaseRemoteConfig.getLong(BaseConstant.CONFIG_QRC20_VERSION)).execute();
        }

    }


    private void onShowUpdateDialog(boolean force) {
        if(force) {
            DialogUpdateForce dialog  = new DialogUpdateForce();
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), "update");

        } else {
            DialogUpdate dialog  = new DialogUpdate();
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), "update");
        }
    }
}
