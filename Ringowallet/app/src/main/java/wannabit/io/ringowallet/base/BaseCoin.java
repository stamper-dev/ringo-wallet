package wannabit.io.ringowallet.base;

public enum BaseCoin {
    BTC(0), BCH(1), LTC(2), ETH(3), ETC(4), QTUM(4);

    private int value;

    BaseCoin(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
