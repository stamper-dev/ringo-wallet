package wannabit.io.ringowallet.utils;

import com.google.common.collect.ImmutableList;

import org.bitcoincashj.core.Base58;
import org.bitcoincashj.core.DumpedPrivateKey;
import org.bitcoincashj.core.ECKey;
import org.bitcoincashj.core.NetworkParameters;
import org.bitcoincashj.crypto.ChildNumber;
import org.bitcoincashj.crypto.DeterministicHierarchy;
import org.bitcoincashj.crypto.DeterministicKey;
import org.bitcoincashj.crypto.HDKeyDerivation;

import java.math.BigInteger;
import java.util.List;

public class WBHCUtils {

    private NetworkParameters mParams;
    private DeterministicHierarchy mDeterministicHierarchy;
    private DeterministicKey mMasterKey;


    public WBHCUtils() {
        try {
            this.mParams  = BCHMainNetParams.get();
        } catch (Exception e) {}

    }

    public WBHCUtils(String seed) {
        try {
            byte[] hd_seed                  = WKeyUtils.getHDSeed(seed);
            this.mParams                    = BCHMainNetParams.get();
            this.mMasterKey                 = HDKeyDerivation.createMasterPrivateKey(hd_seed);
            this.mDeterministicHierarchy    = new DeterministicHierarchy(mMasterKey);

        } catch (Exception e) {}
    }

    public List<ChildNumber> getParentPath() {
        return  ImmutableList.of(new ChildNumber(44, true), new ChildNumber(145, true), ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);
    }

    public DeterministicKey onGenerateKey(int position) {
        return mDeterministicHierarchy.deriveChild(getParentPath(), true, true,  new ChildNumber(position));
    }

    public DeterministicKey getFirstKey() {
        return onGenerateKey(0);
    }

    public String getPrivateKey(DeterministicKey key) {
        return key.getPrivateKeyAsWiF(mParams);
    }

    public String getAddress(DeterministicKey key) {
        return key.toAddress(mParams).toString();
    }

    public boolean isValidPrivateKey(String input) {
        boolean result = false;
        try {
            if (input.length() == 51 || input.length() == 52) {
                DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(mParams, input);
                dumpedPrivateKey.getKey();
            } else {
                BigInteger privKey = Base58.decodeToBigInteger(input);
                ECKey.fromPrivate(privKey);
            }
            result = true;

        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    public String getAddress(String input) {
        String result = "";
        try {
            if (input.length() == 51 || input.length() == 52) {
                DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(mParams, input);
                result = dumpedPrivateKey.getKey().toAddress(mParams).toString();
            } else {
                BigInteger privKey = Base58.decodeToBigInteger(input);
                result = ECKey.fromPrivate(privKey).toAddress(mParams).toString();
            }


        } catch (Exception e) {}

        return result;
    }
}
