package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.crypto.CryptoHelper;
import wannabit.io.ringowallet.model.Mnemonic;
import wannabit.io.ringowallet.model.Password;

public class CheckMnemonicTask extends AsyncTask<String, Void, TaskResult> {

    private BaseApplication mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;

    public CheckMnemonicTask(BaseApplication app, TaskCallback mCallback) {
        this.mApp = app;
        this.mCallback = mCallback;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.CONST_PW_CHECK_MNEMONIC;
    }


    /**
     *
     * @param strings
     *  strings[0] : pincode
     *
     * @return
     */
    @Override
    protected TaskResult doInBackground(String... strings) {
        Password checkPw = mApp.getBaseDao().onSelectPassword();
        if(!CryptoHelper.verifyData(strings[0], checkPw.getResource(), BaseConstant.PASSWORD_KEY)) {
            mResult.isSuccess = false;
            mResult.resultCode = -99;
        }
        try {
            Mnemonic mnemonic  = mApp.getBaseDao().onSelectMnemonic();
            mResult.isSuccess = true;
            mResult.resultData3 = CryptoHelper.doDecryptData(BaseConstant.MNEMONIC_KEY+mnemonic.getUuid(), mnemonic.getResource(), mnemonic.getSpec());

        } catch (Exception e) {
            mResult.resultMsg = e.getMessage();
        }

        return mResult;
    }


    @Override
    protected void onPostExecute(TaskResult taskResult) {
        super.onPostExecute(taskResult);
        mCallback.onTaskResponse(taskResult);
    }
}
