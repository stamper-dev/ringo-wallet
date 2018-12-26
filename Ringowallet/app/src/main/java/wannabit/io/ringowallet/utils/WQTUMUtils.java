package wannabit.io.ringowallet.utils;


import com.google.common.collect.ImmutableList;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;

import java.math.BigInteger;
import java.util.List;

public class WQTUMUtils {

    private NetworkParameters mParams;
    private DeterministicHierarchy mDeterministicHierarchy;
    private DeterministicKey mMasterKey;


    public WQTUMUtils() {
        this.mParams  = QtumMainNetParams.get();

    }

    public WQTUMUtils(String seed) {
        try {
            byte[] hd_seed                  = WKeyUtils.getHDSeed(seed);
            this.mParams                    = QtumMainNetParams.get();
            this.mMasterKey                 = HDKeyDerivation.createMasterPrivateKey(hd_seed);
            this.mDeterministicHierarchy    = new DeterministicHierarchy(mMasterKey);

        } catch (Exception e) {

        }
    }


    public List<ChildNumber> getParentPath() {
        return  ImmutableList.of(new ChildNumber(44, true), new ChildNumber(88, true), ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);
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
