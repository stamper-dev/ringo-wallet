package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.WalletItem;
import wannabit.io.ringowallet.network.ApiClient;
import wannabit.io.ringowallet.network.WannbitService;
import wannabit.io.ringowallet.network.res.ResUTXByAddress;
import wannabit.io.ringowallet.utils.WLog;

public class LtcBalanceCheckTask extends AsyncTask<String, Void, TaskResult> {

    private BaseApplication         mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;
    private WalletItem              mWalletItem;

    public LtcBalanceCheckTask(BaseApplication app, TaskCallback mCallback, WalletItem walletItem, int position) {
        this.mApp = app;
        this.mCallback = mCallback;
        this.mWalletItem = walletItem;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_BALANCE;
        this.mResult.resultData1 = position;
        this.mResult.resultData4 = walletItem.symbol;
    }

    @Override
    protected TaskResult doInBackground(String... strings) {
        try {
            for(Key key : mWalletItem.keys) {
                Response<List<ResUTXByAddress>> response = ApiClient.getWannabitService(mApp).getLTCUTXOByAddr(key.address).execute();
                if(response.isSuccessful() && response.body() != null && response.body().size() > 0) {
                    BigDecimal utxoSum = BigDecimal.ZERO;
                    ArrayList<ResUTXByAddress> uTXO = new ArrayList<ResUTXByAddress>(response.body());
                    for (ResUTXByAddress data : uTXO) {
                        utxoSum = utxoSum.add(BigDecimal.valueOf(data.getValue()).movePointRight(8));
                    }
                    mApp.getBaseDao().onUpdateBalance(key.uuid, utxoSum.toPlainString());
                }
            }
            mResult.isSuccess = true;

        } catch (Exception e) {

        }
        return mResult;
    }


    @Override
    protected void onPostExecute(TaskResult result) {
        super.onPostExecute(result);
        mCallback.onTaskResponse(result);
    }
}
