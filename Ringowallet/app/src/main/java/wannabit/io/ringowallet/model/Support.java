package wannabit.io.ringowallet.model;

import com.google.gson.annotations.SerializedName;

public class Support {

    @SerializedName("BTC")
    public boolean btc;

    @SerializedName("LTC")
    public boolean ltc;

    @SerializedName("ETH")
    public boolean eth;

    @SerializedName("ERC20")
    public boolean erc20;

    @SerializedName("BCH")
    public boolean bch;

    @SerializedName("ETC")
    public boolean etc;

    @SerializedName("QTUM")
    public boolean qtum;

    @SerializedName("QRC20")
    public boolean qrc20;

    @SerializedName("BSV")
    public boolean bsv;
}
