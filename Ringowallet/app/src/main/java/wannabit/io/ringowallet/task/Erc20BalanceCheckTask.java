package wannabit.io.ringowallet.task;

import android.os.AsyncTask;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import wannabit.io.ringowallet.base.BaseApplication;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.model.Key;
import wannabit.io.ringowallet.model.WalletItem;
import wannabit.io.ringowallet.utils.WLog;

public class Erc20BalanceCheckTask extends AsyncTask<String, Void, TaskResult> {
    private BaseApplication         mApp;
    private TaskCallback            mCallback;
    private TaskResult              mResult;
    private WalletItem              mWalletItem;

    public Erc20BalanceCheckTask(BaseApplication app, TaskCallback mCallback, WalletItem walletItem, int position) {
//        WLog.w("Erc20BalanceCheckTask " + walletItem.symbol + "  " + walletItem.type );
        this.mApp = app;
        this.mCallback = mCallback;
        this.mWalletItem = walletItem;

        this.mResult = new TaskResult();
        this.mResult.taskType = BaseConstant.TASK_BALANCE;
        this.mResult.resultData1 = position;
        this.mResult.resultData4 = walletItem.symbol;
    }

    @Override
    protected TaskResult doInBackground(String... strings) {

        Web3j web3 = Web3jFactory.build(new HttpService("https://mainnet.infura.io/"));

        try {
            for(Key key : mWalletItem.keys) {
//                WLog.w("address : " + key.address + " con : " + mWalletItem.contractAddr);
//
//                EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(key.address, DefaultBlockParameterName.LATEST).sendAsync().get();
//                BigInteger nonce = ethGetTransactionCount.getTransactionCount();
//                BigInteger blockGasLimit = web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock().getGasLimit();
//                WLog.w("nonce : " + nonce);
//                WLog.w("blockGasLimit : " + blockGasLimit.toString());
//
////                EthEstimateGas gas = web3.ethEstimateGas(new Transaction(key.address, nonce, maxGasPrice, blockGasLimit, mWalletItem.contractAddr, value, call.data)).send();
//                EthEstimateGas gas = web3.ethEstimateGas(
//                        Transaction.createContractTransaction(
//                                "0x52b93c80364dc2dd4444c146d73b9836bbbb2b3f", BigInteger.ONE,
//                                BigInteger.TEN, "")).send();
//                WLog.w("gas : " + gas.getAmountUsed());
//
//                EthEstimateGas sendgas = web3.ethEstimateGas(
//                        Transaction.createEthCallTransaction(
//                                "0xa70e8dd61c5d32be8058bb8eb970870f07233155",
//                                "0x52b93c80364dc2dd4444c146d73b9836bbbb2b3f", "0x0")).send();
//
//                WLog.w("sendgas : " + sendgas.getAmountUsed());


                Function function = new Function(
                        "balanceOf",
                        Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(key.address)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));

                String encodedFunction = FunctionEncoder.encode(function);
                EthCall response = web3.ethCall(Transaction.createEthCallTransaction(key.address, mWalletItem.contractAddr , encodedFunction), DefaultBlockParameterName.LATEST).sendAsync().get();
                List<Type> someTypes = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());

                BigDecimal balance = BigDecimal.ZERO;
                if(someTypes != null && someTypes.size() > 0) {
                    for(Type type: someTypes) {
                        balance = balance.add(new BigDecimal(type.getValue().toString()));
                    }
                }
                mApp.getBaseDao().onUpdateBalance(key.uuid, balance.toPlainString());
//                if(!key.lastBalance.equals(balance)) result.resultData2 = 1;
                mResult.isSuccess = true;
            }

        } catch (Exception e) {
            WLog.w("Erc20BalanceCheckTask : "  +e.getMessage());
        }
        return mResult;
    }

    @Override
    protected void onPostExecute(TaskResult result) {
        super.onPostExecute(result);
        mCallback.onTaskResponse(result);
    }
}
