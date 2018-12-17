package wannabit.io.ringowallet.network.res;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResEtcHistory {

    @SerializedName("offset")
    public long offset;


    @SerializedName("items")
    public ArrayList<Item> items;

    public class Item {

        @SerializedName("from")
        public String from;

        @SerializedName("to")
        public String to;

        @SerializedName("timestamp")
        public String timestamp;

        @SerializedName("hash")
        public String hash;

        @SerializedName("value")
        public Value value;

    }

    public class Value {
        @SerializedName("ether")
        public double ether;

        @SerializedName("wei")
        public String wei;

    }
}
