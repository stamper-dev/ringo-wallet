package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import org.bitcoinj.crypto.DeterministicKey;

import wannabit.io.ringowallet.BuildConfig;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.crypto.CryptoHelper;
import wannabit.io.ringowallet.crypto.EncResult;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.Mnemonic;
import wannabit.io.ringowallet.model.Support;
import wannabit.io.ringowallet.utils.WETCUtils;
import wannabit.io.ringowallet.utils.WBHCUtils;
import wannabit.io.ringowallet.utils.WBTCUtils;
import wannabit.io.ringowallet.utils.WETHUtils;
import wannabit.io.ringowallet.utils.WLTCUtils;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WQTUMUtils;

public class InitWalletTask extends AsyncTask<String, Void, TaskResult> {

    private BaseApplication         mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;

    public InitWalletTask(BaseApplication app, TaskCallback mCallback) {
        this.mApp = app;
        this.mCallback = mCallback;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_INIT_WALLET;
    }

    @Override
    protected TaskResult doInBackground(String... strings) {
        try {
            Mnemonic mnemonic  = mApp.getBaseDao().onSelectMnemonic();
            String seed        = CryptoHelper.doDecryptData(BaseConstant.MNEMONIC_KEY+mnemonic.getUuid(), mnemonic.getResource(), mnemonic.getSpec());

            Support support     = mApp.getBaseDao().getSupport();
            if(support.btc) mApp.getBaseDao().onInsertKey(onGenerateFirstBTC(seed));
            if(support.eth) mApp.getBaseDao().onInsertKey(onGenerateFirstETH(seed));
            if(support.ltc) mApp.getBaseDao().onInsertKey(onGenerateFirstLTC(seed));
            if(support.etc) mApp.getBaseDao().onInsertKey(onGenerateFirstETC(seed));
            if(support.bch) mApp.getBaseDao().onInsertKey(onGenerateFirstBCH(seed));
            if(support.qtum) mApp.getBaseDao().onInsertKey(onGenerateFirstQTUM(seed));

            mResult.isSuccess = true;


        } catch (Exception e) {
            WLog.w("ex : " + e.getMessage());
        }

        return mResult;
    }

    @Override
    protected void onPostExecute(TaskResult taskResult) {
        super.onPostExecute(taskResult);
        mCallback.onTaskResponse(taskResult);
    }


    private Key onGenerateFirstBTC(String seed) {
        Key result                      = new Key();
        WBTCUtils util_btc              = new WBTCUtils(seed);
        DeterministicKey firstKey_btc   = util_btc.getFirstKey();
        EncResult encR                  = CryptoHelper.doEncryptData(result.uuid, util_btc.getPrivateKey(firstKey_btc), false);

        result.init(BaseConstant.COIN_BTC,
                BaseConstant.COIN_BTC,
                firstKey_btc.getChildNumber().getI(),
                false,
                encR.getEncDataString(),
                encR.getIvDataString(),
                util_btc.getAddress(firstKey_btc),
                false,
                System.currentTimeMillis());
        return result;
    }

    private Key onGenerateFirstETH(String seed) {
        Key result                      = new Key();
        WETHUtils util_eth              = new WETHUtils(seed);
        DeterministicKey first_eth      = util_eth.getFirstKey();
        EncResult encR                  = CryptoHelper.doEncryptData(result.uuid, util_eth.getPrivateKey(first_eth), false);

        result.init(BaseConstant.COIN_ETH,
                BaseConstant.COIN_ETH,
                first_eth.getChildNumber().getI(),
                false,
                encR.getEncDataString(),
                encR.getIvDataString(),
                util_eth.getAddress(first_eth),
                false,
                System.currentTimeMillis());
        return result;
    }

    private Key onGenerateFirstETC(String seed) {
        Key result                      = new Key();
        WETCUtils util_etc               = new WETCUtils(seed);
        DeterministicKey first_eth      = util_etc.getFirstKey();
        EncResult encR                  = CryptoHelper.doEncryptData(result.uuid, util_etc.getPrivateKey(first_eth), false);

        result.init(BaseConstant.COIN_ETC,
                BaseConstant.COIN_ETC,
                first_eth.getChildNumber().getI(),
                false,
                encR.getEncDataString(),
                encR.getIvDataString(),
                util_etc.getAddress(first_eth),
                false,
                System.currentTimeMillis());
        return result;
    }

    private Key onGenerateFirstBCH(String seed) {
        Key result                                              = new Key();
        WBHCUtils util_bch                                      = new WBHCUtils(seed);
        org.bitcoincashj.crypto.DeterministicKey first_bch      = util_bch.getFirstKey();
        EncResult encR                                          = CryptoHelper.doEncryptData(result.uuid, util_bch.getPrivateKey(first_bch), false);

        result.init(BaseConstant.COIN_BCH,
                BaseConstant.COIN_BCH,
                first_bch.getChildNumber().getI(),
                false,
                encR.getEncDataString(),
                encR.getIvDataString(),
                util_bch.getAddress(first_bch),
                false,
                System.currentTimeMillis());
        return result;
    }

    private Key onGenerateFirstLTC(String seed) {
        Key result                      = new Key();
        WLTCUtils util_ltc              = new WLTCUtils(seed);
        DeterministicKey first_ltc      = util_ltc.getFirstKey();
        EncResult encR                  = CryptoHelper.doEncryptData(result.uuid, util_ltc.getPrivateKey(first_ltc), false);

        result.init(BaseConstant.COIN_LTC,
                BaseConstant.COIN_LTC,
                first_ltc.getChildNumber().getI(),
                false,
                encR.getEncDataString(),
                encR.getIvDataString(),
                util_ltc.getAddress(first_ltc),
                false,
                System.currentTimeMillis());
        return result;
    }

    private Key onGenerateFirstQTUM(String seed) {
        Key result                      = new Key();
        WQTUMUtils util_qtum            = new WQTUMUtils(seed);
        DeterministicKey first_qtum     = util_qtum.getFirstKey();
        EncResult encR                  = CryptoHelper.doEncryptData(result.uuid, util_qtum.getPrivateKey(first_qtum), false);

        result.init(BaseConstant.COIN_QTUM,
                BaseConstant.COIN_QTUM,
                first_qtum.getChildNumber().getI(),
                false,
                encR.getEncDataString(),
                encR.getIvDataString(),
                util_qtum.getAddress(first_qtum),
                false,
                System.currentTimeMillis());
        return result;
    }

}
