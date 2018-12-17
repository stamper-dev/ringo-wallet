package wannabit.io.ringowallet.network.res;

import com.google.gson.annotations.SerializedName;

public class ResQtumUTX {

    @SerializedName("txid")
    public String txid;

    @SerializedName("vout")
    public int vout;

    @SerializedName("scriptPubKey")
    public String scriptPubKey;

    @SerializedName("address")
    public String address;

    @SerializedName("amount")
    public double amount;
}
