package wannabit.io.ringowallet.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.ReceiveActivity;
import wannabit.io.ringowallet.acticites.SendActivity;
import wannabit.io.ringowallet.acticites.WebActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.Price;
import wannabit.io.ringowallet.model.Tx;
import wannabit.io.ringowallet.network.ApiClient;
import wannabit.io.ringowallet.network.res.ResBchHistory;
import wannabit.io.ringowallet.network.res.ResBtcHistory;
import wannabit.io.ringowallet.network.res.ResEtcHistory;
import wannabit.io.ringowallet.network.res.ResEthHistory;
import wannabit.io.ringowallet.network.res.ResLtcHistory;
import wannabit.io.ringowallet.network.res.ResQtumHistory;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;
import wannabit.io.ringowallet.utils.converter.AddressConverter;

public class WalletDetailFragment extends BaseFragment implements View.OnClickListener{

    private ImageView               mFavoImg, mKeyRawImg;
    private TextView                mAddressTv, mPageTv, mBalanceTv, mValueTv;
    private FrameLayout             mSendBtn, mReceiveBtn, mSwapBtn;
    private RecyclerView            mRecyclerView;
    private LinearLayout            mTxEmptyLayer;


    private LinearLayoutManager     mLinearLayoutManager;
    private TxAdapter               mTxAdapter;

    private Key                     mKey;
    private Price                   mPrice;
    private ArrayList<Tx>           mTxs = new ArrayList<>();

