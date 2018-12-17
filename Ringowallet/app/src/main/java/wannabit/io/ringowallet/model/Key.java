package wannabit.io.ringowallet.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Key {
    public Long id;
    public String uuid;
    public String type;
    public String symbol;
    public int path;
    public boolean isRaw;
    public String resource;
    public String spec;
    public String address;
    public boolean isFavo;
    public long genDate;
    public BigDecimal lastBalance;

    public Key() {
        this.uuid = UUID.randomUUID().toString();
        this.lastBalance = BigDecimal.ZERO;
    }

    public Key(Long id, String uuid, String type, String symbol, int path, boolean isRaw, String resource, String spec, String address, boolean isFavo, BigDecimal lastBalance, long gendate) {
        this.id = id;
        this.uuid = uuid;
        this.type = type;
        this.symbol = symbol;
        this.path = path;
        this.isRaw = isRaw;
        this.resource = resource;
        this.spec = spec;
        this.address = address;
        this.isFavo = isFavo;
        this.lastBalance = lastBalance;
        this.genDate = gendate;
    }


    public void init(String type, String symbol, int path, boolean isRaw, String resource, String spec, String address, boolean isFavo, long gendate) {
        this.type = type;
        this.symbol = symbol;
        this.path = path;
        this.isRaw = isRaw;
        this.resource = resource;
        this.spec = spec;
        this.address = address;
        this.isFavo = isFavo;
        this.genDate = gendate;
    }

    public void setLastBalanceString(String balance) {
        BigDecimal input = new BigDecimal("balance");
        this.lastBalance = input;

    }
}
