package wannabit.io.ringowallet.network.req;

import com.google.gson.annotations.SerializedName;

public class ReqSendRawTx {

    @SerializedName("idfAccount")
    int idfAccount;

    @SerializedName("signedRawTransaction")
    String signedRawTransaction;

    public ReqSendRawTx(int idfAccount, String signedRawTransaction) {
        this.idfAccount = idfAccount;
        this.signedRawTransaction = signedRawTransaction;
    }

    public ReqSendRawTx(String signedRawTransaction) {
        this.idfAccount = 1;
        this.signedRawTransaction = signedRawTransaction;
    }
}
