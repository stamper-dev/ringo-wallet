package wannabit.io.ringowallet.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Token implements Parcelable {


    @SerializedName("name")
    public String  name;

    @SerializedName("symbol")
    public String  symbol;


    @SerializedName("typeBlockchain")
    public String  type;

    @SerializedName("decimals")
    public int     decimals;

    @SerializedName("contractAddr")
    public String  contractAddr;

    @SerializedName("iconUrl")
    public String  iconUrl;

    @SerializedName("iconId")
    public int     iconId;

    public Token() {}


    public Token(String name, String symbol, String type, int decimals, String contractAddr, String iconUrl, int iconId) {
        this.name = name;
        this.symbol = symbol;
        this.type = type;
        this.decimals = decimals;
        this.contractAddr = contractAddr;
        this.iconUrl = iconUrl;
        this.iconId = iconId;
    }

    //for server side erc qrc toekn
    public Token(String name,String symbol,  String type, int decimals, String contractAddr, String iconUrl) {
        this.name = name;
        this.symbol = symbol;
        this.type = type;
        this.decimals = decimals;
        this.contractAddr = contractAddr;
        this.iconUrl = iconUrl;
        this.iconId = -1;
    }

    protected Token(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        name = in.readString();
        symbol = in.readString();
        type = in.readString();
        decimals = in.readInt();
        contractAddr = in.readString();
        iconUrl = in.readString();
        iconId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(symbol);
        dest.writeString(type);
        dest.writeInt(decimals);
        dest.writeString(contractAddr);
        dest.writeString(iconUrl);
        dest.writeInt(iconId);
    }

    public static final Creator<Token> CREATOR = new Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel in) {
            return new Token(in);
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public String getContractAddr() {
        return contractAddr;
    }

    public void setContractAddr(String contractAddr) {
        this.contractAddr = contractAddr;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}