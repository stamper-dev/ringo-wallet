package wannabit.io.ringowallet.network.res;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class ResPrice {

    @SerializedName("USD")
    public BigDecimal USD;

    @SerializedName("KRW")
    public BigDecimal KRW;

    public BigDecimal getUSD() {
        return USD;
    }

    public void setUSD(BigDecimal USD) {
        this.USD = USD;
    }

    public BigDecimal getKRW() {
        return KRW;
    }

    public void setKRW(BigDecimal KRW) {
        this.KRW = KRW;
    }
}
