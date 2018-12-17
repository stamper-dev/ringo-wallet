package wannabit.io.ringowallet.task.send;

import android.os.Bundle;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.libdohj.params.LitecoinMainNetParams;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.crypto.CryptoHelper;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.Password;
import wannabit.io.ringowallet.network.ApiClient;
import wannabit.io.ringowallet.network.WannbitService;
import wannabit.io.ringowallet.network.req.ReqCreateRawTx;
import wannabit.io.ringowallet.network.req.ReqSendRawTx;
import wannabit.io.ringowallet.network.res.ResCreateRawTx;
import wannabit.io.ringowallet.network.res.ResRawTx;
import wannabit.io.ringowallet.network.res.ResSendRawTx;
import wannabit.io.ringowallet.network.res.ResUTXByAddress;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.task.send.SendTask;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class LtcSendTask extends SendTask {

    private BaseApplication         mApp;
    private TaskCallback mCallback;
    private TaskResult mResult;

    public LtcSendTask(BaseApplication app, TaskCallback mCallback) {
        WLog.w("LtcSendTask");
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


        NetworkParameters params = LitecoinMainNetParams.get();
        try {
            DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, from_pKey);
            ECKey pkey = dumpedPrivateKey.getKey();

            Response<List<ResUTXByAddress>> resUTX = ApiClient.getWannabitService(mApp).getLTCUTXOByAddr(pkey.toAddress(params).toString()).execute();
            for(ResUTXByAddress utxb : resUTX.body()) {
                totalAmount = totalAmount.add(BigDecimal.valueOf(utxb.getValue()));
            }
            WLog.w("totalAmount : " + totalAmount.toPlainString());
            toSendAmount = new BigDecimal(amount).movePointLeft(8);
            WLog.w("toSendAmount : " + toSendAmount);
            feeAmount = new BigDecimal(fee).movePointLeft(8);
            WLog.w("feeAmount : " + feeAmount);

//            Transaction[] rawInputTransactions = new Transaction[resUTX.body().size()];
//            for(int i = 0; i < resUTX.body().size() ; i ++) {
//                Response<ResRawTx> resRawTx = service.getLTCRawTx(resUTX.body().get(i).getTxid()).execute();
//                rawInputTransactions[i] = new Transaction(params, WUtils.hexStringToByteArray(resRawTx.body().getRawTransaction()));
//            }

            ReqCreateRawTx rawNewPropose = WUtils.onGenerateLocalTx(resUTX.body(), pkey, params, totalAmount, toSendAmount, feeAmount, toAddress);
            Response<ResCreateRawTx> rawTxResponse = ApiClient.getWannabitService(mApp).createLTCRawTx(rawNewPropose).execute();
            Transaction transaction = new Transaction(params, WUtils.hexStringToByteArray(rawTxResponse.body().getRawTransaction()));

            for (int i = 0; i < transaction.getInputs().size(); i++) {
                TransactionInput transactionInput = transaction.getInput(i);
                Script scriptPubKey = ScriptBuilder.createOutputScript(Address.fromBase58(params, resUTX.body().get(i).getAddress()));
                Sha256Hash hash = transaction.hashForSignature(i, scriptPubKey, Transaction.SigHash.ALL, true);
                ECKey.ECDSASignature ecSig = pkey.sign(hash);
                TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.ALL, true);
                if (scriptPubKey.isSentToRawPubKey()) {
                    transactionInput.setScriptSig(ScriptBuilder.createInputScript(txSig));
                } else {
                    if (!scriptPubKey.isSentToAddress()) {
                        throw new ScriptException("Don\'t know how to sign for this kind of scriptPubKey: " + scriptPubKey);
                    }
                    transactionInput.setScriptSig(ScriptBuilder.createInputScript(txSig, pkey));
                }
            }

            ReqSendRawTx sendReq = new ReqSendRawTx(WUtils.bytesToHex(transaction.bitcoinSerialize()).trim());
            Response<ResSendRawTx> mainResult = ApiClient.getWannabitService(mApp).sendLTCRawTx(sendReq).execute();
            if(mainResult.isSuccessful()) {
                mResult.isSuccess = true;
                mResult.resultData3 = mainResult.body().getTxid();

            } else {
                WLog.w("mainResult Error : " +mainResult.body().toString());


            }

        } catch (Exception e) {
            WLog.w("LtcSendTask : "  + e.getMessage());
            mResult.resultMsg = e.getMessage();
        }
        return mResult;
    }

    @Override
    protected void onPostExecute(TaskResult result) {
        super.onPostExecute(result);
        mCallback.onTaskResponse(result);
    }



}
