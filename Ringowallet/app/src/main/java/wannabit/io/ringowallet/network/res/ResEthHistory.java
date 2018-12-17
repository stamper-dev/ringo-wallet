package wannabit.io.ringowallet.network.res;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResEthHistory {

    @SerializedName("status")
    public int status;

    @SerializedName("result")
    public ArrayList<Result> result;

    public class Result {
        @SerializedName("timeStamp")
        public String timeStamp;

        @SerializedName("hash")
        public String hash;

        @SerializedName("from")
        public String from;

        @SerializedName("to")
        public String to;

        @SerializedName("value")
        public String value;

        @SerializedName("gas")
        public String gas;

        @SerializedName("gasPrice")
        public String gasPrice;

        @SerializedName("contractAddress")
        public String contractAddress;


    }
}
