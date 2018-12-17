package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import retrofit2.Response;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.WalletItem;
import wannabit.io.ringowallet.network.ApiClient;
import wannabit.io.ringowallet.utils.WLog;

public class BchBalanceCheckTask extends AsyncTask<String, Void, TaskResult> {
    private BaseApplication         mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;
    private WalletItem              mWalletItem;


    public BchBalanceCheckTask(BaseApplication app, TaskCallback mCallback, WalletItem walletItem, int position) {
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
                Response<String> response = ApiClient.getBlockDozerService(mApp).getBchBalance(key.address).execute();
                if(response.isSuccessful()) mApp.getBaseDao().onUpdateBalance(key.uuid, response.body());

            }
            mResult.isSuccess = true;
        } catch (Exception e) {
            WLog.w("BchBalanceCheckTask : "  + e.getMessage());
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
