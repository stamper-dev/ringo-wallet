package wannabit.io.ringowallet.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import wannabit.io.ringowallet.network.res.ResEthHistory;

public interface EtherScanService {


    @GET("api")
    Call<ResEthHistory> getEthHistory(@Query("module") String module, @Query("action") String action,
                                      @Query("address") String address, @Query("startblock") long startblock,
                                      @Query("endblock") long endblock, @Query("sort") String sort,
                                      @Query("apikey") String apikey);

    @GET("api")
    Call<ResEthHistory> getTokenHistory(@Query("module") String module, @Query("action") String action,
                                   @Query("address") String address, @Query("startblock") long startblock,
                                   @Query("endblock") long endblock, @Query("sort") String sort,
                                   @Query("apikey") String apikey);
}
