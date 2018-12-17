package wannabit.io.ringowallet.acticites;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.dialog.Dialog_DeleteKey;
import wannabit.io.ringowallet.dialog.Dialog_SendOK;
import wannabit.io.ringowallet.utils.WKeyUtils;
import wannabit.io.ringowallet.utils.WUtils;

public class RemindActivity extends BaseActivity implements View.OnClickListener{

    private TextView            mTitle;
    private Button              mBtnDelete, mBtnCopy;
    private LinearLayout        mNemonicLayer, mKeyLayer;
    private TextView[]          mTvChecks = new TextView[12];
    private TextView            mKeyP, mKeyPath;


    private int                         mType;
    private String                      mUuid;
    private String                      mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_remind);
        mTitle              = findViewById(R.id.toolbar_title);
        mBtnDelete          = findViewById(R.id.btn_delete);
        mBtnCopy            = findViewById(R.id.btn_copy);
        mNemonicLayer       = findViewById(R.id.mnemonic_layer);
        mKeyLayer           = findViewById(R.id.key_layer);
        mKeyP               = findViewById(R.id.tv_pkey);
        mKeyPath            = findViewById(R.id.tv_path);

        ((TextView)findViewById(R.id.tv_check_mnemonic)).setTypeface(WUtils.getTypefaceRegular(this));
        ((TextView)findViewById(R.id.tv_check_key)).setTypeface(WUtils.getTypefaceRegular(this));
        mTitle.setTypeface(WUtils.getTypefaceRegular(this));
        mKeyP.setTypeface(WUtils.getTypefaceRegular(this));
        mKeyPath.setTypeface(WUtils.getTypefaceLight(this));
        mBtnDelete.setTypeface(WUtils.getTypefaceRegular(this));
        mBtnCopy.setTypeface(WUtils.getTypefaceRegular(this));

        for(int i = 0; i < mTvChecks.length; i++) {
            mTvChecks[i] = findViewById(getResources().getIdentifier("mnemonic_" + i , "id", getPackageName()));
            mTvChecks[i].setTypeface(WUtils.getTypefaceRegular(this));
        }

        mType   = getIntent().getIntExtra(BaseConstant.CONST_PW_PURPOSE, -1);
        mUuid   = getIntent().getStringExtra("uuid");
        mData   = getIntent().getStringExtra("data");

        mBtnDelete.setOnClickListener(this);
        mBtnCopy.setOnClickListener(this);

        onInitView();

    }

    private void onInitView() {
        if(mType == BaseConstant.CONST_PW_CHECK_MNEMONIC) {
            mTitle.setText(R.string.str_mnemonic);
            mBtnDelete.setVisibility(View.GONE);
            mBtnCopy.setVisibility(View.VISIBLE);
            mNemonicLayer.setVisibility(View.VISIBLE);
            mKeyLayer.setVisibility(View.GONE);

            ArrayList<String> mWords = new ArrayList<String>(WKeyUtils.getRandomMnemonic(WUtils.HexStringToByteArray(mData)));
            for(int i = 0; i < mTvChecks.length; i++) {
                mTvChecks[i].setText(mWords.get(i));
            }

        } else {
            mTitle.setText(R.string.str_private_key);
            mBtnDelete.setVisibility(View.VISIBLE);
            mBtnCopy.setVisibility(View.VISIBLE);
            mNemonicLayer.setVisibility(View.GONE);
            mKeyLayer.setVisibility(View.VISIBLE);
            if(getBaseDao().onSelectKey(mUuid).isRaw) {
                mKeyPath.setVisibility(View.GONE);
            } else {
                mKeyPath.setVisibility(View.VISIBLE);
                mKeyPath.setText(WUtils.getDefaultPath(getBaseApplication(), getBaseDao().onSelectKey(mUuid).type) + getBaseDao().onSelectKey(mUuid).path);
            }
            mKeyP.setText(mData);

        }

    }

    public void onDeleteKey() {
        if(getBaseDao().onDeletePrivateKey(mUuid) > 0) {
            Toast.makeText(this, R.string.msg_delete_key_success, Toast.LENGTH_SHORT).show();
        }
        onStartMainActivity();

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBtnDelete)) {
            if(getBaseDao().onSelectAllKeys().size() <= 1) {
                Toast.makeText(this, R.string.msg_can_not_delete_last_key, Toast.LENGTH_SHORT).show();

            } else {
                Dialog_DeleteKey dialog = Dialog_DeleteKey.newInstance();
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "dialog");
            }



        } else if (v.equals(mBtnCopy)) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            String data = "";
            if(mType == BaseConstant.CONST_PW_CHECK_MNEMONIC) {
                ArrayList<String> words = new ArrayList<String>(WKeyUtils.getRandomMnemonic(WUtils.HexStringToByteArray(mData)));
                StringBuilder builder = new StringBuilder();
                for(String s : words) {
                    builder.append(" ");
                    builder.append(s);
                }
                data = builder.toString();

            } else {
                data = mData;
            }
            ClipData clip = ClipData.newPlainText("private data", data);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.msg_copied, Toast.LENGTH_SHORT).show();

        }

    }
}
