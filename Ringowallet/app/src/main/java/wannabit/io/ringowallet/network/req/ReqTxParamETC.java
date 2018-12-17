package wannabit.io.ringowallet.network.req;

import com.google.gson.annotations.SerializedName;

public class ReqTxParamETC {

    @SerializedName("idfAccount")
    String idfAccount;

    @SerializedName("from")
    String from;

    @SerializedName("to")
    String to;

    @SerializedName("value")
    String value;

    @SerializedName("gasPrice")
    String gasPrice;

    @SerializedName("gasLimit")
    String gasLimit;

    public ReqTxParamETC(String idfAccount, String from, String to, String value, String gasPrice, String gasLimit) {
        this.idfAccount = idfAccount;
        this.from = from;
        this.to = to;
        this.value = value;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
    }
}
