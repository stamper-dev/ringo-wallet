package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;

import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.utils.WLog;

public class Erc20MotherBalanceCheckTask extends AsyncTask<String, Void, TaskResult> {
    private TaskCallback            mCallback;
    private TaskResult              mResult;

    public Erc20MotherBalanceCheckTask(TaskCallback mCallback) {
        this.mCallback = mCallback;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_MOTHER_BALANCE;
    }


    /**
     *
     * @param strings
     *  strings[0] : mother address
     *
     * @return
     */
    @Override
    protected TaskResult doInBackground(String... strings) {
        Web3j web3 = Web3jFactory.build(new HttpService("https://mainnet.infura.io/"));
        try {
            EthGetBalance ethGetBalance = web3
                    .ethGetBalance(strings[0], DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();
            if(ethGetBalance != null && !ethGetBalance.hasError() && ethGetBalance.getBalance() != null) {
                mResult.isSuccess = true;
                mResult.resultData3 = ethGetBalance.getBalance().toString();
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
