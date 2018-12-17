package wannabit.io.ringowallet.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ImmutableList;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.DeterministicSeed;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.CreateWalletActivity;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.utils.WKeyUtils;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

import static com.google.common.base.Preconditions.checkArgument;

public class GenerateMnemonicFragment extends BaseFragment implements View.OnClickListener{

    private TextView        mTvMsg01, mTvMsg02, mTvCopy;
    private Button          mBtnRefresh, mBtnNext;
    private FrameLayout     mBtnCopy;
    private TextView[]      mTvWords = new TextView[12];

    private ArrayList<String>   mWords = new ArrayList<>();
    private byte[]              mSeed;

    public static GenerateMnemonicFragment newInstance() {
        GenerateMnemonicFragment fragment = new GenerateMnemonicFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView   = inflater.inflate(R.layout.fragment_generate_mnemonic, container, false);
        mTvMsg01        = rootView.findViewById(R.id.tv_generate_msg0);
        mTvMsg02        = rootView.findViewById(R.id.tv_generate_msg1);
        mTvCopy         = rootView.findViewById(R.id.tv_copy);
        mBtnCopy        = rootView.findViewById(R.id.btn_copy);
        mBtnRefresh     = rootView.findViewById(R.id.btn_refresh);
        mBtnNext        = rootView.findViewById(R.id.btn_next);

        for(int i = 0; i < mTvWords.length; i++) {
            mTvWords[i] = rootView.findViewById(getResources().getIdentifier("tv_mnemonic_" + i , "id", getBaseActivity().getPackageName()));
            mTvWords[i].setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        }

        mTvMsg01.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mTvMsg02.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mTvCopy.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mBtnRefresh.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mBtnNext.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));

        mBtnCopy.setOnClickListener(this);
        mBtnRefresh.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);

        onRefreshSeeds();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CreateWalletActivity)getBaseActivity()).onUpdateTitle(getString(R.string.title_create_new_wallet));
    }




    @Override
    public void onClick(View v) {
        if (v.equals(mBtnRefresh)) {
            onRefreshSeeds();

        } else if (v.equals(mBtnCopy)) {
            ClipboardManager clipboard = (ClipboardManager) getBaseActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("mnemonic", getClipboard());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getBaseActivity(), R.string.msg_copied, Toast.LENGTH_SHORT).show();

        } else if (v.equals(mBtnNext)) {
            CheckMnemonicFragment checkMnemonicFragment = CheckMnemonicFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("words", mWords);
            bundle.putString("seed", WUtils.ByteArrayToHexString(mSeed));
            checkMnemonicFragment.setArguments(bundle);

            FragmentTransaction transaction = getTransaction();
            transaction.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit, R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit);
            transaction.replace(R.id.main_container, checkMnemonicFragment);
            transaction.addToBackStack("generate");
            transaction.commit();
        }
    }


    private void onRefreshSeeds() {
        mSeed = WKeyUtils.getEntropy();
        mWords = new ArrayList<String>(WKeyUtils.getRandomMnemonic(mSeed));

        for(int i = 0; i < mTvWords.length; i++) {
            mTvWords[i].setText(mWords.get(i));
        }
    }

    private String getClipboard() {
        StringBuilder builder = new StringBuilder();
        for(String s : mWords) {
            builder.append(" ");
            builder.append(s);
        }
        return builder.toString();
    }

}