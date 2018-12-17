package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.crypto.CryptoHelper;
import wannabit.io.ringowallet.model.Password;

public class UnLockTask extends AsyncTask<String, Void, TaskResult> {

    private BaseApplication mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;

    public UnLockTask(BaseApplication app, TaskCallback mCallback) {
        this.mApp = app;
        this.mCallback = mCallback;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_UNLOCK;

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
            return mResult;
        } else {
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
