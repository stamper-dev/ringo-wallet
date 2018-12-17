package wannabit.io.ringowallet.network.req;

import com.google.gson.annotations.SerializedName;

public class ReqSendRawTxBCH {

    @SerializedName("rawtx")
    String rawtx;

    public ReqSendRawTxBCH(String rawtx) {
        this.rawtx = rawtx;
    }
}
