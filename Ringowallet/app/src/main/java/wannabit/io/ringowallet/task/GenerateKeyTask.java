package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import org.bitcoinj.crypto.DeterministicKey;

import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.crypto.CryptoHelper;
import wannabit.io.ringowallet.crypto.EncResult;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.Mnemonic;
import wannabit.io.ringowallet.utils.WBHCUtils;
import wannabit.io.ringowallet.utils.WBTCUtils;
import wannabit.io.ringowallet.utils.WETCUtils;
import wannabit.io.ringowallet.utils.WETHUtils;
import wannabit.io.ringowallet.utils.WLTCUtils;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WQTUMUtils;

public class GenerateKeyTask extends AsyncTask<String, Void, TaskResult> {

    private BaseApplication         mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;

    public GenerateKeyTask(BaseApplication app, TaskCallback mCallback) {
        this.mApp = app;
        this.mCallback = mCallback;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_INSERT_GENERATE_WITH_MNEMONIC;
    }


    /**
     *
     * @param strings
     *  strings[0] : coinType
     *  strings[1] : coinSymbol
     *
     * @return
     */
    @Override
    protected TaskResult doInBackground(String... strings) {
        long result = -1;
        try {
            Mnemonic mnemonic  = mApp.getBaseDao().onSelectMnemonic();
            String seed        = CryptoHelper.doDecryptData(BaseConstant.MNEMONIC_KEY+mnemonic.getUuid(), mnemonic.getResource(), mnemonic.getSpec());

            if(strings[0].equals(BaseConstant.COIN_BTC)) {
                result =  mApp.getBaseDao().onInsertKey(onGenerateBTC(seed, strings[0], strings[1]));

            } else if (strings[0].equals(BaseConstant.COIN_BCH)) {
                result =  mApp.getBaseDao().onInsertKey(onGenerateBCH(seed, strings[0], strings[1]));

            } else if (strings[0].equals(BaseConstant.COIN_LTC)) {
                result =  mApp.getBaseDao().onInsertKey(onGenerateLTC(seed, strings[0], strings[1]));

            } else if (strings[0].equals(BaseConstant.COIN_ETH)) {
                result =  mApp.getBaseDao().onInsertKey(onGenerateETH(seed, strings[0], strings[1]));

            } else if (strings[0].equals(BaseConstant.COIN_ETC)) {
                result =  mApp.getBaseDao().onInsertKey(onGenerateETC(seed, strings[0], strings[1]));

            } else if (strings[0].equals(BaseConstant.COIN_QTUM)) {
                result =  mApp.getBaseDao().onInsertKey(onGenerateQTUM(seed, strings[0], strings[1]));

            } else if (strings[0].equals(BaseConstant.COIN_ERC20)) {
                result =  mApp.getBaseDao().onInsertKey(onGenerateErc20(seed, strings[0], strings[1]));

            } else if (strings[0].equals(BaseConstant.COIN_QRC20)) {
            }

            if(result > 0) {
                mResult.isSuccess = true;
            }

        } catch (Exception e) {
            mResult.isSuccess = false;
            mResult.resultMsg = e.getMessage();
        }

        return mResult;
    }


    @Override
    protected void onPostExecute(TaskResult taskResult) {
        super.onPostExecute(taskResult);
        mCallback.onTaskResponse(taskResult);
    }


    private Key onGenerateBTC(String seed, String type, String symbol) {
        Key result                      = new Key();
        WBTCUtils util_btc              = new WBTCUtils(seed);
        DeterministicKey newBtcKey      = util_btc.onGenerateKey(mApp.getBaseDao().getNextPath(type, symbol));
        EncResult encR                  = CryptoHelper.doEncryptData(result.uuid, util_btc.getPrivateKey(newBtcKey), false);
        result.init(BaseConstant.COIN_BTC,
                BaseConstant.COIN_BTC,
                newBtcKey.getChildNumber().getI(),
                false,
                encR.getEncDataString(),
                encR.getIvDataString(),
                util_btc.getAddress(newBtcKey),
                false,
                System.currentTimeMillis());
        return result;
    }


