package wannabit.io.ringowallet.network.req;

import com.google.gson.annotations.SerializedName;

public class ReqSendRawTxETC {

    @SerializedName("idfAccount")
    int idfAccount;

    @SerializedName("signedRawTx")
    String signedRawTx;

    public ReqSendRawTxETC(int idfAccount, String signedRawTransaction) {
        this.idfAccount = idfAccount;
        this.signedRawTx = signedRawTransaction;
    }
}
