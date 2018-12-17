package wannabit.io.ringowallet.network;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import wannabit.io.ringowallet.network.req.ReqSendRawTxBCH;
import wannabit.io.ringowallet.network.req.ReqSendRawTxETC;
import wannabit.io.ringowallet.network.res.ResBchHistory;
import wannabit.io.ringowallet.network.res.ResBchUTX;
import wannabit.io.ringowallet.network.res.ResSendRawTx;

public interface BlockDozerService {

    @GET("/insight-api/addr/{address}/balance")
    Call<String> getBchBalance(@Path("address") String address);

    @GET("/insight-api/txs")
    Call<ResBchHistory> getBchHistory(@Query("address") String address);

    @GET("/insight-api/addr/{address}/utxo")
    Call<List<ResBchUTX>> getBchUtxo(@Path("address") String address);

    @POST("/insight-api/tx/send")
    Call<ResSendRawTx> sendRawTxBch(@Body ReqSendRawTxBCH data);
}
