package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.utils.WLog;

public class EthGasLimitTask extends AsyncTask<String, Void, TaskResult> {

    private BaseApplication         mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;

    public EthGasLimitTask(BaseApplication app, TaskCallback mCallback) {
        this.mApp = app;
        this.mCallback = mCallback;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_GAS_LIMIT;

    }

    /**
     *
     * @param strings
     *  strings[0] : key.uuid
     *  strings[1] : target address
     *  strings[2] : send amount
     *  strings[3] : gasPrice
     *  strings[4] : ContractAdd
     *
     * @return
     */
    @Override
    protected TaskResult doInBackground(String... strings) {
        Web3j web3 = Web3jFactory.build(new HttpService(mApp.getString(R.string.infura_url)));
        try {

            Key key = mApp.getBaseDao().onSelectKey(strings[0]);
            String from_address = key.address;

            Function function = new Function(
                    mApp.getString(R.string.str_transfer_from),
                    Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(from_address),
                            new org.web3j.abi.datatypes.Address(strings[1]),
                            new org.web3j.abi.datatypes.generated.Uint256(Convert.toWei(strings[2], Convert.Unit.ETHER).toBigInteger())),
                    Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder.encode(function);

            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(from_address, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();

            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    Convert.toWei("22", Convert.Unit.MWEI).toBigInteger(),          // gasPrice
                    Convert.toWei("44", Convert.Unit.GWEI).toBigInteger(),          // gasLimit
                    strings[4],                                                     // targetCoin
                    encodedFunction);

            EthEstimateGas gas = web3.ethEstimateGas(
                    Transaction.createContractTransaction(
                            from_address,
                            BigInteger.ONE,
                            BigInteger.ONE,
                            BigInteger.TEN,
                            BigInteger.ONE,
                            encodedFunction)).send();


            if(gas != null) {
                if(gas.getAmountUsed() != null) {
                    mResult.resultData3 = gas.getAmountUsed().toString();
                }

            } else {
                WLog.w("gas null");
            }


        } catch (Exception e) {
            WLog.w("EthBalanceCheckTask : "  + e.getMessage());
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