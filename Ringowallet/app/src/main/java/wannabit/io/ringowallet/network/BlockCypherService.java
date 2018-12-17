package wannabit.io.ringowallet.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import wannabit.io.ringowallet.network.res.ResLtcHistory;

public interface BlockCypherService {

    @GET("v1/ltc/main/addrs/{address}/full")
    Call<ResLtcHistory> getLTCHistory(@Path("address") String address, @Query("limit") int limit);
}
