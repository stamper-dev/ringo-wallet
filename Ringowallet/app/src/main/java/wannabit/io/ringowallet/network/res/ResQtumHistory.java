package wannabit.io.ringowallet.network.res;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResQtumHistory {


    @SerializedName("txs")
    public ArrayList<Txs> txs;


    public class Txs {

        @SerializedName("isqrc20Transfer")
        public boolean isqrc20Transfer;

        @SerializedName("confirmations")
        public long confirmations;

        @SerializedName("time")
        public long time;

        @SerializedName("txid")
        public String txid;

        @SerializedName("fees")
        public double fees;

        @SerializedName("vin")
        public ArrayList<Vin> vin;

        @SerializedName("vout")
        public ArrayList<Vout> vout;

    }

    public class Vin {
        @SerializedName("addr")
        public String addr;

        @SerializedName("value")
        public double value;
    }

    public class Vout {
        @SerializedName("value")
        public String value;


        @SerializedName("scriptPubKey")
        public ScriptPubKey scriptPubKey;
    }

    public class ScriptPubKey {
        @SerializedName("addresses")
        public ArrayList<String> addresses;
    }
}
