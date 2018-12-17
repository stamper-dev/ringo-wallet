package wannabit.io.ringowallet.task.send;

import android.os.Bundle;

import com.google.gson.JsonObject;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

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
import wannabit.io.ringowallet.network.req.ReqSendRawTxETC;
import wannabit.io.ringowallet.network.req.ReqTxParamETC;
import wannabit.io.ringowallet.network.res.ResTxParamETC;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.utils.WLog;

public class EtcSendTask extends SendTask {

    private BaseApplication         mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;

    public EtcSendTask(BaseApplication app, TaskCallback mCallback) {
        WLog.w("EtcSendTask");
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
     *  strings[4] : gasPrice
     *  strings[5] : gasLimit
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
        String gasPrice         = bundle.getString("gasPrice");
        String gasLimit         = bundle.getString("gasLimit");
        WLog.w("bundle : " + bundle.toString());
        WLog.w("pincode : " + pincode);
        WLog.w("address : " + toAddress);


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


        ReqTxParamETC param = new ReqTxParamETC(
                "101",                                                              //useless
                from_address,                                                       //from
                toAddress,                                                          //to
                Convert.fromWei(amount, Convert.Unit.ETHER).toPlainString(),        //value
                gasPrice,                                                           //gasprice
                gasLimit);                                                          //gaslimit


        try {
            Response<ResTxParamETC> responseTx = ApiClient.getWannabitService(mApp).getTxParams(param).execute();
            WLog.w("responseTx : " + responseTx);
            WLog.w("responseTx body : " + responseTx.body());
            WLog.w("response value : " + new BigInteger(Numeric.cleanHexPrefix(responseTx.body().getNonce()), 16));
            WLog.w("nonce : " + new BigInteger(Numeric.cleanHexPrefix(responseTx.body().getNonce()), 16));
            WLog.w("gasPrice : " + new BigInteger(Numeric.cleanHexPrefix(responseTx.body().getGasPrice()), 16));
            WLog.w("gasLimit : " + new BigInteger(Numeric.cleanHexPrefix(responseTx.body().getGasLimit()), 16));
            WLog.w("to : " + responseTx.body().getTo());
            WLog.w("value : " + new BigInteger(Numeric.cleanHexPrefix(responseTx.body().getValue()), 16));
            WLog.w("chainId : " + responseTx.body().getChainId());



            RawTransaction rawTransaction = RawTransaction.createEtherTransaction (
                    new BigInteger(Numeric.cleanHexPrefix(responseTx.body().getNonce()), 16),                   // nonce
                    new BigInteger(Numeric.cleanHexPrefix(responseTx.body().getGasPrice()), 16),                // gasPrice
                    new BigInteger(Numeric.cleanHexPrefix(responseTx.body().getGasLimit()), 16),                // gasLimit
                    toAddress,                                                                                  // receiveAddress
                    new BigInteger(Numeric.cleanHexPrefix(responseTx.body().getValue()), 16));                  // sendAmount

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, (byte)0x3D,  Credentials.create(from_pKey));
            String hexValue = Numeric.toHexString(signedMessage);
            WLog.w("hexValue : " + hexValue);

            Response<JsonObject> sendResponse = ApiClient.getWannabitService(mApp).sendRawTxETC(new ReqSendRawTxETC(101, hexValue)).execute();
            if(sendResponse.isSuccessful()) {
                mResult.isSuccess = true;
                mResult.resultData3 = sendResponse.body().get("txid").getAsString();

            } else {
                WLog.w("sendResponse Error : " +sendResponse.body().toString());
            }


        } catch (Exception e) {
            WLog.w("EtcSendTask : "  + e.getMessage());
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