    private Key onGenerateETH(String seed, String type, String symbol) {
        Key result                      = new Key();
        WETHUtils util_eth              = new WETHUtils(seed);
        DeterministicKey newEthKey      = util_eth.onGenerateKey(mApp.getBaseDao().getNextPath(type,symbol));
        EncResult encR                  = CryptoHelper.doEncryptData(result.uuid, util_eth.getPrivateKey(newEthKey), false);

        result.init(BaseConstant.COIN_ETH,
                BaseConstant.COIN_ETH,
                newEthKey.getChildNumber().getI(),
                false,
                encR.getEncDataString(),
                encR.getIvDataString(),
                util_eth.getAddress(newEthKey),
                false,
                System.currentTimeMillis());
        return result;
    }

    private Key onGenerateBCH(String seed, String type, String symbol) {
        Key result                                              = new Key();
        WBHCUtils util_bch                                      = new WBHCUtils(seed);
        org.bitcoincashj.crypto.DeterministicKey newBchKey      = util_bch.onGenerateKey(mApp.getBaseDao().getNextPath(type,symbol));
        EncResult encR                                          = CryptoHelper.doEncryptData(result.uuid, util_bch.getPrivateKey(newBchKey), false);

        result.init(BaseConstant.COIN_BCH,
                BaseConstant.COIN_BCH,
                newBchKey.getChildNumber().getI(),
                false,
                encR.getEncDataString(),
                encR.getIvDataString(),
                util_bch.getAddress(newBchKey),
                false,
                System.currentTimeMillis());
        return result;
    }

    private Key onGenerateLTC(String seed, String type, String symbol) {
        Key result                      = new Key();
        WLTCUtils util_ltc              = new WLTCUtils(seed);
        DeterministicKey newLtcKey      = util_ltc.onGenerateKey(mApp.getBaseDao().getNextPath(type,symbol));
        EncResult encR                  = CryptoHelper.doEncryptData(result.uuid, util_ltc.getPrivateKey(newLtcKey), false);

        result.init(BaseConstant.COIN_LTC,
                BaseConstant.COIN_LTC,
                newLtcKey.getChildNumber().getI(),
                false,
                encR.getEncDataString(),
                encR.getIvDataString(),
                util_ltc.getAddress(newLtcKey),
                false,
                System.currentTimeMillis());
        return result;
    }


    private Key onGenerateErc20(String seed, String type, String symbol) {
        WLog.w("onGenerateErc20");
        Key result                      = new Key();
        WETHUtils util_eth              = new WETHUtils(seed);
        DeterministicKey newEthKey      = util_eth.onGenerateKey(mApp.getBaseDao().getNextPath(type,symbol));
        EncResult encR                  = CryptoHelper.doEncryptData(result.uuid, util_eth.getPrivateKey(newEthKey), false);

        result.init(BaseConstant.COIN_ERC20,
                symbol,
                newEthKey.getChildNumber().getI(),
                false,
                encR.getEncDataString(),
                encR.getIvDataString(),
                util_eth.getAddress(newEthKey),
                false,
                System.currentTimeMillis());
        return result;
    }

    private Key onGenerateETC(String seed, String type, String symbol) {
        Key result                      = new Key();
        WETCUtils util_etc              = new WETCUtils(seed);
        DeterministicKey newEtcKey      = util_etc.onGenerateKey(mApp.getBaseDao().getNextPath(type,symbol));
        EncResult encR                  = CryptoHelper.doEncryptData(result.uuid, util_etc.getPrivateKey(newEtcKey), false);

        result.init(BaseConstant.COIN_ETC,
                BaseConstant.COIN_ETC,
                newEtcKey.getChildNumber().getI(),
                false,
                encR.getEncDataString(),
                encR.getIvDataString(),
                util_etc.getAddress(newEtcKey),
                false,
                System.currentTimeMillis());
        return result;
    }

    private Key onGenerateQTUM(String seed, String type, String symbol) {
        Key result                      = new Key();
        WQTUMUtils util_qtum            = new WQTUMUtils(seed);
        DeterministicKey newEtcKey      = util_qtum.onGenerateKey(mApp.getBaseDao().getNextPath(type,symbol));
        EncResult encR                  = CryptoHelper.doEncryptData(result.uuid, util_qtum.getPrivateKey(newEtcKey), false);

        result.init(BaseConstant.COIN_QTUM,
                BaseConstant.COIN_QTUM,
                newEtcKey.getChildNumber().getI(),
                false,
                encR.getEncDataString(),
                encR.getIvDataString(),
                util_qtum.getAddress(newEtcKey),
                false,
                System.currentTimeMillis());
        return result;
    }
}
