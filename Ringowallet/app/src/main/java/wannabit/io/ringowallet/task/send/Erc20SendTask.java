package wannabit.io.ringowallet.task.send;

import android.os.Bundle;
import android.text.TextUtils;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.crypto.CryptoHelper;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.Password;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.utils.WLog;

public class Erc20SendTask extends SendTask {

    private BaseApplication         mApp;
    private TaskCallback mCallback;
    private TaskResult mResult;

    public Erc20SendTask(BaseApplication app, TaskCallback mCallback) {
        WLog.w("Erc20SendTask");
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
     *  strings[3] : send amount            // full decimal
     *  strings[4] : gasPrice
     *  strings[5] : gasLimit
     *  strings[6] : contract address
     *
     * @return
     */
    @Override
    protected TaskResult doInBackground(Bundle... bundles) {
        Bundle bundle           = bundles[0];
        String pincode          = bundle.getString("pincode");
        String uuid             = bundle.getString("uuid");
        String to_address       = bundle.getString("address");
        String amount           = bundle.getString("amount");
        String gasPrice         = bundle.getString("gasPrice");
        String gasLimit         = bundle.getString("gasLimit");
        String contractAddress  = bundle.getString("contractAddr");

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
            WLog.w("nonce : " + nonce);

            EthGetCode ethGetCode = web3.ethGetCode(contractAddress, DefaultBlockParameterName.LATEST).send();
            String binary = ethGetCode.getCode();
            WLog.w("binary : " + binary);

            Function function = new Function(
                    "transferFrom",
                    Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(from_address),
                            new org.web3j.abi.datatypes.Address(to_address),
                            new org.web3j.abi.datatypes.generated.Uint256(new BigInteger(amount))),
                    Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);
            WLog.w("encodedFunction : " + encodedFunction);

            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    Convert.toWei(gasPrice, Convert.Unit.GWEI).toBigInteger(),          // gasPrice
                    Convert.toWei(gasLimit, Convert.Unit.WEI).toBigInteger(),           // gasLimit
                    contractAddress,                                                    // targetCoin
                    encodedFunction);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(from_pKey));
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction transactionResponse = web3.ethSendRawTransaction(hexValue).sendAsync().get();
            if(transactionResponse != null && !transactionResponse.hasError() && !TextUtils.isEmpty(transactionResponse.getTransactionHash())) {
                WLog.w("resultTxid : " + transactionResponse.getTransactionHash());
                mResult.isSuccess = true;
                mResult.resultData3 = transactionResponse.getTransactionHash();
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
