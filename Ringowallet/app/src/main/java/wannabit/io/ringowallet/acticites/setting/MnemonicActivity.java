package wannabit.io.ringowallet.acticites.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import wannabit.io.ringowallet.acticites.PasswordActivity;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.Mnemonic;
import wannabit.io.ringowallet.utils.WUtils;

public class MnemonicActivity extends BaseActivity {

    private TextView                    mTitle;
    private SwipeRefreshLayout          mSwipeRefreshLayout;
    private RecyclerView                mRecyclerView;

    private Mnemonic                    mNemonic;
    private ArrayList<Key>              mKeys = new ArrayList<>();
    private MnemonicAdapter             mAdapater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_keypair);

        mTitle                  = findViewById(R.id.toolbar_title);
        mSwipeRefreshLayout     = findViewById(R.id.layer_refresher);
        mRecyclerView           = findViewById(R.id.recycler);
        mTitle.setText(R.string.str_mnemonic_manage);
        mTitle.setTypeface(WUtils.getTypefaceRegular(this));

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        mRecyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mAdapater = new MnemonicAdapter(Glide.with(getBaseContext()));
        mRecyclerView.setAdapter(mAdapater);
        onUpdateView();
    }


    private void onUpdateView() {
        mNemonic = getBaseDao().onSelectMnemonic();
        mKeys = getBaseDao().onSelectAllDeterministicKeys();
        mAdapater.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }




    class MnemonicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_MNEMONIC  = 0;
        private static final int TYPE_KEY       = 1;
        private RequestManager mRequestManager;

        public MnemonicAdapter(RequestManager requestManager) {
            this.mRequestManager = requestManager;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            if(viewType == TYPE_MNEMONIC) {
                View v = getLayoutInflater().inflate(R.layout.item_keypair_mnemonic, viewGroup, false);
                return  new MnemonicHolder(v);

            } else if(viewType == TYPE_KEY) {
                View v = getLayoutInflater().inflate(R.layout.item_keypair_manage, viewGroup, false);
                return new KeyHolder(v);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(position == 0) {
                final MnemonicHolder mnemonicHolder = (MnemonicHolder)holder;
                mnemonicHolder.itemtxt.setTypeface(WUtils.getTypefaceLight(getBaseContext()));
                mnemonicHolder.itemRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), PasswordActivity.class);
                        intent.putExtra(BaseConstant.CONST_PW_PURPOSE, BaseConstant.CONST_PW_CHECK_MNEMONIC);
                        startActivity(intent);
                    }
                });

            } else {
                final KeyHolder keyHolder = (KeyHolder)holder;
                final Key key = mKeys.get(position -1);
                keyHolder.itemKeySymbol.setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
                keyHolder.itemDate.setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
                keyHolder.itemPath.setTypeface(WUtils.getTypefaceRegular(getBaseContext()));
                keyHolder.itemAddress.setTypeface(WUtils.getTypefaceLight(getBaseContext()));

                keyHolder.itemKeySymbol.setText(key.symbol);
                keyHolder.itemDate.setText(WUtils.getDpKeyDate(getBaseApplication(), key.genDate));
                keyHolder.itemPath.setText(WUtils.getDefaultPath(getBaseApplication(), key.type) + key.path);
                keyHolder.itemAddress.setText(key.address);

                if(WUtils.isMainCoin(key.symbol)) {
                    keyHolder.itemKeyImg.setImageDrawable(getResources().getDrawable(WUtils.getIconResource(key)));

                } else {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(getBaseContext())
                                    .load(getBaseDao().onSelectTokenBySymbol(key.symbol).iconUrl)
                                    .into(keyHolder.itemKeyImg);
                        }
                    });
                }

                keyHolder.itemRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), PasswordActivity.class);
                        intent.putExtra(BaseConstant.CONST_PW_PURPOSE, BaseConstant.CONST_PW_CHECK_KEY);
                        intent.putExtra("uuid", key.uuid);
                        startActivity(intent);
                    }
                });
            }


        }

        @Override
        public int getItemCount() {
            if(mKeys.size() <= 0) {
                return 0;
            } else {
                return mKeys.size() + 1;
            }
        }


        @Override
        public int getItemViewType(int position) {
            if(position == 0)
                return TYPE_MNEMONIC;
            return TYPE_KEY;
        }


        public class MnemonicHolder extends RecyclerView.ViewHolder {
            CardView        itemRoot;
            TextView        itemtxt;

            public MnemonicHolder(View v) {
                super(v);
                itemRoot      = itemView.findViewById(R.id.mnemonic_root);
                itemtxt       = itemView.findViewById(R.id.mnemonic_tv);
            }
        }

        public class KeyHolder extends RecyclerView.ViewHolder {
            CardView        itemRoot;
            ImageView       itemKeyImg;
            TextView        itemKeySymbol;
            TextView        itemDate, itemPath, itemAddress;

            public KeyHolder(View v) {
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
