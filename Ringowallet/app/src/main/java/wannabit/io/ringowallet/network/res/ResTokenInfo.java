package wannabit.io.ringowallet.network.res;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import wannabit.io.ringowallet.model.Token;

public class ResTokenInfo {

    @SerializedName("tokenList")
    ArrayList<Token> tokenList;

    public ArrayList<Token> getTokenList() {
        return tokenList;
    }

    public void setTokenList(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
    }
}
