package wannabit.io.ringowallet.fragments;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.CreateWalletActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.task.InitMnemonicTask;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.utils.WKeyUtils;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class RestoreByMnemonicFragment extends BaseFragment implements View.OnClickListener, TaskCallback {

    private EditText            mInput;
    private FrameLayout         mBtnPaste;
    private TextView[]          mTvChecks = new TextView[12];
    private Button              mNextBtn;

    private ArrayList<String>   mWords = new ArrayList<>();

    public static RestoreByMnemonicFragment newInstance() {
        RestoreByMnemonicFragment fragment = new RestoreByMnemonicFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restore_by_mnemonic, container, false);
        ((TextView)rootView.findViewById(R.id.tv_restore_msg0)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        ((TextView)rootView.findViewById(R.id.tv_paste)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));

        mInput      = rootView.findViewById(R.id.et_mneminocs_input);
        mBtnPaste   = rootView.findViewById(R.id.btn_paste);
        for(int i = 0; i < mTvChecks.length; i++) {
            mTvChecks[i] = rootView.findViewById(getResources().getIdentifier("tv_restore_" + i , "id", getBaseActivity().getPackageName()));
            mTvChecks[i].setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
            mTvChecks[i].setOnClickListener(this);
        }
        mNextBtn = rootView.findViewById(R.id.btn_restore);
        mBtnPaste.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((CreateWalletActivity)getBaseActivity()).onUpdateTitle(getString(R.string.title_restore_mnemonics));
    }


    private void onUpdateWordsList() {
        for(int i = 0; i < mTvChecks.length; i++) {
            mTvChecks[i].setBackground(getResources().getDrawable(R.drawable.roundbox_word_normal));
            if(mWords.size() > i) {
                mTvChecks[i].setText(mWords.get(i));
                if(!WKeyUtils.isMnemonicWord(mWords.get(i))) {
                    mTvChecks[i].setBackground(getResources().getDrawable(R.drawable.roundbox_word_unchecked));
                }

            } else {
                mTvChecks[i].setText("");
            }
        }

        if(isValidMnemonic()) {
            mNextBtn.setText(R.string.str_next);
        } else {
            mNextBtn.setText(R.string.str_insert);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBtnPaste)) {
            ClipboardManager clipboard = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            if(clipboard.getPrimaryClip() != null && clipboard.getPrimaryClip().getItemCount() > 0) {
                mInput.setText(clipboard.getPrimaryClip().getItemAt(0).coerceToText(getActivity()));
            } else {
                Toast.makeText(getActivity(), R.string.msg_error_clipboard, Toast.LENGTH_SHORT).show();
            }

        } else if (v.equals(mNextBtn)) {
            getBaseActivity().onHideKeyboard();
            if(isValidMnemonic()) {
                String seed = WKeyUtils.getSeedfromWords(mWords);
                if(!TextUtils.isEmpty(seed)) {
                    getBaseActivity().onShowWaitDialog();
                    new InitMnemonicTask(getBaseApplication(), this).execute(seed);
                }

            } else {
                String userinput = mInput.getText().toString().trim();
                if(TextUtils.isEmpty(userinput)) return;

                ArrayList<String> newinsert = new ArrayList<>(Arrays.asList(userinput.split("\\s+")));
                mInput.setText("");
                for(String text:newinsert) {
                    text = text.replace(",","").replace(" ", "");
                    mWords.add(text);
                    if(mWords.size() >= 12) break;
                }
                onUpdateWordsList();
            }

        } else if (v instanceof TextView){
            String target = ((TextView)v).getText().toString().trim();
            if(!TextUtils.isEmpty(target)) {
                mWords.remove(target);
                onUpdateWordsList();
            }

        }

    }


    private boolean isValidMnemonic() {
        if(mWords.size() != 12) return false;
        for(String text : mWords) {
            if(!WKeyUtils.isMnemonicWord(text)) return false;
        }
        return true;

    }

    @Override
    public void onTaskResponse(TaskResult result) {
        if(!isAdded()) return;
        getBaseActivity().onHideWaitDialog();
        if (result.taskType == BaseConstant.TASK_INIT_MN) {
            if(result.isSuccess) {
                getBaseActivity().onStartMainActivity();
            } else {}
        }
    }
}