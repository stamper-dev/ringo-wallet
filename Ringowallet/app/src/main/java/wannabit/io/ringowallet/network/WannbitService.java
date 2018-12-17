package wannabit.io.ringowallet.network;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.network.req.ReqCreateRawTx;
import wannabit.io.ringowallet.network.req.ReqSendRawTx;
import wannabit.io.ringowallet.network.req.ReqSendRawTxETC;
import wannabit.io.ringowallet.network.req.ReqTxParamETC;
import wannabit.io.ringowallet.network.res.ResCreateRawTx;
import wannabit.io.ringowallet.network.res.ResRawTx;
import wannabit.io.ringowallet.network.res.ResSendRawTx;
import wannabit.io.ringowallet.network.res.ResTxParamETC;
import wannabit.io.ringowallet.network.res.ResUTXByAddress;

public interface WannbitService {

    @GET("/eos/version/android")
    Call<JsonObject> getVersion();


    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    @GET("/eth/getTokenInfo")
    Call<JsonObject> getEthTokenInfo();


    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    @GET("/qtum/getTokenInfo")
    Call<JsonObject> getQtumTokenInfo();


    @GET("/btc/getUTXOByAddr/{address}")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<List<ResUTXByAddress>> getBTCUTXOByAddr(@Path("address") String address);

    @POST("/btc/createRawTx")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<ResCreateRawTx> createBTCRawTx(@Body ReqCreateRawTx data);

    @GET("/btc/getRawTx/{txid}")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<ResRawTx> getBTCRawTx(@Path("txid") String txid);

    @POST("/btc/sendRawTx")

    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<ResSendRawTx> sendBTCRawTx(@Body ReqSendRawTx data);



    @GET("/ltc/getUTXOByAddr/{address}")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<List<ResUTXByAddress>> getLTCUTXOByAddr(@Path("address") String address);

    @POST("/ltc/createRawTx")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<ResCreateRawTx> createLTCRawTx(@Body ReqCreateRawTx data);


    @GET("/ltc/getRawTx/{txid}")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<ResRawTx> getLTCRawTx(@Path("txid") String txid);

    @POST("/ltc/sendRawTx")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<ResSendRawTx> sendLTCRawTx(@Body ReqSendRawTx data);



    @GET("/bch/getUTXOByAddr/{address}")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<List<ResUTXByAddress>> getBCHUTXOByAddr(@Path("address") String address);

    @POST("/bch/createRawTx")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<ResCreateRawTx> createBCHRawTx(String auth, @Body ReqCreateRawTx data);


    @GET("/bch/getRawTx/{txid}")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<ResRawTx> getBCHRawTx(@Path("txid") String txid);


    @POST("/bch/sendRawTx")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<JsonObject> sendBCHRawTx(@Body ReqSendRawTx data);



    @GET("/etc/getBalance/{address}")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<JsonObject> getBalanceETC(@Path("address") String address);

    @POST("/etc/getTxParams")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<ResTxParamETC> getTxParams(@Body ReqTxParamETC data);

    @POST("/etc/sendRawTx")
    @Headers(BaseConstant.WANNABIT_TEST_TOKEN)
    Call<JsonObject> sendRawTxETC(@Body ReqSendRawTxETC data);
}
