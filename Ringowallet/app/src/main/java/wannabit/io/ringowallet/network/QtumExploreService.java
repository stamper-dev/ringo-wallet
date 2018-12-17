package wannabit.io.ringowallet.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import wannabit.io.ringowallet.network.req.ReqSendRawTxBCH;
import wannabit.io.ringowallet.network.req.ReqSendRawTxQTUM;
import wannabit.io.ringowallet.network.res.ResBchUTX;
import wannabit.io.ringowallet.network.res.ResQtumHistory;
import wannabit.io.ringowallet.network.res.ResQtumUTX;
import wannabit.io.ringowallet.network.res.ResSendRawTx;

public interface QtumExploreService {

    @GET("/insight-api/addr/{address}/balance")
    Call<String> getQtumBalance(@Path("address") String address);

    @GET("/insight-api/txs")
    Call<ResQtumHistory> getQtumHistory(@Query("address") String address);

    @GET("/insight-api/addr/{address}/utxo")
    Call<List<ResQtumUTX>> getQtumUtxo(@Path("address") String address);

    @POST("/insight-api/tx/send")
    Call<ResSendRawTx> sendRawTxQtum(@Body ReqSendRawTxQTUM data);
}
