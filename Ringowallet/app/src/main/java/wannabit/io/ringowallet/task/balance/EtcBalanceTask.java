package wannabit.io.ringowallet.task.balance;

import android.os.Bundle;

import com.google.gson.JsonObject;

import org.web3j.utils.Convert;

import java.util.ArrayList;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.network.ApiClient;
import wannabit.io.ringowallet.network.WannbitService;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.utils.WLog;

public class EtcBalanceTask extends BalanceCheckByKeyTask {
    private BaseApplication     mApp;
    private TaskCallback        mCallback;
    private TaskResult          mResult;
    private ArrayList<Key>      mKeys;

    public EtcBalanceTask(BaseApplication app, TaskCallback mCallback, ArrayList<Key> keys) {
        this.mApp       = app;
        this.mCallback  = mCallback;
        this.mKeys      = keys;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_BALANCE;
        this.mResult.resultData3 = mKeys.get(0).type;
    }

    @Override
    protected TaskResult doInBackground(Bundle... bundles) {
        try {
            for(Key key : mKeys) {
                Response<JsonObject> response = ApiClient.getWannabitService(mApp).getBalanceETC(key.address).execute();
                mApp.getBaseDao().onUpdateBalance(key.uuid, Convert.toWei(response.body().get("value").getAsString(), Convert.Unit.ETHER).toBigInteger().toString());
            }
            mResult.isSuccess = true;

        } catch (Exception e) {
            WLog.w("EtcBalanceTask Exception : "  + e.getMessage());
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
