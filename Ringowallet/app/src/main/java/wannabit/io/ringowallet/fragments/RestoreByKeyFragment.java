package wannabit.io.ringowallet.fragments;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.CreateWalletActivity;
import wannabit.io.ringowallet.acticites.MainActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.task.RawKeyInsertTask;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.utils.WBHCUtils;
import wannabit.io.ringowallet.utils.WBTCUtils;
import wannabit.io.ringowallet.utils.WETCUtils;
import wannabit.io.ringowallet.utils.WETHUtils;
import wannabit.io.ringowallet.utils.WLTCUtils;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WQTUMUtils;
import wannabit.io.ringowallet.utils.WUtils;
import wannabit.io.ringowallet.utils.converter.AddressConverter;
import wannabit.io.ringowallet.views.BottomSheetAdapter;
import wannabit.io.ringowallet.views.BottomSheetListener;

public class RestoreByKeyFragment extends BaseFragment implements View.OnClickListener, TaskCallback, BottomSheetListener {

    private FrameLayout         mBgLayout;
    private View                mDummyView;
    private ImageView           mTopImg, mCoinImg, mArrow;
    private RelativeLayout      mLayerSelectCoin;
    private LinearLayout        mLayerSelectedCoin;
    private TextView            mTvMsg01, mTvCoinNameHint, mTvCoinName, mBtnPaste, mTvError;
    private EditText            mEtInputKey;
    private Button              mBtnConfirm;


    private SearchView          mBottomSearchView;
    private RecyclerView        mBottomRecycler;

    private BottomSheetBehavior mBottomSheetBehavior;
    private BottomSheetAdapter  mBottomSheetAdapter;

    private String          mSymbol, mTypeChain;

