package wannabit.io.ringowallet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.MainActivity;
import wannabit.io.ringowallet.acticites.WalletDetailActivity;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.model.WalletItem;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class MainWalletFragment extends BaseFragment {

    private SwipeRefreshLayout              mSwipeRefreshLayout;
    private RecyclerView                    mRecyclerView;

    private LinearLayoutManager             mLinearLayoutManager;
    private WalletAdapter                   mWalletAdapter;

    public static MainWalletFragment newInstance() {
        MainWalletFragment fragment = new MainWalletFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_wallet, container, false);
        mSwipeRefreshLayout     = rootView.findViewById(R.id.layer_refresher);
        mRecyclerView           = rootView.findViewById(R.id.recycler);

        getMainActivity().getFloatingBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMainActivity().getFloatingBtn().hide();
                getMainActivity().onShowHideBottomSheet(true);

            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMainActivity().onFetchKeys();
            }
        });

        mLinearLayoutManager = new LinearLayoutManager(getBaseActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mWalletAdapter = new WalletAdapter(Glide.with(getBaseActivity()));
        mRecyclerView.setAdapter(mWalletAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (getMainActivity().getFloatingBtn().isShown()) {
                        getMainActivity().getFloatingBtn().hide();
                    }
                }
                else if (dy < 0) {
                    if (!getMainActivity().getFloatingBtn().isShown()) {
                        getMainActivity().getFloatingBtn().show();
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getMainActivity().mWalletItems != null)
            mWalletAdapter.setItems(getMainActivity().mWalletItems);
    }

    @Override
    public void onRefreshTab(boolean deep) {
        super.onRefreshTab(deep);
        if(deep) {
        }

    }

    public void onSetWallets(ArrayList<WalletItem> items) {
        if(mWalletAdapter != null) mWalletAdapter.setItems(items);
        if(mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(false);

    }

    class WalletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_CARD1 = 1;
        private static final int TYPE_CARD2 = 2;
        private static final int TYPE_CARD3 = 3;
        private RequestManager mRequestManager;
        private ArrayList<WalletItem>  mItems = new ArrayList<>();


        public WalletAdapter(RequestManager requestManager) {
            this.mRequestManager = requestManager;

        }

        public void setItems(ArrayList<WalletItem> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view;
            if(viewType == TYPE_CARD1) {
                view = getLayoutInflater().inflate(R.layout.item_wallet_card1, viewGroup, false);
                return new WalletItemHolder(view);

            } else if (viewType == TYPE_CARD2){
                view = getLayoutInflater().inflate(R.layout.item_wallet_card2, viewGroup, false);
                return new WalletItemHolder(view);

            }else {
                view = getLayoutInflater().inflate(R.layout.item_wallet_card3, viewGroup, false);
                return new WalletItemHolder(view);
            }
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final WalletItem walletItem = mItems.get(position);
            final WalletItemHolder itemHolder = (WalletItemHolder)holder;
            itemHolder.itemCoinSymbol.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
            itemHolder.itemCoinName.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
            itemHolder.itemCoinBalance.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
            itemHolder.itemCoinValue.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));

            if(WUtils.isMainCoin(walletItem.symbol)) {
                itemHolder.itemCoinImg.setImageDrawable(getResources().getDrawable(walletItem.iconId));

            } else {
                mRequestManager
                        .load(walletItem.iconUrl)
                        .into(itemHolder.itemCoinImg);
            }
            itemHolder.itemCoinSymbol.setText(walletItem.symbol);
            itemHolder.itemCoinName.setText(walletItem.name);
            itemHolder.itemCoinBalance.setText(WUtils.getDpTotalWalletBalance(getBaseApplication(), walletItem));
            itemHolder.itemCoinValue.setText(WUtils.getDPTotalWalletValue(getBaseApplication(), walletItem, getBaseDao().onSelectPriceBySymbol(walletItem.symbol)));

            itemHolder.itemCard0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(getBaseActivity(), WalletDetailActivity.class);
                    detailIntent.putExtra("symbol",         walletItem.symbol);
                    detailIntent.putExtra("type",           walletItem.type);
                    if(!WUtils.isMainCoin(walletItem.symbol))
                        detailIntent.putExtra("contractAddr", walletItem.contractAddr);
                    startActivity(detailIntent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (mItems.get(position).keys.size() == 1) {
                return TYPE_CARD1;
            } else if (mItems.get(position).keys.size() == 2) {
                return TYPE_CARD2;
            } else {
                return TYPE_CARD3;
            }

        }


        public class WalletItemHolder extends RecyclerView.ViewHolder {
            CardView  itemCard0, itemCard1, itemCard2;
            ImageView itemCoinImg;
            TextView itemCoinSymbol, itemCoinName, itemCoinBalance, itemCoinValue;

            public WalletItemHolder(View v) {
                super(v);
                itemCard0           = itemView.findViewById(R.id.card_main0);
                itemCard1           = itemView.findViewById(R.id.card_main1);
                itemCard2           = itemView.findViewById(R.id.card_main2);
                itemCoinImg         = itemView.findViewById(R.id.item_coin_img);
                itemCoinSymbol      = itemView.findViewById(R.id.item_coin_symbol);
                itemCoinName        = itemView.findViewById(R.id.item_coin_name);
                itemCoinBalance     = itemView.findViewById(R.id.item_coin_balance);
                itemCoinValue       = itemView.findViewById(R.id.item_coin_value);

            }
        }
    }

    public MainActivity getMainActivity() {
        return (MainActivity)getBaseActivity();
    }

}