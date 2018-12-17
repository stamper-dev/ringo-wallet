package wannabit.io.ringowallet.model;

import com.google.gson.annotations.SerializedName;

public class WBInputDtoList {

    @SerializedName("txid")
    String txid;

    @SerializedName("n")
    int n;

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }
}
