package wannabit.io.ringowallet.task.balance;

import android.os.Bundle;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;

import java.util.ArrayList;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.utils.WLog;

public class EthBalanceTask extends BalanceCheckByKeyTask {
    private BaseApplication     mApp;
    private TaskCallback        mCallback;
    private TaskResult          mResult;
    private ArrayList<Key>          mKeys;

    public EthBalanceTask(BaseApplication app, TaskCallback mCallback, ArrayList<Key> keys) {
        this.mApp       = app;
        this.mCallback  = mCallback;
        this.mKeys      = keys;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_BALANCE;
        this.mResult.resultData3 = mKeys.get(0).type;
    }

    @Override
    protected TaskResult doInBackground(Bundle... bundles) {
        Web3j web3 = Web3jFactory.build(new HttpService(mApp.getString(R.string.infura_url)));

        try {
            for(Key key : mKeys) {
                EthGetBalance ethGetBalance = web3
                        .ethGetBalance(key.address, DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();
                WLog.w("getBalance() : "  + ethGetBalance.getBalance().toString());
                mApp.getBaseDao().onUpdateBalance(key.uuid, ethGetBalance.getBalance().toString());
            }
            mResult.isSuccess = true;

        } catch (Exception e) {
            WLog.w("EthBalanceTask Exception : "  + e.getMessage());
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
