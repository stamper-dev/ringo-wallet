package wannabit.io.ringowallet.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.dialog.BottomDialog_New_KeyPair;
import wannabit.io.ringowallet.model.Token;
import wannabit.io.ringowallet.utils.WUtils;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.BottomSheetItemHolder> implements Filterable {
    BaseApplication     app;
    ArrayList<Token>    coinList = new ArrayList<>();
    ArrayList<Token>    coinListFiltered = new ArrayList<>();
    BottomSheetListener listener;
    RequestManager mRequestManager;

    public BottomSheetAdapter(BaseApplication app, BottomSheetListener listener, RequestManager requestManager) {
        this.app = app;
        this.listener = listener;
        this.mRequestManager = requestManager;
        coinList = WUtils.getAddList(app);
        coinListFiltered = WUtils.getAddList(app);

    }

    @Override
    public BottomSheetAdapter.BottomSheetItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater)app.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v = inflater.inflate(R.layout.item_add_bottom_sheet, viewGroup, false);
        return new BottomSheetItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final BottomSheetAdapter.BottomSheetItemHolder holder, final int position) {
        final Token item = coinListFiltered.get(position);
        if(!TextUtils.isEmpty(item.iconUrl)) {
            mRequestManager
                    .load(item.iconUrl)
                    .into(holder.itemBottomSheetCoinImg);
        } else {
            holder.itemBottomSheetCoinImg.setImageDrawable(app.getResources().getDrawable(item.iconId));
        }
        holder.itemBottomSheetCoinSymbol.setTypeface(WUtils.getTypefaceRegular(app));
        holder.itemBottomSheetCoinName.setTypeface(WUtils.getTypefaceRegular(app));
        holder.itemBottomSheetCoinSymbol.setText(item.symbol);
        holder.itemBottomSheetCoinName.setText(item.name);
        holder.itemBottomSheetRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("symbol",              item.symbol);
                bundle.putString("type",                item.type);
                bundle.putString("imageUrl",            item.iconUrl);
                bundle.putInt("imgId",                  item.iconId);
                listener.onSelectItem(bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return coinListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    coinListFiltered = coinList;

                } else {
                    ArrayList<Token> filteredList = new ArrayList<>();
                    for (Token row : coinList) {
                        if (row.symbol.toLowerCase().contains(charString.toLowerCase()) || row.name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    coinListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = coinListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                coinListFiltered = (ArrayList<Token>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public class BottomSheetItemHolder extends RecyclerView.ViewHolder {
        RelativeLayout itemBottomSheetRoot;
        ImageView itemBottomSheetCoinImg;
        TextView itemBottomSheetCoinSymbol;
        TextView itemBottomSheetCoinName;

        public BottomSheetItemHolder(View v) {
            super(v);
            itemBottomSheetRoot         = itemView.findViewById(R.id.item_add_bottom_sheet_root);
            itemBottomSheetCoinImg      = itemView.findViewById(R.id.item_add_bottom_sheet_img);
            itemBottomSheetCoinSymbol   = itemView.findViewById(R.id.item_add_bottom_sheet_symbol);
            itemBottomSheetCoinName     = itemView.findViewById(R.id.item_add_bottom_sheet_name);
        }
    }
}