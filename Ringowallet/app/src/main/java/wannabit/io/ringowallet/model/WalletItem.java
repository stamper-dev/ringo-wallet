package wannabit.io.ringowallet.model;

import java.math.BigDecimal;
import java.util.ArrayList;

public class WalletItem extends Token {

    public ArrayList<Key> keys = new ArrayList<Key>();

    public WalletItem() {
    }


    // for base coin
    public WalletItem(String name, String symbol, String type, int decimals, String iconUrl, int iconId) {
        this.name = name;
        this.symbol = symbol;
        this.type = type;
        this.decimals = decimals;
        this.iconUrl = iconUrl;
        this.iconId = iconId;
    }

    //for erc20
    public WalletItem(String name, String symbol, String type, int decimals, String iconUrl, int iconId, String contractAddr) {
        this.name = name;
        this.symbol = symbol;
        this.type = type;
        this.decimals = decimals;
        this.iconUrl = iconUrl;
        this.iconId = iconId;
        this.contractAddr = contractAddr;
    }


    public void addKey(Key key) {
        if(keys == null) {
            keys = new ArrayList<>();
        }
        keys.add(key);
    }

    public BigDecimal getTotalBalance() {
        BigDecimal result = BigDecimal.ZERO;
        if(keys != null) {
            for(Key key : keys) {
                result = result.add(key.lastBalance);
            }
        }
        return result;
    }
}
