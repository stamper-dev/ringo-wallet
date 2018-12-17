package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.crypto.CryptoHelper;
import wannabit.io.ringowallet.crypto.EncResult;
import wannabit.io.ringowallet.model.Key;

public class RawKeyInsertTask extends AsyncTask<String, Void, TaskResult> {

    private BaseApplication         mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;

    public RawKeyInsertTask(BaseApplication app, TaskCallback mCallback) {
        this.mApp = app;
        this.mCallback = mCallback;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_INSERT_RAW_KEY;
    }


    /**
     *
     * @param strings
     *  strings[0] : coinType
     *  strings[1] : coinSymbol
     *  strings[2] : privateKey
     *  strings[3] : address
     *
     * @return
     */
    @Override
    protected TaskResult doInBackground(String... strings) {
        if(mApp.getBaseDao().isExistingKey(strings[0], strings[1], strings[3])) {
            mResult.resultCode = -2;
            return  mResult;
        }

        Key result          = new Key();
        EncResult encR      = CryptoHelper.doEncryptData(result.uuid, strings[2], false);
        result.init(strings[0], strings[1], -1, true, encR.getEncDataString(), encR.getIvDataString(), strings[3], false, System.currentTimeMillis());
        if(mApp.getBaseDao().onInsertKey(result) > 0) {
            mResult.isSuccess = true;
        }
        return mResult;
    }


    @Override
    protected void onPostExecute(TaskResult resultTask) {
        super.onPostExecute(resultTask);
        mCallback.onTaskResponse(resultTask);
    }
}
