package wannabit.io.ringowallet.acticites.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.PasswordActivity;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.fragments.MainFavoFragment;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.utils.WUtils;

public class KeyPairSetActivity extends BaseActivity {

    private TextView                    mTitle;
    private SwipeRefreshLayout          mSwipeRefreshLayout;
    private RecyclerView                mRecyclerView;

    private ArrayList<Key>              mKeys = new ArrayList<>();
    private KeyPairAdapter              mAdapater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_keypair);

        mTitle                  = findViewById(R.id.toolbar_title);
        mSwipeRefreshLayout     = findViewById(R.id.layer_refresher);
        mRecyclerView           = findViewById(R.id.recycler);
        mTitle.setText(R.string.str_key_pair_manage);
        mTitle.setTypeface(WUtils.getTypefaceRegular(this));

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        mRecyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mAdapater = new KeyPairAdapter(Glide.with(getBaseContext()));
        mRecyclerView.setAdapter(mAdapater);
        onUpdateView();
    }


    private void onUpdateView() {
        mKeys = getBaseDao().onSelectAllRawKeys();
        mAdapater.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }



    class KeyPairAdapter extends RecyclerView.Adapter<KeyPairAdapter.KeyPairHolder> {

        private RequestManager mRequestManager;

        public KeyPairAdapter(RequestManager requestManager) {
            this.mRequestManager = requestManager;
        }

        @NonNull
        @Override
        public KeyPairHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = getLayoutInflater().inflate(R.layout.item_keypair_manage, viewGroup, false);
            return new KeyPairHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final KeyPairHolder holder, int position) {
            final Key key = mKeys.get(position);
            holder.itemKeySymbol.setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
            holder.itemDate.setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
            holder.itemPath.setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
            holder.itemAddress.setTypeface(WUtils.getTypefaceLight(getBaseContext()));

            holder.itemKeySymbol.setText(key.symbol);
            holder.itemDate.setText(WUtils.getDpKeyDate(getBaseApplication(), key.genDate));
            holder.itemPath.setVisibility(View.GONE);
//            holder.itemPath.setText(WUtils.getDefaultPath(getBaseApplication(), key.type) + key.path);
            holder.itemAddress.setText(key.address);

            if(WUtils.isMainCoin(key.symbol)) {
                holder.itemKeyImg.setImageDrawable(getResources().getDrawable(WUtils.getIconResource(key)));

            } else {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mRequestManager
                                .load(getBaseDao().onSelectTokenBySymbol(key.symbol).iconUrl)
                                .into(holder.itemKeyImg);
                    }
                });
            }

            holder.itemRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), PasswordActivity.class);
                    intent.putExtra(BaseConstant.CONST_PW_PURPOSE, BaseConstant.CONST_PW_CHECK_KEY);
                    intent.putExtra("uuid", key.uuid);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mKeys.size();
        }

        public class KeyPairHolder extends RecyclerView.ViewHolder {
            CardView        itemRoot;
            ImageView       itemKeyImg;
            TextView        itemKeySymbol;
            TextView        itemDate, itemPath, itemAddress;

            public KeyPairHolder(View v) {
                super(v);
                itemRoot            = itemView.findViewById(R.id.key_root);
                itemKeyImg          = itemView.findViewById(R.id.item_key_img);
                itemKeySymbol       = itemView.findViewById(R.id.item_key_symbol);
                itemDate            = itemView.findViewById(R.id.item_key_date);
                itemPath            = itemView.findViewById(R.id.item_key_path);
                itemAddress         = itemView.findViewById(R.id.item_key_address);

            }
        }
    }
}
