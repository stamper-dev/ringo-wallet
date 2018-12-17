package wannabit.io.ringowallet.utils;

import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicSeed;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import wannabit.io.ringowallet.base.BaseConstant;

import static com.google.common.base.Preconditions.checkArgument;

public class WKeyUtils {


    public static byte[] getEntropy() {
        byte[] seed = new byte[16];
        new SecureRandom().nextBytes(seed);
        return seed;
    }

    public static List<String> getRandomMnemonic(byte[] seed) {
        List<String> result = new ArrayList<>();
        try {
            result = MnemonicCode.INSTANCE.toMnemonic(seed);

        } catch (MnemonicException.MnemonicLengthException e) {
            if(BaseConstant.IS_SHOWLOG)
                e.printStackTrace();

        }
        return result;
    }


    public static String getRandomWord() {
        String result = "";
        byte[] seed = new byte[16];
        new SecureRandom().nextBytes(seed);
        try {
            List<String> words = MnemonicCode.INSTANCE.toMnemonic(seed);
            result = words.get(0);

        } catch (Exception e) {
            WLog.w("Exception : " + e.getMessage());
        }
        return result;
    }

    public static byte[] getHDSeed(byte[] entropy) {
        try {
            return MnemonicCode.toSeed(MnemonicCode.INSTANCE.toMnemonic(entropy), "");
        } catch (Exception e) {
            return null;
        }

    }

    public static byte[] getHDSeed(String seed) {
        try {
            byte[] entropy     = WUtils.HexStringToByteArray(seed);
            return MnemonicCode.toSeed(MnemonicCode.INSTANCE.toMnemonic(entropy), "");
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSeedfromWords(ArrayList<String> words) {
        try {
            return WUtils.ByteArrayToHexString(new MnemonicCode().toEntropy(words));

        } catch (Exception e) {
            return null;
        }
    }



    public static DeterministicSeed getDeterministicSeed(byte[] entropy) {
        return new DeterministicSeed(entropy, "", System.currentTimeMillis() / 1000);
    }


    public static boolean isMnemonicWord(String word) {
        List<String> words = MnemonicCode.INSTANCE.getWordList();
        if(words.contains(word)) return true;
        else return false;
    }

}
