package wannabit.io.ringowallet.network.req;

import com.google.gson.annotations.SerializedName;

public class ReqSendRawTxQTUM {

    @SerializedName("rawtx")
    String rawtx;

    public ReqSendRawTxQTUM(String rawtx) {
        this.rawtx = rawtx;
    }
}
