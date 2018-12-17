package wannabit.io.ringowallet.network.res;

import com.google.gson.annotations.SerializedName;

public class ResSendRawTx {

    @SerializedName("txid")
    String txid;

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }
}
