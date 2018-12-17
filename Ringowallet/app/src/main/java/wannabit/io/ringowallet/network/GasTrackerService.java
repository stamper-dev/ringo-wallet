package wannabit.io.ringowallet.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import wannabit.io.ringowallet.network.res.ResEtcHistory;

public interface GasTrackerService {

    @GET("v1/addr/{address}/transactions")
    Call<ResEtcHistory> getETC(@Path("address") String address);
}
