package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.model.Token;
import wannabit.io.ringowallet.network.ApiClient;
import wannabit.io.ringowallet.network.res.ResTokenInfo;
import wannabit.io.ringowallet.utils.WLog;

public class ERCTokenUpdateTask extends AsyncTask<String, Void, String > {

    private BaseApplication mApp;
    private int             mVersion;

    public ERCTokenUpdateTask(BaseApplication app, int version) {
        this.mApp = app;
        this.mVersion = version;
    }

    @Override
    protected String doInBackground(String... strings) {

        ApiClient.getWannabitService(mApp).getEthTokenInfo().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    ResTokenInfo res = new Gson().fromJson(response.body(), ResTokenInfo.class);
                    ArrayList<Token> tokens = res.getTokenList();
                    mApp.getBaseDao().onReInsertErcTokens(tokens);
                    mApp.getBaseDao().setErcVersion(mVersion);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) { }
        });

        return null;
    }
}