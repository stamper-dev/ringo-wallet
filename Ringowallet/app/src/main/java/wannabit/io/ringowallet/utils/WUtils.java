package wannabit.io.ringowallet.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.spongycastle.jcajce.provider.digest.SHA3;
import org.spongycastle.util.encoders.Hex;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseActivity;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.Price;
import wannabit.io.ringowallet.model.Support;
import wannabit.io.ringowallet.model.Token;
import wannabit.io.ringowallet.model.Tx;
import wannabit.io.ringowallet.model.WBInputDtoList;
import wannabit.io.ringowallet.model.WBOutputDtoList;
import wannabit.io.ringowallet.model.WalletItem;
import wannabit.io.ringowallet.network.req.ReqCreateRawTx;
import wannabit.io.ringowallet.network.res.ResBchHistory;
import wannabit.io.ringowallet.network.res.ResBtcHistory;
import wannabit.io.ringowallet.network.res.ResEtcHistory;
import wannabit.io.ringowallet.network.res.ResEthHistory;
import wannabit.io.ringowallet.network.res.ResLtcHistory;
import wannabit.io.ringowallet.network.res.ResQtumHistory;
import wannabit.io.ringowallet.network.res.ResUTXByAddress;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class WUtils {


    public static BigDecimal getTotalWalletBalance(BaseApplication app, WalletItem item) {
        return item.getTotalBalance().movePointLeft(item.decimals).setScale(getDpDecimal(app, item), BigDecimal.ROUND_CEILING);
    }

    public static SpannableString getDpTotalWalletBalance(BaseApplication app, WalletItem item) {
        SpannableString result = new SpannableString("0");
        try {
            result = new SpannableString(getDecimalFormat(app, getDpDecimal(app, item)).format(getTotalWalletBalance(app, item)));
            result.setSpan(new RelativeSizeSpan(0.7f), result.length() - getDpDecimal(app, item), result.length(), SPAN_INCLUSIVE_INCLUSIVE);
        } catch (Exception e) {
        } finally {
            return result;
        }
    }

    public static BigDecimal getTotalWalletValue(BaseApplication app, WalletItem item, Price price) {
        return  item.getTotalBalance().movePointLeft(item.decimals).multiply(getPrice(app, price)).setScale(getPriceDecimal(app), BigDecimal.ROUND_CEILING);
    }

    public static SpannableString getDPTotalWalletValue(BaseApplication app, WalletItem item, Price price) {
        SpannableString result = new SpannableString("0");
        try {
            result = new SpannableString(getPriceSymbol(app) + " " + getDecimalFormat(app, getPriceDecimal(app)).format(getTotalWalletValue(app, item, price)));
            result.setSpan(new RelativeSizeSpan(0.7f), result.length() - getPriceDecimal(app), result.length(), SPAN_INCLUSIVE_INCLUSIVE);
        } catch (Exception e)  {
        } finally {
            return result;
        }
    }

    public static SpannableString getDpAllValue(BaseApplication app, BigDecimal total) {
        SpannableString result =
                new SpannableString(getPriceSymbol(app) + " " + getDecimalFormat(app, getPriceDecimal(app)).format(total));
        result.setSpan(new RelativeSizeSpan(0.7f), result.length() - getPriceDecimal(app), result.length(), SPAN_INCLUSIVE_INCLUSIVE);
        return result;
    }


    public static int getDpDecimal(BaseApplication app, WalletItem item) {
        if (app.getBaseDao().getDecimal() == 0) {
            return item.decimals;
        } else if (app.getBaseDao().getDecimal() == 1) {
            return 6;
        } else {
            return 4;
        }
    }

    public static int getDpDecimal(BaseApplication app, Key key) {
        if (app.getBaseDao().getDecimal() == 0) {
            return getTokenDecimal(app, key);
        } else if (app.getBaseDao().getDecimal() == 1) {
            return 6;
        } else {
            return 4;
        }
    }

    public static int getTokenDecimal(BaseApplication app, Key key) {
        if (key.type.equals(BaseConstant.COIN_BTC) || key.type.equals(BaseConstant.COIN_BCH)|| key.type.equals(BaseConstant.COIN_LTC) || key.type.equals(BaseConstant.COIN_QTUM)) {
            return 8;
        } else if (key.type.equals(BaseConstant.COIN_ETH)|| key.type.equals(BaseConstant.COIN_ETC)) {
            return 18;
        } else {
            Token token = app.getBaseDao().onSelectTokenBySymbol(key.symbol);
            if(token.decimals > 0) {
                return token.decimals;
            } else {
                return 8;
            }
        }
    }


    public static BigDecimal getPrice(BaseApplication app, Price price) {
        if (app.getBaseDao().getCurrency() == 0) {
            return price.getUsdBigDecimal();
        } else {
            return price.getkrwBigDecimal();
        }
    }

    public static int getPriceDecimal(BaseApplication app) {
        if (app.getBaseDao().getCurrency() == 0) {
            return 2;
        } else {
            return 0;
        }
    }

    public static String getPriceSymbol(BaseApplication app) {
        if (app.getBaseDao().getCurrency() == 0) {
            return "$";
        } else {
            return "￦";
        }
    }


    public static BigDecimal getKeyBalance(BaseApplication app, Key key, int decimal) {
        return key.lastBalance.movePointLeft(getTokenDecimal(app, key)).setScale(decimal, BigDecimal.ROUND_CEILING);

    }

    public static SpannableString getDpKeyBalance(BaseApplication app, Key key, boolean withSymbol) {
        SpannableString result = new SpannableString("0");
        try {
            int decimal = getDpDecimal(app, key);
            if(withSymbol) {
                result = new SpannableString(getDecimalFormat(app, decimal).format(getKeyBalance(app, key, decimal)) + " " + key.symbol);
                result.setSpan(new RelativeSizeSpan(0.7f), result.length() - decimal - key.symbol.length() - 1, result.length(), SPAN_INCLUSIVE_INCLUSIVE);
            } else {
                result = new SpannableString(getDecimalFormat(app, decimal).format(getKeyBalance(app, key, decimal)));
                result.setSpan(new RelativeSizeSpan(0.7f), result.length() - decimal, result.length(), SPAN_INCLUSIVE_INCLUSIVE);
            }

        } catch (Exception e) {

        } finally {
            return result;
        }
    }

    public static SpannableString getDpKeyFullBalance(BaseApplication app, Key key, boolean withSymbol) {
        SpannableString result = new SpannableString("0");
        try {
            int decimal = getTokenDecimal(app, key);
            BigDecimal balance = key.lastBalance.movePointLeft(getTokenDecimal(app, key)).setScale(decimal, BigDecimal.ROUND_CEILING);
            if(withSymbol) {
                result = new SpannableString(getDecimalFormat(app, decimal).format(balance) + " " + key.symbol);
                result.setSpan(new RelativeSizeSpan(0.7f), result.length() - decimal - key.symbol.length() - 1, result.length(), SPAN_INCLUSIVE_INCLUSIVE);
            } else {
                result = new SpannableString(getDecimalFormat(app, decimal).format(balance));
                result.setSpan(new RelativeSizeSpan(0.7f), result.length() - decimal, result.length(), SPAN_INCLUSIVE_INCLUSIVE);
            }

        } catch (Exception e) {

        } finally {
            return result;
        }
    }

    public static SpannableString getBigDecimalToDpBalance(BaseApplication app, BigDecimal input, Key key, boolean withSymbol) {
        SpannableString result = new SpannableString("0");
        try {
            int decimal = getDpDecimal(app, key);
            BigDecimal balance = input.movePointLeft(getTokenDecimal(app, key)).setScale(decimal, BigDecimal.ROUND_CEILING);
            if(withSymbol) {
                result = new SpannableString(getDecimalFormat(app, decimal).format(balance) + " " + key.symbol);
                result.setSpan(new RelativeSizeSpan(0.7f), result.length() - decimal - key.symbol.length() - 1, result.length(), SPAN_INCLUSIVE_INCLUSIVE);
            } else {
                result = new SpannableString(getDecimalFormat(app, decimal).format(balance));
                result.setSpan(new RelativeSizeSpan(0.7f), result.length() - decimal, result.length(), SPAN_INCLUSIVE_INCLUSIVE);
            }

        } catch (Exception e) {

        } finally {
            return result;
        }
    }


    public static SpannableString getBigDecimalToDpFullBalance(BaseApplication app, BigDecimal input, Key key, boolean withSymbol) {
        SpannableString result = new SpannableString("0");
        try {
            int decimal = getTokenDecimal(app, key);
            BigDecimal balance = input.movePointLeft(getTokenDecimal(app, key)).setScale(decimal, BigDecimal.ROUND_CEILING);
            if(withSymbol) {
                result = new SpannableString(getDecimalFormat(app, decimal).format(balance) + " " + key.symbol);
                result.setSpan(new RelativeSizeSpan(0.7f), result.length() - decimal - key.symbol.length() - 1, result.length(), SPAN_INCLUSIVE_INCLUSIVE);
            } else {
                result = new SpannableString(getDecimalFormat(app, decimal).format(balance));
                result.setSpan(new RelativeSizeSpan(0.7f), result.length() - decimal, result.length(), SPAN_INCLUSIVE_INCLUSIVE);
            }

        } catch (Exception e) {

        } finally {
            return result;
        }
    }


    public static BigDecimal getKeyValue(BaseApplication app, Key key, Price price) {
        return key.lastBalance.movePointLeft(getTokenDecimal(app, key)).multiply(getPrice(app, price)).setScale(getPriceDecimal(app), BigDecimal.ROUND_CEILING);
    }

    public static SpannableString getDpKeyValue(BaseApplication app, Key key, Price price) {
        SpannableString result = new SpannableString("0");
        try {
            result = new SpannableString(getPriceSymbol(app) + " " + getDecimalFormat(app, getPriceDecimal(app)).format(getKeyValue(app, key, price)));
            result.setSpan(new RelativeSizeSpan(0.7f), result.length() - getPriceDecimal(app), result.length(), SPAN_INCLUSIVE_INCLUSIVE);
        } catch (Exception e) {

        } finally {
            return result;
        }
    }


    public static BigDecimal getTxAmount(BaseApplication app, Tx tx, Key key, int decimal) {
        return new BigDecimal(tx.amount).movePointLeft(getTokenDecimal(app, key)).setScale(decimal, BigDecimal.ROUND_CEILING);
    }

    public static SpannableString getDpTxAmount(BaseApplication app, Tx tx, Key key, boolean withSymbol) {
        SpannableString result = new SpannableString("0");
        try {
            int decimal = getDpDecimal(app, key);
            if(withSymbol) {
                result = new SpannableString(getDecimalFormat(app, decimal).format(getTxAmount(app, tx, key, decimal)) + " " + key.symbol);
                result.setSpan(new RelativeSizeSpan(0.7f), result.length() - decimal - key.symbol.length() - 1, result.length(), SPAN_INCLUSIVE_INCLUSIVE);
            } else {
                result = new SpannableString(getDecimalFormat(app, decimal).format(getTxAmount(app, tx, key, decimal)));
                result.setSpan(new RelativeSizeSpan(0.7f), result.length() - decimal, result.length(), SPAN_INCLUSIVE_INCLUSIVE);
            }

        } catch (Exception e) {

        } finally {
           return result;
        }

    }


    public static SpannableString getSendDpDialog(Context context, BigDecimal input, String symbol, int decimal) {
        SpannableString result = new SpannableString("0");
        try {
            BigDecimal balance = input.movePointLeft(decimal).setScale(decimal, BigDecimal.ROUND_CEILING);
            result = new SpannableString(getDecimalFormat(context, decimal).format(balance) + " " + symbol);
            result.setSpan(new RelativeSizeSpan(0.8f), result.length() - decimal - symbol.length() - 1, result.length(), SPAN_INCLUSIVE_INCLUSIVE);

        } catch (Exception e) {

        } finally {
            return result;
        }
    }


    //TODO double check utc
    public static String getDpTxDate(BaseApplication app, Tx tx, Key key) {
        String result = "";
        SimpleDateFormat dpFormat = new SimpleDateFormat(app.getString(R.string.str_tx_time_format));

        try {
            if (key.type.equals(BaseConstant.COIN_BTC)) {
                Date d = new Date(tx.date * 1000);
                result = dpFormat.format(d);

            } else if (key.type.equals(BaseConstant.COIN_BCH)) {
                Date d = new Date(tx.date * 1000);
                result = dpFormat.format(d);

            } else if (key.type.equals(BaseConstant.COIN_BSV)) {

            } else if (key.type.equals(BaseConstant.COIN_LTC)) {
                Date d = new Date(tx.date);
                result = dpFormat.format(d);

            } else if (key.type.equals(BaseConstant.COIN_ETH)) {
                Date d = new Date(tx.date * 1000);
                result = dpFormat.format(d);

            } else if (key.type.equals(BaseConstant.COIN_ETC)) {
                Date d = new Date(tx.date);
                result = dpFormat.format(d);

            } else if (key.type.equals(BaseConstant.COIN_QTUM)) {
                Date d = new Date(tx.date * 1000);
                result = dpFormat.format(d);

            } else if (key.type.equals(BaseConstant.COIN_ERC20)) {
                Date d = new Date(tx.date * 1000);
                result = dpFormat.format(d);

            } else if (key.type.equals(BaseConstant.COIN_QRC20)) {

            }

        }catch (Exception e) {

        }finally {
            return result;
        }
    }

    public static String getDpKeyDate(BaseApplication app, long time) {
        String result = "";
        SimpleDateFormat dpFormat = new SimpleDateFormat(app.getString(R.string.str_tx_time_format));
        result = dpFormat.format(new Date(time));
        return result;
    }




    public static SpannableString getDpSendAmountValue(BaseApplication app, BigDecimal input, Key key, Price price) {
        SpannableString result = new SpannableString("0");
        try {
            BigDecimal bal = input.multiply(getPrice(app, price)).setScale(getPriceDecimal(app), BigDecimal.ROUND_CEILING);
            result = new SpannableString("~"+ getPriceSymbol(app) + " " + getDecimalFormat(app, getPriceDecimal(app)).format(bal));
            result.setSpan(new RelativeSizeSpan(0.7f), result.length() - getPriceDecimal(app), result.length(), SPAN_INCLUSIVE_INCLUSIVE);
        } catch (Exception e) {

        } finally {
            return result;
        }
    }


    public static Typeface getTypefaceRegular(Context context) {
        return Typeface.createFromAsset(context.getResources().getAssets(), context.getString(R.string.font_regular));

    }

    public static Typeface getTypefaceLight(Context context) {
        return Typeface.createFromAsset(context.getResources().getAssets(), context.getString(R.string.font_light));

    }

    public static AnimatorSet getMatchingAnim(View from, View to) {

        int[] fromLoc = new int[2];
        from.getLocationOnScreen(fromLoc);

        int[] toLoc = new int[2];
        to.getLocationOnScreen(toLoc);

        AnimatorSet matchAnim = new AnimatorSet();
        ObjectAnimator transX = ObjectAnimator.ofFloat(from, "translationX", toLoc[0] - fromLoc[0]);
        transX.setDuration(600);
        ObjectAnimator transY = ObjectAnimator.ofFloat(from, "translationY", toLoc[1] - fromLoc[1]);
        transY.setDuration(600);
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(from, "scaleX", 2.2f);
        scaleUpX.setDuration(300);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(from, "scaleY", 2.2f);
        scaleUpY.setDuration(300);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(from, "scaleX", 1.0f);
        scaleDownX.setDuration(300);
        scaleDownX.setStartDelay(300);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(from, "scaleY", 1.0f);
        scaleDownY.setDuration(300);
        scaleDownY.setStartDelay(300);

        matchAnim.playTogether(transX, transY, scaleUpX, scaleUpY, scaleDownX, scaleDownY);
        matchAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        return matchAnim;
    }

    public static AnimationSet getFadeAnim() {

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(400);


        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(400);
        fadeIn.setDuration(400);

        AnimationSet anim = new AnimationSet(false);
        anim.addAnimation(fadeOut);
        anim.addAnimation(fadeIn);

        return anim;
    }

    public static boolean checkPasscodePattern(String pincode) {
        if(pincode.length() != 5)
            return false;
        String regex = "^\\d{4}+[A-Z]{1}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(pincode);
        boolean isNormal = m.matches();
        return isNormal;
    }


    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static boolean isMainCoin(String symbol) {
        if(symbol.equals(BaseConstant.COIN_BTC) || symbol.equals(BaseConstant.COIN_BCH) || symbol.equals(BaseConstant.COIN_LTC) ||
                symbol.equals(BaseConstant.COIN_ETH) || symbol.equals(BaseConstant.COIN_ETC) || symbol.equals(BaseConstant.COIN_QTUM)) {
            return true;
        }
        return false;
    }

    public static WalletItem getBaseWalletItem(String symbol) {
        if(symbol.equals(BaseConstant.COIN_BTC)) {
            return getBtcItem();

        } else if (symbol.equals(BaseConstant.COIN_BCH)) {
            return getBchItem();

        } else if (symbol.equals(BaseConstant.COIN_LTC)) {
            return getLtcItem();

        } else if (symbol.equals(BaseConstant.COIN_ETH)) {
            return getEthItem();

        } else if (symbol.equals(BaseConstant.COIN_ETC)) {
            return getEtcItem();

        } else if (symbol.equals(BaseConstant.COIN_QTUM)) {
            return getQtumItem();

        }
        return null;
    }

    public static WalletItem getBtcItem() {
        return new WalletItem(BaseConstant.COIN_BTC_NAME, BaseConstant.COIN_BTC, BaseConstant.COIN_BTC, 8, null, R.drawable.bitcoin_ic);

    }

    public static WalletItem getBchItem() {
        return new WalletItem(BaseConstant.COIN_BCH_NAME, BaseConstant.COIN_BCH, BaseConstant.COIN_BCH, 8, null, R.drawable.btccash_ic);

    }

    public static WalletItem getLtcItem() {
        return new WalletItem(BaseConstant.COIN_LTC_NAME, BaseConstant.COIN_LTC, BaseConstant.COIN_LTC, 8, null, R.drawable.ltc_ic);

    }

    public static WalletItem getEthItem() {
        return new WalletItem(BaseConstant.COIN_ETH_NAME, BaseConstant.COIN_ETH, BaseConstant.COIN_ETH, 18, null, R.drawable.eth_ic);

    }

    public static WalletItem getEtcItem() {
        return new WalletItem(BaseConstant.COIN_ETC_NAME, BaseConstant.COIN_ETC, BaseConstant.COIN_ETC, 18, null, R.drawable.ethc_ic);

    }

    public static WalletItem getQtumItem() {
        return new WalletItem(BaseConstant.COIN_QTUM_NAME, BaseConstant.COIN_QTUM, BaseConstant.COIN_QTUM, 8, null, R.drawable.qtum_ic);

    }

    public static WalletItem getBsvItem() {
        return new WalletItem(BaseConstant.COIN_BSV_NAME, BaseConstant.COIN_BSV, BaseConstant.COIN_BSV, 8, null, R.drawable.bitcoinsv_ic);

    }


    public static ArrayList<Token> getAddList(BaseApplication app) {
        Support support     = app.getBaseDao().getSupport();
        ArrayList<Token> result = new ArrayList<Token>();
        if(support.btc) result.add(new Token(BaseConstant.COIN_BTC_NAME , BaseConstant.COIN_BTC, BaseConstant.COIN_BTC, 8, "",  "", R.drawable.bitcoin_ic));
        if(support.eth) result.add(new Token(BaseConstant.COIN_ETH_NAME , BaseConstant.COIN_ETH, BaseConstant.COIN_ETH, 18, "",  "", R.drawable.eth_ic));
        if(support.ltc) result.add(new Token(BaseConstant.COIN_LTC_NAME , BaseConstant.COIN_LTC, BaseConstant.COIN_LTC, 8, "",  "", R.drawable.ltc_ic));
        if(support.etc) result.add(new Token(BaseConstant.COIN_ETC_NAME , BaseConstant.COIN_ETC, BaseConstant.COIN_ETC, 18, "",  "", R.drawable.ethc_ic));
        if(support.bch) result.add(new Token(BaseConstant.COIN_BCH_NAME , BaseConstant.COIN_BCH, BaseConstant.COIN_BCH, 8, "",  "", R.drawable.btccash_ic));
        if(support.qtum) result.add(new Token(BaseConstant.COIN_QTUM_NAME , BaseConstant.COIN_QTUM, BaseConstant.COIN_QTUM, 8, "",  "", R.drawable.qtum_ic));
        if(support.bsv) result.add(new Token(BaseConstant.COIN_BSV_NAME , BaseConstant.COIN_BSV, BaseConstant.COIN_BSV, 8, "",  "", R.drawable.bitcoinsv_ic));


        ArrayList<Token> tokens = app.getBaseDao().onSelectAllTokens();
        for (Token token : tokens) {
            if (token.type.equals(BaseConstant.COIN_ERC20) && support.erc20) {
                result.add(token);
            } else if (token.type.equals(BaseConstant.COIN_QRC20) && support.qrc20) {
                result.add(token);
            }
        }

        return result;
    }

    public static int getIconResource(Key key) {
        if(key.symbol.equals(BaseConstant.COIN_BTC)) {
            return R.drawable.bitcoin_ic;

        } else if(key.symbol.equals(BaseConstant.COIN_ETH)) {
            return R.drawable.eth_ic;

        } else if(key.symbol.equals(BaseConstant.COIN_BCH)) {
            return R.drawable.btccash_ic;

        } else if(key.symbol.equals(BaseConstant.COIN_LTC)) {
            return R.drawable.ltc_ic;

        } else if(key.symbol.equals(BaseConstant.COIN_ETC)) {
            return R.drawable.ethc_ic;

        } else if(key.symbol.equals(BaseConstant.COIN_QTUM)) {
            return R.drawable.qtum_ic;

        } else if(key.symbol.equals(BaseConstant.COIN_BSV)) {
            return R.drawable.bitcoinsv_ic;

        } else {
            return -1;
        }

    }

    public static String getDefaultPath(BaseApplication app, String type) {
        if(type.equals(BaseConstant.COIN_BTC)) {
            return app.getString(R.string.path_btc);

        } else if (type.equals(BaseConstant.COIN_ETH) || type.equals(BaseConstant.COIN_ERC20)) {
            return app.getString(R.string.path_eth);

        } else if (type.equals(BaseConstant.COIN_BCH)) {
            return app.getString(R.string.path_bch);

        } else if (type.equals(BaseConstant.COIN_LTC)) {
            return app.getString(R.string.path_ltc);

        } else if (type.equals(BaseConstant.COIN_ETC)) {
            return app.getString(R.string.path_etc);

        } else if (type.equals(BaseConstant.COIN_QTUM) || type.equals(BaseConstant.COIN_QRC20)) {
            return app.getString(R.string.path_qtum);

        }
        return "";
    }



    public static ArrayList<Tx> onChangeEthHistoryToTx(Key key, long top, ArrayList<ResEthHistory.Result> histories) {
        ArrayList<Tx> result = new ArrayList<>();
        for(ResEthHistory.Result history : histories) {
            if(Long.parseLong(history.timeStamp) > top) {
                Tx temp = new Tx(key.uuid,
                        history.hash,
                        Long.parseLong(history.timeStamp),
                        history.from,
                        history.to,
                        history.value,
                        getEthFee(history.gas, history.gasPrice));
                result.add(temp);
            }
        }
        WLog.w("for add size : " + result.size());
        return result;
    }

    public static ArrayList<Tx> onChangeErcHistoryToTx(Key key, long top, ArrayList<ResEthHistory.Result> histories, String address) {
        ArrayList<Tx> result = new ArrayList<>();
        for(ResEthHistory.Result history : histories) {
            if(Long.parseLong(history.timeStamp) > top) {
                Tx temp = new Tx(history.hash,
                        Long.parseLong(history.timeStamp),
                        history.from,
                        history.to,
                        history.value,
                        getEthFee(history.gas, history.gasPrice),
                        address,
                        history.contractAddress);
                result.add(temp);
            }
        }
        WLog.w("for add size : " + result.size());
        return result;
    }

    public static String getEthFee(String gas, String gasPrice) {
        BigDecimal gasWei = new BigDecimal(gas);
        BigDecimal gaspriceWei = new BigDecimal(gasPrice);
//        return Convert.fromWei(gasWei.multiply(gaspriceWei), Convert.Unit.ETHER).toPlainString();
        return gasWei.multiply(gaspriceWei).toPlainString();
    }


    public static ArrayList<Tx> onChangeBtcHistoryToTx(Key key, long top, ArrayList<ResBtcHistory.Txs> histories) {
        ArrayList<Tx> result = new ArrayList<>();
        for(ResBtcHistory.Txs history : histories) {
            if(history.time > top) {
                String sender = getBtcFrom(history.inputs, key.address);
                Tx temp = new Tx(key.uuid,
                        history.hash,
                        history.time,
                        sender,
                        getBtcTo(history.out, key.address, sender),
                        getBtcValue(history, key.address),
                        getBtcFee(history));
                result.add(temp);
            }
        }

        WLog.w("for add size : " + result.size());
        return result;
    }

    public static String getBtcFrom(ArrayList<ResBtcHistory.Input> preinput, String myAddr) {
        String result = "";
        for (ResBtcHistory.Input pre : preinput) {
            if(pre.prev_out.addr.equals(myAddr)) {
                result = pre.prev_out.addr;
                break;
            } else {
                if(TextUtils.isEmpty(result)) result = pre.prev_out.addr;
                else result = result + ", " + pre.prev_out.addr;
            }
        }
        return result;
    }

    public static String getBtcTo(ArrayList<ResBtcHistory.Out> outs, String myAddr, String sender) {
        String result = "";
        if(sender.equals(myAddr)) {
            for (ResBtcHistory.Out out : outs) {
                if(!out.addr.equals(myAddr)) {
                    if(TextUtils.isEmpty(result)) result = out.addr;
                    else result = result + ", " + out.addr;
                }
            }

        } else {
            for (ResBtcHistory.Out out : outs) {
                if(out.addr.equals(myAddr)) {
                    result = out.addr;
                    break;
                }
            }
        }
        return result;
    }

    public static String getBtcValue(ResBtcHistory.Txs txs, String myAddr) {
        BigDecimal beforeAmount = BigDecimal.ZERO;
        BigDecimal afterAmount  = BigDecimal.ZERO;
        for (ResBtcHistory.Input pre : txs.inputs) {
            if(pre.prev_out.addr.equals(myAddr)) {
                beforeAmount = beforeAmount.add(new BigDecimal(pre.prev_out.value));
            }
        }
        for(ResBtcHistory.Out out : txs.out) {
            if(out.addr.equals(myAddr)) {
                afterAmount = afterAmount.add(new BigDecimal(out.value));
            }
        }
        return beforeAmount.subtract(afterAmount).abs().toPlainString();
    }

    public static String getBtcFee(ResBtcHistory.Txs txs) {
        BigDecimal beforeAmount = BigDecimal.ZERO;
        BigDecimal afterAmount  = BigDecimal.ZERO;
        for (ResBtcHistory.Input pre : txs.inputs) {
            beforeAmount = beforeAmount.add(new BigDecimal(pre.prev_out.value));
        }
        for(ResBtcHistory.Out out : txs.out) {
            afterAmount = afterAmount.add(new BigDecimal(out.value));
        }
        return afterAmount.subtract(beforeAmount).abs().toPlainString();
    }


    public static ArrayList<Tx> onChangeLTCHistoryToTx(Key key, long top, ArrayList<ResLtcHistory.Txs> histories) {
        ArrayList<Tx> result = new ArrayList<>();
        SimpleDateFormat blockCypherFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        blockCypherFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        for(ResLtcHistory.Txs history : histories) {
            try {
                Date d = blockCypherFormat.parse(history.confirmed);
                if(top >=  d.getTime()) continue;
                String sender = getLtcFrom(history.inputs, key.address);
                Tx temp = new Tx(key.uuid,
                        history.hash,
                        d.getTime(),
                        sender,
                        getLtcTo(history.outputs, key.address, sender),
                        getLtcValue(history, key.address),
                        ""+history.fees);
                result.add(temp);
            } catch (Exception e) {
                WLog.w("time parse error " + e.getMessage());
            }
        }

        WLog.w("for LTC add size : " + result.size());
        return result;
    }

    public static ArrayList<Tx> onChangeBCHHistoryToTx(Key key, long top, ArrayList<ResBchHistory.Txs> histories) {
        ArrayList<Tx> result = new ArrayList<>();
        for(ResBchHistory.Txs history : histories) {
            if(history.time > top) {
                String sender = getBchSender(history.vin, key.address);
                Tx temp = new Tx(key.uuid,
                        history.txid,
                        history.time,
                        sender,
                        getBchTo(history.vout, key.address, sender),
                        getBchValue(history, key.address, sender, history.fees),
                        BigDecimal.valueOf(history.fees).movePointRight(8).toPlainString());
                result.add(temp);
            }
        }

        WLog.w("for BCH add size : " + result.size());
        return result;
    }

    public static String getBchSender(ArrayList<ResBchHistory.Vin> ins, String myAddr) {
        String result = "";
        for (ResBchHistory.Vin in : ins) {
            if(in.addr.equals(myAddr)) {
                result = myAddr;
                break;
            } else {
                if(TextUtils.isEmpty(result)) result = in.addr;
                else result = result + ", " + in.addr;
            }
        }
        return result;
    }

    public static String getBchTo(ArrayList<ResBchHistory.Vout> outs, String myAddr, String sender) {
        String result = "";
        if(sender.equals(myAddr)) {
            for (ResBchHistory.Vout out : outs) {
                if(!out.scriptPubKey.addresses.get(0).equals(myAddr)) {
                    if(TextUtils.isEmpty(result)) result = out.scriptPubKey.addresses.get(0);
                    else result = result + ", " + out.scriptPubKey.addresses.get(0);
                }
            }
        } else {
            for (ResBchHistory.Vout out : outs) {
                if(out.scriptPubKey == null ||out.scriptPubKey.addresses == null) continue;
//                WLog.w("out.addresses : " + out.scriptPubKey.addresses.get(0) + "         myaddr : " + myAddr);
                if(out.scriptPubKey.addresses.get(0).equals(myAddr)) {
                    result = out.scriptPubKey.addresses.get(0);
                    break;
                }
            }
        }
        return result;
    }

    public static String getBchValue(ResBchHistory.Txs txs, String myAddr, String sender, double fee) {
        BigDecimal beforeAmount = BigDecimal.ZERO;
        BigDecimal afterAmount  = BigDecimal.ZERO;
        for (ResBchHistory.Vin input : txs.vin) {
            if(input.addr.equals(myAddr)) {
                beforeAmount = beforeAmount.add(BigDecimal.valueOf(input.value));
            }
        }
        for(ResBchHistory.Vout out : txs.vout) {
            if(out.scriptPubKey.addresses == null) continue;
            if(out.scriptPubKey.addresses.get(0).equals(myAddr)) {
                afterAmount = afterAmount.add(new BigDecimal(out.value));
            }
        }
        if(myAddr.equals(sender)) {
            return beforeAmount.subtract(afterAmount).subtract(BigDecimal.valueOf(fee)).movePointRight(8).abs().toPlainString();
        } else {
            return beforeAmount.subtract(afterAmount).movePointRight(8).abs().toPlainString();
        }

    }



    public static ArrayList<Tx> onChangeQtumHistoryToTx(Key key, long top, ArrayList<ResQtumHistory.Txs> histories) {
        ArrayList<Tx> result = new ArrayList<>();
        for(ResQtumHistory.Txs history : histories) {
            if(history.time > top && !history.isqrc20Transfer) {
                String sender = getQtumSender(history.vin, key.address);
                Tx temp = new Tx(key.uuid,
                        history.txid,
                        history.time,
                        sender,
                        getQtumTo(history.vout, key.address, sender),
                        getQtumValue(history, key.address, sender, history.fees),
                        BigDecimal.valueOf(history.fees).movePointRight(8).toPlainString());
                result.add(temp);
            }
        }

        WLog.w("for QTUM add size : " + result.size());
        return result;
    }

    public static String getQtumSender(ArrayList<ResQtumHistory.Vin> ins, String myAddr) {
        String result = "";
        for (ResQtumHistory.Vin in : ins) {
            if(in.addr.equals(myAddr)) {
                result = myAddr;
                break;
            } else {
                if(TextUtils.isEmpty(result)) result = in.addr;
                else result = result + ", " + in.addr;
            }
        }
        return result;
    }

    public static String getQtumTo(ArrayList<ResQtumHistory.Vout> outs, String myAddr, String sender) {
        String result = "";
        if(sender.equals(myAddr)) {
            for (ResQtumHistory.Vout out : outs) {
                if(!out.scriptPubKey.addresses.get(0).equals(myAddr)) {
                    if(TextUtils.isEmpty(result)) result = out.scriptPubKey.addresses.get(0);
                    else result = result + ", " + out.scriptPubKey.addresses.get(0);
                }
            }
        } else {
            for (ResQtumHistory.Vout out : outs) {
                if(out.scriptPubKey == null ||out.scriptPubKey.addresses == null) continue;
                if(out.scriptPubKey.addresses.get(0).equals(myAddr)) {
                    result = out.scriptPubKey.addresses.get(0);
                    break;
                }
            }
        }
        return result;
    }

    public static String getQtumValue(ResQtumHistory.Txs txs, String myAddr, String sender, double fee) {
        BigDecimal beforeAmount = BigDecimal.ZERO;
        BigDecimal afterAmount  = BigDecimal.ZERO;
        for (ResQtumHistory.Vin input : txs.vin) {
            if(input.addr.equals(myAddr)) {
                beforeAmount = beforeAmount.add(BigDecimal.valueOf(input.value));
            }
        }
        for(ResQtumHistory.Vout out : txs.vout) {
            if(out.scriptPubKey.addresses == null) continue;
            if(out.scriptPubKey.addresses.get(0).equals(myAddr)) {
                afterAmount = afterAmount.add(new BigDecimal(out.value));
            }
        }
        if(myAddr.equals(sender)) {
            return beforeAmount.subtract(afterAmount).subtract(BigDecimal.valueOf(fee)).movePointRight(8).abs().toPlainString();
        } else {
            return beforeAmount.subtract(afterAmount).movePointRight(8).abs().toPlainString();
        }

    }


    public static String getLtcFrom(ArrayList<ResLtcHistory.Input> preinput, String myAddr) {
        String result = "";
        for (ResLtcHistory.Input pre : preinput) {
            if(pre.addresses.get(0).equals(myAddr)) {
                result = pre.addresses.get(0);
                break;
            } else {
                if(TextUtils.isEmpty(result)) result = pre.addresses.get(0);
                else result = result + ", " + pre.addresses.get(0);
            }
        }
        return result;
    }

    public static String getLtcTo(ArrayList<ResLtcHistory.Out> outs, String myAddr, String sender) {
        String result = "";
        if(sender.equals(myAddr)) {
            for (ResLtcHistory.Out out : outs) {
                if(!out.addresses.get(0).equals(myAddr)) {
                    if(TextUtils.isEmpty(result)) result = out.addresses.get(0);
                    else result = result + ", " + out.addresses.get(0);
                }
            }
        } else {
            for (ResLtcHistory.Out out : outs) {
                if(out.addresses.get(0).equals(myAddr)) {
                    result = out.addresses.get(0);
                    break;
                }
            }
        }
        return result;
    }

    public static String getLtcValue(ResLtcHistory.Txs txs, String myAddr) {
        BigDecimal beforeAmount = BigDecimal.ZERO;
        BigDecimal afterAmount  = BigDecimal.ZERO;
        for (ResLtcHistory.Input input : txs.inputs) {
            if(input.addresses.get(0).equals(myAddr)) {
                beforeAmount = beforeAmount.add(BigDecimal.valueOf(input.output_value));
            }
        }
        for(ResLtcHistory.Out out : txs.outputs) {
            if(out.addresses.get(0).equals(myAddr)) {
                afterAmount = afterAmount.add(BigDecimal.valueOf(out.value));
            }
        }
        return beforeAmount.subtract(afterAmount).abs().toPlainString();
    }




    public static ArrayList<Tx> onChangeEtcHistoryToTx(Key key, long top, ArrayList<ResEtcHistory.Item> histories) {
        ArrayList<Tx> result = new ArrayList<>();
        SimpleDateFormat gastrackerFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        gastrackerFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        for(ResEtcHistory.Item history : histories) {
            try {
                Date d = gastrackerFormat.parse(history.timestamp);
                Tx temp = new Tx(key.uuid,
                        history.hash,
                        d.getTime(),
                        history.from,
                        history.to,
                        history.value.wei,
                        "");
                result.add(temp);
            } catch (Exception e) {
                WLog.w("time parse error " + e.getMessage());
            }
        }
        WLog.w("for add size : " + result.size());
        return result;
    }



    public static DecimalFormat getDecimalFormat(Context c, int cnt) {
        DecimalFormat decimalformat = null;
        switch (cnt) {
            case 0:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_0));
                break;
            case 1:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_1));
                break;
            case 2:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_2));
                break;
            case 3:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_3));
                break;
            case 4:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_4));
                break;
            case 5:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_5));
                break;
            case 6:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_6));
                break;
            case 7:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_7));
                break;
            case 8:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_8));
                break;
            case 9:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_9));
                break;
            case 10:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_10));
                break;
            case 11:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_11));
                break;
            case 12:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_12));
                break;
            case 13:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_13));
                break;
            case 14:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_14));
                break;
            case 15:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_15));
                break;
            case 16:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_16));
                break;
            case 17:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_17));
                break;
            case 18:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_18));
                break;

            default:
                decimalformat = new DecimalFormat(c.getString(R.string.str_decimal_pattern_6));
                break;
        }
        return decimalformat;
    }

    public static BigDecimal getEstimateFee(BaseApplication app, String type, int position) {
        BigDecimal result = BigDecimal.ZERO;

        if (type.equals(BaseConstant.COIN_BTC)) {
            result = new BigDecimal(new ArrayList<String>(Arrays.asList(app.getResources().getStringArray(R.array.btc_fee))).get(position));

        } else if (type.equals(BaseConstant.COIN_LTC)) {
            result = new BigDecimal(new ArrayList<String>(Arrays.asList(app.getResources().getStringArray(R.array.ltc_fee))).get(position));

        } else if (type.equals(BaseConstant.COIN_BCH)) {
            result = new BigDecimal(new ArrayList<String>(Arrays.asList(app.getResources().getStringArray(R.array.bch_fee))).get(position));

        } else if (type.equals(BaseConstant.COIN_BSV)) {

        } else if (type.equals(BaseConstant.COIN_QTUM)) {
            result = new BigDecimal(new ArrayList<String>(Arrays.asList(app.getResources().getStringArray(R.array.qtum_fee))).get(position));
        } else {
            //TODO
            return null;
        }
        return result;
    }

    public static BigDecimal getEstimateGasPrice(BaseApplication app, String type, int position) {
        BigDecimal result = BigDecimal.ZERO;
        if (type.equals(BaseConstant.COIN_ETH)) {
            result = new BigDecimal(new ArrayList<String>(Arrays.asList(app.getResources().getStringArray(R.array.eth_gas_price))).get(position));
        } else if (type.equals(BaseConstant.COIN_ETC)) {
            result = new BigDecimal(new ArrayList<String>(Arrays.asList(app.getResources().getStringArray(R.array.etc_gas_price))).get(position));

        } else if (type.equals(BaseConstant.COIN_ERC20)) {
            result = new BigDecimal(new ArrayList<String>(Arrays.asList(app.getResources().getStringArray(R.array.eth_gas_price))).get(position));
        }
        return result;
    }

    public static BigDecimal getEstimateGasLimit(BaseApplication app, String type, int position) {
        BigDecimal result = BigDecimal.ZERO;
        if (type.equals(BaseConstant.COIN_ETH)) {
            result = new BigDecimal(new ArrayList<String>(Arrays.asList(app.getResources().getStringArray(R.array.eth_gas_limit))).get(position));
        } else if (type.equals(BaseConstant.COIN_ETC)) {
            result = new BigDecimal(new ArrayList<String>(Arrays.asList(app.getResources().getStringArray(R.array.etc_gas_limit))).get(position));

        } else if (type.equals(BaseConstant.COIN_ERC20)) {
            result = new BigDecimal(new ArrayList<String>(Arrays.asList(app.getResources().getStringArray(R.array.erc_gas_limit))).get(position));
        }
        return result;
    }


    public static BigDecimal getMinAmountToSend(BaseApplication app, String type) {
        BigDecimal result = BigDecimal.ZERO;

        if (type.equals(BaseConstant.COIN_BTC)) {
            return new BigDecimal(app.getString(R.string.btc_min));

        } else if (type.equals(BaseConstant.COIN_LTC)) {
            return new BigDecimal(app.getString(R.string.ltc_min));

        } else if (type.equals(BaseConstant.COIN_BCH)) {
            return new BigDecimal(app.getString(R.string.bch_min));

        } else if (type.equals(BaseConstant.COIN_ETH)) {

        } else if (type.equals(BaseConstant.COIN_ETC)) {

        } else if (type.equals(BaseConstant.COIN_BSV)) {

        } else if (type.equals(BaseConstant.COIN_QTUM)) {
            return new BigDecimal(app.getString(R.string.qtum_min));

        } else {
            //TODO
            return null;
        }

        return result;
    }







    private static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    public static boolean validateBitcoinAddress(String addr) {
        if (addr.length() < 26 || addr.length() > 35)
            return false;
        byte[] decoded = decodeBase58To25Bytes(addr);
        if (decoded == null)
            return false;

        byte[] hash1 = sha256(Arrays.copyOfRange(decoded, 0, 21));
        byte[] hash2 = sha256(hash1);

        return Arrays.equals(Arrays.copyOfRange(hash2, 0, 4), Arrays.copyOfRange(decoded, 21, 25));
    }

    private static byte[] decodeBase58To25Bytes(String input) {
        BigInteger num = BigInteger.ZERO;
        for (char t : input.toCharArray()) {
            int p = ALPHABET.indexOf(t);
            if (p == -1)
                return null;
            num = num.multiply(BigInteger.valueOf(58)).add(BigInteger.valueOf(p));
        }

        byte[] result = new byte[25];
        byte[] numBytes = num.toByteArray();
        System.arraycopy(numBytes, 0, result, result.length - numBytes.length, numBytes.length);
        return result;
    }

    private static byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean validateEthereumAddress(String addr) {
        String regex = "^0x[0-9a-fA-F]{40}$";
        if(!addr.matches(regex))
        {
            return false;
        }

        String subAddr = addr.substring(2);
        String subAddrLower = subAddr.toLowerCase();

        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        digestSHA3.update(subAddrLower.getBytes());
        String digestMessage = Hex.toHexString(digestSHA3.digest());

        for(short i=0 ;i < subAddr.length();i++)
        {
            if(subAddr.charAt(i)>=65 && subAddr.charAt(i)<=91)
            {

                String ss = Character.toString(digestMessage.charAt(i));
                if(!(Integer.parseInt(ss,16) > 7 ))
                {
                    return false;
                }
            }
        }

        return true;

    }


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static ReqCreateRawTx onGenerateLocalTx(List<ResUTXByAddress> utxo, ECKey key, NetworkParameters param, BigDecimal totalAmount, BigDecimal sendAmount, BigDecimal feeAmount, String targetAddress) {
        ReqCreateRawTx request = new ReqCreateRawTx();
        for (ResUTXByAddress data : utxo) {
            WBInputDtoList tempInput = new WBInputDtoList();
            tempInput.setTxid(data.getTxid());
            tempInput.setN(data.getN());
            request.addInputList(tempInput);
        }

        WBOutputDtoList self = new WBOutputDtoList();
        self.setAddress(key.toAddress(param).toString());

        BigDecimal remainAmount = totalAmount.subtract(sendAmount).subtract(feeAmount);
        self.setValue(remainAmount.stripTrailingZeros().toPlainString());
        request.addOutputList(self);

        WBOutputDtoList receiver = new WBOutputDtoList();
        receiver.setAddress(targetAddress);
        receiver.setValue(sendAmount.stripTrailingZeros().toPlainString());
        request.addOutputList(receiver);

        return request;
    }

}
