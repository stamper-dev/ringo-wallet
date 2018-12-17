package wannabit.io.ringowallet.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import wannabit.io.ringowallet.network.res.ResBtcHistory;

public interface BlockChainInfoService {

    @GET("address/{address}")
    Call<ResBtcHistory> getBTCHistory(@Path("address") String address, @Query("format") String format, @Query("offset") int offset);
}
