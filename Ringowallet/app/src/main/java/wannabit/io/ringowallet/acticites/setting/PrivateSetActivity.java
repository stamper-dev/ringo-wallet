package wannabit.io.ringowallet.acticites.setting;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Arrays;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.dialog.Dialog_LockTime;
import wannabit.io.ringowallet.dialog.Dialog_SendConfirm;
import wannabit.io.ringowallet.utils.WUtils;

public class PrivateSetActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private RelativeLayout      mBtnLockTime, mBtnFingerPrint;
    private TextView            mLockTime;
    private SwitchCompat        mFingerSwitch;
    private View                mMiddleLine, mEndLine;

    private ArrayList<String>   mTime = new ArrayList<>();
    private FingerprintManagerCompat mFingerprintManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_private);
        mBtnLockTime        = findViewById(R.id.set_lock_time);
        mBtnFingerPrint     = findViewById(R.id.set_fingerprint);
        mLockTime           = findViewById(R.id.set_lock_time_tv);
        mMiddleLine         = findViewById(R.id.set_lock_time_middle);
        mEndLine            = findViewById(R.id.set_lock_time_end);
        mFingerSwitch       = findViewById(R.id.set_fingerprint_switch);

        mFingerprintManagerCompat = FingerprintManagerCompat.from(this);

        ((TextView)findViewById(R.id.toolbar_title)).setTypeface(WUtils.getTypefaceRegular(this));
        ((TextView)findViewById(R.id.set_lock_time_title)).setTypeface(WUtils.getTypefaceRegular(this));
        ((TextView)findViewById(R.id.set_fingerprint_tv)).setTypeface(WUtils.getTypefaceRegular(this));
        mLockTime.setTypeface(WUtils.getTypefaceRegular(this));

        mTime = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.lock_time)));
        mBtnLockTime.setOnClickListener(this);
        mFingerSwitch.setOnCheckedChangeListener(this);

        onUpdateView();
    }


    public void onUpdateView() {
        mLockTime.setText(mTime.get(getBaseDao().getLockTime()));

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                mFingerprintManagerCompat.isHardwareDetected() &&
                mFingerprintManagerCompat.hasEnrolledFingerprints()) {
            mBtnFingerPrint.setVisibility(View.VISIBLE);
            mMiddleLine.setVisibility(View.VISIBLE);
            mEndLine.setVisibility(View.GONE);
            if(getBaseDao().getUsingFingerprint()) {
                mFingerSwitch.setChecked(true);
            } else {
                mFingerSwitch.setChecked(false);
            }

        } else {
            mBtnFingerPrint.setVisibility(View.GONE);
            mMiddleLine.setVisibility(View.GONE);
            mEndLine.setVisibility(View.VISIBLE);

        }

    }



    @Override
    public void onClick(View v) {
        if(v.equals(mBtnLockTime)) {
            Dialog_LockTime dialog = Dialog_LockTime.newInstance();
            dialog.setCancelable(true);
            dialog.show(getSupportFragmentManager(), "dialog");
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        if(buttonView.isPressed()) {
            if(isChecked) {
                new TedPermission(this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                getBaseDao().setUsingFingerprint(isChecked);
                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                Toast.makeText(getBaseContext(), R.string.msg_error_permission, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPermissions(Manifest.permission.USE_FINGERPRINT)
                        .setRationaleMessage("need permission for finger print")
                        .check();
            } else {
                getBaseDao().setUsingFingerprint(isChecked);
            }
        }
    }
}
