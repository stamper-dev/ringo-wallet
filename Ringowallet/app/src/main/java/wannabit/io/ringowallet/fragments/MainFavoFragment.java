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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import java.util.ArrayList;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.WalletDetailActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.task.balance.BalanceCheckFactory;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class MainFavoFragment extends BaseFragment {

    private SwipeRefreshLayout          mSwipeRefreshLayout;
    private RecyclerView                mRecyclerView;
    private LinearLayout                mEmptyView;

    private LinearLayoutManager         mLinearLayoutManager;
    private FavoAdapter                 mFavoAdapter;
    private ArrayList<Key>              mKeys = new ArrayList<>();

    public static MainFavoFragment newInstance() {
        MainFavoFragment fragment = new MainFavoFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_favo, container, false);
        mSwipeRefreshLayout     = rootView.findViewById(R.id.layer_refresher);
        mRecyclerView           = rootView.findViewById(R.id.recycler);
        mEmptyView              = rootView.findViewById(R.id.favo_empty_layer);
        ((TextView)rootView.findViewById(R.id.trx_empty_tv)).setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mRecyclerView.setNestedScrollingEnabled(false);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onUpdateView();
            }
        });

        mLinearLayoutManager = new LinearLayoutManager(getBaseActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mFavoAdapter = new FavoAdapter(Glide.with(getBaseActivity()));
        mRecyclerView.setAdapter(mFavoAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onUpdateView();
    }

    @Override
    public void onRefreshTab(boolean deep) {
        super.onRefreshTab(deep);
        if(deep) {
            onUpdateView();
        }
    }

    private void onUpdateView() {
        mKeys = getBaseDao().onSelectAllFavoKeys();
        WLog.w("mItems : " + mKeys.size());
        if(mKeys.size() > 0) {
            mFavoAdapter.notifyDataSetChanged();
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void onFetchBalance(final String uuid) {
        boolean contain = FluentIterable.from(mKeys).anyMatch(new Predicate<Key>() {
            @Override
            public boolean apply(@Nullable Key input) {
                return input.uuid.equals(uuid);
            }
        });
        if(contain)
            onUpdateView();

    }

    class FavoAdapter extends RecyclerView.Adapter<FavoAdapter.FavoItemHolder> {


        private RequestManager mRequestManager;

        public FavoAdapter(RequestManager requestManager) {
            this.mRequestManager = requestManager;
        }

        @NonNull
        @Override
        public FavoItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = getLayoutInflater().inflate(R.layout.item_favo, viewGroup, false);
            return new FavoItemHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final FavoItemHolder holder, int position) {
            final Key key = mKeys.get(position);
            holder.itemCoinSymbol.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
            holder.itemTypeTv.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
            holder.itemDate.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
            holder.itemBalance.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
            holder.itemAddress.setTypeface(WUtils.getTypefaceLight(getBaseActivity()));

            if(key.isRaw) {
                holder.itemKeyType.setImageDrawable(getResources().getDrawable(R.drawable.key_ic));
                holder.itemTypeTv.setText(R.string.str_with_private_key);
            } else{
                holder.itemKeyType.setImageDrawable(getResources().getDrawable(R.drawable.mnemonics_ic));
                holder.itemTypeTv.setText(R.string.str_mnemonics);
            }

            holder.itemCoinSymbol.setText(key.symbol);
            holder.itemAddress.setText(key.address);
            holder.itemBalance.setText(WUtils.getDpKeyBalance(getBaseApplication(), key, false));
            holder.itemDate.setText(WUtils.getDpKeyDate(getBaseApplication(),key.genDate));

            if(WUtils.isMainCoin(key.symbol)) {
                holder.itemCoinImg.setImageDrawable(getResources().getDrawable(WUtils.getIconResource(key)));

            } else {
                mRequestManager
                        .load(getBaseDao().onSelectTokenBySymbol(key.symbol).iconUrl)
                        .into(holder.itemCoinImg);
            }

            holder.itemRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(getBaseActivity(), WalletDetailActivity.class);
                    detailIntent.putExtra("symbol",         key.symbol);
                    detailIntent.putExtra("type",           key.type);
                    if(!WUtils.isMainCoin(key.symbol))
                        detailIntent.putExtra("contractAddr",  getBaseDao().onSelectTokenBySymbol(key.symbol).contractAddr);
                    detailIntent.putExtra("page",           key.uuid);
                    startActivity(detailIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mKeys.size();
        }

        public class FavoItemHolder extends RecyclerView.ViewHolder {
            CardView        itemRoot;
            ImageView       itemCoinImg, itemKeyType;
            TextView        itemCoinSymbol;
            TextView        itemDate, itemBalance, itemAddress, itemTypeTv;

            public FavoItemHolder(View v) {
                super(v);
                itemRoot            = itemView.findViewById(R.id.favo_root);
                itemCoinImg         = itemView.findViewById(R.id.item_coin_img);
                itemKeyType         = itemView.findViewById(R.id.item_type);
                itemCoinSymbol      = itemView.findViewById(R.id.item_coin_symbol);
                itemDate            = itemView.findViewById(R.id.item_create_date);
                itemBalance         = itemView.findViewById(R.id.item_balance);
                itemAddress         = itemView.findViewById(R.id.item_address);
                itemTypeTv          = itemView.findViewById(R.id.item_type_tv);

            }
        }
    }
}
