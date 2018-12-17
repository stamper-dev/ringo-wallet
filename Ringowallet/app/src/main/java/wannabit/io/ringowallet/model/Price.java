package wannabit.io.ringowallet.model;

import android.text.TextUtils;

import java.math.BigDecimal;

import wannabit.io.ringowallet.utils.WLog;

public class Price {
    public int     id;
    public String  symbol;
    public String  usd;
    public String  krw;

    public Price() {
        this.usd = "";
        this.krw = "";
    }

    public Price(int id, String symbol, String usd, String krw) {
        this.id = id;
        this.symbol = symbol;
        this.usd = usd;
        this.krw = krw;
    }

    public Price(String symbol, String usd, String krw) {
        this.symbol = symbol;
        this.usd = usd;
        this.krw = krw;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getKrw() {
        return krw;
    }

    public void setKrw(String krw) {
        this.krw = krw;
    }

    public String getUsd() {
        return usd;
    }

    public void setUsd(String usd) {
        this.usd = usd;
    }

    public BigDecimal getUsdBigDecimal() {
        if(TextUtils.isEmpty(usd)) {
            return BigDecimal.ZERO;
        }  else {
            return  new BigDecimal(usd);
        }

    }

    public BigDecimal getkrwBigDecimal() {
        if(TextUtils.isEmpty(krw)) {
            return BigDecimal.ZERO;
        }  else {
            return  new BigDecimal(krw);
        }
    }
}
