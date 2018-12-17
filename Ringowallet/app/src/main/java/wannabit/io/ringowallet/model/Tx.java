package wannabit.io.ringowallet.model;

public class Tx {

    public long     id;
//    public String   typeBlockChain;
//    public String   symbol;
    public String   uuid;
    public String   hash;
    public long     date;
    public String   from;
    public String   to;
    public String   amount;
    public String   fee;
    public String   address;
    public String   tokenAddr;

    public Tx() {

    }

    public Tx(long id, String uuid, String hash, long date, String from, String to, String amount, String fee) {
        this.id = id;
        this.uuid = uuid;
        this.hash = hash;
        this.date = date;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.fee = fee;
    }

    public Tx(String uuid, String hash, long date, String from, String to, String amount, String fee) {
        this.uuid = uuid;
        this.hash = hash;
        this.date = date;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.fee = fee;
    }


    // for ERC20
    public Tx(long id, String hash, long date, String from, String to, String amount, String fee, String address, String tokenAddr) {
        this.id = id;
        this.hash = hash;
        this.date = date;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.fee = fee;
        this.address = address;
        this.tokenAddr = tokenAddr;
    }

    public Tx(String hash, long date, String from, String to, String amount, String fee, String address, String tokenAddr) {
        this.hash = hash;
        this.date = date;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.fee = fee;
        this.address = address;
        this.tokenAddr = tokenAddr;
    }
}
