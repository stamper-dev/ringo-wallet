package wannabit.io.ringowallet.network.res;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResLtcHistory {

    @SerializedName("n_tx")
    public int n_tx;


    @SerializedName("txs")
    public ArrayList<Txs> txs;





    public class Txs {

        @SerializedName("confirmed")
        public String confirmed;

        @SerializedName("hash")
        public String hash;

        @SerializedName("fees")
        public long fees;

        @SerializedName("inputs")
        public ArrayList<Input> inputs;

        @SerializedName("outputs")
        public ArrayList<Out> outputs;

    }

    public class Input {
        @SerializedName("output_value")
        public long output_value;

        @SerializedName("addresses")
        public ArrayList<String> addresses;
    }

    public class Out {
        @SerializedName("value")
        public long value;

        @SerializedName("addresses")
        public ArrayList<String> addresses;

    }
}
