package wannabit.io.ringowallet.task.balance;

import android.os.Bundle;

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
import wannabit.io.ringowallet.network.ApiClient;
import wannabit.io.ringowallet.network.WannbitService;
import wannabit.io.ringowallet.network.res.ResUTXByAddress;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.utils.WLog;

public class BtcBalanceTask extends BalanceCheckByKeyTask {
    private BaseApplication         mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;
    private ArrayList<Key>          mKeys;

    public BtcBalanceTask(BaseApplication app, TaskCallback mCallback, ArrayList<Key> keys) {
        this.mApp       = app;
        this.mCallback  = mCallback;
        this.mKeys      = keys;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_BALANCE;
        this.mResult.resultData3 = mKeys.get(0).type;
    }

    @Override
    protected TaskResult doInBackground(Bundle... bundles) {
        try {
            for(Key key : mKeys) {
                Response<List<ResUTXByAddress>> response = ApiClient.getWannabitService(mApp).getBTCUTXOByAddr(key.address).execute();
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
            WLog.w("BtcBalanceTask Exception : "  + e.getMessage());
            mResult.resultMsg = e.getMessage();

        }

        return mResult;
    }

    @Override
    protected void onPostExecute(TaskResult result) {
        super.onPostExecute(result);
        mCallback.onTaskResponse(result);
    }
}
