package wannabit.io.ringowallet.task.send;

import android.os.Bundle;
import android.text.TextUtils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.crypto.CryptoHelper;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.Password;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.utils.WLog;

public class EthSendTask extends SendTask {

    private BaseApplication         mApp;
    private TaskCallback mCallback;
    private TaskResult mResult;


    public EthSendTask(BaseApplication app, TaskCallback mCallback) {
        WLog.w("EthSendTask");
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

        Web3j web3 = Web3jFactory.build(new HttpService("https://mainnet.infura.io/"));
        try {
            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(from_address, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();

            WLog.w("gasPrice : " + Convert.toWei(gasPrice, Convert.Unit.GWEI).toBigInteger());
            WLog.w("gasLimit : " + Convert.toWei(gasLimit, Convert.Unit.WEI).toBigInteger());
            WLog.w("amount : " + new BigInteger(amount).toString());

            RawTransaction rawTransaction = RawTransaction.createEtherTransaction (
                    nonce,                                                          // nonce
                    Convert.toWei(gasPrice, Convert.Unit.GWEI).toBigInteger(),      // gasPrice
                    Convert.toWei(gasLimit, Convert.Unit.WEI).toBigInteger(),      // gasLimit
                    toAddress,                                                      // receiveAddress
                    new BigInteger(amount));                                        // amount

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(from_pKey));
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).sendAsync().get();
            if(ethSendTransaction != null && !ethSendTransaction.hasError() && !TextUtils.isEmpty(ethSendTransaction.getTransactionHash())) {
                WLog.w("resultTxid : " + ethSendTransaction.getTransactionHash());
                mResult.isSuccess = true;
                mResult.resultData3 = ethSendTransaction.getTransactionHash();
            }


        } catch (Exception e) {
            WLog.w("EthSendTask : "  + e.getMessage());
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
