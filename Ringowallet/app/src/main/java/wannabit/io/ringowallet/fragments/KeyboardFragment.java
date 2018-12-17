package wannabit.io.ringowallet.fragments;

import android.support.v4.app.Fragment;

import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.utils.KeyboardListener;

public class KeyboardFragment extends BaseFragment {

    protected KeyboardListener mListner;

    public void setListener(KeyboardListener listener) {
        mListner = listener;
    }

    public void onShuffleKeyboard() { }
}


