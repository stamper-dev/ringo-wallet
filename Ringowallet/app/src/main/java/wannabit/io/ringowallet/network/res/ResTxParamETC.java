package wannabit.io.ringowallet.network.res;

import com.google.gson.annotations.SerializedName;

public class ResTxParamETC {

    @SerializedName("nonce")
    String nonce;

    @SerializedName("gasPrice")
    String gasPrice;

    @SerializedName("gasLimit")
    String gasLimit;

    @SerializedName("to")
    String to;

    @SerializedName("value")
    String value;

    @SerializedName("chainId")
    String chainId;

    public String getNonce() {
        return nonce;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public String getGasLimit() {
        return gasLimit;
    }

    public String getTo() {
        return to;
    }

    public String getValue() {
        return value;
    }

    public String getChainId() {
        return chainId;
    }
}
