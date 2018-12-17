package wannabit.io.ringowallet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.TransitionInflater;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.CreateWalletActivity;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class CreateTypeFragment extends BaseFragment implements View.OnClickListener{


    private ImageView           mTopImg;
    private TextView            mMsg01;
    private Button              mRestoreKeyBtn, mRestoreMnemonicBtn, mCreateNewBtn;

    public static CreateTypeFragment newInstance(Bundle bundle) {
        CreateTypeFragment fragment = new CreateTypeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_type, container, false);

        mTopImg             = rootView.findViewById(R.id.image_top);
        mMsg01              = rootView.findViewById(R.id.create_msg1);
        mRestoreKeyBtn      = rootView.findViewById(R.id.btn_restore_key);
        mRestoreMnemonicBtn = rootView.findViewById(R.id.btn_restore_mnemonic);
        mCreateNewBtn       = rootView.findViewById(R.id.btn_new_wallet);

        mMsg01.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mRestoreKeyBtn.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mRestoreMnemonicBtn.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mCreateNewBtn.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));

        mRestoreKeyBtn.setOnClickListener(this);
        mRestoreMnemonicBtn.setOnClickListener(this);
        mCreateNewBtn.setOnClickListener(this);

        if(getArguments().getBoolean("mnemonic", false)) {
            mRestoreKeyBtn.setVisibility(View.INVISIBLE);
        } else {
            mRestoreKeyBtn.setVisibility(View.VISIBLE);
        }

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((CreateWalletActivity)getBaseActivity()).onUpdateTitle(getString(R.string.title_create_restore));
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mRestoreKeyBtn)) {
            FragmentTransaction transaction = getTransaction();
            transaction.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit, R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit);
            transaction.replace(R.id.main_container, RestoreByKeyFragment.newInstance(null));
            transaction.addSharedElement(mTopImg, "topImage");
            transaction.addToBackStack("type");
            transaction.commit();


        } else if (v.equals(mRestoreMnemonicBtn)) {
            FragmentTransaction transaction = getTransaction();
            transaction.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit, R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit);
            transaction.replace(R.id.main_container, RestoreByMnemonicFragment.newInstance());
            transaction.addToBackStack("type");
            transaction.commit();

        } else if (v.equals(mCreateNewBtn)) {
            FragmentTransaction transaction = getTransaction();
            transaction.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit, R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit);
            transaction.replace(R.id.main_container, GenerateMnemonicFragment.newInstance());
            transaction.addSharedElement(mTopImg, "topImage");
            transaction.addToBackStack("type");
            transaction.commit();

        }

    }
}