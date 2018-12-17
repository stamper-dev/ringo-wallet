package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.crypto.CryptoHelper;
import wannabit.io.ringowallet.crypto.EncResult;
import wannabit.io.ringowallet.model.Mnemonic;
import wannabit.io.ringowallet.utils.WLog;

public class InitMnemonicTask extends AsyncTask<String, Void, TaskResult> {

    private BaseApplication         mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;

    public InitMnemonicTask(BaseApplication app, TaskCallback mCallback) {
        this.mApp = app;
        this.mCallback = mCallback;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_INIT_MN;
    }


    /**
     *
     * @param strings
     *  strings[0] : seed
     *
     * @return
     */
    @Override
    protected TaskResult doInBackground(String... strings) {
        Mnemonic newMn = new Mnemonic();
        EncResult encR = CryptoHelper.doEncryptData(BaseConstant.MNEMONIC_KEY + newMn.getUuid(), strings[0], false);
        newMn.setResource(encR.getEncDataString());
        newMn.setSpec(encR.getIvDataString());

        if(mApp.getBaseDao().onInsertMnemonic(newMn) > 0 ) {
            mResult.isSuccess = true;
        }

        return mResult;
    }

    @Override
    protected void onPostExecute(TaskResult taskResult) {
        super.onPostExecute(taskResult);
        mCallback.onTaskResponse(taskResult);
    }
}
