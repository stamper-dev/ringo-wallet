package wannabit.io.ringowallet.network.res;

import com.google.gson.annotations.SerializedName;

public class ResError {
    @SerializedName("errorCode")
    int errorCode;

    @SerializedName("errorMsg")
    String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
