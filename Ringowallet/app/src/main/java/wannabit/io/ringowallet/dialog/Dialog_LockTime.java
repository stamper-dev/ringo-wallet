package wannabit.io.ringowallet.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.setting.PrivateSetActivity;
import wannabit.io.ringowallet.utils.WUtils;

public class Dialog_LockTime extends DialogFragment {

    private RecyclerView        mRecyclerView;
    private ArrayList<String>   mTime = new ArrayList<>();

    public static Dialog_LockTime newInstance() {
        Dialog_LockTime frag = new Dialog_LockTime();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View contentView    = View.inflate(getContext(), R.layout.dialog_locktime, null);
        ((TextView)contentView.findViewById(R.id.dialog_lock_time_title)).setTypeface(WUtils.getTypefaceRegular(getActivity()));
        mRecyclerView       = contentView.findViewById(R.id.recycler);
        mTime = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.lock_time)));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new LockTimeAdapter());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(contentView);
        return builder.create();
    }

    class LockTimeAdapter extends RecyclerView.Adapter<LockTimeAdapter.LockTimeHolder> {

        @Override
        public LockTimeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = getLayoutInflater().inflate(R.layout.item_locktime, viewGroup, false);
            return new LockTimeHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull LockTimeHolder holder, final int position) {
            final String time = mTime.get(position);
            holder.itemTime.setTypeface(WUtils.getTypefaceRegular(getActivity()));
            holder.itemTime.setText(time);
            holder.itemRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((PrivateSetActivity)getActivity()).getBaseDao().setLockTime(position);
                    ((PrivateSetActivity)getActivity()).onUpdateView();
                    getDialog().dismiss();

                }
            });
        }

        @Override
        public int getItemCount() {
            return mTime.size();
        }

        public class LockTimeHolder extends RecyclerView.ViewHolder {
            RelativeLayout itemRoot;
            TextView itemTime;

            public LockTimeHolder(View v) {
                super(v);
                itemRoot     = itemView.findViewById(R.id.item_time_root);
                itemTime     = itemView.findViewById(R.id.item_time);
            }
        }
    }

}