    public static WalletDetailFragment newInstance(Bundle bundle) {
        WalletDetailFragment fragment = new WalletDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView           = inflater.inflate(R.layout.fragment_wallet_detail, container, false);
        mRecyclerView           = rootView.findViewById(R.id.recycler);
        mFavoImg                = rootView.findViewById(R.id.data_favo);
        mKeyRawImg              = rootView.findViewById(R.id.data_raw);
        mAddressTv              = rootView.findViewById(R.id.data_address);
        mPageTv                 = rootView.findViewById(R.id.data_page);
        mBalanceTv              = rootView.findViewById(R.id.data_balance);
        mValueTv                = rootView.findViewById(R.id.data_value);
        mSendBtn                = rootView.findViewById(R.id.btn_send);
        mReceiveBtn             = rootView.findViewById(R.id.btn_receive);
        mSwapBtn                = rootView.findViewById(R.id.btn_swap);
        mTxEmptyLayer           = rootView.findViewById(R.id.trx_empty_layer);

        ((TextView)rootView.findViewById(R.id.tv_send)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        ((TextView)rootView.findViewById(R.id.tv_receive)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        ((TextView)rootView.findViewById(R.id.tv_swap)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        ((TextView)rootView.findViewById(R.id.data_transaction)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        ((TextView)rootView.findViewById(R.id.trx_empty_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mAddressTv.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mPageTv.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mBalanceTv.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mValueTv.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));

        mFavoImg.setOnClickListener(this);
        mAddressTv.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mReceiveBtn.setOnClickListener(this);
        mSwapBtn.setOnClickListener(this);

        mLinearLayoutManager = new LinearLayoutManager(getBaseActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mTxAdapter = new TxAdapter();
        mRecyclerView.setAdapter(mTxAdapter);

        onInitDataLayer();
        return rootView;
    }

    public void setKey(Key key) {
        this.mKey = key;


    }

    private void onInitDataLayer() {
        if(mKey != null) {
            onFetchHistory();
            mPrice = getBaseDao().onSelectPriceBySymbol(mKey.symbol);
            mPageTv.setText(String.valueOf(getArguments().getInt("position") + 1) + " / " + String.valueOf(getArguments().getInt("size")));
//            if(mKey.type.equals(BaseConstant.COIN_BCH) || mKey.type.equals(BaseConstant.COIN_BSV)) mAddressTv.setText(AddressConverter.toCashAddress(mKey.address));
//            else mAddressTv.setText(mKey.address);
            mAddressTv.setText(mKey.address);
            if(mKey.isFavo) mFavoImg.setImageDrawable(getResources().getDrawable(R.drawable.favarite_on));
            else mFavoImg.setImageDrawable(getResources().getDrawable(R.drawable.favarite_none));
            if(mKey.isRaw) mKeyRawImg.setImageDrawable(getResources().getDrawable(R.drawable.key_ic));
            else mKeyRawImg.setImageDrawable(getResources().getDrawable(R.drawable.mnemonics_ic));
            mBalanceTv.setText(WUtils.getDpKeyBalance(getBaseApplication(), mKey, true));
            mValueTv.setText(WUtils.getDpKeyValue(getBaseApplication(), mKey, mPrice));
            onUpdateHistory();
        }
    }

    private void onUpdateHistory() {
        if(WUtils.isMainCoin(mKey.symbol)) {
            mTxs = getBaseDao().onSelectAllTxByUuid(mKey.uuid);
        } else {
            mTxs = getBaseDao().onSelectAllERCTxByToken(mKey.address, getArguments().getString("contractAddr"));
        }
        if(mTxs.size() > 0 ) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTxEmptyLayer.setVisibility(View.GONE);
            mTxAdapter.notifyDataSetChanged();

        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mTxEmptyLayer.setVisibility(View.VISIBLE);

        }

    }


    @Override
    public void onClick(View v) {
        if (v.equals(mFavoImg)) {
            if(mKey.isFavo) {
                if(getBaseDao().onToggleFavo(mKey.uuid, false) > 0) {
                    mKey.isFavo = false;
                    mFavoImg.setImageDrawable(getResources().getDrawable(R.drawable.favarite_none));
                }

            } else {
                if(getBaseDao().onToggleFavo(mKey.uuid, true) > 0) {
                    mKey.isFavo = true;
                    mFavoImg.setImageDrawable(getResources().getDrawable(R.drawable.favarite_on));
                }
            }


        } else if (v.equals(mAddressTv)) {
            ClipboardManager clipboard = (ClipboardManager) getBaseActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("mnemonic", mAddressTv.getText().toString().trim());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getBaseActivity(), R.string.msg_copied, Toast.LENGTH_SHORT).show();

        } else if (v.equals(mSendBtn)) {
            if(mKey.lastBalance.equals(BigDecimal.ZERO)) {
                Toast.makeText(getBaseActivity(), R.string.msg_not_enough_balance, Toast.LENGTH_SHORT).show();
            } else {
                Intent sendStart = new Intent(getBaseActivity(), SendActivity.class);
                sendStart.putExtra("uuid" , mKey.uuid);
                startActivity(sendStart);
            }


        } else if (v.equals(mReceiveBtn)) {
            Intent receiveStart = new Intent(getBaseActivity(), ReceiveActivity.class);
            receiveStart.putExtra("uuid" , mKey.uuid);
            startActivity(receiveStart);

        } else if (v.equals(mSwapBtn)) {
            Toast.makeText(getBaseActivity(), R.string.msg_prepareing_swap, Toast.LENGTH_SHORT).show();

        }

    }


    class TxAdapter extends RecyclerView.Adapter<TxAdapter.TxItemHolder> {

        @Override
        public TxItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = getLayoutInflater().inflate(R.layout.item_detail_tx, viewGroup, false);
            return new TxItemHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final TxItemHolder holder, final int position) {
            final Tx tx = mTxs.get(position);
            if(position == 0) {
                holder.itemTxSeparatorTop.setVisibility(View.VISIBLE);
            } else {
                holder.itemTxSeparatorTop.setVisibility(View.GONE);
            }
            holder.itemTxType.setTypeface(WUtils.getTypefaceLight(getBaseActivity()));
            holder.itemTxResult.setTypeface(WUtils.getTypefaceLight(getBaseActivity()));
            holder.itemTxAmout.setTypeface(WUtils.getTypefaceLight(getBaseActivity()));
            holder.itemTxPrice.setTypeface(WUtils.getTypefaceLight(getBaseActivity()));
            holder.itemTxDate.setTypeface(WUtils.getTypefaceLight(getBaseActivity()));

            if(tx.from.toLowerCase().contains(mKey.address.toLowerCase())) {
                //Send tx
                holder.itemTxImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_ic));
                holder.itemTxType.setText(R.string.str_sent);

            } else {
                //Receive tx
                holder.itemTxImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_receive_ic));
                holder.itemTxType.setText(R.string.str_received);


            }

            holder.itemTxAmout.setText(WUtils.getDpTxAmount(getBaseApplication(), tx, mKey, true));
            holder.itemTxDate.setText(WUtils.getDpTxDate(getBaseApplication(), tx, mKey));
            holder.itemTxRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent webintent = new Intent(getBaseActivity(), WebActivity.class);
                    webintent.putExtra("type", mKey.type);
                    webintent.putExtra("txid", tx.hash);
                    webintent.putExtra("goMain", false);
                    startActivity(webintent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mTxs.size();
        }

