package wannabit.io.ringowallet.utils;

import com.google.common.collect.ImmutableList;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.math.BigInteger;
import java.util.List;

public class WETCUtils {

    private DeterministicHierarchy mDeterministicHierarchy;


    public WETCUtils() {
    }

    public WETCUtils(String seed) {
        try {
            byte[]              hd_seed             = WKeyUtils.getHDSeed(seed);
            DeterministicKey master_key             = HDKeyDerivation.createMasterPrivateKey(hd_seed);
            this.mDeterministicHierarchy            = new DeterministicHierarchy(master_key);
        } catch (Exception e) {

        }
    }

    public List<ChildNumber> getParentPath() {
        return  ImmutableList.of(new ChildNumber(44, true), new ChildNumber(61, true), ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);
    }

    public DeterministicKey onGenerateKey(int position) {
        return mDeterministicHierarchy.deriveChild(getParentPath(), true, true,  new ChildNumber(position));
    }


    public DeterministicKey getFirstKey() {
        return onGenerateKey(0);
    }

    public String getPrivateKey(DeterministicKey key) {
        ECKeyPair eCKeyPair = ECKeyPair.create(key.getPrivKey().toByteArray());
        return "0x" + eCKeyPair.getPrivateKey().toString();
    }

    public String getAddress(DeterministicKey key) {
        ECKeyPair eCKeyPair = ECKeyPair.create(key.getPrivKey().toByteArray());
        return "0x" + Keys.getAddress(eCKeyPair.getPublicKey());
    }

    public boolean isValidPrivateKey(String input) {
        String key = input;
        if(key.startsWith("0x")) {
            key = key.substring(2, key.length());
        }
        WLog.w("key : " + key);
        try {
            BigInteger bigInt = new BigInteger(key, 16);
            ECKeyPair.create(bigInt);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String getAddress(String input) {
        String result = "";
        String key = input;
        if(key.startsWith("0x")) {
            key = key.substring(2, key.length());
        }
        try {
            BigInteger bigInt = new BigInteger(key, 16);
            result = "0x" + Keys.getAddress(ECKeyPair.create(bigInt));
        } catch (Exception e) { }

        return result;
    }
}
