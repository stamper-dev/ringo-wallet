package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.WalletItem;
import wannabit.io.ringowallet.utils.WLog;

public class EthBalanceCheckTask extends AsyncTask<String, Void, TaskResult> {
    private BaseApplication         mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;
    private WalletItem              mWalletItem;


    public EthBalanceCheckTask(BaseApplication app, TaskCallback mCallback, WalletItem walletItem, int position) {
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
        Web3j web3 = Web3jFactory.build(new HttpService("https://mainnet.infura.io/"));
        try {
            for(Key key : mWalletItem.keys) {
                EthGetBalance ethGetBalance = web3
                        .ethGetBalance(key.address, DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();
                mApp.getBaseDao().onUpdateBalance(key.uuid, ethGetBalance.getBalance().toString());
            }
            mResult.isSuccess = true;

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
