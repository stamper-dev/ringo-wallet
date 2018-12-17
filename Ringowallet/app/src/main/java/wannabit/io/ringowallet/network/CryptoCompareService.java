package wannabit.io.ringowallet.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import wannabit.io.ringowallet.network.res.ResPrice;

public interface CryptoCompareService {

    @GET("data/price")
    Call<ResPrice> getTokenPrice(@Query("fsym") String fsym, @Query("tsyms") String tsyms);
}