    public static RestoreByKeyFragment newInstance(Bundle bundle) {
        RestoreByKeyFragment fragment = new RestoreByKeyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WLog.w("onCreateView");
        View view  = inflater.inflate(R.layout.fragment_restore_by_key, container, false);

        mBgLayout           = view.findViewById(R.id.frame);
        mDummyView          = view.findViewById(R.id.dummy);
        mTopImg             = view.findViewById(R.id.image_top);
        mLayerSelectCoin    = view.findViewById(R.id.btn_select_coin);
        mTvMsg01            = view.findViewById(R.id.tv_restore_msg);
        mTvCoinNameHint     = view.findViewById(R.id.et_coin_name_hint);
        mLayerSelectedCoin  = view.findViewById(R.id.layer_selected);
        mCoinImg            = view.findViewById(R.id.et_coin_img);
        mTvCoinName         = view.findViewById(R.id.et_coin_name);
        mArrow              = view.findViewById(R.id.et_coin_arrow);
        mBtnPaste           = view.findViewById(R.id.btn_add_paste);
        mTvError            = view.findViewById(R.id.tv_key_error);
        mEtInputKey         = view.findViewById(R.id.et_key_input);
        mBtnConfirm         = view.findViewById(R.id.btn_restore);

        mTvError.setVisibility(View.INVISIBLE);

        mTvMsg01.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mTvCoinNameHint.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mTvCoinName.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mBtnPaste.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mTvError.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mEtInputKey.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mBtnConfirm.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));


        if(getArguments() != null && getArguments().getString("symbol") != null) {
            mSymbol                 = getArguments().getString("symbol");
            mTypeChain              = getArguments().getString("type");
            final String imageUrl   = getArguments().getString("imageUrl");
            final int imgId         = getArguments().getInt("imgId");

            if(!TextUtils.isEmpty(imageUrl)) {
                mCoinImg.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getBaseActivity())
                                .load(imageUrl)
                                .into(mCoinImg);
                    }
                });

            } else {
                mCoinImg.setImageDrawable(getResources().getDrawable(imgId));
            }
            mTvCoinName.setText(mSymbol);
            mTvCoinNameHint.setVisibility(View.GONE);
            mLayerSelectedCoin.setVisibility(View.VISIBLE);
            mArrow.setVisibility(View.GONE);
            mLayerSelectCoin.setEnabled(false);

        } else {
            mTvCoinNameHint.setVisibility(View.VISIBLE);
            mLayerSelectedCoin.setVisibility(View.GONE);
            mArrow.setVisibility(View.VISIBLE);
            mLayerSelectCoin.setEnabled(true);
            mLayerSelectCoin.setOnClickListener(this);

            mBottomSearchView   = getPactivity().getBottomSheet().findViewById(R.id.bottom_searchView);
            mBottomRecycler     = getPactivity().getBottomSheet().findViewById(R.id.bottom_sheet_recycler);

            mBottomSheetBehavior = BottomSheetBehavior.from(getPactivity().getBottomSheet());
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_EXPANDED:
                            WLog.w("STATE_EXPANDED");
                            break;

                        case BottomSheetBehavior.STATE_COLLAPSED:
                        case BottomSheetBehavior.STATE_HIDDEN:
                            WLog.w("STATE_HIDDEN");
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) { }
            });

            mBottomRecycler.setLayoutManager(new LinearLayoutManager(getPactivity(), LinearLayoutManager.VERTICAL, false));
            mBottomRecycler.setHasFixedSize(true);
            mBottomSheetAdapter = new BottomSheetAdapter(getBaseApplication(), this, Glide.with(getBaseActivity()));
            mBottomRecycler.setAdapter(mBottomSheetAdapter);

            mBottomSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mBottomSheetAdapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    mBottomSheetAdapter.getFilter().filter(query);
                    return false;
                }
            });
        }

        mBgLayout.setOnClickListener(this);
        mBtnPaste.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);

        return view;
    }

    public void onBackPressed() {
        if (mBottomSheetBehavior != null &&
                mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        } else {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0){
                fragmentManager.popBackStackImmediate();
            } else {
                getActivity().finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CreateWalletActivity)getBaseActivity()).onUpdateTitle(getString(R.string.title_private_key));
    }

    @Override
    public void onClick(View v) {
        WLog.w("onClick");
        getBaseActivity().onHideKeyboard();
        mDummyView.requestFocus();
        if (v.equals(mLayerSelectCoin)) {
            WLog.w("Click mLayerSelectCoin");
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else if (v.equals(mBtnPaste)) {
            WLog.w("Click mBtnPaste");
            ClipboardManager clipboard = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            if(clipboard.getPrimaryClip() != null && clipboard.getPrimaryClip().getItemCount() > 0) {
                mEtInputKey.setText(clipboard.getPrimaryClip().getItemAt(0).coerceToText(getActivity()));
            } else {
                Toast.makeText(getActivity(), R.string.msg_error_clipboard, Toast.LENGTH_SHORT).show();
            }

        } else if (v.equals(mBtnConfirm)) {
            String userInput = mEtInputKey.getText().toString().trim();
            if(onCheckKeyValidate(userInput)) {
                mTvError.setVisibility(View.GONE);
                String address = getAddress(userInput);
                if(!TextUtils.isEmpty(address)) {
                    getBaseActivity().onShowWaitDialog();
                    new RawKeyInsertTask(getBaseApplication(), this).execute(mTypeChain, mSymbol, userInput, address);
                }

            } else {
                mTvError.setVisibility(View.VISIBLE);
            }


        }
    }


    private boolean onCheckKeyValidate(String input) {
        if(mTypeChain.equals(BaseConstant.COIN_BTC)) {
            WBTCUtils utils = new WBTCUtils();
            return utils.isValidPrivateKey(input);

        } else if (mTypeChain.equals(BaseConstant.COIN_BCH)) {
            WBHCUtils utils = new WBHCUtils();
            return utils.isValidPrivateKey(input);

        } else if (mTypeChain.equals(BaseConstant.COIN_BSV)) {

        } else if (mTypeChain.equals(BaseConstant.COIN_LTC)) {
            WLTCUtils utils = new WLTCUtils();
            return utils.isValidPrivateKey(input);

        } else if (mTypeChain.equals(BaseConstant.COIN_ETH) || mTypeChain.equals(BaseConstant.COIN_ERC20)) {
            WETHUtils utils = new WETHUtils();
            return utils.isValidPrivateKey(input);

        } else if (mTypeChain.equals(BaseConstant.COIN_ETC)) {
            WETCUtils utils = new WETCUtils();
            return utils.isValidPrivateKey(input);

        } else if (mTypeChain.equals(BaseConstant.COIN_QTUM) || mTypeChain.equals(BaseConstant.COIN_QRC20)) {
            WQTUMUtils utils = new WQTUMUtils();
            return utils.isValidPrivateKey(input);
        }
        return false;
    }

    private String getAddress(String input) {
        if(mTypeChain.equals(BaseConstant.COIN_BTC)) {
            WBTCUtils utils = new WBTCUtils();
            return utils.getAddress(input);

        } else if (mTypeChain.equals(BaseConstant.COIN_BCH)) {
            WBHCUtils utils = new WBHCUtils();
            return utils.getAddress(input);

        } else if (mTypeChain.equals(BaseConstant.COIN_BSV)) {

        } else if (mTypeChain.equals(BaseConstant.COIN_LTC)) {
            WLTCUtils utils = new WLTCUtils();
            return utils.getAddress(input);

        } else if (mTypeChain.equals(BaseConstant.COIN_ETH) || mTypeChain.equals(BaseConstant.COIN_ERC20)) {
            WETHUtils utils = new WETHUtils();
            return utils.getAddress(input);

        } else if (mTypeChain.equals(BaseConstant.COIN_ETC)) {
            WETCUtils utils = new WETCUtils();
            return utils.getAddress(input);

        } else if (mTypeChain.equals(BaseConstant.COIN_QTUM) || mTypeChain.equals(BaseConstant.COIN_QRC20)) {
            WQTUMUtils utils = new WQTUMUtils();
            return utils.getAddress(input);

        }
        return "";
    }



    @Override
    public void onTaskResponse(TaskResult result) {
        if(!isAdded()) return;
        getBaseActivity().onHideWaitDialog();
        if (result.taskType == BaseConstant.TASK_INSERT_RAW_KEY) {
            if(result.isSuccess) {
                getBaseActivity().onStartMainActivity();
            } else {
                if(result.resultCode == -2) {
                    Toast.makeText(getBaseActivity(), R.string.msg_key_already_existed, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private CreateWalletActivity getPactivity() {
        return (CreateWalletActivity)getBaseActivity();
    }

    @Override
    public void onSelectItem(final Bundle bundle) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mSymbol         = bundle.getString("symbol");
        mTypeChain      = bundle.getString("type");

        mTvCoinName.setText(mSymbol);
        mTvCoinNameHint.setVisibility(View.GONE);
        mLayerSelectedCoin.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(bundle.getString("imageUrl"))) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(getBaseActivity())
                            .load(bundle.getString("imageUrl"))
                            .centerCrop()
                            .override(78, 78)
                            .placeholder(R.drawable.dialog_bg)
                            .into(mCoinImg);
                }
            });
        } else {
            mCoinImg.setImageDrawable(getResources().getDrawable(bundle.getInt("imgId")));
        }

    }
}