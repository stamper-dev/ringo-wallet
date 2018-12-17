package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import com.google.gson.JsonObject;

import org.web3j.utils.Convert;

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
import wannabit.io.ringowallet.utils.WLog;

public class EtcBalanceCheckTask extends AsyncTask<String, Void, TaskResult> {
    private BaseApplication         mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;
    private WalletItem              mWalletItem;

    public EtcBalanceCheckTask(BaseApplication app, TaskCallback mCallback, WalletItem walletItem, int position) {
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
                Response<JsonObject> response = ApiClient.getWannabitService(mApp).getBalanceETC(key.address).execute();
                mApp.getBaseDao().onUpdateBalance(key.uuid, Convert.toWei(response.body().get("value").getAsString(), Convert.Unit.ETHER).toBigInteger().toString());
            }
            mResult.isSuccess = true;

        } catch (Exception e) {
            WLog.w("EtcBalanceCheckTask : "  + e.getMessage());
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