        public class TxItemHolder extends RecyclerView.ViewHolder {
            LinearLayout    itemTxRoot;
            View            itemTxSeparatorTop, itemTxSeparatorBottom;
            ImageView       itemTxImg;
            TextView        itemTxType, itemTxResult, itemTxAmout, itemTxPrice, itemTxDate;

            public TxItemHolder(View v) {
                super(v);
                itemTxRoot              = itemView.findViewById(R.id.item_root);
                itemTxSeparatorTop      = itemView.findViewById(R.id.separator_top);
                itemTxSeparatorBottom   = itemView.findViewById(R.id.separator_bottom);
                itemTxImg               = itemView.findViewById(R.id.item_tx_type);
                itemTxType              = itemView.findViewById(R.id.item_type);
                itemTxResult            = itemView.findViewById(R.id.item_type_result);
                itemTxAmout             = itemView.findViewById(R.id.item_tx_amount);
                itemTxPrice             = itemView.findViewById(R.id.item_tx_price);
                itemTxDate              = itemView.findViewById(R.id.item_tx_date);
            }
        }

    }


    public void onFetchHistory() {
        if (mKey.type.equals(BaseConstant.COIN_BTC)) {
            ApiClient.getBlockChainInfoService(getBaseActivity()).getBTCHistory(mKey.address, "json", 0).enqueue(new Callback<ResBtcHistory>() {
                @Override
                public void onResponse(Call<ResBtcHistory> call, Response<ResBtcHistory> response) {
                    if(isAdded() && response.isSuccessful()) {
                        if(response.body().txs != null && response.body().txs.size() > 0) {
                            if(mTxs == null || mTxs.size() == 0) {
                                getBaseDao().onInsertTxs(WUtils.onChangeBtcHistoryToTx(mKey, 0, response.body().txs));
                            } else {
                                getBaseDao().onInsertTxs(WUtils.onChangeBtcHistoryToTx(mKey, mTxs.get(0).date, response.body().txs));
                            }
                            onUpdateHistory();
                        }

                    } else {
                        WLog.w("BTC has error");
                    }
                }

                @Override
                public void onFailure(Call<ResBtcHistory> call, Throwable t) {
                    WLog.w("BTC onFetchHistory Fail");
                }
            });


        } else if (mKey.type.equals(BaseConstant.COIN_BCH)) {
            ApiClient.getBlockDozerService(getBaseActivity()).getBchHistory(mKey.address).enqueue(new Callback<ResBchHistory>() {
                @Override
                public void onResponse(Call<ResBchHistory> call, Response<ResBchHistory> response) {
                    if(isAdded() && response.isSuccessful()) {
                        if(mTxs == null || mTxs.size() == 0) {
                            getBaseDao().onInsertTxs(WUtils.onChangeBCHHistoryToTx(mKey, 0, response.body().txs));
                        } else {
                            getBaseDao().onInsertTxs(WUtils.onChangeBCHHistoryToTx(mKey, mTxs.get(0).date, response.body().txs));
                        }
                        onUpdateHistory();

                    } else {
                        WLog.w("BCH has error");
                    }
                }

                @Override
                public void onFailure(Call<ResBchHistory> call, Throwable t) {
                    WLog.w("BCH onFetchHistory Fail");
                }
            });

        } else if (mKey.type.equals(BaseConstant.COIN_BSV)) {

        } else if (mKey.type.equals(BaseConstant.COIN_LTC)) {
            ApiClient.getBlockCypherService(getBaseActivity()).getLTCHistory(mKey.address, 50).enqueue(new Callback<ResLtcHistory>() {
                @Override
                public void onResponse(Call<ResLtcHistory> call, Response<ResLtcHistory> response) {
                    if(isAdded() && response.isSuccessful()) {
                        if(response.body().txs != null && response.body().txs.size() > 0) {
                            if(mTxs == null || mTxs.size() == 0) {
                                getBaseDao().onInsertTxs(WUtils.onChangeLTCHistoryToTx(mKey, 0, response.body().txs));
                            } else {
                                getBaseDao().onInsertTxs(WUtils.onChangeLTCHistoryToTx(mKey, mTxs.get(0).date, response.body().txs));
                            }
                            onUpdateHistory();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResLtcHistory> call, Throwable t) {
                    WLog.w("LTC has error : " + t.getMessage());
                }
            });

        } else if (mKey.type.equals(BaseConstant.COIN_ETH)) {
            ApiClient.getEtherScanService(getBaseActivity()).getEthHistory("account", "txlist", mKey.address, 0, 99999999, "desc", "").enqueue(new Callback<ResEthHistory>() {
                @Override
                public void onResponse(Call<ResEthHistory> call, Response<ResEthHistory> response) {
                    if(response.isSuccessful() && response.body().status == 1) {
                        if(isAdded() && response.body().result != null && response.body().result.size() > 0) {
                            if(mTxs == null || mTxs.size() == 0) {
                                getBaseDao().onInsertTxs(WUtils.onChangeEthHistoryToTx(mKey, 0, response.body().result));
                            } else {
                                getBaseDao().onInsertTxs(WUtils.onChangeEthHistoryToTx(mKey, mTxs.get(0).date, response.body().result));
                            }
                            onUpdateHistory();
                        }

                    } else {

                    }
                }

                @Override
                public void onFailure(Call<ResEthHistory> call, Throwable t) {
                    WLog.w("ETH onFetchHistory Fail");
                }
            });


        } else if (mKey.type.equals(BaseConstant.COIN_ETC)) {
            ApiClient.getGasTrackerService(getBaseActivity()).getETC(mKey.address).enqueue(new Callback<ResEtcHistory>() {
                @Override
                public void onResponse(Call<ResEtcHistory> call, Response<ResEtcHistory> response) {
                    if(isAdded() && response.body().items != null && response.body().items.size() > 0) {
                        if(mTxs == null || mTxs.size() == 0) {
                            getBaseDao().onInsertTxs(WUtils.onChangeEtcHistoryToTx(mKey, 0, response.body().items));
                        } else {
                            getBaseDao().onInsertTxs(WUtils.onChangeEtcHistoryToTx(mKey, mTxs.get(0).date, response.body().items));
                        }
                        onUpdateHistory();
                    }
                }

                @Override
                public void onFailure(Call<ResEtcHistory> call, Throwable t) {
                    WLog.w("ETC onFetchHistory Fail : " + t.getMessage());
                }
            });

        } else if (mKey.type.equals(BaseConstant.COIN_QTUM)) {
            ApiClient.getQtumExploreService(getBaseActivity()).getQtumHistory(mKey.address).enqueue(new Callback<ResQtumHistory>() {
                @Override
                public void onResponse(Call<ResQtumHistory> call, Response<ResQtumHistory> response) {
                    if(mTxs == null || mTxs.size() == 0) {
                        getBaseDao().onInsertTxs(WUtils.onChangeQtumHistoryToTx(mKey, 0, response.body().txs));
                    } else {
                        getBaseDao().onInsertTxs(WUtils.onChangeQtumHistoryToTx(mKey, mTxs.get(0).date, response.body().txs));
                    }
                    onUpdateHistory();
                }

                @Override
                public void onFailure(Call<ResQtumHistory> call, Throwable t) {
                    WLog.w("ETC onFetchHistory Fail : " + t.getMessage());
                }
            });


        } else if (mKey.type.equals(BaseConstant.COIN_ERC20)) {
            ApiClient.getEtherScanService(getBaseActivity()).getTokenHistory("account", "tokentx", mKey.address, 0, 99999999, "desc", "").enqueue(new Callback<ResEthHistory>() {
                @Override
                public void onResponse(Call<ResEthHistory> call, Response<ResEthHistory> response) {
                    if(isAdded() && response.isSuccessful() && response.body().status == 1) {
                        if(response.body().result != null && response.body().result.size() > 0) {
                            if(mTxs == null || mTxs.size() == 0) {
                                getBaseDao().onInsertERCTxs(WUtils.onChangeErcHistoryToTx(mKey, 0, response.body().result, mKey.address));
                            } else {
                                getBaseDao().onInsertERCTxs(WUtils.onChangeErcHistoryToTx(mKey, mTxs.get(0).date, response.body().result, mKey.address));
                            }
                            onUpdateHistory();
                        }

                    } else {

                    }
                }

                @Override
                public void onFailure(Call<ResEthHistory> call, Throwable t) {
                    WLog.w("ERC onFetchHistory Fail ");
                }
            });

        } else if (mKey.type.equals(BaseConstant.COIN_QRC20)) {

        }
    }















}
