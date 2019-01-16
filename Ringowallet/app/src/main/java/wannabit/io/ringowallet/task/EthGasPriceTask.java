package wannabit.io.ringowallet.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.http.HttpService;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.utils.WLog;

public class EthGasPriceTask extends AsyncTask<String, Void, TaskResult> {
    private BaseApplication         mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;

    public EthGasPriceTask(BaseApplication app, TaskCallback mCallback) {
        this.mApp = app;
        this.mCallback = mCallback;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_GAS_PRICE;

    }

    @Override
    protected TaskResult doInBackground(String... strings) {
        Web3j web3 = Web3jFactory.build(new HttpService(mApp.getString(R.string.infura_url)));
        try {

            EthGasPrice ethGasPrice = web3.ethGasPrice().send();
            if(!ethGasPrice.hasError() && !TextUtils.isEmpty(ethGasPrice.getGasPrice().toString())) {
                mResult.isSuccess = true;
                mResult.resultData3 = ethGasPrice.getGasPrice().toString();
            }

        } catch (Exception e) {
            WLog.w("EthBalanceCheckTask : "  + e.getMessage());
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
