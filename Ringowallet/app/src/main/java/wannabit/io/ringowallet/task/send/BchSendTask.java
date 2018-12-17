package wannabit.io.ringowallet.task.send;

import android.os.Bundle;

import org.bitcoincashj.core.Address;
import org.bitcoincashj.core.Coin;
import org.bitcoincashj.core.DumpedPrivateKey;
import org.bitcoincashj.core.ECKey;
import org.bitcoincashj.core.NetworkParameters;
import org.bitcoincashj.core.Sha256Hash;
import org.bitcoincashj.core.Transaction;
import org.bitcoincashj.crypto.TransactionSignature;
import org.bitcoincashj.script.Script;
import org.bitcoincashj.script.ScriptBuilder;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Response;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.crypto.CryptoHelper;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.Password;
import wannabit.io.ringowallet.network.ApiClient;
import wannabit.io.ringowallet.network.req.ReqSendRawTxBCH;
import wannabit.io.ringowallet.network.res.ResBchUTX;
import wannabit.io.ringowallet.network.res.ResSendRawTx;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.utils.BCHMainNetParams;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class BchSendTask extends SendTask {

    private BaseApplication     mApp;
    private TaskCallback        mCallback;
    private TaskResult          mResult;

    public BchSendTask(BaseApplication app, TaskCallback mCallback) {
        WLog.w("BchSendTask");
        this.mApp = app;
        this.mCallback = mCallback;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_SEND;
    }



    /**
     *
     * @param bundles
     *  strings[0] : pincode
     *  strings[1] : key.uuid
     *  strings[2] : target address
     *  strings[3] : send amount
     *  strings[4] : fee
     *
     * @return
     */
    @Override
    protected TaskResult doInBackground(Bundle... bundles) {
        Bundle bundle           = bundles[0];
        String pincode          = bundle.getString("pincode");
        String uuid             = bundle.getString("uuid");
        String toAddress        = bundle.getString("address");
        String amount           = bundle.getString("amount");
        String fee              = bundle.getString("fee");

        WLog.w("toAddress : " + toAddress);
        WLog.w("amount : " + amount);
        WLog.w("fee : " + fee);

        Password checkPw = mApp.getBaseDao().onSelectPassword();
        if(!CryptoHelper.verifyData(pincode, checkPw.getResource(), BaseConstant.PASSWORD_KEY)) {
            mResult.isSuccess = false;
            mResult.resultCode = -99;
            return mResult;
        }


        Key key = mApp.getBaseDao().onSelectKey(uuid);
        String from_address = key.address;
        String from_pKey = CryptoHelper.doDecryptData(key.uuid, key.resource, key.spec);
        WLog.w("from_address : " + from_address);
        WLog.w("from_pKey : " + from_pKey);

        BigDecimal totalAmount  = BigDecimal.ZERO;
        BigDecimal toSendAmount = BigDecimal.ZERO;
        BigDecimal feeAmount    = BigDecimal.ZERO;
        BigDecimal selfReturn   = BigDecimal.ZERO;

        NetworkParameters params = BCHMainNetParams.get();
        try {
            DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, from_pKey);
            ECKey pkey = dumpedPrivateKey.getKey();

            Response<List<ResBchUTX>> resUTX = ApiClient.getBlockDozerService(mApp).getBchUtxo(from_address).execute();
            for(ResBchUTX utxb : resUTX.body()) {
                totalAmount = totalAmount.add(BigDecimal.valueOf(utxb.amount).movePointRight(8));
            }
            WLog.w("resUTX  size: " + resUTX.body().size());

            WLog.w("totalAmount : " + totalAmount.toPlainString());
            toSendAmount = new BigDecimal(amount);
            WLog.w("toSendAmount : " + toSendAmount);
            feeAmount = new BigDecimal(fee);
            WLog.w("feeAmount : " + feeAmount);
            selfReturn = totalAmount.subtract(toSendAmount).subtract(feeAmount);
            WLog.w("selfReturn : " + selfReturn);


            Transaction tx = onCreateRawTransaction(params, resUTX.body(), pkey, toAddress, toSendAmount, from_address, selfReturn);
            for (int i = 0; i < tx.getInputs().size(); i ++) {
                Sha256Hash              hash        = tx.hashForSignature(i, ScriptBuilder.createOutputScript(pkey.toAddress(params)), Transaction.SigHash.ALL, false);
                ECKey.ECDSASignature    sig         = pkey.sign(hash);
                TransactionSignature    signature   = new TransactionSignature(sig, Transaction.SigHash.ALL, false, true);
                Script                  scriptSig   = ScriptBuilder.createInputScript(signature, pkey);
                tx.getInput(i).setScriptSig(scriptSig);
            }
            WLog.w("resultAA : " + WUtils.bytesToHex(tx.bitcoinSerialize()).trim().toLowerCase());

            Response<ResSendRawTx> mainResult = ApiClient.getBlockDozerService(mApp).sendRawTxBch(new ReqSendRawTxBCH(WUtils.bytesToHex(tx.bitcoinSerialize()).trim().toLowerCase())).execute();
            if(mainResult.isSuccessful()) {
                mResult.isSuccess = true;
                mResult.resultData3 = mainResult.body().getTxid();

            } else {
                WLog.w("mainResult Error : " + mainResult.body().toString());

            }


        } catch (Exception e) {
            WLog.w("BchSendTask Error : "  + e.getMessage());
            mResult.resultMsg = e.getMessage();

        }

        return mResult;
    }





    @Override
    protected void onPostExecute(TaskResult result) {
        super.onPostExecute(result);
        mCallback.onTaskResponse(result);
    }


    private Transaction onCreateRawTransaction(NetworkParameters params, List<ResBchUTX> inputs, ECKey pkey, String targetAddr, BigDecimal targetAmount, String selfAddr, BigDecimal selfAmount) {
        Transaction tx = new Transaction(params);
        tx.setVersion(2);
        for(ResBchUTX input:inputs) {
            tx.addInput(new Sha256Hash(input.txid), input.vout, ScriptBuilder.createOutputScript(pkey.toAddress(params)), Coin.valueOf(BigDecimal.valueOf(input.amount).movePointRight(8).longValue()));
        }

        Address targetAddress = Address.fromBase58(params, targetAddr);
        tx.addOutput(Coin.valueOf(targetAmount.longValue()), targetAddress);

        if(selfAmount.compareTo(BigDecimal.ZERO) > 0) {
            Address selfAddress = Address.fromBase58(params, selfAddr);
            tx.addOutput(Coin.valueOf(selfAmount.longValue()), selfAddress);
        }
        return tx;
    }


}
