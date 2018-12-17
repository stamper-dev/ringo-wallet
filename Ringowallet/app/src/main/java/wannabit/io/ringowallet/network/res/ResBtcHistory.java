package wannabit.io.ringowallet.network.res;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResBtcHistory {

    @SerializedName("n_tx")
    public int n_tx;

//    @SerializedName("total_received")
//    public long total_received;
//
//    @SerializedName("total_sent")
//    public long total_sent;
//
//    @SerializedName("final_balance")
//    public long final_balance;

    @SerializedName("txs")
    public ArrayList<Txs> txs;

    public class Txs {

        @SerializedName("time")
        public long time;

        @SerializedName("hash")
        public String hash;

        @SerializedName("inputs")
        public ArrayList<Input> inputs;

        @SerializedName("out")
        public ArrayList<Out> out;
    }

    public class Input {
        @SerializedName("prev_out")
        public PrevOut prev_out;
    }

    public class PrevOut {
        @SerializedName("addr")
        public String addr;

        @SerializedName("value")
        public long value;

    }


    public class Out {
        @SerializedName("addr")
        public String addr;

        @SerializedName("value")
        public long value;
    }
}
