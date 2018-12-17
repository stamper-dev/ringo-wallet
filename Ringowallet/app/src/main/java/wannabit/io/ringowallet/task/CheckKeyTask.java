package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.crypto.CryptoHelper;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.Password;

public class CheckKeyTask extends AsyncTask<String, Void, TaskResult> {

    private BaseApplication mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;

    public CheckKeyTask(BaseApplication app, TaskCallback mCallback) {
        this.mApp = app;
        this.mCallback = mCallback;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.CONST_PW_CHECK_KEY;
    }


    /**
     *
     * @param strings
     *  strings[0] : pincode
     *  strings[1] : key.uuid
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
        }

        try {
            Key key = mApp.getBaseDao().onSelectKey(strings[1]);
            mResult.isSuccess = true;
            mResult.resultData3 = CryptoHelper.doDecryptData(key.uuid, key.resource, key.spec);
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
